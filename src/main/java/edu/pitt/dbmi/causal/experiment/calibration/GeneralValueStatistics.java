package edu.pitt.dbmi.causal.experiment.calibration;

import edu.pitt.dbmi.lib.math.classification.calibration.HosmerLemeshow;
import edu.pitt.dbmi.lib.math.classification.calibration.HosmerLemeshowRiskGroup;
import edu.pitt.dbmi.lib.math.classification.calibration.plot.HosmerLemeshowPlot;
import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
import edu.pitt.dbmi.lib.math.classification.plot.PlotColors;
import edu.pitt.dbmi.lib.math.classification.plot.PlotShapes;
import edu.pitt.dbmi.lib.math.classification.roc.DeLongROCCurve;
import edu.pitt.dbmi.lib.math.classification.roc.ROC;
import edu.pitt.dbmi.lib.math.classification.roc.plot.ROCCurvePlot;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Apr 27, 2023 12:46:52 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class GeneralValueStatistics {

    private final List<GeneralValue> generalValues;

    private final HosmerLemeshow hosmerLemeshow;
    private final ROC roc;

    public GeneralValueStatistics(List<GeneralValue> generalValues) {
        this.generalValues = generalValues;

        ObservedPredictedValue[] observedPredictedValues = toObservedPredictedValues(generalValues);
        this.hosmerLemeshow = new HosmerLemeshowRiskGroup(observedPredictedValues);
        this.roc = new DeLongROCCurve(observedPredictedValues);
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
        plot.addDataSeries(hosmerLemeshow, name, name, PlotColors.FOREST_GREEN, PlotShapes.CIRCLE_SHAPE, true);

        plot.saveImageAsPNG(file.toFile(), width, height);
    }

    public void saveROCPlot(String title, String name, int width, int height, Path file) throws IOException {
        ROCCurvePlot plot = new ROCCurvePlot(title);
        plot.add(roc, "", name, PlotColors.FOREST_GREEN);

        plot.saveImageAsPNG(file.toFile(), width, height);
    }

    public void saveData(Path file) throws IOException {
        try (PrintStream writer = new PrintStream(file.toFile())) {
            generalValues.forEach(value -> {
                writer.printf("\"%s\",%f,%d%n", value.getLabel(), value.getPredictedValue(), value.getObservedValue());
            });
        }
    }

    private ObservedPredictedValue[] toObservedPredictedValues(List<GeneralValue> generalValues) {
        List<ObservedPredictedValue> values = new LinkedList<>();

        generalValues.forEach(value -> {
            values.add(new ObservedPredictedValue(value.getObservedValue(), value.getPredictedValue()));
        });

        return values.stream().toArray(ObservedPredictedValue[]::new);
    }

}
