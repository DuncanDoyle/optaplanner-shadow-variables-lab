/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.training.vehiclerouting.persistence;

import java.io.File;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractSolutionExporter<Solution_> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSolutionExporter.class);

    public abstract String getOutputFileSuffix();

    public abstract void writeSolution(Solution_ solution, File outputFile);

    public static abstract class OutputBuilder {

    }

}
