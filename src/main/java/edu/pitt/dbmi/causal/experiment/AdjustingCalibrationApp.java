package edu.pitt.dbmi.causal.experiment;

import edu.pitt.dbmi.causal.experiment.calibration.GeneralValue;
import edu.pitt.dbmi.causal.experiment.calibration.GeneralValueStatistics;
import edu.pitt.dbmi.lib.math.classification.utils.ResourcesLoader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * Sep 13, 2023 1:13:26 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class AdjustingCalibrationApp {

    private static void plot(Set<GeneralValue> generalValues, String outputDir) throws IOException {
        Set<GeneralValue> values = new HashSet<>();
        for (GeneralValue value : generalValues) {
            double p = value.getPredictedValue();
            if (p < 0.39) {
                p = 0;
            } else if (p >= 0.39 && p < 0.9) {
                p = 0.222222;
            } else if (p >= 0.9 && p < 0.92) {
                p = 0.500000;
            } else if (p >= 0.92 && p < 0.97) {
                p = 0.0;
            } else {
                p = 0.666667;
            }
            values.add(new GeneralValue(value.getLabel(), p, value.getObservedValue()));
        }

        GeneralValueStatistics genValStats = new GeneralValueStatistics(values);
        genValStats.saveCalibrationPlot(
                "PAG Sampling RFCI: Probabilistic Test", "probabilistic",
                1000, 1000,
                Paths.get(outputDir, "independence_test_calibration.png"));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Path dataFile = Paths.get(args[0]);
        String dirOut = args[1];

        Set<GeneralValue> generalValues = new HashSet<>();
        try {
            Path file = dataFile;
            Pattern delimiter = Pattern.compile(",");
            boolean hasHeader = false;
            generalValues.addAll(ResourcesLoader.loadGeneralValues(file, delimiter, hasHeader));
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
        }

        int nCount = 0;
        int count = 0;
        for (GeneralValue value : generalValues) {
            double p = value.getPredictedValue();
            if (p < 0.39) {
                nCount++;
                if (value.getObservedValue() == 1) {
                    count++;
                }
            } else if (p >= 0.39 && p < 0.9) {
            } else if (p >= 0.9 && p < 0.92) {
            } else if (p >= 0.92 && p < 0.97) {
            } else {
            }
        }
        System.out.println("================================================================================");
        System.out.printf("%d, %d => %f%n", count, nCount, ((double) count) / nCount);
        System.out.println("================================================================================");
        try {
            plot(generalValues, dirOut);
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
        }
    }

}
