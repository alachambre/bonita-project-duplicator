package com.bonitasoft.duplicator;

import com.bonitasoft.duplicator.command.DuplicateCommand;

import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new DuplicateCommand()).execute(args);
        System.exit(exitCode);
    }

}
