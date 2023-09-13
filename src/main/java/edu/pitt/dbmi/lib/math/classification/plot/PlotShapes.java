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

import edu.pitt.dbmi.lib.math.classification.plot.PlotShapeFactory;
import java.awt.Shape;

/**
 *
 * Jun 6, 2012 4:15:41 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class PlotShapes {

    public static final Shape SQUARE_SHAPE = PlotShapeFactory.STANDARD_SHAPES.get(PlotShapeFactory.SQUARE_SHAPE);
    public static final Shape CIRCLE_SHAPE = PlotShapeFactory.STANDARD_SHAPES.get(PlotShapeFactory.CIRCLE_SHAPE);
    public static final Shape HORIZONTAL_RECTANGLE_SHAPE = PlotShapeFactory.STANDARD_SHAPES.get(PlotShapeFactory.HORIZONTAL_RECTANGLE_SHAPE);
    public static final Shape HORIZONTAL_ELLIPSE_SHAPE = PlotShapeFactory.STANDARD_SHAPES.get(PlotShapeFactory.HORIZONTAL_ELLIPSE_SHAPE);
    public static final Shape VERTICAL_RECTANGLE_SHAPE = PlotShapeFactory.STANDARD_SHAPES.get(PlotShapeFactory.VERTICAL_RECTANGLE_SHAPE);
    public static final Shape UP_POINTING_TRIANGLE_SHAPE = PlotShapeFactory.STANDARD_SHAPES.get(PlotShapeFactory.UP_POINTING_TRIANGLE_SHAPE);
    public static final Shape DIAMOND_SHAPE = PlotShapeFactory.STANDARD_SHAPES.get(PlotShapeFactory.DIAMOND_SHAPE);
    public static final Shape DOWN_POINTING_TRIANGLE_SHAPE = PlotShapeFactory.STANDARD_SHAPES.get(PlotShapeFactory.DOWN_POINTING_TRIANGLE_SHAPE);
    public static final Shape RIGHT_POINTING_TRIANGLE_SHAPE = PlotShapeFactory.STANDARD_SHAPES.get(PlotShapeFactory.RIGHT_POINTING_TRIANGLE_SHAPE);
    public static final Shape LEFT_POINTING_TRIANGLE_SHAPE = PlotShapeFactory.STANDARD_SHAPES.get(PlotShapeFactory.LEFT_POINTING_TRIANGLE_SHAPE);

    private PlotShapes() {
    }

}
