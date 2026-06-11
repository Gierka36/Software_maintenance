/*
 * @(#)ThenStage.java
 *
 * JGiven "Then" stage for the BDD scenarios covering the intersect-based
 * selection mode feature (Lab 8 - BDD Lab).
 *
 * Uses Mockito verify() to check which figures were added to the selection,
 * and AssertJ's assertThat() to make a domain-specific assertion about the
 * total set of figures that ended up selected.
 */
package org.jhotdraw.draw.tool;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ThenStage extends Stage<ThenStage> {

    @ExpectedScenarioState
    DrawingView view;

    @ExpectedScenarioState(resolution = ScenarioState.Resolution.NAME)
    Figure enclosedFigure;

    @ExpectedScenarioState(resolution = ScenarioState.Resolution.NAME)
    Figure overlappingFigure;

    public ThenStage only_the_enclosed_figure_is_selected() {
        verify(view).addToSelection(enclosedFigure);
        verify(view, never()).addToSelection(overlappingFigure);
        return self();
    }

    public ThenStage both_figures_are_selected() {
        verify(view).addToSelection(enclosedFigure);
        verify(view).addToSelection(overlappingFigure);
        return self();
    }

    public ThenStage exactly_one_figure_is_selected_in_total() {
        ArgumentCaptor<Figure> captor = ArgumentCaptor.forClass(Figure.class);
        verify(view, times(1)).addToSelection(captor.capture());
        assertThat(captor.getAllValues()).containsExactly(enclosedFigure);
        return self();
    }

    public ThenStage exactly_two_figures_are_selected_in_total() {
        ArgumentCaptor<Figure> captor = ArgumentCaptor.forClass(Figure.class);
        verify(view, times(2)).addToSelection(captor.capture());
        assertThat(captor.getAllValues())
                .containsExactlyInAnyOrder(enclosedFigure, overlappingFigure);
        return self();
    }
}
