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

import edu.pitt.dbmi.causal.experiment.util.DataFiles;
import edu.pitt.dbmi.causal.experiment.util.FileIO;
import edu.pitt.dbmi.causal.experiment.util.GraphFiles;
import java.nio.file.Path;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * Mar 11, 2023 6:34:38 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
@Disabled
public class RficGSquareBootstrapAppTest {

    @TempDir
    public static Path tempDir;

    /**
     * Test of main method, of class RficGSquareBootstrapApp.
     */
    @Test
    public void testMain() throws Exception {
        String dataset = DataFiles.SIM_DISC_20VAR_1KCASE_2;
        String trueGraph = GraphFiles.SIM_DISC_20VAR_1KCASE_2_TRUE_GRAPH;
        String dirOut = FileIO.createSubdirectory(tempDir, "rfci_g_square_bootstrap").toString();
        String[] args = {
            dataset,
            trueGraph,
            dirOut
        };
        RficGSquareBootstrapApp.main(args);
    }

}
