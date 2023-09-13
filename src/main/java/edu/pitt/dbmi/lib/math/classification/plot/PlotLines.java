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

import java.awt.BasicStroke;

/**
 *
 * May 17, 2023 11:48:57 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class PlotLines {

    public static final BasicStroke DOTTED_LINE = createDottedLine(3.0f);
    public static final BasicStroke SOLID_LINE = createSolidLine(3.0f);

    private PlotLines() {
    }

    public static BasicStroke createSolidLine(float width) {
        return new BasicStroke(width);
    }

    public static BasicStroke createDottedLine(float width) {
        return new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[]{6.0f, 6.0f}, 0.0f);
    }

}
