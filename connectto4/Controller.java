package com.internsala.connectto4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable{

     private static final int Columns = 7;
     private static final  int rows = 6 ;
     private  static final int circle_dim = 80 ;
     private static final String Discolor1 = "#24303E";
     private static  final String Discolor2 = "#4CAA88";

     private static  String Player_One = "Player one";
     private  static String Player_two = "Player Two";

     private Boolean isPlayerOne = true ;

     private Disc[][] insertedDiscArray = new Disc[rows][Columns]; //for Structural changes

    @FXML
    public GridPane rootgridpane ;
    @FXML
    public Pane inserteddiscpane ;

    @FXML
    public Label Playernamelable ;

    private  boolean isAllowedToInsert = true ;

    public void createPlayground(){

        Shape rectanglewithholes = createGameStructuralGrid();
        rootgridpane.add(rectanglewithholes,0,1);

        List<Rectangle> rectangleList = createClickableColums();
        for (Rectangle rectangle : rectangleList) {
            rootgridpane.add(rectangle, 0, 1);
        }
    }
    private  Shape createGameStructuralGrid(){

        Shape rectanglewithholes = new Rectangle((Columns+1)*circle_dim ,(rows+1)*circle_dim);

        for (int row = 0 ; row <rows ; row++) {
            for (int col = 0; col < Columns; col++) {
                Circle circle = new Circle();
                circle.setRadius(circle_dim / 2);
                circle.setCenterX(circle_dim / 2);
                circle.setCenterY(circle_dim / 2);
                circle.setSmooth(true);

                circle.setTranslateX(col*(circle_dim+5)+circle_dim/4);
                circle.setTranslateY(row*(circle_dim+5)+circle_dim/4);
                rectanglewithholes = Shape.subtract(rectanglewithholes, circle);
            }
        }
        rectanglewithholes.setFill(Color.WHITE);

        return rectanglewithholes;

    }

    private  List<Rectangle> createClickableColums() {
        List<Rectangle> rectangleList = new ArrayList<>();

        for (int col = 0; col < Columns; col++) {
            Rectangle rectangle = new Rectangle(circle_dim, (rows + 1) * circle_dim);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (circle_dim + 5) + circle_dim / 4);

            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.TRANSPARENT));

            final  int Column = col ;
            rectangle.setOnMouseClicked(event -> {
                if(isAllowedToInsert) {
                    isAllowedToInsert = false ; // when disc is being dropped then no more disc will be inserted
                    insertDisc(new Disc(isPlayerOne), Column);
                }
            });
            rectangleList.add(rectangle);
        }
        return rectangleList;
    }

    private void  insertDisc(Disc disc , int column){

        int row = rows - 1 ;
        while(row>=0){

            if(getDiscIfPresent(row,column)==null)
                break;

                row-- ;

        }

        if(row<0){    //if it is full , we cannot insert anymore disc
            return;
        }

        insertedDiscArray[row][column] = disc ;  // for structural changes : for devlopers

        inserteddiscpane.getChildren().add(disc); //for visual Changes : for players
        int currentRow = row ;
        disc.setTranslateX(column * (circle_dim + 5) + circle_dim / 4);
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
        translateTransition.setToY(row*(circle_dim+5)+circle_dim/4);

        translateTransition.setOnFinished(event -> {
            isAllowedToInsert = true ; //Finally , when disc is droped allow next player to nsert disc
            if(gameEnded(currentRow,column)){
                gameOver();
                return;

            }
            isPlayerOne =  !isPlayerOne ;

            Playernamelable.setText(isPlayerOne?Player_One:Player_two);
        });

        translateTransition.play();
    }

    private boolean gameEnded(int row , int columns){

    //Vertical points
      List<javafx.geometry.Point2D> verticalPoints =  IntStream.rangeClosed(row-3,row+3)
                .mapToObj(r->new javafx.geometry.Point2D(r,columns))
                .collect(Collectors.toList());

        List<javafx.geometry.Point2D> horizontalPoints =  IntStream.rangeClosed(columns-3,columns+3)
                .mapToObj(col->new javafx.geometry.Point2D(row,col))
                .collect(Collectors.toList());

        javafx.geometry.Point2D startPoint1 = new javafx.geometry.Point2D(row-3 , columns+3);
        List<javafx.geometry.Point2D> diagonalPoint1 = IntStream.rangeClosed(0,6)
                .mapToObj(i->startPoint1.add(i,-i))
                .collect(Collectors.toList());

        javafx.geometry.Point2D startPoint2 = new javafx.geometry.Point2D(row-3 , columns-3);
        List<javafx.geometry.Point2D> diagonalPoint2 = IntStream.rangeClosed(0,6)
                .mapToObj(i->startPoint2.add(i,i))
                .collect(Collectors.toList());



      boolean isEnded = checkCombinations(verticalPoints) ||checkCombinations(horizontalPoints) || checkCombinations(diagonalPoint1)||checkCombinations(diagonalPoint2);

        return isEnded;

    }

    private boolean checkCombinations(List<javafx.geometry.Point2D> points ) {
        int chain = 0 ;
        for (javafx.geometry.Point2D point : points) {

            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray , columnIndexForArray);

            if (disc != null && disc.isPlayerOneMove == isPlayerOne) {
                chain++;
                if (chain == 4) {
                    return true;
                }
            } else {
                chain = 0;
            }
        }
            return  false ;
        }

        private Disc getDiscIfPresent(int row , int colums){
           if(row >= rows|| row < 0 || colums >=Columns || colums<0)
               return null;

           return insertedDiscArray[row][colums];

        }



    private void gameOver(){
     String winner = isPlayerOne ? Player_One : Player_two ;
        System.out.println("Winner is :"+winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("The Winner is :"+ winner);
        alert.setContentText("Want to play again ?");

        ButtonType yesBtn = new ButtonType("yes");
        ButtonType noBtn = new ButtonType("No , Exit");
        alert.getButtonTypes().setAll(yesBtn , noBtn);

        Platform.runLater(()->{
            Optional<ButtonType> btnClicked = alert.showAndWait();

            if(btnClicked.isPresent()&& btnClicked.get() == yesBtn){

                resetGame();
            }else{
                Platform.exit();
                System.exit(0);
            }
        });

    }

    public void resetGame() {

        inserteddiscpane.getChildren().clear(); //remove all inserted disc  from pane

        for(int row= 0 ; row<insertedDiscArray.length;row++){

            for(int col = 0 ; col < insertedDiscArray[row].length ; col++){
                insertedDiscArray[row][col] =null;
            }
        }

        isPlayerOne = true ; //let player start the game
        Playernamelable.setText(Player_One);

        createPlayground(); // prepare a fresh playground

    }

    private  static  class Disc extends Circle{
        private final  boolean isPlayerOneMove ;
        public Disc(boolean isPlayerOneMove){
            this.isPlayerOneMove = isPlayerOneMove ;
            setRadius(circle_dim/2);

            setFill(isPlayerOneMove?Color.valueOf(Discolor1):Color.valueOf(Discolor2));
            setCenterX(circle_dim/2);
            setCenterY(circle_dim/2);
        }

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

