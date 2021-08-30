[![Actions Status](https://github.com/alachambre/bonita-project-duplicator/workflows/Build/badge.svg)](https://github.com/alachambre/bonita-project-duplicator/actions?query=workflow%3ABuild)

! **- This project is in progress -**

# bonita-project-duplicator

Duplicate content in a Bonita project to generate fat projects, for performance testing

## Build the project

`./mvnw clean package`

-> the Jar _bonita-project-duplicator-\<version\>-jar-with-dependencies.jar_ is created in the target folder

## Usage

**Only process duplication is supported for now.**

`java -jar bonita-project-duplicator-<VERSION>-jar-with-dependencies.jar -p <PATH TO PROJECT> -n <NUMBER OF DUPLICATE> process`

ex:
  
`java -jar bonita-project-duplicator-1.0.0-SNAPSHOT-jar-with-dependencies.jar -p BonitaStudioCommunity-2021.2-SNAPSHOT.app/Contents/Eclipse/workspace/My\ project -n 5 process`
