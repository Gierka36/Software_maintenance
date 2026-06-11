/*
 * @(#)WhenStage.java
 *
 * JGiven "When" stage for the BDD scenarios covering the intersect-based
 * selection mode feature (Lab 8 - BDD Lab).
 *
 * Drives DefaultSelectAreaTracker#selectGroup(boolean), which is the same
 * package-private entry point exercised by the Lab 7 unit tests, mirroring
 * what mouseReleased(MouseEvent) does based on whether Alt is held.
 */
package org.jhotdraw.draw.tool;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

public class WhenStage extends Stage<WhenStage> {

    @ExpectedScenarioState
    DefaultSelectAreaTracker tracker;

    public WhenStage the_user_releases_the_mouse_button_without_holding_alt() {
        tracker.selectGroup(false);
        return self();
    }

    public WhenStage the_user_releases_the_mouse_button_while_holding_alt() {
        tracker.selectGroup(true);
        return self();
    }
}
