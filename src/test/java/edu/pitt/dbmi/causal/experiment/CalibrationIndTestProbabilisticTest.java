package edu.pitt.dbmi.causal.experiment;

import edu.pitt.dbmi.causal.experiment.util.FileIO;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * Apr 26, 2023 11:10:28 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class CalibrationIndTestProbabilisticTest {

    @TempDir
    public static Path tempDir;

    /**
     * Test of main method, of class CalibrationIndTestProbabilistic.
     */
    @Test
    public void testMain() throws Exception {
        String dirOut = FileIO.createSubdirectory(tempDir, "calibration_independence_exp").toString();
        String[] args = {
            dirOut
        };
        CalibrationIndTestProbabilistic.main(args);
    }

}
