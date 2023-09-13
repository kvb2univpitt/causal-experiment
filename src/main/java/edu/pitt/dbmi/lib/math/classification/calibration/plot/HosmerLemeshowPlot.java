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
package edu.pitt.dbmi.lib.math.classification.calibration.plot;

import edu.pitt.dbmi.lib.math.classification.calibration.HosmerLemeshow;
import edu.pitt.dbmi.lib.math.classification.plot.AbstractXYStatPlot;
import java.awt.Color;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;

/**
 *
 * May 4, 2012 12:00:34 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class HosmerLemeshowPlot extends AbstractXYStatPlot {

    private final XYIntervalSeriesCollection pointDataset;
    private final XYIntervalSeriesCollection errorBarDataset;

    private final XYErrorRenderer dataSeriesRenderer;
    private final XYErrorRenderer dataSeriesErrorBarRenderer;

    private final Map<String, XYIntervalSeries> dataSeriesMap;
    private final Map<String, XYIntervalSeries> dataSeriesErrorBarMap;

    public HosmerLemeshowPlot() {
        this("");
    }

    public HosmerLemeshowPlot(String title) {
        this(title, "Predicted (proportion)", "Observed (proportion)");
    }

    public HosmerLemeshowPlot(String title, String xAxisLabel, String yAxisLabel) {
        super(title, xAxisLabel, yAxisLabel);

        pointDataset = new XYIntervalSeriesCollection();
        errorBarDataset = new XYIntervalSeriesCollection();

        dataSeriesRenderer = new XYErrorRenderer();
        dataSeriesErrorBarRenderer = new XYErrorRenderer();

        dataSeriesMap = new HashMap<String, XYIntervalSeries>();
        dataSeriesErrorBarMap = new HashMap<String, XYIntervalSeries>();
    }

    public void addDataSeries(HosmerLemeshow hl, String key, String name, Color color, Shape shape, boolean errorBars) {
        double pvalue = hl.getPValue();
        String label = (pvalue < 0.0001f)
                ? String.format("%s (p-value < 0.0001)", name)
                : String.format("%s (p-value = %1.4f)", name, pvalue);

        XYIntervalSeries dataSeries = new XYIntervalSeries(label);
        XYIntervalSeries dataErrorBarSeries = new XYIntervalSeries(label);

        double[] expectedValues = hl.getHlExpectedValues();
        double[] observedValues = hl.getHlObservedValues();
        double[] marginOfErrorPerGroup = hl.getMarginOfErrorPerGroup();
        for (int i = 0; i < expectedValues.length; i++) {
            double xValue = expectedValues[i];
            double yValue = observedValues[i];
            if ((xValue >= 0.0 && xValue <= 1.0) || (yValue >= 0.0 && yValue <= 1.0)) {
                // no error bars
                dataSeries.add(xValue, xValue, xValue, yValue, yValue, yValue);

                // has error bars
                double yLowValue = (marginOfErrorPerGroup[i] == 0)
                        ? marginOfErrorPerGroup[i] - yValue
                        : yValue - marginOfErrorPerGroup[i];
                double yHighValue = yValue + marginOfErrorPerGroup[i];
                dataErrorBarSeries.add(
                        xValue, xValue, xValue,
                        yValue, yLowValue, yHighValue);
            }
        }

        pointDataset.addSeries(dataSeries);
        errorBarDataset.addSeries(dataErrorBarSeries);

        int index = pointDataset.indexOf(dataSeries.getKey());
        dataSeriesRenderer.setSeriesPaint(index, color);
        dataSeriesRenderer.setSeriesShape(index, shape);
        dataSeriesMap.put(key, dataSeries);

        index = errorBarDataset.indexOf(dataSeries.getKey());
        dataSeriesErrorBarRenderer.setSeriesPaint(index, color);
        dataSeriesErrorBarRenderer.setSeriesShape(index, shape);
        dataSeriesErrorBarMap.put(key, dataErrorBarSeries);

        setToErrorBars(errorBars);
    }

    private void setToErrorBars(boolean errorBars) {
        if (errorBars) {
            plot.setDataset(1, errorBarDataset);
            plot.setRenderer(1, dataSeriesErrorBarRenderer);
        } else {
            plot.setDataset(1, pointDataset);
            plot.setRenderer(1, dataSeriesRenderer);
        }

        chart.fireChartChanged();
    }

    public void setDataSeriesColor(final String key, final Color color) {
        XYIntervalSeries series = dataSeriesMap.get(key);
        dataSeriesRenderer.setSeriesPaint(pointDataset.indexOf(series.getKey()), color);

        series = dataSeriesErrorBarMap.get(key);
        dataSeriesErrorBarRenderer.setSeriesPaint(errorBarDataset.indexOf(series.getKey()), color);
    }

    public void setDataSeriesVisible(final String key, final boolean flag) {
        XYIntervalSeries series = dataSeriesMap.get(key);
        dataSeriesRenderer.setSeriesVisible(pointDataset.indexOf(series.getKey()), flag);

        series = dataSeriesErrorBarMap.get(key);
        dataSeriesErrorBarRenderer.setSeriesVisible(errorBarDataset.indexOf(series.getKey()), flag);
    }

    public void setDataSeriesShape(final String key, final Shape shape) {
        XYIntervalSeries series = dataSeriesMap.get(key);
        dataSeriesRenderer.setSeriesShape(pointDataset.indexOf(series.getKey()), shape);

        series = dataSeriesErrorBarMap.get(key);
        dataSeriesErrorBarRenderer.setSeriesShape(errorBarDataset.indexOf(series.getKey()), shape);
    }

}
