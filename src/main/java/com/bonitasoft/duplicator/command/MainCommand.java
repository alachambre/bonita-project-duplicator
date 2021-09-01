/**
 * Copyright (C) 2021 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.bonitasoft.duplicator.command;

import java.io.File;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@Command(
        name = "duplicate",
        description = "Duplicate an artifact of a Bonita project",
        subcommands = {
                DuplicateProcessCommand.class
        // DuplicateFormCommand.class
        })
public class MainCommand implements Callable<Integer> {

    @Spec
    CommandSpec spec;

    private File project;
    private int number;

    @Option(names = { "-c", "--clean" }, description = "Clean existing duplicates before to perform duplications")
    private boolean clean;

    @Override
    public Integer call() throws Exception {
        return 0; // OK
    }

    @Option(names = { "-p", "--project" }, description = "Path to the Bonita project to duplicate")
    public void setProject(File project) {
        if (!project.exists() && !project.isDirectory()) {
            throw new ParameterException(spec.commandLine(),
                    String.format("Invalid value '%s' for option '--project': " +
                            "A valid Bonita project folder is expected.", project.getAbsolutePath()));
        }
        this.project = project;
    }

    @Option(names = { "-n", "--number" }, description = "Number of duplication")
    public void setNumber(int number) {
        if (number <= 0) {
            throw new ParameterException(spec.commandLine(),
                    String.format("Invalid value '%s' for option '--number': " +
                            "A positive integer is expected.", number));
        }
        this.number = number;
    }

    public File getProject() {
        return project;
    }

    public int getNumber() {
        return number;
    }

    public boolean cleanBeforeDuplicate() {
        return clean;
    }
}
