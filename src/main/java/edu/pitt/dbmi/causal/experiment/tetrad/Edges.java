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
package edu.pitt.dbmi.causal.experiment.tetrad;

import edu.cmu.tetrad.graph.Edge;
import edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.aa;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.ac;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.at;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.ca;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.cc;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.nil;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.ta;
import static edu.cmu.tetrad.graph.EdgeTypeProbability.EdgeType.tt;
import edu.cmu.tetrad.graph.Endpoint;
import edu.cmu.tetrad.graph.Node;

/**
 *
 * Mar 10, 2023 3:45:59 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class Edges {

    private Edges() {
    }

    public static String toString(Endpoint endpoint1, Endpoint endpoint2) {
        if (endpoint1 == Endpoint.TAIL && endpoint2 == Endpoint.ARROW) {
            return "-->";
        } else if (endpoint1 == Endpoint.ARROW && endpoint2 == Endpoint.TAIL) {
            return "<--";
        } else if (endpoint1 == Endpoint.CIRCLE && endpoint2 == Endpoint.ARROW) {
            return "o->";
        } else if (endpoint1 == Endpoint.ARROW && endpoint2 == Endpoint.CIRCLE) {
            return "<-o";
        } else if (endpoint1 == Endpoint.CIRCLE && endpoint2 == Endpoint.CIRCLE) {
            return "o-o";
        } else if (endpoint1 == Endpoint.ARROW && endpoint2 == Endpoint.ARROW) {
            return "<->";
        } else if (endpoint1 == Endpoint.TAIL && endpoint2 == Endpoint.TAIL) {
            return "---";
        } else if (endpoint1 == Endpoint.NULL && endpoint2 == Endpoint.NULL) {
            return "...";
        } else {
            return "   ";
        }
    }

    public static String toString(Edge edge) {
        String node1 = edge.getNode1().getName();
        String node2 = edge.getNode2().getName();
        Endpoint endpoint1 = edge.getEndpoint1();
        Endpoint endpoint2 = edge.getEndpoint2();

        return String.format("%s %s %s", node1, toString(endpoint1, endpoint2), node2);
    }

    public static String toString(EdgeType edgeType) {
        switch (edgeType) {
            case aa:
                return "<->";
            case ac:
                return "<-o";
            case at:
                return "<--";
            case ca:
                return "o->";
            case cc:
                return "o-o";
            case ta:
                return "-->";
            case tt:
                return "---";
            case nil:
                return "...";
            default:
                return "   ";
        }
    }

    public static EdgeType getEdgeType(Edge edge, Node node1, Node node2) {
        Endpoint endpoint1 = edge.getProximalEndpoint(node1);
        Endpoint endpoint2 = edge.getProximalEndpoint(node2);

        if (endpoint1 == Endpoint.TAIL && endpoint2 == Endpoint.ARROW) {
            return EdgeType.ta;
        } else if (endpoint1 == Endpoint.ARROW && endpoint2 == Endpoint.TAIL) {
            return EdgeType.at;
        } else if (endpoint1 == Endpoint.CIRCLE && endpoint2 == Endpoint.ARROW) {
            return EdgeType.ca;
        } else if (endpoint1 == Endpoint.ARROW && endpoint2 == Endpoint.CIRCLE) {
            return EdgeType.ac;
        } else if (endpoint1 == Endpoint.CIRCLE && endpoint2 == Endpoint.CIRCLE) {
            return EdgeType.cc;
        } else if (endpoint1 == Endpoint.ARROW && endpoint2 == Endpoint.ARROW) {
            return EdgeType.aa;
        } else if (endpoint1 == Endpoint.TAIL && endpoint2 == Endpoint.TAIL) {
            return EdgeType.tt;
        } else {
            return EdgeType.nil;
        }
    }

    public static EdgeType getReversed(EdgeType edgeType) {
        switch (edgeType) {
            case ac:
                return EdgeType.ca;
            case at:
                return EdgeType.ta;
            case ca:
                return EdgeType.ac;
            case ta:
                return EdgeType.at;
            default:
                return edgeType;
        }
    }

}
