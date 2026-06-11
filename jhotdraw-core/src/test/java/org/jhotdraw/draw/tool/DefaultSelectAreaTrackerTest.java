/*
 * @(#)DefaultSelectAreaTrackerTest.java
 *
 * JUnit 4 tests for the intersect-based selection mode feature
 * (Lab 7 - Testing Lab).
 *
 * These tests exercise DefaultSelectAreaTracker#selectGroup(boolean) in
 * isolation, using a Mockito mock of DrawingView so that no real Swing
 * components, geometry, or rendering are required (per the lab's note
 * on using mocks/stubs to remove dependencies that would otherwise be
 * exercised by a "unit" test).
 */
package org.jhotdraw.draw.tool;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.junit.Before;
import org.junit.Test;

import java.awt.Rectangle;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies the selection-mode behavior added in the Actualization Lab:
 * {@link DefaultSelectAreaTracker#selectGroup(boolean)} must select figures
 * found by {@link DrawingView#findFiguresWithin(Rectangle)} (containment
 * mode, {@code intersectMode == false}) or by
 * {@link DrawingView#findFigures(Rectangle)} (intersect mode,
 * {@code intersectMode == true}), and must only add figures for which
 * {@link Figure#isSelectable()} returns {@code true}.
 */
public class DefaultSelectAreaTrackerTest {

    private DefaultSelectAreaTracker tracker;
    private DrawingView view;

    @Before
    public void setUp() {
        tracker = new DefaultSelectAreaTracker();

        // Stub out the editor/view so getView() (in AbstractTool) returns
        // our mock without requiring a real Swing DrawingEditor.
        DrawingEditor editor = mock(DrawingEditor.class);
        view = mock(DrawingView.class);
        when(editor.getActiveView()).thenReturn(view);
        tracker.editor = editor;
    }

    /**
     * Best-case scenario: default (containment) mode selects exactly the
     * selectable figures returned by findFiguresWithin, and never queries
     * findFigures.
     */
    @Test
    public void containmentMode_selectsSelectableFiguresFoundWithin() {
        Figure figure = mock(Figure.class);
        when(figure.isSelectable()).thenReturn(true);
        when(view.findFiguresWithin(any(Rectangle.class)))
                .thenReturn(Collections.singletonList(figure));

        tracker.selectGroup(false);

        verify(view).findFiguresWithin(any(Rectangle.class));
        verify(view, never()).findFigures(any(Rectangle.class));
        verify(view).addToSelection(figure);
    }

    /**
     * Best-case scenario: intersect mode (Alt+drag-release) selects exactly
     * the selectable figures returned by findFigures, and never queries
     * findFiguresWithin.
     */
    @Test
    public void intersectMode_selectsSelectableFiguresFoundIntersecting() {
        Figure figure = mock(Figure.class);
        when(figure.isSelectable()).thenReturn(true);
        when(view.findFigures(any(Rectangle.class)))
                .thenReturn(Collections.singletonList(figure));

        tracker.selectGroup(true);

        verify(view).findFigures(any(Rectangle.class));
        verify(view, never()).findFiguresWithin(any(Rectangle.class));
        verify(view).addToSelection(figure);
    }

    /**
     * Boundary case: a figure returned by the spatial query but for which
     * isSelectable() is false must not be added to the selection, in either
     * mode.
     */
    @Test
    public void nonSelectableFigure_isNotAddedToSelection() {
        Figure figure = mock(Figure.class);
        when(figure.isSelectable()).thenReturn(false);
        when(view.findFiguresWithin(any(Rectangle.class)))
                .thenReturn(Collections.singletonList(figure));

        tracker.selectGroup(false);

        verify(view, never()).addToSelection(any(Figure.class));
    }

    /**
     * Boundary case: an empty rubberband / no figures found must not throw
     * and must not call addToSelection at all, in either mode.
     */
    @Test
    public void emptyResult_doesNotSelectAnythingOrThrow() {
        when(view.findFiguresWithin(any(Rectangle.class)))
                .thenReturn(Collections.emptyList());
        when(view.findFigures(any(Rectangle.class)))
                .thenReturn(Collections.emptyList());

        tracker.selectGroup(false);
        tracker.selectGroup(true);

        verify(view, never()).addToSelection(any(Figure.class));
    }
}
