package edu.pitt.dbmi.causal.experiment.util;

import edu.cmu.tetrad.graph.Node;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * Apr 26, 2023 3:51:52 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static String toString(Node x, Node y, Node... z) {
        return (z != null && z.length > 0)
                ? String.format("P(%s,%s|%s)",
                        x.getName(),
                        y.getName(),
                        Arrays.stream(z).map(Node::getName).collect(Collectors.joining(",")))
                : String.format("P(%s,%s)",
                        x.getName(),
                        y.getName());
    }

}
