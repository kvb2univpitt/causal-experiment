package edu.pitt.dbmi.causal.experiment;

import edu.pitt.dbmi.causal.experiment.util.FileIO;
import java.nio.file.Path;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * Sep 13, 2023 1:16:19 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
@Disabled
public class AdjustingCalibrationAppTest {

    @TempDir
    public static Path tempDir;

    /**
     * Test of main method, of class AdjustingCalibrationApp.
     */
    @Test
    public void testMain() throws Exception {
        String dirOut = FileIO.createSubdirectory(tempDir, "adjust_calibration_independence").toString();
        String dataFile = getDataFile();

        String[] args = {
            dataFile,
            dirOut
        };
        AdjustingCalibrationApp.main(args);
    }

    private String getDataFile() {
        return AdjustingCalibrationAppTest.class
                .getResource("/adjust_calibration/independence_test_data.csv").getFile();
    }

}
