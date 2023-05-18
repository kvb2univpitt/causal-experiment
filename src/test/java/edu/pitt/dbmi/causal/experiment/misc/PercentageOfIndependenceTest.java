package edu.pitt.dbmi.causal.experiment.misc;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * May 4, 2023 3:03:20 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
@Disabled
public class PercentageOfIndependenceTest {

    /**
     * Test of main method, of class PercentageOfIndependence.
     */
    @Test
    public void testMain() throws Exception {
        String[] args = {
            this.getClass().getResource("/data/experiments/exp1/independence_test_data.csv").getFile()
        };
        PercentageOfIndependence.main(args);
    }

}
