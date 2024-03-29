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

package org.optaplanner.training.vehiclerouting.domain.solver;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.training.vehiclerouting.domain.Customer;


/**
 * On large datasets, the constructed solution looks like a zebra crossing.
 */
public class LatitudeCustomerDifficultyComparator implements Comparator<Customer>, Serializable {

    @Override
    public int compare(Customer a, Customer b) {
        return new CompareToBuilder()
                .append(a.getLocation().getLatitude(), b.getLocation().getLatitude())
                .append(a.getLocation().getLongitude(), b.getLocation().getLongitude())
                .append(a.getDemand(), b.getDemand())
                .append(a.getId(), b.getId())
                .toComparison();
    }

}
