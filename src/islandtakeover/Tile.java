/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package islandtakeover;

import java.awt.Point;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author asdas
 */
public class Tile {
    int x;
    int y;
    int ownership;
    int dice_val;
    Rectangle tile = new Rectangle(50, 50);
    Text num_dice = new Text();
    boolean owner_selected = false;
    boolean enemy_selected = false;
    boolean hasSwapped = false;
    
    public Tile(int xval, int yval){
        x = xval;
        y = yval;
        dice_val = 0;
        num_dice.setFont(Font.font("Courier New", 17));
        num_dice.setText(Integer.toString(dice_val));
        setFormat();
    }
    
    public Tile(int xval, int yval, int own){
        x = xval;
        y = yval;
        ownership = own;
        setFormat();
    }
    
    //formats each of the tiles (fill, stroke, size, positioning, etc.)
    private void setFormat(){
        num_dice.setFill(Color.WHITE);
        num_dice.setTranslateY(77 + 52 * y);
        num_dice.setTranslateX(163 + 52 * x);
        tile.setTranslateY(50 + 52 * y);
        tile.setTranslateX(140 + 52 * x);
        tile.setStrokeWidth(3);
        tile.setStroke(Color.BLACK);
        //tile.setOpacity(0.5);
        if(ownership == 1)
            tile.setFill(Color.PURPLE);
        if(ownership == 2)
            tile.setFill(Color.GREEN);
    }
   
    public void setX(int x_){
        x = x_;
    }
    
    public void setY(int y_){
        y = y_;
    }
    
    public void setOwner(int o){
        ownership = o;
        
        if(ownership == 1)
            tile.setFill(Color.PURPLE);
        if(ownership == 2)
            tile.setFill(Color.GREEN);
    }
    
    public void setDiceVal(int v){
        dice_val = v;
        num_dice.setText(Integer.toString(dice_val));
    }
    
    public boolean isAdjacent(int enemy_x, int enemy_y){
        if((enemy_x == x + 1 && enemy_y == y) ||
            (enemy_x == x - 1 && enemy_y == y) || 
             (enemy_x == x && enemy_y == y - 1) ||
              (enemy_x == x && enemy_y == y + 1))
            return true;
        return false;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public int getOwner(){
        return ownership;
    }
    
    public Rectangle getSquare(){
        return tile;
    }
    
    public Text getDiceText(){
        return num_dice;
    }
    
    public int getDiceVal(){
        return dice_val;
    }
}
