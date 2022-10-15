## CLI and I/O

- Accept arguments (e.g. number of processors) from the command line.
- Read information of an input dot file of a specified name and store it in the application.
- Write information from a data structure representing a valid schedule into a dot file.

## Solution for CLI

- [Commons CLI](https://commons.apache.org/proper/commons-cli/) was used to accept command line arguments.

The Apache Commons CLI are the components of the Apache Commons which are derived from Java API and provides an API to 
parse command line arguments/options which are passed to the programs. 

All the methods to do with Command Line Parsing are extracted into the `CLIParser.class`.

## Solution for I/O