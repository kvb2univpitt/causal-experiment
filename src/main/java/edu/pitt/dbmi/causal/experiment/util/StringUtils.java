package edu.pitt.dbmi.causal.experiment.util;

import edu.cmu.tetrad.graph.Node;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * Apr 26, 2023 3:51:52 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class StringUtils {

    private static Set<String> setXY = new TreeSet<>();
    private static Set<String> setZ = new TreeSet<>();

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

    public static String toStringSorted(Node x, Node y, Node... z) {
        setXY.add(x.getName());
        setXY.add(y.getName());
        setZ.addAll(Arrays.stream(z).map(Node::getName).toList());

        String xyVars = setXY.stream().collect(Collectors.joining(","));
        String zVars = setZ.stream().collect(Collectors.joining(","));

        setXY.clear();
        setZ.clear();

        return zVars.isBlank()
                ? String.format("P(%s)", xyVars)
                : String.format("P(%s|%s)", xyVars, zVars);
    }
}
