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
package edu.pitt.dbmi.lib.math.classification.roc;

/**
 * Interface for Receiver Operating Characteristics (ROC) classes.
 *
 * Mar 27, 2012 8:48:05 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public interface ROC {

    /**
     * When it's actually true, how often does it predict true?<br/>
     * Also known as sensitivity, hit rate, recall.<br />
     * The x-values on the AUROC plot.
     *
     * @return true positive rate
     */
    public double[] getTruePositiveRates();

    /**
     * When it's actually false, how often does it predict true?<br>
     * The x-values on the AUROC plot.
     *
     * @return false positive rate
     */
    public double[] getFalsePositiveRates();

    /**
     * Predicted values for positive cases.
     *
     * @return predicted values
     */
    public double[] getPositivePredictedValues();

    /**
     * Predicted values for negative cases.
     *
     * @return predicted values
     */
    public double[] getNegativePredictedValues();

    /**
     * Get the confusion matrices calculated for each threshold.
     *
     * @return confusion matrices
     */
    public ConfusionMatrix[] getConfusionMatrices();

    /**
     * Get number of positive cases.
     *
     * @return number of positive cases
     */
    public int getNumberOfPositives();

    /**
     * Get number of negative cases.
     *
     * @return number of negative cases
     */
    public int getNumberOfNegatives();

    /**
     * Get the area under the ROC curve.
     *
     * @return area under the ROC curve
     */
    public double getAreaUnderRocCurve();

}
