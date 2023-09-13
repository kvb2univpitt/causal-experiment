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
 * Mar 28, 2012 10:34:05 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public abstract class AbstractConfidenceInterval implements ConfidenceInterval {

    /**
     * Critical value used to compute 95% CI.
     */
    private static final double CRITICAL_VALUE = 1.96;

    /**
     * Standard error.
     */
    protected double standardError;

    /**
     * Lower confidence interval.
     */
    protected double lowerConfidenceInterval;

    /**
     * Upper confidence interval.
     */
    protected double upperConfidenceInterval;

    public AbstractConfidenceInterval(ROC roc) {
        if (roc == null) {
            throw new IllegalArgumentException("Receiver Operating Characteristics (ROC) required.");
        }

        double stdError = computeStandardError(roc);

        double marginOfError = CRITICAL_VALUE * stdError;
        double areaUnderCurve = roc.getAreaUnderRocCurve();

        lowerConfidenceInterval = areaUnderCurve - marginOfError;
        upperConfidenceInterval = areaUnderCurve + marginOfError;
        standardError = stdError;
    }

    /**
     * Compute the standard error using whatever method the subclass decided to
     * use.
     *
     * @return standard error
     */
    protected abstract double computeStandardError(ROC roc);

    @Override
    public String toString() {
        return String.format(
                "CI: [%f, %f]",
                lowerConfidenceInterval, upperConfidenceInterval);
    }

    @Override
    public double getStandardError() {
        return standardError;
    }

    @Override
    public double getLowerConfidenceInterval() {
        return lowerConfidenceInterval;
    }

    @Override
    public double getUpperConfidenceInterval() {
        return upperConfidenceInterval;
    }

}
