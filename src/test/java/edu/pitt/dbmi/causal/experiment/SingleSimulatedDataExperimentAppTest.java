package edu.pitt.dbmi.causal.experiment;

import edu.pitt.dbmi.causal.experiment.util.FileIO;
import java.nio.file.Path;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * Mar 31, 2023 12:09:26 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
@Disabled
public class SingleSimulatedDataExperimentAppTest {

    @TempDir
    public static Path tempDir;

    /**
     * Test of main method, of class SingleSimulatedDataExperimentApp.
     */
    @Test
    public void testMain() throws Exception {
        String dirOut = FileIO.createSubdirectory(tempDir, "single_sim_data_exp").toString();
        String[] args = {
            dirOut
        };
        SingleSimulatedDataExperimentApp.main(args);
    }

}
