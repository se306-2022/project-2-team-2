# SOFTENG 306 Project 2
This project is about using artificial intelligence and parallel processing power to solve a difficult scheduling problem.

## Running the Project
Push a .dot file to the root directory of the project and run the command below. 
```
java -jar scheduler.jar <input_file.dot> <num_processors>
```
- `<input_file.dot>`: an input dot file containing the graph description.
- `<processors>`: an integer number of processors the schedule will be performed on.

## Acknowledgements
- Amy Rimmer (arim402@aucklanduni.ac.nz)
- Brendan Zhou (bz503@aucklanduni.ac.nz)
- Ellen Zhang (ezha122@aucklanduni.ac.nz)
- Ishaan Bhide (ibhi775@aucklanduni.ac.nz)
- Matthew Ouyang (wouy448@aucklanduni.ac.nz)
- Yuewen Zheng (azhe202@aucklanduni.ac.nz)


# Development
This section contains developer instructions to test and the code locally.

## Running JavaFX (IntelliJ)
If you want to run JavaFX for development purposes using IntelliJ IDE:
1. Download [JavaFX SDK](https://gluonhq.com/products/javafx/).
2. IntelliJ > Run > Edit configurations.
3. Add configuration targeting ```visualisation.VisualizationApplication```
3. Modify options > Add VM option.
4. Add the following line:
```bash
--module-path "<path/to/javafx_sdk>" --add-modules=javafx.controls,javafx.fxml
```
5. Run the configuration to test the JavaFX GUI.


