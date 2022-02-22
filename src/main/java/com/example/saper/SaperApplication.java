package com.example.saper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SaperApplication extends Application {

    private static final int WIDTH = 520;
    private static final int HEIGHT = 565;


    public static Stage StageApp;

    public static void SetSizeStage(int width, int height){
        StageApp.setMinHeight(height);
        StageApp.setMaxHeight(height);

        StageApp.setMinWidth(width);
        StageApp.setMaxWidth(width);
    }

    @Override
    public void start(Stage stage) throws IOException {
        StageApp = stage;
        SetSizeStage(WIDTH, HEIGHT);
        FXMLLoader fxmlLoader = new FXMLLoader(SaperApplication.class.getResource("MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Сапёр!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}