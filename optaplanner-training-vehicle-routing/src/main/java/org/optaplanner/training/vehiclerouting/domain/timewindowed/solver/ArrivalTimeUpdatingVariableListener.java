/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.training.vehiclerouting.domain.timewindowed.solver;

import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.training.vehiclerouting.domain.Customer;
import org.optaplanner.training.vehiclerouting.domain.Standstill;
import org.optaplanner.training.vehiclerouting.domain.Vehicle;
import org.optaplanner.training.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.training.vehiclerouting.domain.timewindowed.TimeWindowedDepot;

// TODO When this class is added only for TimeWindowedCustomer, use TimeWindowedCustomer instead of Customer
public class ArrivalTimeUpdatingVariableListener implements VariableListener<Customer> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Customer customer) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Customer customer) {
        if (customer instanceof TimeWindowedCustomer) {
            updateArrivalTime(scoreDirector, (TimeWindowedCustomer) customer);
        }
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Customer customer) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Customer customer) {
        if (customer instanceof TimeWindowedCustomer) {
            updateArrivalTime(scoreDirector, (TimeWindowedCustomer) customer);
        }
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Customer customer) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Customer customer) {
        // Do nothing
    }

    protected void updateArrivalTime(ScoreDirector scoreDirector, TimeWindowedCustomer sourceCustomer) {
        
    }

    private Long calculateArrivalTime(TimeWindowedCustomer customer, Long previousDepartureTime) {
        return null;
    }

}
