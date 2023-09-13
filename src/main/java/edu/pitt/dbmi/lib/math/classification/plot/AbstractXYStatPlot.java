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
package edu.pitt.dbmi.lib.math.classification.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * Oct 26, 2011 4:23:43 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public abstract class AbstractXYStatPlot implements XYStatPlot {

    protected JFreeChart chart;

    protected XYPlot plot;

    protected NumberAxis xAxis;

    protected NumberAxis yAxis;

    public AbstractXYStatPlot(String title, String xAxisLabel, String yAxisLabel) {
        chart = ChartFactory.createXYLineChart(
                title, // Title
                xAxisLabel, // x-axis Label
                yAxisLabel, // y-axis Label
                null, // Dataset (don't set any dataset yet)
                PlotOrientation.VERTICAL, // XYStatPlot Orientation
                true, // Show Legend
                false, // Use tooltips
                false // Configure chart to generate URLs?
        );
        chart.setBackgroundPaint(Color.WHITE); // Set the background colour of the chart
        chart.setAntiAlias(true);
        chart.setPadding(new RectangleInsets(20, 20, 20, 20));
        setLegend(chart);

        // get plot
        plot = chart.getXYPlot();
        setVisual(plot);
        addFourtyFiveDegreeLine(plot);
        addAxis(plot);
    }

    private void setLegend(JFreeChart chart) {
        LegendTitle legend = chart.getLegend();
        legend.setItemFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        legend.setPosition(RectangleEdge.BOTTOM);
        legend.setBorder(1, 1, 1, 1);
        legend.setItemLabelPadding(new RectangleInsets(2, 5, 2, 20));
        legend.setPadding(new RectangleInsets(10, 20, 10, 20));
    }

    private void setVisual(XYPlot plot) {
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
    }

    private void addAxis(XYPlot plot) {
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 18);
        NumberTickUnit tickUnit = new NumberTickUnit(0.10);

        // y-axis
        yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setTickUnit(tickUnit);
        yAxis.setRangeAboutValue(0.5, 1.02);  // centered at 0.5 with length 1.02
        yAxis.setMinorTickCount(5);
        yAxis.setMinorTickMarksVisible(true);
        yAxis.setTickLabelFont(font);
        yAxis.setLabelFont(font);

        // x-axis
        xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setTickUnit(tickUnit);
        xAxis.setRangeAboutValue(0.5, 1.02);  // centered at 0.5 with length 1.02
        xAxis.setMinorTickCount(5);
        xAxis.setMinorTickMarksVisible(true);
        xAxis.setTickLabelFont(font);
        xAxis.setLabelFont(font);
    }

    private void addFourtyFiveDegreeLine(XYPlot plot) {
        // line points
        XYSeries xYSeries = new XYSeries("");
        xYSeries.add(-0.02, -0.02);
        xYSeries.add(1.02, 1.02);

        // line visual
        XYLineAndShapeRenderer shapeRenderer = new XYLineAndShapeRenderer();
        shapeRenderer = new XYLineAndShapeRenderer();
        shapeRenderer.setSeriesShapesVisible(0, false);  // do not show shape for random line
        shapeRenderer.setSeriesVisibleInLegend(0, false);
        shapeRenderer.setSeriesStroke(0, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f));
        shapeRenderer.setSeriesPaint(0, Color.GRAY);

        // add line to plot
        plot.setRenderer(shapeRenderer);
        plot.setDataset(0, new XYSeriesCollection(xYSeries));
    }

    @Override
    public void setTitle(final String title) {
        chart.setTitle(title);
    }

    @Override
    public String getTitle() {
        return chart.getTitle().getText();
    }

    @Override
    public void setXAxisLabel(final String label) {
        xAxis.setLabel(label);
    }

    @Override
    public String getXAxisLabel() {
        return xAxis.getLabel();
    }

    @Override
    public void setYAxisLabel(final String label) {
        yAxis.setLabel(label);
    }

    @Override
    public String getYAxisLabel() {
        return yAxis.getLabel();
    }

    @Override
    public void setXAxisRange(final double lower, final double upper) {
        xAxis.setRange(lower, upper);
    }

    @Override
    public void setYAxisRange(final double lower, final double upper) {
        yAxis.setRange(lower, upper);
    }

    @Override
    public void setGridLineVisible(final boolean visible) {
        plot.setDomainGridlinesVisible(visible);
        plot.setRangeGridlinesVisible(visible);
    }

    @Override
    public boolean isGridLineVisible() {
        return plot.isDomainGridlinesVisible();
    }

    @Override
    public void setPlotOutlineVisible(final boolean visible) {
        plot.setOutlineVisible(visible);
    }

    @Override
    public boolean isPlotOutlineVisible() {
        return plot.isOutlineVisible();
    }

    public XYPlot getPlot() {
        return plot;
    }

    public NumberAxis getXAxis() {
        return xAxis;
    }

    public NumberAxis getYAxis() {
        return yAxis;
    }

    public BufferedImage createPlotImage(int width, int height) {
        return chart.createBufferedImage(width, height);
    }

    public void saveImageAsPNG(File file, int width, int height) throws IOException {
        ChartUtils.saveChartAsPNG(file, chart, width, height);
    }

    @Override
    public JFreeChart getChart() {
        return chart;
    }

}
