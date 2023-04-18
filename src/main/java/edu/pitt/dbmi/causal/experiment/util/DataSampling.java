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
package edu.pitt.dbmi.causal.experiment.util;

import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.data.DataUtils;
import edu.cmu.tetrad.util.Parameters;
import edu.cmu.tetrad.util.Params;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.SynchronizedRandomGenerator;
import org.apache.commons.math3.random.Well44497b;

/**
 *
 * Mar 10, 2023 12:15:01 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class DataSampling {

    private DataSampling() {
    }

    public static List<DataSet> sample(DataSet dataSet, Parameters parameters, RandomGenerator randomGenerator) {
        boolean resamplingWithReplacement = parameters.getBoolean(Params.RESAMPLING_WITH_REPLACEMENT);

        return resamplingWithReplacement
                ? sampleWithReplacement(dataSet, parameters, randomGenerator)
                : sampleWithoutReplacement(dataSet, parameters, randomGenerator);
    }

    public static List<DataSet> sample(DataSet dataSet, Parameters parameters) {
        boolean resamplingWithReplacement = parameters.getBoolean(Params.RESAMPLING_WITH_REPLACEMENT);
        RandomGenerator randomGenerator = createRandomGenerator(parameters);

        return resamplingWithReplacement
                ? sampleWithReplacement(dataSet, parameters, randomGenerator)
                : sampleWithoutReplacement(dataSet, parameters, randomGenerator);
    }

    private static List<DataSet> sampleWithoutReplacement(DataSet dataSet, Parameters parameters, RandomGenerator randomGenerator) {
        return Collections.EMPTY_LIST;
    }

    public static DataSet sampleWithReplacement(DataSet dataSet, RandomGenerator randomGenerator) {
        return DataUtils.getBootstrapSample(dataSet, dataSet.getNumRows(), randomGenerator);
    }

    private static List<DataSet> sampleWithReplacement(DataSet dataSet, Parameters parameters, RandomGenerator randomGenerator) {
        List<DataSet> dataSets = new LinkedList<>();

        int numberOfResampling = parameters.getInt(Params.NUMBER_RESAMPLING);
        for (int i = 0; i < numberOfResampling; i++) {
            dataSets.add(sampleWithReplacement(dataSet, randomGenerator));
        }

        boolean addOriginalDataset = parameters.getBoolean(Params.ADD_ORIGINAL_DATASET);
        if (addOriginalDataset) {
            dataSets.add(dataSet);
        }

        return dataSets;
    }

    public static RandomGenerator createRandomGenerator(Parameters parameters) {
        long seed = parameters.getLong(Params.SEED);

        return (seed < 0)
                ? new SynchronizedRandomGenerator(new Well44497b(System.nanoTime()))
                : new SynchronizedRandomGenerator(new Well44497b(seed));
    }

}
