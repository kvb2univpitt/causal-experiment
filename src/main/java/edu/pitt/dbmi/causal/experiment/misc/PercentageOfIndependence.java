package edu.pitt.dbmi.causal.experiment.misc;

import edu.pitt.dbmi.causal.experiment.calibration.GeneralValue;
import edu.pitt.dbmi.causal.experiment.util.FileIO;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * May 4, 2023 3:02:39 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class PercentageOfIndependence {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Path dataFile = Paths.get(args[0]);
        try {
            GeneralValue[] generalValues = FileIO.loadGeneralValues(dataFile).stream().toArray(GeneralValue[]::new);

            double min = 0;
            double max = 0.1;
            int count = 0;
            int numOfIndependence = 0;
            for (GeneralValue value : generalValues) {
                if (min <= value.getPredictedValue() && value.getPredictedValue() < max) {
                    count++;
                    if (value.getObservedValue() == 1) {
                        numOfIndependence++;
                    }
                }
            }
            System.out.printf("[%f, %f) = %f%n", min, max, ((double) numOfIndependence) / count);

            min = 0.1;
            max = 0.9;
            count = 0;
            numOfIndependence = 0;
            for (GeneralValue value : generalValues) {
                if (min <= value.getPredictedValue() && value.getPredictedValue() < max) {
                    count++;
                    if (value.getObservedValue() == 1) {
                        numOfIndependence++;
                    }
                }
            }
            System.out.printf("[%f, %f) = %f%n", min, max, ((double) numOfIndependence) / count);

            min = 0.9;
            max = 0.97;
            count = 0;
            numOfIndependence = 0;
            for (GeneralValue value : generalValues) {
                if (min <= value.getPredictedValue() && value.getPredictedValue() < max) {
                    count++;
                    if (value.getObservedValue() == 1) {
                        numOfIndependence++;
                    }
                }
            }
            System.out.printf("[%f, %f) = %f%n", min, max, ((double) numOfIndependence) / count);

            min = 0.97;
            max = 1.0;
            count = 0;
            numOfIndependence = 0;
            for (GeneralValue value : generalValues) {
                if (min <= value.getPredictedValue() && value.getPredictedValue() <= max) {
                    count++;
                    if (value.getObservedValue() == 1) {
                        numOfIndependence++;
                    }
                }
            }
            System.out.printf("[%f, %f] = %f%n", min, max, ((double) numOfIndependence) / count);
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
        }
    }

}
