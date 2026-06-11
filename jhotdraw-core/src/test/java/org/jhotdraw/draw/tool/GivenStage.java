/*
 * @(#)GivenStage.java
 *
 * JGiven "Given" stage for the BDD scenarios covering the intersect-based
 * selection mode feature (Lab 8 - BDD Lab).
 *
 * Sets up a DefaultSelectAreaTracker with a mocked DrawingView and two
 * mocked Figures: one fully enclosed by the rubberband (returned by
 * findFiguresWithin), and one that merely overlaps the rubberband (returned
 * only by findFigures, alongside the enclosed figure).
 */
package org.jhotdraw.draw.tool;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;

import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class GivenStage extends Stage<GivenStage> {

    @ProvidedScenarioState
    DefaultSelectAreaTracker tracker;

    @ProvidedScenarioState
    DrawingView view;

    @ProvidedScenarioState(resolution = ScenarioState.Resolution.NAME)
    Figure enclosedFigure;

    @ProvidedScenarioState(resolution = ScenarioState.Resolution.NAME)
    Figure overlappingFigure;

    public GivenStage a_drawing_with_a_fully_enclosed_figure_and_an_overlapping_figure() {
        tracker = new DefaultSelectAreaTracker();

        DrawingEditor editor = mock(DrawingEditor.class);
        view = mock(DrawingView.class);
        Mockito.when(editor.getActiveView()).thenReturn(view);
        tracker.editor = editor;

        enclosedFigure = mock(Figure.class, "enclosedFigure");
        overlappingFigure = mock(Figure.class, "overlappingFigure");
        Mockito.when(enclosedFigure.isSelectable()).thenReturn(true);
        Mockito.when(overlappingFigure.isSelectable()).thenReturn(true);

        // Containment query: only the fully enclosed figure is found.
        Mockito.when(view.findFiguresWithin(any(Rectangle.class)))
                .thenReturn(Collections.singletonList(enclosedFigure));

        // Intersection query: both the enclosed figure and the merely
        // overlapping figure are found.
        Mockito.when(view.findFigures(any(Rectangle.class)))
                .thenReturn(Arrays.asList(enclosedFigure, overlappingFigure));

        return self();
    }
}
