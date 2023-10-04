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
package edu.pitt.dbmi.lib.math.classification.utils;

import edu.pitt.dbmi.causal.experiment.calibration.GeneralValue;
import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * Mar 6, 2023 1:39:09 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class ResourcesLoader {

    private ResourcesLoader() {
    }

    public static Set<GeneralValue> loadGeneralValues(Path file, Pattern delimiter, boolean hasHeader) throws IOException {
        Set<GeneralValue> generalValues = new HashSet<>();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();

                // skip blank lines
                if (line.isEmpty()) {
                    continue;
                }

                // skip header
                if (hasHeader) {
                    hasHeader = false;
                    continue;
                }

                String[] fields = delimiter.split(line);
                if (fields.length >= 3) {
                    String[] strArray = new String[fields.length - 2];
                    System.arraycopy(fields, 0, strArray, 0, strArray.length);
                    String label = Arrays.stream(strArray).collect(Collectors.joining());
                    label = line.substring(0, label.length() + strArray.length - 1);

                    double predictedValue = Double.parseDouble(fields[fields.length - 2]);
                    int observedValue = Integer.parseInt(fields[fields.length - 1]);
                    generalValues.add(new GeneralValue(label, predictedValue, observedValue));
                }
            }
        }

        return generalValues;
    }

    /**
     * Read in observed values and predicted values from a file.
     *
     * @param file containing a column of observed values and a column of
     * predicted values
     * @param delimiter a character that is used to separate data
     * @param observedColumn the number of the column containing observed values
     * @param predictedColumn the number of the column containing predicted
     * values
     * @param hasHeader true if the first line of the file is the header
     * @return a list of observed values and their corresponding predicted
     * values
     * @throws IOException
     */
    public static List<ObservedPredictedValue> loadData(Path file, Pattern delimiter, int observedColumn, int predictedColumn, boolean hasHeader) throws IOException {
        List<ObservedPredictedValue> data = new LinkedList<>();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            int observedIndex = observedColumn - 1;
            int predictedIndex = predictedColumn - 1;
            int maxColumn = Integer.max(observedColumn, predictedColumn);
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();

                // skip blank lines
                if (line.isEmpty()) {
                    continue;
                }

                // skip header
                if (hasHeader) {
                    hasHeader = false;
                    continue;
                }

                String[] fields = delimiter.split(line);
                if (fields.length >= maxColumn) {
                    data.add(new ObservedPredictedValue(
                            Integer.parseInt(fields[observedIndex]),
                            Double.parseDouble(fields[predictedIndex])));
                }
            }
        }

        return Collections.unmodifiableList(data);
    }

}
