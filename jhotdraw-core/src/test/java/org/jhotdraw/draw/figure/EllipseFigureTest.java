package org.jhotdraw.draw.figure;

import org.junit.Test;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static org.junit.Assert.*;

public class EllipseFigureTest {

    private static final double DELTA = 0.0001;

    @Test
    public void defaultConstructor_createsZeroSizedEllipseAtOrigin() {
        EllipseFigure figure = new EllipseFigure();

        Rectangle2D.Double bounds = figure.getBounds();

        assertEquals(0.0, bounds.x, DELTA);
        assertEquals(0.0, bounds.y, DELTA);
        assertEquals(0.0, bounds.width, DELTA);
        assertEquals(0.0, bounds.height, DELTA);
    }

    @Test
    public void constructorWithCoordinates_setsInitialBounds() {
        EllipseFigure figure = new EllipseFigure(10, 20, 30, 40);

        Rectangle2D.Double bounds = figure.getBounds();

        assertEquals(10.0, bounds.x, DELTA);
        assertEquals(20.0, bounds.y, DELTA);
        assertEquals(30.0, bounds.width, DELTA);
        assertEquals(40.0, bounds.height, DELTA);
    }

    @Test
    public void getBounds_returnsCurrentEllipseBounds() {
        EllipseFigure figure = new EllipseFigure(5, 6, 7, 8);

        Rectangle2D.Double bounds = figure.getBounds();

        assertEquals(5.0, bounds.x, DELTA);
        assertEquals(6.0, bounds.y, DELTA);
        assertEquals(7.0, bounds.width, DELTA);
        assertEquals(8.0, bounds.height, DELTA);
    }

    @Test
    public void contains_pointInsideEllipse_returnsTrue() {
        EllipseFigure figure = new EllipseFigure(0, 0, 100, 50);

        assertTrue(figure.contains(new Point2D.Double(50, 25)));
    }

    @Test
    public void contains_pointOutsideEllipse_returnsFalse() {
        EllipseFigure figure = new EllipseFigure(0, 0, 100, 50);

        assertFalse(figure.contains(new Point2D.Double(150, 150)));
    }

    @Test
    public void setBounds_withNormalCoordinates_setsExpectedBounds() {
        EllipseFigure figure = new EllipseFigure();

        figure.setBounds(
                new Point2D.Double(10, 20),
                new Point2D.Double(60, 80));

        Rectangle2D.Double bounds = figure.getBounds();

        assertEquals(10.0, bounds.x, DELTA);
        assertEquals(20.0, bounds.y, DELTA);
        assertEquals(50.0, bounds.width, DELTA);
        assertEquals(60.0, bounds.height, DELTA);
    }

    @Test
    public void setBounds_withReversedCoordinates_setsPositiveBounds() {
        EllipseFigure figure = new EllipseFigure();

        figure.setBounds(
                new Point2D.Double(60, 80),
                new Point2D.Double(10, 20));

        Rectangle2D.Double bounds = figure.getBounds();

        assertEquals(10.0, bounds.x, DELTA);
        assertEquals(20.0, bounds.y, DELTA);
        assertEquals(50.0, bounds.width, DELTA);
        assertEquals(60.0, bounds.height, DELTA);
    }

    @Test
    public void setBounds_withZeroWidth_usesMinimumWidth() {
        EllipseFigure figure = new EllipseFigure();

        figure.setBounds(
                new Point2D.Double(10, 20),
                new Point2D.Double(10, 80));

        Rectangle2D.Double bounds = figure.getBounds();

        assertEquals(10.0, bounds.x, DELTA);
        assertEquals(20.0, bounds.y, DELTA);
        assertEquals(0.1, bounds.width, DELTA);
        assertEquals(60.0, bounds.height, DELTA);
    }

    @Test
    public void setBounds_withZeroHeight_usesMinimumHeight() {
        EllipseFigure figure = new EllipseFigure();

        figure.setBounds(
                new Point2D.Double(10, 20),
                new Point2D.Double(60, 20));

        Rectangle2D.Double bounds = figure.getBounds();

        assertEquals(10.0, bounds.x, DELTA);
        assertEquals(20.0, bounds.y, DELTA);
        assertEquals(50.0, bounds.width, DELTA);
        assertEquals(0.1, bounds.height, DELTA);
    }

    @Test
    public void setBounds_withSameAnchorAndLead_usesMinimumWidthAndHeight() {
        EllipseFigure figure = new EllipseFigure();

        figure.setBounds(
                new Point2D.Double(10, 20),
                new Point2D.Double(10, 20));

        Rectangle2D.Double bounds = figure.getBounds();

        assertEquals(10.0, bounds.x, DELTA);
        assertEquals(20.0, bounds.y, DELTA);
        assertEquals(0.1, bounds.width, DELTA);
        assertEquals(0.1, bounds.height, DELTA);
    }

    @Test
    public void transform_withTranslation_movesEllipse() {
        EllipseFigure figure = new EllipseFigure(10, 20, 30, 40);

        figure.transform(AffineTransform.getTranslateInstance(5, 10));

        Rectangle2D.Double bounds = figure.getBounds();

        assertEquals(15.0, bounds.x, DELTA);
        assertEquals(30.0, bounds.y, DELTA);
        assertEquals(30.0, bounds.width, DELTA);
        assertEquals(40.0, bounds.height, DELTA);
    }

    @Test
    public void transform_withScaling_scalesEllipse() {
        EllipseFigure figure = new EllipseFigure(10, 20, 30, 40);

        figure.transform(AffineTransform.getScaleInstance(2, 3));

        Rectangle2D.Double bounds = figure.getBounds();

        assertEquals(20.0, bounds.x, DELTA);
        assertEquals(60.0, bounds.y, DELTA);
        assertEquals(60.0, bounds.width, DELTA);
        assertEquals(120.0, bounds.height, DELTA);
    }

    @Test
    public void clone_createsDifferentObjectWithSameBounds() {
        EllipseFigure original = new EllipseFigure(10, 20, 30, 40);

        EllipseFigure copy = original.clone();

        assertNotSame(original, copy);
        assertEquals(original.getBounds(), copy.getBounds());
    }

    @Test
    public void clone_modifyingCloneDoesNotChangeOriginal() {
        EllipseFigure original = new EllipseFigure(10, 20, 30, 40);
        EllipseFigure copy = original.clone();

        copy.setBounds(
                new Point2D.Double(100, 100),
                new Point2D.Double(200, 200));

        Rectangle2D.Double originalBounds = original.getBounds();
        Rectangle2D.Double copyBounds = copy.getBounds();

        assertEquals(10.0, originalBounds.x, DELTA);
        assertEquals(20.0, originalBounds.y, DELTA);
        assertEquals(30.0, originalBounds.width, DELTA);
        assertEquals(40.0, originalBounds.height, DELTA);

        assertEquals(100.0, copyBounds.x, DELTA);
        assertEquals(100.0, copyBounds.y, DELTA);
        assertEquals(100.0, copyBounds.width, DELTA);
        assertEquals(100.0, copyBounds.height, DELTA);
    }

    @Test
    public void restoreTransformTo_restoresPreviousGeometry() {
        EllipseFigure figure = new EllipseFigure(10, 20, 30, 40);
        Object restoreData = figure.getTransformRestoreData();

        figure.setBounds(
                new Point2D.Double(100, 100),
                new Point2D.Double(200, 200));

        figure.restoreTransformTo(restoreData);

        Rectangle2D.Double bounds = figure.getBounds();

        assertEquals(10.0, bounds.x, DELTA);
        assertEquals(20.0, bounds.y, DELTA);
        assertEquals(30.0, bounds.width, DELTA);
        assertEquals(40.0, bounds.height, DELTA);
    }
    @Test
    public void invariant_setBoundsNeverCreatesNonPositiveWidth() {
        EllipseFigure figure = new EllipseFigure();

        figure.setBounds(
                new Point2D.Double(10, 10),
                new Point2D.Double(10, 50));

        Rectangle2D.Double bounds = figure.getBounds();

        assert bounds.width > 0 : "Ellipse width should always be positive";
        assertTrue(bounds.width > 0);
    }

    @Test
    public void invariant_setBoundsNeverCreatesNonPositiveHeight() {
        EllipseFigure figure = new EllipseFigure();

        figure.setBounds(
                new Point2D.Double(10, 10),
                new Point2D.Double(50, 10));

        Rectangle2D.Double bounds = figure.getBounds();

        assert bounds.height > 0 : "Ellipse height should always be positive";
        assertTrue(bounds.height > 0);
    }

    @Test
    public void invariant_setBoundsAlwaysUsesSmallestXCoordinateAsOrigin() {
        EllipseFigure figure = new EllipseFigure();

        figure.setBounds(
                new Point2D.Double(80, 20),
                new Point2D.Double(30, 60));

        Rectangle2D.Double bounds = figure.getBounds();

        assert bounds.x == 30.0 : "Ellipse x should be the smaller x coordinate";
        assertEquals(30.0, bounds.x, DELTA);
    }

    @Test
    public void invariant_setBoundsAlwaysUsesSmallestYCoordinateAsOrigin() {
        EllipseFigure figure = new EllipseFigure();

        figure.setBounds(
                new Point2D.Double(20, 90),
                new Point2D.Double(60, 40));

        Rectangle2D.Double bounds = figure.getBounds();

        assert bounds.y == 40.0 : "Ellipse y should be the smaller y coordinate";
        assertEquals(40.0, bounds.y, DELTA);
    }

    @Test
    public void invariant_cloneMustNotShareMutableGeometryWithOriginal() {
        EllipseFigure original = new EllipseFigure(10, 20, 30, 40);
        EllipseFigure clone = original.clone();

        clone.setBounds(
                new Point2D.Double(100, 100),
                new Point2D.Double(150, 150));

        Rectangle2D.Double originalBounds = original.getBounds();

        assert originalBounds.x == 10.0 : "Original x should not change when clone changes";
        assert originalBounds.y == 20.0 : "Original y should not change when clone changes";
        assert originalBounds.width == 30.0 : "Original width should not change when clone changes";
        assert originalBounds.height == 40.0 : "Original height should not change when clone changes";

        assertEquals(10.0, originalBounds.x, DELTA);
        assertEquals(20.0, originalBounds.y, DELTA);
        assertEquals(30.0, originalBounds.width, DELTA);
        assertEquals(40.0, originalBounds.height, DELTA);
    }
}