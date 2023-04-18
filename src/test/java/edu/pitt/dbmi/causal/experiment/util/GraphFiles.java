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

/**
 *
 * Mar 10, 2023 11:23:24 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class GraphFiles {

    public static final String SIM_DISC_20VAR_1KCASE_TRUE_GRAPH = DataFiles.class
            .getResource("/data/discrete_20var_1kcase/simulation/graph/graph.1.txt").getFile();

    public static final String SIM_DISC_20VAR_1KCASE_2_TRUE_GRAPH = DataFiles.class
            .getResource("/data/discrete_20var_1kcase_2/simulation/graph/graph.1.txt").getFile();

    public static final String SEARCH_GRAPH = DataFiles.class
            .getResource("/graph/search_graph.txt").getFile();

    public static final String PAG_FROM_DAG_GRAPH = DataFiles.class
            .getResource("/graph/pag_from_dag_graph.txt").getFile();

    private GraphFiles() {
    }

}
