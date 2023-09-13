/*
 * Copyright (C) 2023 University of Pittsburgh.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package edu.pitt.dbmi.lib.math.classification.plot;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * Feb 24, 2011 3:14:19 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class PlotShapeFactory {

    public static final String SQUARE_SHAPE = "square";
    public static final String CIRCLE_SHAPE = "circle";
    public static final String HORIZONTAL_RECTANGLE_SHAPE = "horizontal rectangle";
    public static final String HORIZONTAL_ELLIPSE_SHAPE = "horizontal ellipse";
    public static final String VERTICAL_RECTANGLE_SHAPE = "vertical rectangle";
    public static final String UP_POINTING_TRIANGLE_SHAPE = "up-pointing triangle";
    public static final String DIAMOND_SHAPE = "diamond";
    public static final String DOWN_POINTING_TRIANGLE_SHAPE = "down-pointing triangle";
    public static final String RIGHT_POINTING_TRIANGLE_SHAPE = "right-pointing triangle";
    public static final String LEFT_POINTING_TRIANGLE_SHAPE = "left-pointing triangle";

    public static final Map<String, Shape> STANDARD_SHAPES = createShapes(12.0);

    private PlotShapeFactory() {
    }

    public static Map<String, Shape> createShapes(double size) {
        Map<String, Shape> shapeMap = new TreeMap<String, Shape>();

        double delta = size / 2.0;

        shapeMap.put(CIRCLE_SHAPE, new Ellipse2D.Double(-delta, -delta, size, size));
        shapeMap.put(VERTICAL_RECTANGLE_SHAPE, new Rectangle2D.Double(-delta / 2, -delta, size / 2, size));
        shapeMap.put(SQUARE_SHAPE, new Rectangle2D.Double(-delta, -delta, size, size));
        shapeMap.put(HORIZONTAL_RECTANGLE_SHAPE, new Rectangle2D.Double(-delta, -delta / 2, size, size / 2));
        shapeMap.put(HORIZONTAL_ELLIPSE_SHAPE, new Ellipse2D.Double(-delta, -delta / 2, size, size / 2));

        int[] xpoints, ypoints;

        xpoints = intArray(0.0, delta, -delta);
        ypoints = intArray(-delta, delta, delta);
        shapeMap.put(UP_POINTING_TRIANGLE_SHAPE, new Polygon(xpoints, ypoints, 3));

        xpoints = intArray(0.0, delta, 0.0, -delta);
        ypoints = intArray(-delta, 0.0, delta, 0.0);
        shapeMap.put(DIAMOND_SHAPE, new Polygon(xpoints, ypoints, 4));

        xpoints = intArray(-delta, +delta, 0.0);
        ypoints = intArray(-delta, -delta, delta);
        shapeMap.put(DOWN_POINTING_TRIANGLE_SHAPE, new Polygon(xpoints, ypoints, 3));

        xpoints = intArray(-delta, delta, -delta);
        ypoints = intArray(-delta, 0.0, delta);
        shapeMap.put(RIGHT_POINTING_TRIANGLE_SHAPE, new Polygon(xpoints, ypoints, 3));

        xpoints = intArray(-delta, delta, delta);
        ypoints = intArray(0.0, -delta, +delta);
        shapeMap.put(LEFT_POINTING_TRIANGLE_SHAPE, new Polygon(xpoints, ypoints, 3));

        return shapeMap;
    }

    public static Shape createCircle(double size) {
        double delta = size / 2.0;

        return new Rectangle2D.Double(-delta, -delta, size, size);
    }

    private static int[] intArray(double a, double b, double c) {
        return new int[]{(int) a, (int) b, (int) c};
    }

    private static int[] intArray(double a, double b, double c, double d) {
        return new int[]{(int) a, (int) b, (int) c, (int) d};
    }

}
