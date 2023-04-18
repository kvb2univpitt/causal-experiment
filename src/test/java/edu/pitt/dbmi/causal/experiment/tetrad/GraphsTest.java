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

import edu.cmu.tetrad.graph.Graph;
import edu.pitt.dbmi.causal.experiment.util.FileIO;
import edu.pitt.dbmi.causal.experiment.util.GraphFiles;
import edu.pitt.dbmi.causal.experiment.util.ResourceLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * Mar 12, 2023 1:30:23 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class GraphsTest {

    @TempDir
    public static Path tempDir;

    /**
     * Test of exportAsPngImage method, of class Graphs.
     */
    @Disabled
    @Test
    public void testExportAsPngImage() throws Exception {
        Graph graph = ResourceLoader.loadGraph(Paths.get(GraphFiles.SIM_DISC_20VAR_1KCASE_TRUE_GRAPH));
        String dirOut = FileIO.createSubdirectory(tempDir, "pag_sampling_rfci").toString();
        int width = 1000;
        int height = 1000;
        Path output = Paths.get(dirOut, "true_graph.png");
        Graphs.exportAsPngImage(graph, width, height, output);
    }

}
