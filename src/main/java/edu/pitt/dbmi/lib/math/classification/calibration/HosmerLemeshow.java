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
package edu.pitt.dbmi.lib.math.classification.calibration;

/**
 *
 * Mar 30, 2012 11:29:48 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public interface HosmerLemeshow {

    public static final double CRITICAL_VALUE = 1.96;

    public final int NUM_OF_INTERVAL = 10;  // break the data into 10 intervals

    public String getSummary();

    public int getNumberOfPredictions();

    public int[] getObservedValues();

    public double[] getPredictedValues();

    public int[] getGroups();

    public int[] getNumberOfDataPerGroup();

    public int[] getPositiveObservedSumPerGroup();

    public double[] getPredictedSumPerGroup();

    public double[] getHlChi2PerGroup();

    public double[] getMarginOfErrorPerGroup();

    public double[] getHlObservedValues();

    public double[] getHlExpectedValues();

    public int getDegreesOfFreedom();

    public double getPValue();

    public double getExpectedCalibrationError();

    public double getMaxCalibrationError();

    public double getAverageCalibrationError();

}
