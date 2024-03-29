/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
import java.util.Arrays;

import org.optaplanner.core.api.domain.solution.PlanningSolution;


import org.optaplanner.training.vehiclerouting.business.ProblemFileComparator;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class SolutionConverter<Solution_> {

    private static final Logger logger = LoggerFactory.getLogger(SolutionConverter.class);

    public static <Solution_> SolutionConverter<Solution_> createImportConverter(String dataDirName,
            AbstractSolutionImporter<Solution_> importer, Class<Solution_> solutionClass) {
        SolutionFileIO<Solution_> inputSolutionFileIO = new SolutionFileIO<Solution_>() {
            @Override
            public String getInputFileExtension() {
                return importer.getInputFileSuffix();
            }

            @Override
            public Solution_ read(File inputSolutionFile) {
                return importer.readSolution(inputSolutionFile);
            }

            @Override
            public void write(Solution_ solution_, File outputSolutionFile) {
                throw new UnsupportedOperationException();
            }
        };
        XStreamSolutionFileIO<Solution_> outputSolutionFileIO = new XStreamSolutionFileIO<>(solutionClass);
        return new SolutionConverter<>(dataDirName,
                inputSolutionFileIO, "import", importer.isInputFileDirectory(),
                outputSolutionFileIO, "unsolved");
    }

    public static <Solution_> SolutionConverter<Solution_> createExportConverter(String dataDirName,
            Class<Solution_> solutionClass, AbstractSolutionExporter<Solution_> exporter) {
        XStreamSolutionFileIO<Solution_> inputSolutionFileIO = new XStreamSolutionFileIO<>(solutionClass);
        SolutionFileIO<Solution_> outputSolutionFileIO = new SolutionFileIO<Solution_>() {
            @Override
            public String getInputFileExtension() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getOutputFileExtension() {
                return exporter.getOutputFileSuffix();
            }

            @Override
            public Solution_ read(File inputSolutionFile) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void write(Solution_ solution, File outputSolutionFile) {
                exporter.writeSolution(solution, outputSolutionFile);
            }
        };
        return new SolutionConverter<>(dataDirName,
                inputSolutionFileIO, "solved", false,
                outputSolutionFileIO, "export");
    }

    protected SolutionFileIO<Solution_> inputSolutionFileIO;
    protected final File inputDir;
    protected final boolean inputFileIsDirectory;
    protected SolutionFileIO<Solution_> outputSolutionFileIO;
    protected final File outputDir;

    private SolutionConverter(String dataDirName,
            SolutionFileIO<Solution_> inputSolutionFileIO, String inputDirName, boolean inputFileIsDirectory,
            SolutionFileIO<Solution_> outputSolutionFileIO, String outputDirName) {
        this.inputSolutionFileIO = inputSolutionFileIO;
        this.inputFileIsDirectory = inputFileIsDirectory;
        this.outputSolutionFileIO = outputSolutionFileIO;
        //File dataDir = CommonApp.determineDataDir(dataDirName);
        File dataDir = determineDataDir(dataDirName);
        inputDir = new File(dataDir, inputDirName);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            throw new IllegalStateException("The directory inputDir (" + inputDir.getAbsolutePath()
                    + ") does not exist or is not a directory.");
        }
        outputDir = new File(dataDir, outputDirName);
    }

    public void convertAll() {
        File[] inputFiles = inputDir.listFiles();
        if (inputFiles == null) {
            throw new IllegalStateException("Unable to list the files in the inputDirectory ("
                    + inputDir.getAbsolutePath() + ").");
        }
        Arrays.sort(inputFiles, new ProblemFileComparator());
        for (File inputFile : inputFiles) {
            if (acceptInputFile(inputFile)) {
                convert(inputFile);
            }
        }
    }

    public boolean acceptInputFile(File inputFile) {
        if (inputFileIsDirectory) {
            return inputFile.isDirectory();
        }
        return inputFile.getName().endsWith("." + inputSolutionFileIO.getInputFileExtension());
    }

    public void convert(String inputFileName) {
        String outputFileName = inputFileName.substring(0,
                inputFileName.length() - inputSolutionFileIO.getInputFileExtension().length())
                + outputSolutionFileIO.getOutputFileExtension();
        convert(inputFileName, outputFileName);
    }

    public void convert(String inputFileName, String outputFileName) {
        File inputFile = new File(inputDir, inputFileName);
        if (!inputFile.exists()) {
            throw new IllegalStateException("The file inputFile (" + inputFile.getAbsolutePath()
                    + ") does not exist.");
        }
        File outputFile = new File(outputDir, outputFileName);
        outputFile.getParentFile().mkdirs();
        convert(inputFile, outputFile);
    }

    public void convert(File inputFile) {
        String inputFileName = inputFile.getName();
        String outputFileName = inputFileName.substring(0,
                inputFileName.length() - inputSolutionFileIO.getInputFileExtension().length())
                + outputSolutionFileIO.getOutputFileExtension();
        File outputFile = new File(outputDir, outputFileName);
        convert(inputFile, outputFile);
    }

    protected void convert(File inputFile, File outputFile) {
        Solution_ solution = inputSolutionFileIO.read(inputFile);
        outputSolutionFileIO.write(solution, outputFile);
        logger.info("Saved: {}", outputFile);
    }


    public static final String DATA_DIR_SYSTEM_PROPERTY = "org.optaplanner.examples.dataDir";

    public static File determineDataDir(String dataDirName) {
        String dataDirPath = System.getProperty(DATA_DIR_SYSTEM_PROPERTY, "data/");
        File dataDir = new File(dataDirPath, dataDirName);
        if (!dataDir.exists()) {
            throw new IllegalStateException("The directory dataDir (" + dataDir.getAbsolutePath()
                    + ") does not exist.\n" +
                    " Either the working directory should be set to the directory that contains the data directory" +
                    " (which is not the data directory itself), or the system property "
                    + DATA_DIR_SYSTEM_PROPERTY + " should be set properly.\n" +
                    " The data directory is different in a git clone (optaplanner/optaplanner-examples/data)" +
                    " and in a release zip (examples/sources/data).\n" +
                    " In an IDE (IntelliJ, Eclipse, NetBeans), open the \"Run configuration\""
                    + " to change \"Working directory\" (or add the system property in \"VM options\").");
        }
        return dataDir;
    }
}
