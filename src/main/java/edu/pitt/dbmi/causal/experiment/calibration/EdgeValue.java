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

import edu.cmu.tetrad.graph.EdgeTypeProbability;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.aa;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.ac;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.at;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.ca;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.cc;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.ta;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.tt;
import java.util.Objects;

/**
 *
 * Feb 17, 2023 3:04:53 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class EdgeValue implements Comparable<EdgeValue> {

    private double predictedValue;
    private int observedValue;

    private final String node1;
    private final String node2;
    private final EdgeTypeProbability.EdgeType edgeType;

    public EdgeValue(String node1, String node2, EdgeTypeProbability.EdgeType edgeType) {
        this.node1 = node1;
        this.node2 = node2;
        this.edgeType = edgeType;
    }

    public EdgeValue(double probability, int observedValue, String node1, String node2, EdgeTypeProbability.EdgeType edgeType) {
        this.predictedValue = probability;
        this.observedValue = observedValue;
        this.node1 = node1;
        this.node2 = node2;
        this.edgeType = edgeType;
    }

    @Override
    public int compareTo(EdgeValue o) {
        if (node1.compareTo(o.node1) < 0) {
            return -1;
        } else if (node1.compareTo(o.node1) > 0) {
            return 1;
        } else {
            return node2.compareTo(o.node2);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.node1);
        hash = 67 * hash + Objects.hashCode(this.node2);
        hash = 67 * hash + Objects.hashCode(this.edgeType);

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EdgeValue other = (EdgeValue) obj;
        if (!Objects.equals(this.node1, other.node1)) {
            return false;
        }
        if (!Objects.equals(this.node2, other.node2)) {
            return false;
        }

        return this.edgeType == other.edgeType;
    }

    @Override
    public String toString() {
        if (predictedValue > 0) {
            switch (edgeType) {
                case aa:
                    return String.format("%s <-> %s: %f %d", node1, node2, predictedValue, observedValue);
                case ac:
                    return String.format("%s <-o %s: %f %d", node1, node2, predictedValue, observedValue);
                case at:
                    return String.format("%s <-- %s: %f %d", node1, node2, predictedValue, observedValue);
                case ca:
                    return String.format("%s o-> %s: %f %d", node1, node2, predictedValue, observedValue);
                case cc:
                    return String.format("%s o-o %s: %f %d", node1, node2, predictedValue, observedValue);
                case ta:
                    return String.format("%s --> %s: %f %d", node1, node2, predictedValue, observedValue);
                case tt:
                    return String.format("%s --- %s: %f %d", node1, node2, predictedValue, observedValue);
                default:
                    return String.format("no edge: %f %d", predictedValue, observedValue);
            }
        } else {
            switch (edgeType) {
                case aa:
                    return String.format("%s <-> %s: 0 %d", node1, node2, observedValue);
                case ac:
                    return String.format("%s <-o %s: 0 %d", node1, node2, observedValue);
                case at:
                    return String.format("%s <-- %s: 0 %d", node1, node2, observedValue);
                case ca:
                    return String.format("%s o-> %s: 0 %d", node1, node2, observedValue);
                case cc:
                    return String.format("%s o-o %s: 0 %d", node1, node2, observedValue);
                case ta:
                    return String.format("%s --> %s: 0 %d", node1, node2, observedValue);
                case tt:
                    return String.format("%s --- %s: 0 %d", node1, node2, observedValue);
                default:
                    return String.format("no edge: 0 %d", observedValue);
            }
        }
    }

    public double getPredictedValue() {
        return predictedValue;
    }

    public void setPredictedValue(double predictedValue) {
        this.predictedValue = predictedValue;
    }

    public int getObservedValue() {
        return observedValue;
    }

    public void setObservedValue(int observedValue) {
        this.observedValue = observedValue;
    }

    public String getNode1() {
        return node1;
    }

    public String getNode2() {
        return node2;
    }

    public EdgeTypeProbability.EdgeType getEdgeType() {
        return edgeType;
    }

}
