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
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "process",
        description = "Duplicate diagrams and processes in a Bonita project")
public class DuplicateProcessCommand implements Callable<Integer> {

    private final static Logger LOGGER = Logger.getLogger(DuplicateProcessCommand.class.getName());

    @ParentCommand
    private DuplicateCommand duplicateCommand;

    @Override
    public Integer call() throws Exception {
        return duplicateDiagrams();
    }

    private Integer duplicateDiagrams() {
        Path projectPath = duplicateCommand.getProject().toPath();
        Path diagramsPath = projectPath.resolve("diagrams");
        try {
            for (File diagram : diagramsPath.toFile().listFiles(file -> file.getName().endsWith(".proc"))) {
                LOGGER.info(() -> String.format("Duplicating %s...", diagram.getName()));
                duplicateDiagram(diagramsPath, diagram);
            }
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe(e.getMessage());
            return 1;
        }
    }

    private void duplicateDiagram(Path diagramsPath, File diagram) throws IOException {
        for (int i = 1; i <= duplicateCommand.getNumber(); i++) {
            String newName = diagram.getName().replace(".proc", "") + "_copy-" + i + ".proc";
            File copy = diagramsPath.resolve(newName).toFile();
            FileUtils.copyFile(diagram, copy);

            Stream<String> lines = Files.lines(copy.toPath());
            String num = Integer.toString(i);
            String diagramRegex = "(<process:MainProcess.*name=\\\")([a-zA-Z]+)(\\\".*>)";
            String processRegex = "(<elements.*xmi:type=\\\"process:Pool\\\".*name=\\\")(.+)(\\\".*>)";
            List<String> replaced = lines
                    .map(line -> line.replaceAll(diagramRegex, "$1$2" + num + "$3"))
                    .map(line -> line.replaceAll(processRegex, "$1$2" + num + "$3"))
                    .collect(Collectors.toList());
            Files.write(copy.toPath(), replaced);
            lines.close();
        }
    }
}
