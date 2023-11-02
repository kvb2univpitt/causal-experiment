package edu.pitt.dbmi.causal.experiment;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * Oct 4, 2023 1:38:37 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
@Disabled
public class SimpleCalibratingIndependenceTestTest {

    /**
     * Test of main method, of class SimpleCalibratingIndependenceTest.
     */
    @Test
    public void testMain() {
        String graphFile = getGraphFile();
        String dirOut = "/home/kvb2/shared/tmp/causal_experiments_2";
        String[] args = {
            graphFile,
            dirOut
        };
        SimpleCalibratingIndependenceTest.main(args);
    }

    private String getGraphFile() {
        return SimpleCalibratingIndependenceTestTest.class
                .getResource("/simple_calibrating_indepedence_test/graph_xyz.txt").getFile();
//        return SimpleCalibratingIndependenceTestTest.class
//                .getResource("/simple_calibrating_indepedence_test/graph_xy.txt").getFile();
//        return SimpleCalibratingIndependenceTestTest.class
//                .getResource("/simple_calibrating_indepedence_test/graph_xyz_chain.txt").getFile();
    }

}
