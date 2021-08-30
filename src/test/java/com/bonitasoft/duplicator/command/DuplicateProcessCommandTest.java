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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DuplicateProcessCommandTest {

    private static final String PROJECT_NAME = "bonita-project";
    private static final String DIAGRAM_FOLDER = "diagrams";
    private static final String DIAGRAM_NAME = "MyDiagram-1.0.proc";
    private static final String DUPLICATED_DIAGRAM_NAME = "MyDiagram-1.0_copy-1.proc";

    private static Path projectPath;

    @BeforeAll
    public static void globalInit() throws Exception {
        var projectFolder = DuplicateProcessCommandTest.class.getClassLoader().getResource(PROJECT_NAME);
        projectPath = new File(projectFolder.toURI()).toPath();
    }

    @BeforeEach
    public void init() throws Exception {
        // We duplicate the diagram folder, so we only have to delete the duplicated folder in the clean operation
        var diagramFolder = projectPath.resolve(DIAGRAM_FOLDER);
        var duplicatedDiagramFolder = projectPath.resolve(DIAGRAM_FOLDER + "_duplicate");

        FileUtils.copyDirectory(diagramFolder.toFile(), duplicatedDiagramFolder.toFile());
    }

    @AfterEach
    public void clean() {
        var duplicatedDiagramFolder = projectPath.resolve(DIAGRAM_FOLDER + "_duplicate").toFile();
        Arrays.asList(duplicatedDiagramFolder.listFiles()).forEach(File::delete);
        duplicatedDiagramFolder.delete();
    }

    @Test
    public void should_duplicate_diagrams() throws Exception {
        var duplicateProcessCommand = new DuplicateProcessCommand();
        var diagramFolder = projectPath.resolve(DIAGRAM_FOLDER + "_duplicate");
        assertThat(diagramFolder.toFile().listFiles()).hasSize(1);

        duplicateProcessCommand.duplicateDiagram(diagramFolder, diagramFolder.resolve(DIAGRAM_NAME).toFile(), 1);
        assertThat(diagramFolder.toFile().listFiles()).hasSize(2);

        File duplicatedDiagramFile = diagramFolder.resolve(DUPLICATED_DIAGRAM_NAME).toFile();
        assertThat(duplicatedDiagramFile).exists();

        // Original:  [...] name="MyDiagram" [...]
        var newDiagramName = "<process:MainProcess xmi:id=\"_lEjEoAcUEey8o8GE9SBwWw\" name=\"MyDiagram1\" "
                + "author=\"adrien\" bonitaVersion=\"7.13.0\" bonitaModelVersion=\"7.12.0-004\">";
        try (Stream<String> lines = Files.lines(duplicatedDiagramFile.toPath())) {
            assertThat(lines.anyMatch(newDiagramName::equals));
        }

        // Original:  [...] name="FirstProcess" [...]
        var firstPoolNewName = "<elements xmi:type=\"process:Pool\" xmi:id=\"_lEjEoQcUEey8o8GE9SBwWw\" name=\"FirstProcess1\">";
        try (Stream<String> lines = Files.lines(duplicatedDiagramFile.toPath())) {
            assertThat(lines.anyMatch(firstPoolNewName::equals));
        }

        // Original:  [...] name="SecondProcess" [...]
        var secondPoolNewName = "<elements xmi:type=\"process:Pool\" xmi:id=\"_lEjEoQcUEey8o8GE9SBwWw\" name=\"SecondProcess1\">";
        try (Stream<String> lines = Files.lines(duplicatedDiagramFile.toPath())) {
            assertThat(lines.anyMatch(secondPoolNewName::equals));
        }
    }

}
