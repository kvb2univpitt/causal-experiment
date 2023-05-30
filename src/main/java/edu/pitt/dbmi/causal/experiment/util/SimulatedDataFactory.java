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

import edu.cmu.tetrad.algcomparison.Comparison;
import edu.cmu.tetrad.algcomparison.graph.RandomForward;
import edu.cmu.tetrad.algcomparison.simulation.BayesNetSimulation;
import edu.cmu.tetrad.algcomparison.simulation.Simulation;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.search.utils.GraphSearchUtils;
import edu.cmu.tetrad.util.ParamDescriptions;
import edu.cmu.tetrad.util.Parameters;
import edu.cmu.tetrad.util.Params;
import edu.pitt.dbmi.causal.experiment.data.SimulatedData;
import java.nio.file.Path;

/**
 *
 * Mar 22, 2023 3:45:19 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class SimulatedDataFactory {

    private SimulatedDataFactory() {
    }

    public static SimulatedData createBayesNetSimulationData(int numOfVariables, int numOfCases, int avgDegree, long seed, Path dirOut) throws Exception {
        Simulation simulation = new BayesNetSimulation(new RandomForward());
        Parameters parameters = createParameters(simulation, numOfVariables, numOfCases, avgDegree, seed);
        simulation.createData(parameters, true);

        // save data and true graph
        DataSet dataSet = (DataSet) simulation.getDataModel(0);
        Graph trueGraph = simulation.getTrueGraph(0);
        Graph pagFromDagGraph = GraphSearchUtils.dagToPag(trueGraph);

        new Comparison().saveToFilesSingleSimulation(dirOut.toString(), simulation, parameters);

        return new SimulatedData(dataSet, trueGraph, pagFromDagGraph);
    }

    private static Parameters createParameters(Simulation simulation, int numOfVariables, int numOfCases, int avgDegree, long seed) {
        Parameters parameters = new Parameters();

        ParamDescriptions paramDescs = ParamDescriptions.getInstance();
        for (String param : simulation.getParameters()) {
            parameters.set(param, paramDescs.get(param).getDefaultValue());
        }

        // override parameter values
        parameters.set(Params.RANDOMIZE_COLUMNS, Boolean.FALSE);
        parameters.set(Params.NUM_MEASURES, numOfVariables);
        parameters.set(Params.SAMPLE_SIZE, numOfCases);
        parameters.set(Params.AVG_DEGREE, avgDegree);
        parameters.set(Params.SEED, seed);

        return parameters;
    }

}
