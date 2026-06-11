/*
 * @(#)SelectionModeBddTest.java
 *
 * JGiven BDD scenarios for the intersect-based selection mode feature
 * (Lab 8 - BDD Lab).
 *
 * Maps the Lab 2 user story:
 *   "As a software architect, I want my drag-selection rectangle to
 *    optionally select figures that it merely overlaps (not only figures
 *    fully enclosed within it), so that I can select figures more flexibly
 *    without needing to drag a rectangle that completely contains them."
 *
 * ...into two Given-When-Then scenarios covering the default (containment)
 * mode and the Alt-held (intersect) mode of
 * DefaultSelectAreaTracker#selectGroup(boolean).
 */
package org.jhotdraw.draw.tool;

import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;

public class SelectionModeBddTest extends ScenarioTest<GivenStage, WhenStage, ThenStage> {

    @Test
    public void releasing_the_mouse_without_alt_selects_only_fully_enclosed_figures() {
        given().a_drawing_with_a_fully_enclosed_figure_and_an_overlapping_figure();

        when().the_user_releases_the_mouse_button_without_holding_alt();

        then().only_the_enclosed_figure_is_selected()
                .and().exactly_one_figure_is_selected_in_total();
    }

    @Test
    public void releasing_the_mouse_with_alt_held_also_selects_overlapping_figures() {
        given().a_drawing_with_a_fully_enclosed_figure_and_an_overlapping_figure();

        when().the_user_releases_the_mouse_button_while_holding_alt();

        then().both_figures_are_selected()
                .and().exactly_two_figures_are_selected_in_total();
    }
}
