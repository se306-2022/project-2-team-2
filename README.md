# SOFTENG 306 Project 2
This project is about using artificial intelligence and parallel processing power to solve a difficult scheduling problem.

## Running the Project
Push a .dot file to the root directory of the project and run the command below. 
```
java -jar scheduler.jar INPUT.dot P [OPTION]
```
- `INPUT.dot`: an input dot file containing the graph description
- `P`: an integer number of processors the schedule will be performed on

Optional:
- `-p N`: use N cores for execution in parallel (default is sequential)
- `-v`: run visualisation
- `-o OUTPUT`: output file is named OUTPUT (default is INPUT-output.dot)

## Acknowledgements
- Amy Rimmer (arim402@aucklanduni.ac.nz)
- Brendan Zhou (bz503@aucklanduni.ac.nz)
- Ellen Zhang (ezha122@aucklanduni.ac.nz)
- Ishaan Bhide (ibhi775@aucklanduni.ac.nz)
- Matthew Ouyang (wouy448@aucklanduni.ac.nz)
- Yuewen Zheng (azhe202@aucklanduni.ac.nz)

## Running JavaFX (IntelliJ)
If you want to run JavaFX for development purposes using IntelliJ IDE:
1. Download [JavaFX SDK](https://gluonhq.com/products/javafx/).
2. IntelliJ > Run > Edit configurations.
3. Add configuration.
3. Modify options > Add VM option.
4. Add the following line:
```bash
--module-path "<path/to/javafx_sdk>" --add-modules=javafx.controls,javafx.fxml
```
5. Run the configuration to test the JavaFX GUI.


