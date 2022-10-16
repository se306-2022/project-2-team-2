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

- [GraphStream](https://graphstream-project.org/)

GraphStream is a Java library for the modeling and analysis of dynamic graphs, it also handles the reading/writing to dot files, hence why this library was chosen.

All methods to do with IO parsing are extracted into the `IOParser.class`. This class purely handles the reading of a dot file to convert into a graph and converting a graph into a dot file.
