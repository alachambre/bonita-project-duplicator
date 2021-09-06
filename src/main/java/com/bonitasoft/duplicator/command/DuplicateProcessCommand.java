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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "process",
        description = "Duplicate diagrams and processes in a Bonita project")
public class DuplicateProcessCommand extends DuplicateCommand {

    private final static Logger LOGGER = LoggerFactory.getLogger(DuplicateProcessCommand.class);

    private static final String DIAGRAMS_FOLDER = "diagrams";
    private static final String DIAGRAM_EXTENSION = ".proc";

    @ParentCommand
    private MainCommand parentCommand;

    @Override
    protected boolean cleanBeforeDuplicate() {
        return parentCommand.cleanBeforeDuplicate();
    }

    @Override
    protected int getNumberOfDuplicate() {
        return parentCommand.getNumber();
    }

    @Override
    protected Path getFolder() {
        return parentCommand.getProject().toPath().resolve(DIAGRAMS_FOLDER);
    }

    @Override
    protected String getFileExtension() {
        return DIAGRAM_EXTENSION;
    }

    @Override
    protected void updateDuplicatedFileContent(File duplicate, int iteration) throws IOException {
        Stream<String> lines = Files.lines(duplicate.toPath());
        String num = Integer.toString(iteration);
        String diagramRegex = "(<process:MainProcess.*name=\\\")([a-zA-Z]+)(\\\".*>)";
        String processRegex = "(<elements.*xmi:type=\\\"process:Pool\\\".*name=\\\")([^\\\"]+)(\\\".*>)";
        List<String> replaced = lines
                .map(line -> line.replaceAll(diagramRegex, "$1$2" + num + "$3"))
                .map(line -> line.replaceAll(processRegex, "$1$2" + num + "$3"))
                .collect(Collectors.toList());
        Files.write(duplicate.toPath(), replaced);
        lines.close();
    }

}
