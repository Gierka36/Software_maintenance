package org.jhotdraw.draw.figure;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EllipseFigureBDDTest
        extends ScenarioTest<EllipseFigureBDDTest.GivenEllipse,
                             EllipseFigureBDDTest.WhenUserDraws,
                             EllipseFigureBDDTest.ThenEllipse> {

    @Test
    public void user_can_create_an_ellipse_by_dragging_on_the_canvas() {
        given().an_empty_ellipse_figure();

        when().the_user_draws_from_to(10, 20, 60, 80);

        then().the_ellipse_bounds_are(10, 20, 50, 60);
    }

    @Test
    public void user_can_create_an_ellipse_by_dragging_backwards() {
        given().an_empty_ellipse_figure();

        when().the_user_draws_from_to(60, 80, 10, 20);

        then().the_ellipse_bounds_are(10, 20, 50, 60);
    }

    @Test
    public void clicking_without_dragging_creates_a_minimum_size_ellipse() {
        given().an_empty_ellipse_figure();

        when().the_user_draws_from_to(10, 20, 10, 20);

        then().the_ellipse_bounds_are(10, 20, 0.1, 0.1);
    }

    @Test
    public void user_can_resize_an_existing_ellipse() {
        given().an_ellipse_with_bounds(10, 20, 50, 60);

        when().the_user_resizes_the_ellipse_from_to(10, 20, 100, 120);

        then().the_ellipse_bounds_are(10, 20, 90, 100);
    }

    @Test
    public void user_can_create_several_independent_ellipses() {
        given().no_ellipses_exist();

        when().the_user_creates_ellipse_from_to(10, 20, 60, 80)
                .and().the_user_creates_ellipse_from_to(100, 120, 180, 200);

        then().there_are_ellipses(2)
                .and().ellipse_number_has_bounds(0, 10, 20, 50, 60)
                .and().ellipse_number_has_bounds(1, 100, 120, 80, 80);
    }

    public static class GivenEllipse extends Stage<GivenEllipse> {

        @ScenarioState
        EllipseFigure figure;

        @ScenarioState
        List<EllipseFigure> figures;

        public GivenEllipse an_empty_ellipse_figure() {
            figure = new EllipseFigure();
            return self();
        }

        public GivenEllipse an_ellipse_with_bounds(double x, double y, double width, double height) {
            figure = new EllipseFigure(x, y, width, height);
            return self();
        }

        public GivenEllipse no_ellipses_exist() {
            figures = new ArrayList<>();
            return self();
        }
    }

    public static class WhenUserDraws extends Stage<WhenUserDraws> {

        @ScenarioState
        EllipseFigure figure;

        @ScenarioState
        List<EllipseFigure> figures;

        public WhenUserDraws the_user_draws_from_to(double x1, double y1, double x2, double y2) {
            figure.setBounds(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
            return self();
        }

        public WhenUserDraws the_user_resizes_the_ellipse_from_to(double x1, double y1, double x2, double y2) {
            figure.setBounds(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
            return self();
        }

        public WhenUserDraws the_user_creates_ellipse_from_to(double x1, double y1, double x2, double y2) {
            EllipseFigure newFigure = new EllipseFigure();
            newFigure.setBounds(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
            figures.add(newFigure);
            return self();
        }
    }

    public static class ThenEllipse extends Stage<ThenEllipse> {

        @ScenarioState
        EllipseFigure figure;

        @ScenarioState
        List<EllipseFigure> figures;

        public ThenEllipse the_ellipse_bounds_are(double x, double y, double width, double height) {
            Rectangle2D.Double bounds = figure.getBounds();

            assertThat(bounds.x).isEqualTo(x);
            assertThat(bounds.y).isEqualTo(y);
            assertThat(bounds.width).isEqualTo(width);
            assertThat(bounds.height).isEqualTo(height);

            return self();
        }

        public ThenEllipse there_are_ellipses(int expectedCount) {
            assertThat(figures).hasSize(expectedCount);
            return self();
        }

        public ThenEllipse ellipse_number_has_bounds(int index, double x, double y, double width, double height) {
            Rectangle2D.Double bounds = figures.get(index).getBounds();

            assertThat(bounds.x).isEqualTo(x);
            assertThat(bounds.y).isEqualTo(y);
            assertThat(bounds.width).isEqualTo(width);
            assertThat(bounds.height).isEqualTo(height);

            return self();
        }
    }
}