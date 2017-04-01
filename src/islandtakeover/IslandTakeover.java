/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package islandtakeover;

import java.awt.Point;
import java.text.Format;
import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.PURPLE;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author asdas
 */
public class IslandTakeover extends Application {
    AnimationTimer timer;
    TileGenerator tg = new TileGenerator();
    ArrayList<Tile> tiles = tg.getTiles();
    ArrayList<Tile> rem_tiles;
    boolean owner_selected = false;
    Tile selected, enemy;
    Tile cur;
    boolean enemy_selected = false;
    int player = 1;
    int p1_val = 0, p2_val = 0;
    int sum;
    int dist_dice = 0;
    int max_dice = 0;
    int randHold = 0;
    Text playerDisplay = new Text();
    Text tile1 = new Text();
    Text tile2 = new Text();
    HBox attack_display = new HBox();
    Text attack_txt1 = new Text("0");
    Text attack_txt2 = new Text("0");
    Image purple_win = new Image("file:Resource/crossed-swords-purple.png", 50, 50, true, true);
    Image green_win = new Image("file:Resource/crossed-swords-green.png", 50, 50, true, true);
    ImageView swords = new ImageView(purple_win);
    Pane root;
    int win = -1;
    
    Button attack;
    Button end_turn;
    Text winner = new Text("Player " + win + " victory!");
    Button ret = new Button("Return to Main Menu");
    
    STATE cur_state = STATE.START;
    
    public Parent initRoot(){
        root = new Pane();
        root.getChildren().add(initTileCounterSq(1));
        root.getChildren().add(initTileCounterSq(2));
        root.getChildren().add(initSetup());
        //drawing the rectangle outline
        Rectangle outline = new Rectangle(1038, 518);
        outline.setFill(Color.LIGHTBLUE);
        outline.setStroke(Color.BLACK);
        outline.setStrokeWidth(2);
        outline.setTranslateX(140);
        outline.setTranslateY(50);
        root.getChildren().add(outline);
        
        tile1.setFont(Font.font("Courier New", 20));
        tile1.setFill(Color.WHITE);
        tile1.setTranslateX(262);
        tile1.setTranslateY(600);
        tile2.setFont(Font.font("Courier New", 20));
        tile2.setFill(Color.WHITE);
        tile2.setTranslateX(335);
        tile2.setTranslateY(600);
        updateTileCountNum();
        updateTileCountNum();
        
        attack_txt1.setFont(Font.font("Courier New", 50));
        attack_txt2.setFont(Font.font("Courier New", 50));
        attack_display.setPrefSize(300, 100);
        attack_display.setTranslateX(490);
        attack_display.setTranslateY(590);
        attack_display.getChildren().addAll(attack_txt1, swords, attack_txt2);
        root.getChildren().add(attack_display);
        attack_display.setVisible(false);
        
        //adds the tiles to the root
        drawTiles();
        tileButtons();
        
        initPlayerText();
        root.getChildren().add(playerDisplay);
        root.getChildren().add(tile1);
        root.getChildren().add(tile2);
        root.getChildren().add(initEndTurnButton());
        root.getChildren().add(initAttackButton());
        
        

        return root;
    }
    
    public Parent initStart(){
        Pane start_screen = new Pane();
        VBox screen = new VBox();
        screen.setPrefSize(1280, 720);
        screen.setAlignment(Pos.CENTER);
        Text title = new Text("Island Takeover");
        title.setFont(Font.font("Courier New", 70));
        
        Button start_game = new Button("Start Game");
        start_game.setOnAction(e -> {
            cur_state = STATE.GAME;
        });
        
        screen.getChildren().addAll(title, start_game);
        start_screen.getChildren().add(screen);
        return start_screen;
    }
    
    VBox setup = new VBox();
    HBox choices = new HBox();
    HBox slider_box = new HBox();
    Text size = new Text("Map Size");
    Text prompt = new Text("Keep this map?");
    Slider map_size = new Slider(20, 100, 50);
    
    public VBox initSetup(){
        
        
        slider_box.setPrefSize(1280, 10);
        slider_box.setAlignment(Pos.BOTTOM_CENTER);
        
        map_size.setPrefWidth(200);
        map_size.setTranslateY(-25);
        map_size.valueProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                tg.setMaxTiles((int)map_size.getValue());
            }
        });

        setup.setPrefSize(1280, 720);
        setup.setAlignment(Pos.BOTTOM_CENTER);
        
        size.setTranslateX(-20);
        size.setTranslateY(-20);
        size.setFont(Font.font("Courier New", 20));
        
        prompt.setFont(Font.font("Courier New", 20));
        prompt.setTranslateY(-50);
        
        choices.setPrefSize(1280, 50);
        choices.setAlignment(Pos.BOTTOM_CENTER);
        
        Button yes = new Button("Yes");
        yes.setTranslateY(-50);
        yes.setTranslateX(-25);
        yes.setOnAction(e -> {
            attack.setVisible(true);
            end_turn.setVisible(true);
            playerDisplay.setVisible(true);
            choices.setVisible(false);
            prompt.setVisible(false);
            setup.setVisible(false);
        });
        
        Button no = new Button("No");
        no.setTranslateY(-50);
        no.setTranslateX(25);
        no.setOnAction(e -> {
            removeTiles();
            tg.regenerateGrid();
            tiles = tg.getTiles();
            drawTiles();
            tileButtons();
            updateTileCountNum();
        });
        
        choices.getChildren().addAll(yes, no);
        slider_box.getChildren().addAll(size, map_size);
        setup.getChildren().addAll(prompt, choices, slider_box);
        return setup;
    }
    
    public Parent initOver(){
        Pane over_screen = new Pane();
        ret.setOnAction(e-> {
            reset();
            
        });
        
        VBox elements = new VBox();
        elements.setPrefSize(1280, 720);
        elements.setAlignment(Pos.CENTER);
        
        elements.getChildren().addAll(winner, ret);
        over_screen.getChildren().add(elements);
        
        return over_screen;
    }
    
    public void removeTiles(){
        for(Tile t : tiles){
            root.getChildren().remove(t.getSquare());
            root.getChildren().remove(t.getDiceText());
        }
        tiles.clear();
    }
    
    public void drawTiles(){
        for(Tile t : tiles){
            root.getChildren().add(t.getSquare());
            root.getChildren().add(t.getDiceText());
        }
    }
    
    public int diceRoll(int num_dice){
        sum = 0;
        for(int i = 0; i < num_dice; i++){
            sum += (int)(Math.random() * 10000) % 6 + 1;
        }
        
        return sum;
    }
    
    public void compareRolls(int p1, int p2){
        if(p1 > p2){
            enemy.setOwner(player);
            enemy.setDiceVal(selected.getDiceVal() - 1);
            selected.setDiceVal(1);
            colorReset(enemy);
            colorReset(selected);
            tg.incrementOwnedTiles(player);
            updateTileCountNum();
            //VICTORY CONDITION
            if(isVictory(player)){
                cur_state = STATE.OVER;
                win = player;
                winner.setFont(Font.font("Courier New", 70));
                winner.setFill(Color.GREEN);
                winner.setText("Player " + win + " Victory!");
            }
            attack_txt1.setFill(Color.GREEN);
            attack_txt2.setFill(Color.RED);
            if(player == 1)
                swords.setImage(purple_win);
            else
                swords.setImage(green_win);
        }
        
        if(p1 == p2){
            selected.setDiceVal(1);
            colorReset(enemy);
            colorReset(selected);
            if(player == 1)
                swords.setImage(green_win);
            else
                swords.setImage(purple_win);
            
            attack_txt1.setFill(Color.RED);
            attack_txt2.setFill(Color.GREEN);
        }
            
        if(p2 > p1){
            selected.setDiceVal(1);
            colorReset(enemy);
            colorReset(selected);
            if(player == 1)
                swords.setImage(green_win);
            else
                swords.setImage(purple_win);
            
            attack_txt1.setFill(Color.RED);
            attack_txt2.setFill(Color.GREEN);
        }
    }
    
    public void colorReset(Tile t){
        if(t.getOwner() == player){
            if(player == 1)
                t.getSquare().setFill(Color.PURPLE);
            else
               t.getSquare().setFill(Color.GREEN); 
        }
        else{
            if(t.getOwner() == 1)
                t.getSquare().setFill(Color.PURPLE);
            else
               t.getSquare().setFill(Color.GREEN);
        }
    }
    
    public Button initAttackButton(){
        attack = new Button("Attack");
        attack.setVisible(false);
        attack.setTranslateX(1020);
        attack.setTranslateY(580);
        attack.setOnAction(e -> {
            if(enemy_selected && owner_selected){
                p1_val = diceRoll(selected.getDiceVal());
                p2_val = diceRoll(enemy.getDiceVal());
                compareRolls(p1_val, p2_val);
                colorReset(enemy);
                colorReset(selected);
                enemy = null;
                selected = null;
                enemy_selected = false;
                owner_selected = false;
                displayAttack(p1_val, p2_val);
            }
        });
        
        return attack;
    }

    public Button initEndTurnButton(){
        end_turn = new Button("End Turn");
        end_turn.setVisible(false);
        end_turn.setTranslateX(1100);
        end_turn.setTranslateY(580);
        end_turn.setOnAction(e -> { 
            dist_dice = 3 + (int)(tg.getOwnedTiles(player) * 0.5);
            max_dice = tg.getOwnedTiles(player) * 8;
            if(owner_selected == true && enemy_selected != true){
                colorReset(selected);
                enemy = null;
                selected = null;
                enemy_selected = false;
                owner_selected = false;
            }
            
            if(owner_selected == true && enemy_selected == true){
                colorReset(selected);
                colorReset(enemy);
                enemy = null;
                selected = null;
                enemy_selected = false;
                owner_selected = false;
            }
            
            if(player == 1){
                player = 2;
                playerDisplay.setText("Player 2 Turn"); 
                turnDice(1);
            }
            else{
                player = 1;
                playerDisplay.setText("Player 1 Turn");
                turnDice(2);
            }
            attack_display.setVisible(false);
        });
        return end_turn;
    }
    
    public void tileButtons(){
        for(Tile t : tiles){
            t.getSquare().setOnMouseClicked(e -> {
                if(t.getOwner() == player && !owner_selected && t.getDiceVal() > 1 && attack.isVisible()){
                    owner_selected = true;
                    selected = t;
                    t.getSquare().setFill(Color.RED);
                }
                else if(t.getOwner() == player && owner_selected
                        && ((selected.x == t.getX() && selected.y == t.getY())) && attack.isVisible()){
                    owner_selected = false;
                    colorReset(t);
                    selected = null;
                    
                    if(enemy_selected){
                        enemy_selected = false;
                        colorReset(enemy);
                        enemy = null;
                    }
                }
                
                if(t.getOwner() != player && owner_selected && t.isAdjacent(selected.x, selected.y) && !enemy_selected && attack.isVisible()){
                    enemy_selected = true;
                    enemy = t;
                    t.getSquare().setFill(Color.BLUE);
                }
                else if(t.getOwner() != player && enemy_selected
                        && ((enemy.x == t.getX() && enemy.y == t.getY())) && attack.isVisible()){
                    enemy_selected = false;
                    colorReset(t);
                    enemy = null;
                }
            });
        }
    }
    
    public void turnDice(int o){
        if(tg.getDiceTotal(o) + dist_dice > max_dice){
            dist_dice =  dist_dice - ((tg.getDiceTotal(o) + dist_dice) - max_dice);
        }
        
        while(dist_dice > 0){
           cur = tiles.get((int)(Math.random() * 10000) % tiles.size());
           if(cur.getOwner() == o && cur.getDiceVal() < 8){
               randHold = (int)(Math.random() * 10000) % 3 + 1;
               if(cur.getDiceVal() + randHold < 8){
                    cur.setDiceVal(cur.getDiceVal() + randHold);
                    dist_dice -= randHold;
               }
               else{
                   cur.setDiceVal(cur.getDiceVal() + 1);
                   dist_dice--;
               } 
           }
        }
    }
    
    public boolean isVictory(int o){
        System.out.println(tg.getMaxTiles() + " " + tg.getOwnedTiles(o));
        if(tg.getMaxTiles() == tg.getOwnedTiles(o))
            return true;
        return false;
    }
    
    public Rectangle initTileCounterSq(int owner){
        Rectangle r = new Rectangle(50, 50);
        
        if(owner == 1){
            r.setFill(Color.PURPLE);
            r.setTranslateX(250);
            r.setTranslateY(568);
        }
        else{
            r.setFill(Color.GREEN);
            r.setTranslateX(320);
            r.setTranslateY(568);
        }
        
        return r;
    }
    
    public void reset(){
        cur_state = STATE.START;
        
        //resetting the map
        removeTiles();
        tg.regenerateGrid();
        tiles = tg.getTiles();
        drawTiles();
        tileButtons();
        updateTileCountNum();
        
        //set visible
        attack.setVisible(false);
        end_turn.setVisible(false);
        playerDisplay.setVisible(false);
        choices.setVisible(true);
        prompt.setVisible(true);
        setup.setVisible(true);
        slider_box.setVisible(true);
        attack_display.setVisible(false);
        
        if(win == 1)
            player = 2;
        if(win == 2)
            player = 1;
        
        playerDisplay.setText("Player " + player + " Turn");
        
        p1_val = 0;
        p2_val = 0;
        dist_dice = 0;
        max_dice = 0;
        randHold = 0;
        
    }
    
    public void updateTileCountNum(){
        tile1.setText(Integer.toString(tg.getOwnedTiles(1)));
        tile2.setText(Integer.toString(tg.getOwnedTiles(2)));
    }
    
    public void initPlayerText(){
        playerDisplay.setVisible(false);
        playerDisplay.setText("Player " + player + " Turn");
        //playerDisplay.setFont(Font.font("Courier New", 15));
        playerDisplay.setTranslateX(150);
        playerDisplay.setTranslateY(590);
    }
    
    public void displayAttack(int p1, int p2){
        attack_display.setVisible(true);
        attack_txt1.setText(String.format("%-3s", Integer.toString(p1)));
        attack_txt2.setText(String.format("%3s", Integer.toString(p2)));
    }
    
    Parent game;
    @Override
    public void start(Stage primaryStage) {
        game = initRoot();
        Parent start = initStart();
        //Parent setup = initSetup();
        Parent over = initOver();

        Scene scene = new Scene(start, 1280, 720);
        primaryStage.setTitle("Island Takeover");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                switch(cur_state){
                    case START: 
                        scene.setRoot(start);
                        break;
                    case GAME:
                        scene.setRoot(game); 
                        break;
                    case SETUP:
                        scene.setRoot(setup);
                        break;
                    case OVER:
                        scene.setRoot(over);
                        break;
                    default:
                }
            }
        };
        timer.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public static enum STATE{
        START,
        GAME,
        SETUP,
        OVER
    }
}
