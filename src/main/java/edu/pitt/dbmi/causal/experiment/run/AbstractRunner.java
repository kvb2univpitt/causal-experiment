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
package edu.pitt.dbmi.causal.experiment.run;

import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.experiment.data.SimulatedData;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

/**
 *
 * Mar 22, 2023 10:34:35 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public abstract class AbstractRunner {

    protected static final int NUM_THREADS = 10;

    protected static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss");

    protected final SimulatedData simulatedData;
    protected final Parameters parameters;

    public AbstractRunner(SimulatedData simulatedData, Parameters parameters) {
        this.simulatedData = simulatedData;
        this.parameters = parameters;
    }

    public abstract void run(Path parentOutDir) throws Exception;

    protected String getParameterValue(Parameters parameters, String name) {
        String paramValue = String.valueOf(parameters.get(name));
        if (paramValue.equals("true")) {
            paramValue = "Yes";
        } else if (paramValue.equals("false")) {
            paramValue = "No";
        }

        return paramValue;
    }

}
