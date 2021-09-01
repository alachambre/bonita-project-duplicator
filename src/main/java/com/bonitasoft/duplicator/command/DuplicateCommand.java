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
import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DuplicateCommand implements Callable<Integer> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DuplicateCommand.class);

    protected static final String COPY_SUFFIX = "_generatedCopy";

    @Override
    public Integer call() throws Exception {
        if (cleanBeforeDuplicate() && !clean()) {
            return 1; // exit with error
        }
        return duplicate();
    }

    protected abstract Path getFolder();

    protected abstract String getFileExtension();

    protected abstract boolean cleanBeforeDuplicate();

    protected abstract int getNumberOfDuplicate();

    protected abstract void updateDuplicatedFileContent(File duplicate, int titeration) throws IOException;

    private boolean clean() {
        LOGGER.info("Starting clean operation...");
        Path folderPath = getFolder();
        LOGGER.info("----------");
        for (File file : folderPath.toFile().listFiles(file -> file.getName().contains(COPY_SUFFIX))) {
            LOGGER.info(String.format("Deleting '%s'...", file.getName()));
            if (!file.delete()) {
                LOGGER.error(String.format("File '%s' has not been deleted correctly.", file.getName()));
                return false;
            }
        }
        LOGGER.info("----------");
        LOGGER.info("Clean operation completed successfully!\n");
        return true;
    }

    private Integer duplicate() {
        Path folderPath = getFolder();
        try {
            for (File file : folderPath.toFile().listFiles(file -> file.getName().endsWith(getFileExtension()))) {
                LOGGER.info(String.format("Duplicating '%s'...", file.getName()));
                LOGGER.info("----------");
                for (int i = 1; i <= getNumberOfDuplicate(); i++) {
                    String newName = createNewName(file.getName(), i);
                    File copy = folderPath.resolve(newName).toFile();
                    FileUtils.copyFile(file, copy);
                    LOGGER.info(String.format("%s created, updating content...", copy.getName()));
                    updateDuplicatedFileContent(copy, i);
                }
                LOGGER.info("----------");
                LOGGER.info(String.format("'%s' has been successfully duplicated %s times!\n", file.getName(),
                        getNumberOfDuplicate()));
            }
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            return 1;
        }
    }

    private String createNewName(String oldName, int iteration) {
        String nameWithoutExtension = oldName.replace(getFileExtension(), "");
        return String.format("%s%s%s%s", nameWithoutExtension, COPY_SUFFIX, iteration, getFileExtension());
    }

}
