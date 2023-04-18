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
package edu.pitt.dbmi.causal.experiment;

import edu.cmu.tetrad.graph.Graph;
import edu.pitt.dbmi.causal.experiment.calibration.GraphStatistics;
import edu.pitt.dbmi.causal.experiment.util.FileIO;
import edu.pitt.dbmi.causal.experiment.util.ResourceLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * Mar 25, 2023 6:57:50 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class PlotFromGraphFilesApp {

    private static void run(Path searchGraphFile, Path pagFromDagGraphFile, Path dirOut) throws Exception {
        Graph searchGraph = ResourceLoader.loadGraph(searchGraphFile);
        Graph pagFromDagGraph = ResourceLoader.loadGraph(pagFromDagGraphFile);

        Path graphFolder = Paths.get(dirOut.toString(), "graphs");
        FileIO.createNewDirectory(graphFolder);

        String outputDir = graphFolder.toString();
        GraphStatistics graphCalibration = new GraphStatistics(searchGraph, pagFromDagGraph);
        graphCalibration.saveGraphData(Paths.get(outputDir, "edge_data.csv"));
        graphCalibration.saveStatistics(Paths.get(outputDir, "statistics.txt"));
        graphCalibration.saveCalibrationPlot(
                "PAG Sampling RFCI", "pag-sampling-rfci",
                1000, 1000,
                Paths.get(outputDir, "calibration.png"));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("Plot From Graph Files");
        System.out.println("================================================================================");
        try {
            run(Paths.get(args[0]), Paths.get(args[1]), Paths.get(args[2]));
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
        System.out.println("================================================================================");
    }

}
