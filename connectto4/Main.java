package com.internsala.connectto4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application {

    private Controller controller ;
    @Override
    public void start(Stage primaryStage) throws Exception{
       FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootgridpane = loader.load();

        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createmenu() ;
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

      Pane menupane = (Pane) rootgridpane.getChildren().get(0);
      menupane.getChildren().add(menuBar);
        Scene scene = new Scene(rootgridpane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four Game");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    private  MenuBar createmenu(){

     //File menu
        Menu filemenu = new Menu("File");
        MenuItem newgame = new MenuItem("New game");
        newgame.setOnAction(event -> controller.resetGame());
        MenuItem  resetgame = new MenuItem("Reset Game");
        resetgame.setOnAction(event -> controller.resetGame());
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem  exitgame = new MenuItem("Exit game");
        exitgame.setOnAction(event -> exitgame());

        filemenu.getItems().addAll(newgame , resetgame , separatorMenuItem,exitgame);

        //help menu
        Menu Helpmenu = new Menu("Help");
        MenuItem aboutGame = new MenuItem("About Game");
        aboutGame.setOnAction(event -> aboutconnetGame());
        SeparatorMenuItem separatorMenuItems = new SeparatorMenuItem();
        MenuItem  AboutMe = new MenuItem("About Me");
        AboutMe.setOnAction(event ->aboutme() );
        Helpmenu.getItems().addAll(aboutGame , separatorMenuItems, AboutMe);



        MenuBar menubar = new MenuBar();
        menubar.getMenus().addAll(filemenu,Helpmenu);


        return menubar ;
    }

    private void resetgame() {
    }

    private void aboutme() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About The Developer ");
        alert.setHeaderText("Rishabh Bhatt");
        alert.setContentText("I  am Developer and i  love to play around with code and create games");
        alert.show();
    }

    private void aboutconnetGame() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Game ");
        alert.setHeaderText("How to play");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing the right moves.");
        alert.show();
    }

    private void exitgame() {
        Platform.exit();
        System.exit(0);
        System.out.println("exit game");
    }

    }



