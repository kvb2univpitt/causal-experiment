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
 * Confidence interval for AUROC curve using DeLong's method.
 *
 * Mar 28, 2012 10:51:42 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class DeLongConfidenceInterval extends AbstractConfidenceInterval {

    public DeLongConfidenceInterval(ROC roc) {
        super(roc);
    }

    @Override
    protected double computeStandardError(ROC roc) {
        double stdError = 0;

        double[] abNormData = roc.getPositivePredictedValues();
        double[] normData = roc.getNegativePredictedValues();

        double auc = roc.getAreaUnderRocCurve();
        stdError = Math.sqrt((sNorm(normData, abNormData, auc) / normData.length) + (sAbnorm(normData, abNormData, auc) / abNormData.length));

        return stdError;
    }

    private static double indicator(double Xi, double Yi) {
        if (Yi < Xi) {
            return 1.0;
        } else if (Yi == Xi) {
            return 0.5;
        } else {
            return 0.0;
        }
    }

    private static double vNorm(double Yj, double[] abNormData) {
        double result = 0;

        for (double Xi : abNormData) {
            result += indicator(Xi, Yj);
        }

        return result / abNormData.length;
    }

    private static double vAbnorm(double Xi, double[] normData) {
        double result = 0;

        for (double Yj : normData) {
            result += indicator(Xi, Yj);
        }

        return result / normData.length;
    }

    private static double sNorm(double[] normData, double[] abNormData, double auc) {
        double result = 0;

        for (double Yj : normData) {
            result += Math.pow(vNorm(Yj, abNormData) - auc, 2);
        }

        return result / (normData.length - 1);
    }

    private static double sAbnorm(double[] normData, double[] abNormData, double auc) {
        double result = 0;

        for (double Xi : abNormData) {
            result += Math.pow(vAbnorm(Xi, normData) - auc, 2);
        }

        return result / (abNormData.length - 1);
    }

}
