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
package edu.pitt.dbmi.causal.experiment;

import edu.pitt.dbmi.causal.experiment.util.FileIO;
import edu.pitt.dbmi.causal.experiment.util.GraphFiles;
import java.nio.file.Path;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * Mar 25, 2023 6:58:58 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
@Disabled
public class PlotFromGraphFilesAppTest {

    @TempDir
    public static Path tempDir;

    /**
     * Test of main method, of class PlotFromGraphFilesApp.
     */
    @Test
    public void testMain() throws Exception {
        String searchGraphFile = GraphFiles.SEARCH_GRAPH;
        String pagFromDagGraphFile = GraphFiles.PAG_FROM_DAG_GRAPH;
        String dirOut = FileIO.createSubdirectory(tempDir, "plot_from_graphs").toString();
        String[] args = {
            searchGraphFile,
            pagFromDagGraphFile,
            dirOut
        };
        PlotFromGraphFilesApp.main(args);
    }

}
