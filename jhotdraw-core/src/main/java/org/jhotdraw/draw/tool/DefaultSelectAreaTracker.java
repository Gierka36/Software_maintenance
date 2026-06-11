/*
 * @(#)DefaultSelectAreaTracker.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import org.jhotdraw.draw.figure.Figure;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.handle.Handle;

/**
 * <code>DefaultSelectAreaTracker</code> implements interactions with the background
 * area of a <code>Drawing</code>.
 * <p>
 * The <code>DefaultSelectAreaTracker</code> handles one of the three states of the
 * <code>SelectionTool</code>. It comes into action, when the user presses
 * the mouse button over the background of a <code>Drawing</code>.
 * <p>
 * By default, dragging a selection rectangle selects only the figures that
 * are fully enclosed within it. Holding down the Alt key while releasing the
 * mouse button switches to intersect mode, which additionally selects
 * figures that are merely overlapped by the rectangle.
 * <p>
 * Design pattern:<br>
 * Name: Chain of Responsibility.<br>
 * Role: Handler.<br>
 * Partners: {@link SelectionTool} as Handler, {@link DragTracker} as Handler,
 * {@link HandleTracker} as Handler.
 * <p>
 * Design pattern:<br>
 * Name: State.<br>
 * Role: State.<br>
 * Partners: {@link SelectionTool} as Context, {@link DragTracker} as
 * State, {@link HandleTracker} as State.
 *
 * @see SelectionTool
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultSelectAreaTracker extends AbstractTool implements SelectAreaTracker {

    private static final long serialVersionUID = 1L;
    /**
     * The bounds of the rubberband.
     */
    private Rectangle rubberband = new Rectangle();
    /**
     * Rubberband color. When this is null, the tracker does not
     * draw the rubberband.
     */
    private Color rubberbandColor = Color.BLACK;
    /**
     * Rubberband stroke.
     */
    private Stroke rubberbandStroke = new BasicStroke();
    /**
     * The hover handles, are the handles of the figure over which the
     * mouse pointer is currently hovering.
     */
    private LinkedList<Handle> hoverHandles = new LinkedList<>();
    /**
     * The hover Figure is the figure, over which the mouse is currently
     * hovering.
     */
    private Figure hoverFigure = null;

    /**
     * Creates a new instance.
     */
    public DefaultSelectAreaTracker() {
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        clearRubberBand();
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        selectGroup(evt.isAltDown());
        clearRubberBand();
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
        Rectangle invalidatedArea = (Rectangle) rubberband.clone();
        rubberband.setBounds(
                Math.min(anchor.x, evt.getX()),
                Math.min(anchor.y, evt.getY()),
                Math.abs(anchor.x - evt.getX()),
                Math.abs(anchor.y - evt.getY()));
        if (invalidatedArea.isEmpty()) {
            invalidatedArea = (Rectangle) rubberband.clone();
        } else {
            invalidatedArea = invalidatedArea.union(rubberband);
        }
        fireAreaInvalidated(invalidatedArea);
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        clearRubberBand();
        Point point = evt.getPoint();
        DrawingView view = editor.findView((Container) evt.getSource());
        updateCursor(view, point);
        if (view == null || editor.getActiveView() != view) {
            clearHoverHandles();
        } else {
            // Search first, if one of the selected figures contains
            // the current mouse location, and is selectable.
            // Only then search for other
            // figures. This search sequence is consistent with the
            // search sequence of the SelectionTool.
            Figure figure = null;
            Point2D.Double p = view.viewToDrawing(point);
            for (Figure f : view.getSelectedFigures()) {
                if (f.contains(p)) {
                    figure = f;
                }
            }
            if (figure == null) {
                figure = view.findFigure(point);
                while (figure != null && !figure.isSelectable()) {
                    figure = view.getDrawing().findFigureBehind(p, figure);
                }
            }
            updateHoverHandles(view, figure);
        }
    }

    @Override
    public void mouseExited(MouseEvent evt) {
        DrawingView view = editor.findView((Container) evt.getSource());
        updateHoverHandles(view, null);
    }

    private void clearRubberBand() {
        if (!rubberband.isEmpty()) {
            fireAreaInvalidated(rubberband);
            rubberband.width = -1;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setStroke(rubberbandStroke);
        g.setColor(rubberbandColor);
        g.drawRect(rubberband.x, rubberband.y, rubberband.width - 1, rubberband.height - 1);
        if (hoverHandles.size() > 0 && !getView().isFigureSelected(hoverFigure)) {
            for (Handle h : hoverHandles) {
                h.draw(g);
            }
        }
    }

    /**
     * Adds the figures enclosed by the rubberband to the current selection.
     * <p>
     * By default, only figures that are fully contained within the
     * rubberband are selected ({@link DrawingView#findFiguresWithin}).
     * If {@code intersectMode} is {@code true} (the user held down the Alt
     * key while releasing the mouse button), figures that merely overlap
     * the rubberband are selected as well ({@link DrawingView#findFigures}).
     *
     * @param intersectMode whether figures intersecting (rather than fully
     * enclosed by) the rubberband should also be selected.
     */
    // Package-private (was private) so DefaultSelectAreaTrackerTest can
    // exercise this method directly without going through the full
    // Swing mouse-event dispatch chain.
    void selectGroup(boolean intersectMode) {
        Collection<Figure> figures = intersectMode
                ? getView().findFigures(rubberband)
                : getView().findFiguresWithin(rubberband);
        for (Figure f : figures) {
            if (f.isSelectable()) {
                getView().addToSelection(f);
            }
        }
    }

    protected void clearHoverHandles() {
        updateHoverHandles(null, null);
    }

    protected void updateHoverHandles(DrawingView view, Figure f) {
        if (f != hoverFigure) {
            Rectangle r = null;
            if (hoverFigure != null) {
                for (Handle h : hoverHandles) {
                    if (r == null) {
                        r = h.getDrawingArea();
                    } else {
                        r.add(h.getDrawingArea());
                    }
                    h.setView(null);
                    h.dispose();
                }
                hoverHandles.clear();
            }
            hoverFigure = f;
            if (hoverFigure != null && f.isSelectable()) {
                hoverHandles.addAll(hoverFigure.createHandles(-1));
                for (Handle h : hoverHandles) {
                    h.setView(view);
                    if (r == null) {
                        r = h.getDrawingArea();
                    } else {
                        r.add(h.getDrawingArea());
                    }
                }
            }
            if (r != null) {
                r.grow(1, 1);
                fireAreaInvalidated(r);
            }
        }
    }

    @Override
    public void activate(DrawingEditor editor) {
        super.activate(editor);
        clearHoverHandles();
    }

    @Override
    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
        clearHoverHandles();
    }
}
