package visualisation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import solution.SolutionThread;

import java.io.IOException;

public class VisualizationApplication extends Application {

    private VisualizationController controller;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(VisualizationApplication.class.getResource("/visualization.fxml"));


        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        this.controller = fxmlLoader.getController();
        stage.setTitle("Scheduling Algorithm");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setResizable(false);
        stage.show();
    }

    /**
     * This method sets up required fields in the controller object which is then shown on the GUI.
     * @param solutionThread the thread in which the solution is run. Used to communicate with the GUI.
     */
    public void setUpArgs(SolutionThread solutionThread, String inputFile, String outputFile) {
        controller.setUpArgs(solutionThread, inputFile, outputFile);
    }

    public static void main(String[] args) {
        launch();
    }
}