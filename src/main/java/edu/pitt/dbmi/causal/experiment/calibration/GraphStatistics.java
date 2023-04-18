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
package edu.pitt.dbmi.causal.experiment.calibration;

import edu.cmu.tetrad.graph.Graph;
import edu.pitt.dbmi.lib.math.classification.calibration.HosmerLemeshow;
import edu.pitt.dbmi.lib.math.classification.calibration.HosmerLemeshowRiskGroup;
import edu.pitt.dbmi.lib.math.classification.calibration.plot.HosmerLemeshowPlot;
import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
import edu.pitt.dbmi.lib.math.classification.plot.PlotColors;
import edu.pitt.dbmi.lib.math.classification.plot.PlotShapes;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * Mar 10, 2023 4:36:23 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class GraphStatistics {

    private final Set<EdgeValue> graphData;
    private final Set<EdgeValue> edgeData;

    private final HosmerLemeshow hosmerLemeshow;

    public GraphStatistics(Graph searchGraph, Graph trueGraph) {
        this.graphData = GraphData.examineDirectEdge(searchGraph, trueGraph);
        this.edgeData = GraphData.examineEdges(searchGraph, trueGraph);

        ObservedPredictedValue[] observedPredictedValues = toObservedPredictedValues(graphData);
        this.hosmerLemeshow = new HosmerLemeshowRiskGroup(observedPredictedValues);
    }

    public void saveStatistics(Path file) throws IOException {
        try (PrintStream writer = new PrintStream(file.toFile())) {
            writer.println("Hosmer–Lemeshow Test");
            writer.println(hosmerLemeshow.getSummary());
            writer.println();

            writer.println("Plot Points");
            writer.println("========================================");
            double[] expected = hosmerLemeshow.getHlExpectedValues();
            double[] observed = hosmerLemeshow.getHlObservedValues();
            for (int i = 0; i < expected.length; i++) {
                writer.printf("(%f, %f)%n", expected[i], observed[i]);
            }
        }
    }

    public void saveCalibrationPlot(String title, String name, int width, int height, Path file) throws IOException {
        HosmerLemeshowPlot plot = new HosmerLemeshowPlot(title);
        plot.addDataSeries(hosmerLemeshow, name, name, PlotColors.DARK_VIOLET, PlotShapes.CIRCLE_SHAPE, true);

        plot.saveImageAsPNG(file.toFile(), width, height);
    }

    public void saveGraphData(Path file) throws IOException {
        try (PrintStream writer = new PrintStream(file.toFile())) {
            GraphData.write(graphData, writer);
        }
    }

    public void saveEdgeData(Path file) throws IOException {
        try (PrintStream writer = new PrintStream(file.toFile())) {
            GraphData.write(edgeData, writer);
        }
    }

    private ObservedPredictedValue[] toObservedPredictedValues(Set<EdgeValue> graphData) {
        List<ObservedPredictedValue> values = new LinkedList<>();

        graphData.forEach(data -> {
            values.add(new ObservedPredictedValue(data.getObservedValue(), data.getPredictedValue()));
        });

        return values.stream().toArray(ObservedPredictedValue[]::new);
    }

}
