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

import edu.cmu.tetrad.graph.Edge;
import edu.cmu.tetrad.graph.EdgeTypeProbability;
import edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.graph.Node;
import edu.pitt.dbmi.causal.experiment.tetrad.Edges;
import static edu.pitt.dbmi.causal.experiment.tetrad.Edges.getReversed;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Mar 10, 2023 3:58:12 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class GraphData {

    private GraphData() {
    }

    public static void write(Set<EdgeValue> edgeValues, PrintStream writer) {
        edgeValues.forEach(edgeValue -> writer.println(lineData(edgeValue)));
    }

    private static String lineData(EdgeValue edgeValue) {
        String node1 = edgeValue.getNode1();
        String node2 = edgeValue.getNode2();
        double probability = edgeValue.getPredictedValue();
        int observedValue = edgeValue.getObservedValue();

        if (probability > 0) {
            switch (edgeValue.getEdgeType()) {
                case aa:
                    return String.format("%s <-> %s,%f,%d", node1, node2, probability, observedValue);
                case ac:
                    return String.format("%s <-o %s,%f,%d", node1, node2, probability, observedValue);
                case at:
                    return String.format("%s <-- %s,%f,%d", node1, node2, probability, observedValue);
                case ca:
                    return String.format("%s o-> %s,%f,%d", node1, node2, probability, observedValue);
                case cc:
                    return String.format("%s o-o %s,%f,%d", node1, node2, probability, observedValue);
                case ta:
                    return String.format("%s --> %s,%f,%d", node1, node2, probability, observedValue);
                case tt:
                    return String.format("%s --- %s,%f,%d", node1, node2, probability, observedValue);
                default:
                    return String.format("no edge,%f,%d", probability, observedValue);
            }
        } else {
            switch (edgeValue.getEdgeType()) {
                case aa:
                    return String.format("%s <-> %s,0,%d", node1, node2, observedValue);
                case ac:
                    return String.format("%s <-o %s,0,%d", node1, node2, observedValue);
                case at:
                    return String.format("%s <-- %s,0,%d", node1, node2, observedValue);
                case ca:
                    return String.format("%s o-> %s,0,%d", node1, node2, observedValue);
                case cc:
                    return String.format("%s o-o %s,0,%d", node1, node2, observedValue);
                case ta:
                    return String.format("%s --> %s,0,%d", node1, node2, observedValue);
                case tt:
                    return String.format("%s --- %s,0,%d", node1, node2, observedValue);
                default:
                    return String.format("no edge,0,%d", observedValue);
            }
        }
    }

    public static Set<EdgeValue> examineEdges(Graph searchGraph, Graph trueGraph) {
        Set<EdgeValue> edgeValues = createEdgeValues(trueGraph);

        edgeValues.forEach(edgeValue -> {
            String node1 = edgeValue.getNode1();
            String node2 = edgeValue.getNode2();

            Edge trueEdge = trueGraph.getEdge(trueGraph.getNode(node1), trueGraph.getNode(node2));
            if (trueEdge != null) {
                edgeValue.setObservedValue(1);
            }

            Edge predictedEdge = searchGraph.getEdge(searchGraph.getNode(node1), searchGraph.getNode(node2));
            if (predictedEdge != null) {
                edgeValue.setPredictedValue(predictedEdge.getProbability());
            }
        });

        return edgeValues;
    }

    public static Set<EdgeValue> examineDirectEdge(Graph searchGraph, Graph trueGraph) {
        return examineEdgeType(searchGraph, trueGraph, EdgeType.ta);
    }

    public static Set<EdgeValue> examineCircleArrowEdge(Graph searchGraph, Graph trueGraph) {
        return examineEdgeType(searchGraph, trueGraph, EdgeType.ca);
    }

    public static Set<EdgeValue> examineCircleCircleEdge(Graph searchGraph, Graph trueGraph) {
        return examineEdgeType(searchGraph, trueGraph, EdgeType.cc);
    }

    public static Set<EdgeValue> examineArrowArrowEdge(Graph searchGraph, Graph trueGraph) {
        return examineEdgeType(searchGraph, trueGraph, EdgeType.aa);
    }

    public static Set<EdgeValue> examineEdgeType(Graph searchGraph, Graph trueGraph, EdgeType edgeType) {
        Set<EdgeValue> edgeValues = createEdgeValues(trueGraph, edgeType);
        setObservedValues(trueGraph, edgeValues);
        setPredictedValues(searchGraph, edgeValues);

        return edgeValues;
    }

    private static void setPredictedValues(Graph searchGraph, Set<EdgeValue> edgeValues) {
        for (EdgeValue edgeValue : edgeValues) {
            Node node1 = searchGraph.getNode(edgeValue.getNode1());
            Node node2 = searchGraph.getNode(edgeValue.getNode2());
            Edge predictedEdge = searchGraph.getEdge(node1, node2);
            if (predictedEdge != null) {
                List<EdgeTypeProbability> edgeTypeProbs = predictedEdge.getEdgeTypeProbabilities();
                if (edgeTypeProbs != null) {
                    for (EdgeTypeProbability edgeTypeProb : edgeTypeProbs) {
                        if (node1 == predictedEdge.getNode1() && node2 == predictedEdge.getNode2()) {
                            if (edgeValue.getEdgeType() == edgeTypeProb.getEdgeType()) {
                                edgeValue.setPredictedValue(edgeTypeProb.getProbability());
                            }
                        } else if (node1 == predictedEdge.getNode2() && node2 == predictedEdge.getNode1()) {
                            if (edgeValue.getEdgeType() == getReversed(edgeTypeProb.getEdgeType())) {
                                edgeValue.setPredictedValue(edgeTypeProb.getProbability());
                            }
                        }
                    }
                }
            }
        }
    }

    private static void setObservedValues(Graph trueGraph, Set<EdgeValue> edgeValues) {
        for (EdgeValue edgeValue : edgeValues) {
            Node node1 = trueGraph.getNode(edgeValue.getNode1());
            Node node2 = trueGraph.getNode(edgeValue.getNode2());
            Edge trueEdge = trueGraph.getEdge(node1, node2);
            if (trueEdge != null) {
                if (edgeValue.getEdgeType() == Edges.getEdgeType(trueEdge, node1, node2)) {
                    edgeValue.setObservedValue(1);
                }
            }
        }
    }

    private static Set<EdgeValue> createEdgeValues(Graph graph) {
        Set<EdgeValue> edgeValues = new LinkedHashSet<>();

        String[] nodeNames = graph.getNodeNames().stream().toArray(String[]::new);
        for (int i = 0; i < nodeNames.length - 1; i++) {
            for (int j = i + 1; j < nodeNames.length; j++) {
                String node1 = nodeNames[i];
                String node2 = nodeNames[j];
                edgeValues.add(new EdgeValue(node1, node2, EdgeType.tt));
            }
        }

        return edgeValues;
    }

    private static Set<EdgeValue> createEdgeValues(Graph graph, EdgeType edgeType) {
        Set<EdgeValue> edgeValues = new LinkedHashSet<>();

        boolean isAsymmetric = !(edgeType == EdgeType.aa
                || edgeType == EdgeType.cc
                || edgeType == EdgeType.tt
                || edgeType == EdgeType.nil);

        Map<String, List<String>> variableCombinations = new LinkedHashMap<>();
        String[] nodeNames = graph.getNodeNames().stream().toArray(String[]::new);
        for (int i = 0; i < nodeNames.length - 1; i++) {
            for (int j = i + 1; j < nodeNames.length; j++) {
                String node1 = nodeNames[i];
                String node2 = nodeNames[j];
                List<String> variables = variableCombinations.get(node1);
                if (variables == null) {
                    variables = new LinkedList<>();
                    variableCombinations.put(node1, variables);
                }
                variables.add(node2);

                if (isAsymmetric) {
                    variables = variableCombinations.get(node2);
                    if (variables == null) {
                        variables = new LinkedList<>();
                        variableCombinations.put(node2, variables);
                    }
                    variables.add(node1);
                }
            }
        }

        variableCombinations.forEach((node1, variables) -> {
            variables.forEach(node2 -> edgeValues.add(new EdgeValue(node1, node2, edgeType)));
        });

        return edgeValues;
    }

}
