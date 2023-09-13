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
 * Confusion Matrix
 *
 * Mar 27, 2012 8:19:09 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class ConfusionMatrix {

    /**
     * Correctly predicted something to be true (has disease).
     */
    private final int truePositives;

    /**
     * Correctly predicted something to be false (has no disease).
     */
    private final int trueNegatives;

    /**
     * Incorrectly predicted something to be true (has disease) when it is not
     * (has no disease).
     */
    private final int falsePositives;

    /**
     * Incorrectly predicted something to be false (has no disease) when it is
     * true (has disease).<br />
     * Also known as miss, Type II error
     */
    private final int falseNegatives;

    /**
     * The threshold at which FP, FN,TP, and TN were calculated.
     */
    private final double threshold;

    /**
     *
     * @param truePositives true positives (TP)
     * @param trueNegatives true negatives (TN)
     * @param falsePositives false positives (FP)
     * @param falseNegatives false negatives (FN)
     * @param threshold the threshold (at or above) at which FP, FN,TP, and TN
     * were calculated.
     */
    public ConfusionMatrix(int truePositives, int trueNegatives, int falsePositives, int falseNegatives, double threshold) {
        this.truePositives = truePositives;
        this.trueNegatives = trueNegatives;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
        this.threshold = threshold;
    }

    /**
     * Overall, how often is the classifier correct?
     *
     * @return accuracy
     */
    public double getAccuracy() {
        int numOfPositives = truePositives + falseNegatives;
        int numOfNegatives = trueNegatives + falsePositives;
        int total = numOfPositives + numOfNegatives;

        return (total == 0) ? 0 : ((double) (truePositives + trueNegatives)) / total;
    }

    /**
     * A weighted average of the true positive rate (recall) and precision.<br>
     *
     * @return F1 score
     */
    public double getF1score() {
        int numOfPositives = truePositives + falseNegatives;
        int numOfPositivesPredicted = truePositives + falsePositives;
        int denominator = numOfPositives + numOfPositivesPredicted;

        return (denominator == 0) ? 0 : ((double) (2 * truePositives)) / denominator;
    }

    /**
     * Get false discovery rate (FDR).<br>
     * FDR = FP / (FP + TP).
     *
     * @return false discovery rate
     */
    public double getFalseDiscoveryRate() {
        int numOfPositivesPredicted = truePositives + falsePositives;

        return (numOfPositivesPredicted == 0) ? 0 : ((double) falsePositives) / numOfPositivesPredicted;
    }

    /**
     * When it's actually true, how often does it predict true?<br/>
     * Also known as sensitivity, hit rate, recall.<br />
     * The x-values on the AUROC plot.
     *
     * @return true positive rate
     */
    public double getTruePositiveRate() {
        int numOfPositives = truePositives + falseNegatives;

        return (numOfPositives == 0) ? 0 : ((double) truePositives) / numOfPositives;
    }

    /**
     * When it's actually no, how often does it predict no?<br>
     * Also known as specificity (SPC).
     *
     * @return true negative rate
     */
    public double getTrueNegativeRate() {
        int numOfNegatives = trueNegatives + falsePositives;

        return (numOfNegatives == 0) ? 0 : ((double) trueNegatives) / numOfNegatives;
    }

    /**
     * When it's actually false, how often does it predict true?<br>
     * The x-values on the AUROC plot.
     *
     * @return false positive rate
     */
    public double getFalsePositiveRate() {
        int numOfNegatives = trueNegatives + falsePositives;

        return (numOfNegatives == 0) ? 0 : ((double) falsePositives) / numOfNegatives;
    }

    /**
     * Get negative predictive value (NPV).<br>
     * NPV = TN / (TN + FN).
     *
     * @return negative predictive value
     */
    public double getNegativePredictiveValue() {
        int numOfNegativesPredicted = trueNegatives + falseNegatives;

        return (numOfNegativesPredicted == 0) ? 0 : ((double) trueNegatives) / numOfNegativesPredicted;
    }

    /**
     * When it predicts true, how often is it correct?<br>
     * Also known as precision.
     *
     * @return positive predictive value
     */
    public double getPositivePredictiveValue() {
        int numOfPositivesPredicted = truePositives + falsePositives;

        return (numOfPositivesPredicted == 0) ? 0 : ((double) truePositives) / numOfPositivesPredicted;
    }

    /**
     * Overall, how often is it wrong?
     *
     * @return
     */
    public double getMisclassificationRate() {
        int numOfPositives = truePositives + falseNegatives;
        int numOfNegatives = trueNegatives + falsePositives;
        int total = numOfPositives + numOfNegatives;

        return (total == 0) ? 0 : ((double) (falsePositives + falseNegatives)) / total;
    }

    /**
     * How often does the true condition actually occur?
     *
     * @return
     */
    public double getPrevalence() {
        int numOfPositives = truePositives + falseNegatives;
        int numOfNegatives = trueNegatives + falsePositives;
        int total = numOfPositives + numOfNegatives;

        return (total == 0) ? 0 : ((double) numOfPositives) / total;
    }

    /**
     * Items correctly labeled as belonging to the positive class.<br>
     * Also known as hit.
     *
     * @return number of true positives
     */
    public int getTruePositives() {
        return truePositives;
    }

    /**
     * Items correctly labeled as belonging to the negative class.<br>
     * Also known as correct rejection.
     *
     * @return number of true negatives
     */
    public int getTrueNegatives() {
        return trueNegatives;
    }

    /**
     * Items incorrectly labeled as belonging to the positive class.<br>
     * Also known as false alarm,< br/>
     * Type I error.
     *
     * @return number of false positives
     */
    public int getFalsePositives() {
        return falsePositives;
    }

    /**
     * Items which was not labeled as belonging to the positive class but should
     * had been.<br>
     * Also known as miss<br />
     * Type II error.
     *
     * @return number of false negatives
     */
    public int getFalseNegatives() {
        return falseNegatives;
    }

    /**
     * The threshold at which FP, FN,TP, and TN were calculated.
     *
     * @return
     */
    public double getThreshold() {
        return threshold;
    }

}
