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
package edu.pitt.dbmi.causal.experiment.data;

import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.graph.Graph;

/**
 *
 * Mar 22, 2023 5:27:19 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class SimulatedData {

    private final DataSet dataSet;

    private final Graph trueGraph;

    private final Graph pagFromDagGraph;

    public SimulatedData(DataSet dataSet, Graph trueGraph, Graph pagFromDagGraph) {
        this.dataSet = dataSet;
        this.trueGraph = trueGraph;
        this.pagFromDagGraph = pagFromDagGraph;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public Graph getTrueGraph() {
        return trueGraph;
    }

    public Graph getPagFromDagGraph() {
        return pagFromDagGraph;
    }

}
