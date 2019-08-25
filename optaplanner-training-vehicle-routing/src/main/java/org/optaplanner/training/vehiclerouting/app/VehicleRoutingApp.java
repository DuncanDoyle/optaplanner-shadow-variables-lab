/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import org.optaplanner.training.vehiclerouting.domain.Standstill;
import org.optaplanner.training.vehiclerouting.domain.Vehicle;
import org.optaplanner.training.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
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
        
        logFirstChain(solutionOne);

        File outputSolutionFileOne = new File("data/vehiclerouting/solved/cvrptw-100customers-A.xml");
        fileIO.write(solutionOne, outputSolutionFileOne);
    }


    private static void logFirstChain(TimeWindowedVehicleRoutingSolution solution) {
        //Grab the first anchor.
        Vehicle vehicle = solution.getVehicleList().stream().filter(v -> (getChainSize(v) >= 5)).findFirst().get();

        StringBuilder chainStringBuilder = new StringBuilder().append("Chain of vehicle: " + vehicle.getId());
        TimeWindowedCustomer nextCustomer = (TimeWindowedCustomer) vehicle.getNextCustomer();
        while (nextCustomer != null) {
            chainStringBuilder.append("\n- Customer: ").append(nextCustomer.getLocation());
            chainStringBuilder.append(",\t arrivalTime: ").append(nextCustomer.getArrivalTime());
            chainStringBuilder.append(",\t dueTime: ").append(nextCustomer.getDueTime());
            nextCustomer = nextCustomer.getNextCustomer();
        }

        LOGGER.debug(chainStringBuilder.toString());
    }

    private static int getChainSize(Vehicle vehicle) {
        int size = 0;
        Standstill standstill = (Standstill) vehicle.getNextCustomer();
        while(standstill != null) {
            standstill = (Standstill) standstill.getNextCustomer();
            size++;
        }
        return size;
    }
}
