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
package edu.pitt.dbmi.lib.math.classification.roc.plot;

import edu.pitt.dbmi.lib.math.classification.plot.AbstractXYStatPlot;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * May 17, 2023 10:19:51 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public abstract class AbstractROCCurvePlot extends AbstractXYStatPlot {

    protected XYLineAndShapeRenderer fourtyFiveDegreeLineRenderer;

    protected XYSeriesCollection pointDataset;

    protected XYLineAndShapeRenderer pointRender;
    protected XYLineAndShapeRenderer shapeRender;

    protected Map<String, XYSeries> dataSeriesMap;  // render line and color
    protected Map<String, XYSeries> dataShapeSeriesMap;  // render shape and color

    public AbstractROCCurvePlot(String title, String xAxisLabel, String yAxisLabel) {
        super(title, xAxisLabel, yAxisLabel);

        fourtyFiveDegreeLineRenderer = new XYLineAndShapeRenderer();
        fourtyFiveDegreeLineRenderer.setSeriesShapesVisible(0, false);  // do not show shape for random line
        fourtyFiveDegreeLineRenderer.setSeriesVisibleInLegend(0, false);
        fourtyFiveDegreeLineRenderer.setSeriesStroke(0, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f));
        fourtyFiveDegreeLineRenderer.setSeriesPaint(0, Color.GRAY);

        XYSeries fourtyFiveDegreeSeries = new XYSeries("");
        fourtyFiveDegreeSeries.add(-0.02, -0.02);
        fourtyFiveDegreeSeries.add(1.02, 1.02);
        plot.setRenderer(0, fourtyFiveDegreeLineRenderer);
        plot.setDataset(0, new XYSeriesCollection(fourtyFiveDegreeSeries));

        pointDataset = new XYSeriesCollection();
        plot.setDataset(1, pointDataset);

        pointRender = new XYLineAndShapeRenderer();
        plot.setRenderer(1, pointRender);

        dataSeriesMap = new HashMap<String, XYSeries>();
        dataShapeSeriesMap = new HashMap<String, XYSeries>();
    }

    public void setFourtyFiveDegreeLineVisible(boolean visible) {
        fourtyFiveDegreeLineRenderer.setSeriesVisible(0, visible);
    }

    public boolean isFourtyFiveDegreeLineVisible() {
        return fourtyFiveDegreeLineRenderer.getSeriesShapesVisible(0);
    }

    public void setDataSeriesLine(final String key, final BasicStroke line) {
        XYSeries series = dataSeriesMap.get(key);
        pointRender.setSeriesStroke(pointDataset.indexOf(series), line);
    }

    public void setDataSeriesColor(final String key, final Color color) {
        XYSeries series = dataSeriesMap.get(key);
        pointRender.setSeriesPaint(pointDataset.indexOf(series), color);

        series = dataShapeSeriesMap.get(key);
    }

    public void setDataSeriesVisible(final String key, final boolean flag) {
        XYSeries series = dataSeriesMap.get(key);
        pointRender.setSeriesVisible(pointDataset.indexOf(series), flag);

        series = dataShapeSeriesMap.get(key);
    }

    public void setDataSeriesName(final String key, final String name) {
        XYSeries series = dataSeriesMap.get(key);
        series.setKey(name);
        series.fireSeriesChanged();

        series = dataShapeSeriesMap.get(key);
        series.setKey(name);
        series.fireSeriesChanged();
    }

    public void addDataSeries(
            final String key, final String label,
            final double[] xPoints, final double[] yPoints,
            final Color color, final Shape shape, final Stroke stroke) {
        // data points
        XYSeries plotDataPoints = createPlotDataPoints(label, xPoints, yPoints);
        pointDataset.addSeries(plotDataPoints);

        int seriesIndex = pointDataset.indexOf(plotDataPoints);
        pointRender.setSeriesPaint(seriesIndex, color);
        pointRender.setSeriesShape(seriesIndex, shape);
        pointRender.setSeriesStroke(seriesIndex, stroke);
        pointRender.setSeriesVisibleInLegend(seriesIndex, true);
        dataSeriesMap.put(key, plotDataPoints);

        chart.fireChartChanged();
    }

    private XYSeries createPlotDataPoints(final String label, final double[] xPoints, final double[] yPoints) {
        XYSeries series = new XYSeries(label);

        // add plot points
        for (int i = 0; i < xPoints.length; i++) {
            series.add(xPoints[i], yPoints[i]);
        }

        return series;
    }

}
