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
package edu.pitt.dbmi.lib.math.classification.roc.stat;

import edu.pitt.dbmi.lib.math.classification.roc.ROC;

/**
 *
 * Mar 28, 2012 10:58:16 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class HanleyConfidenceInterval extends AbstractConfidenceInterval {

    public HanleyConfidenceInterval(ROC roc) {
        super(roc);
    }

    @Override
    protected double computeStandardError(ROC roc) {
        int numOfPositive = roc.getNumberOfPositives();
        int numOfNegative = roc.getNumberOfNegatives();
        double areaUnderCurve = roc.getAreaUnderRocCurve();

        double aucSq = areaUnderCurve * areaUnderCurve;
        double Q1 = areaUnderCurve / (2 - areaUnderCurve);
        double Q2 = (2 * aucSq) / (1 + areaUnderCurve);

        return Math.sqrt((areaUnderCurve * (1 - areaUnderCurve) + (numOfPositive - 1) * (Q1 - aucSq) + (numOfNegative - 1) * (Q2 - aucSq)) / (numOfPositive * numOfNegative));
    }

}
