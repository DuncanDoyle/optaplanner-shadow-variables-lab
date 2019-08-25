/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.training.vehiclerouting.app;

import java.io.File;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.optaplanner.training.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.training.vehiclerouting.domain.timewindowed.TimeWindowedVehicleRoutingSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VehicleRoutingApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleRoutingApp.class);

    public static final String SOLVER_CONFIG
            = "org/optaplanner/training/vehiclerouting/solver/vehicleRoutingSolverConfig.xml";

    public static final String DATA_DIR_NAME = "vehiclerouting";

    public static void main(String[] args) {
        
        SolverFactory<TimeWindowedVehicleRoutingSolution> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        
        File inputSolutionFileOne = new File("data/vehiclerouting/unsolved/cvrptw-100customers-A.xml");
        
        XStreamSolutionFileIO fileIO = new XStreamSolutionFileIO<>(TimeWindowedVehicleRoutingSolution.class);
        
        TimeWindowedVehicleRoutingSolution problemOne = (TimeWindowedVehicleRoutingSolution) fileIO.read(inputSolutionFileOne);
        
        Solver<TimeWindowedVehicleRoutingSolution> solver = solverFactory.buildSolver();
                
        TimeWindowedVehicleRoutingSolution solutionOne = solver.solve(problemOne);
        
        String scoreExplanation = solver.explainBestScore();
        LOGGER.debug("Finished solving. Explaining the score:");
        LOGGER.debug(scoreExplanation);
        
        File outputSolutionFileOne = new File("data/vehiclerouting/solved/cvrptw-100customers-A.xml");
        fileIO.write(solutionOne, outputSolutionFileOne);
    }

    /*
    public VehicleRoutingApp() {
        super("Vehicle routing",
                "Official competition name: Capacitated vehicle routing problem (CVRP), " +
                        "optionally with time windows (CVRPTW)\n\n" +
                        "Pick up all items of all customers with a few vehicles.\n\n" +
                        "Find the shortest route possible.\n" +
                        "Do not overload the capacity of the vehicles.\n" +
                        "Arrive within the time window of each customer.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                VehicleRoutingPanel.LOGO_PATH);
    }

    @Override
    protected VehicleRoutingPanel createSolutionPanel() {
        return new VehicleRoutingPanel();
    }

    @Override
    public SolutionFileIO<VehicleRoutingSolution> createSolutionFileIO() {
        return new XStreamSolutionFileIO<>(VehicleRoutingSolution.class);
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[]{
                new VehicleRoutingImporter()
        };
    }
    */

}
