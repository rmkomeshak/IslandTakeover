package islandtakeover;


import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author asdas
 */
public class TileGenerator {
    boolean[][] grid = new boolean[10][20];
    int max_tiles = 50;
    int num_tiles = 5;
    int hold;
    int p1_tiles = 0;
    int p2_tiles = 0;
    int p1_owned = 0;
    int p2_owned = 0;
    int totalDice1 = 0;
    int totalDice2 = 0;
    int diceFactor = 0;
    int owner = 0;
    int diceSum = 0;
    
    ArrayList<Tile> tiles = new ArrayList<>();
    
    public TileGenerator(){
        grid[4][9] = true;
        generateGrid(4, 9);
        mapTiles();
        distributeDice();
    }
    
    private void generateGrid(int row, int col){
        //checks if it is the center tile, and generates a square in all 4 directions
        if(row == 4 && col == 9){
            chooseOrder((int)(Math.random() * 1000) % 4 + 1);
        }
        
        //base case: max tiles is reached
        if(num_tiles < max_tiles){
            hold = (int)(Math.random() * 1000) % 4 + 1;
            
            //generate down
            if(validPos(row - 1, col) && hold == 1){
                if(!grid[row - 1][col])
                    num_tiles++;
                createTile(row - 1, col);
            }
            
            //generate up
            if(validPos(row + 1, col) && hold == 2){
                if(!grid[row + 1][col])
                    num_tiles++;
                createTile(row + 1, col);
            }
            
            //generate left
            if(validPos(row, col - 1) && hold == 3){
                if(!grid[row][col - 1])
                    num_tiles++;
                createTile(row, col - 1);
            }
            
            //generate up
            if(validPos(row, col + 1) && hold == 4){
                if(!grid[row][col + 1])
                    num_tiles++;
                createTile(row, col + 1);
            }
        }
    }
    
    //checks if the selected row and column are a valid position for a tile
    private boolean validPos(int r, int c){
        if(r >= 1 && r <= 8 && c >= 1 && c <= 18){
            return true;
        }
        return false;
    }
    
    //creates a tile and recursively calls generateGrid from that tile
    private void createTile(int r, int c){
        grid[r][c] = true;
        generateGrid(r, c);
    }
    
    public boolean[][] getGrid(){
        return grid;
    }
    
    //hardcoded values to modify the order of the 4 base tiles created.
    //since the algorithm is recursive, one direction is often favored depending on which goes first.
    public void chooseOrder(int val){
        switch(val){
            case 1: createTile(3, 9);
                    createTile(5, 9);
                    createTile(4, 10);
                    createTile(4, 8);
                    break;
            case 2: createTile(4, 8);
                    createTile(4, 10);
                    createTile(5, 9);
                    createTile(3, 9);
                    break;
            case 3: createTile(4, 10);
                    createTile(3, 9);
                    createTile(4, 8);
                    createTile(5, 9);
                    break;
            case 4: createTile(5, 9);
                    createTile(4, 8);
                    createTile(3, 9);
                    createTile(4, 10);
                    break;   
        }
    }
    
    
    //maps the grid to an arraylist of tiles
    private void mapTiles(){
        num_tiles = 0;
        int last_x, last_y;
        
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 20; j++){
                if(grid[i][j]){
                    num_tiles++;
                    tiles.add(new Tile(j, i));
                }
            }
        }
        
        last_x = tiles.get(num_tiles - 1).getX();
        last_y = tiles.get(num_tiles - 1).getY();
        
        if(num_tiles % 2 != 0){
            if(last_x + 1 < 19){
                tiles.add(new Tile(last_x + 1, last_y));
                num_tiles++;
            }
            else{
                tiles.remove(num_tiles - 1);
                num_tiles--;
            }
                
            
        }
        
        p1_tiles = num_tiles/2;
        p2_tiles = num_tiles/2;
        p1_owned = p1_tiles;
        p2_owned = p2_tiles;
        
        for(Tile t : tiles){
            t.setDiceVal(3);
            if(p1_tiles > 0 && p2_tiles > 0)
                owner = (int)(Math.random() * 1000) % 2 + 1;
            if(p1_tiles == 0)
                owner = 2;
            else if(p2_tiles == 0)
                owner = 1;

            if(owner == 1 && p1_tiles > 0){
                t.setOwner(1);
                p1_tiles--;
            }
            if(owner == 2 && p2_tiles > 0){
                t.setOwner(2);
                p2_tiles--;
            }
        }
    }
    
    public void distributeDice(){
        for(Tile t : tiles){
            if(t.getDiceVal() == 3){
                diceFactor = (int)(Math.random() * 1000) % 5 - 2;
                t.setDiceVal(3 + diceFactor);
                diceFactor *= -1;
                for(Tile swap : tiles){
                    if(t.getOwner() == swap.getOwner() && 
                      (t.getX() != swap.getX() || t.getY() != swap.getY()) && 
                      ((swap.getDiceVal() + diceFactor >= 1) && (swap.getDiceVal() + diceFactor <= 5))){
                            swap.setDiceVal(swap.getDiceVal() + diceFactor);
                            break;
                    }
                }
            }
        }
    }
    
    public int getDiceTotal(int o){
        diceSum = 0;
        for(Tile t : tiles){
            if(t.getOwner() == o)
                diceSum += t.getDiceVal();
        }
        
        return diceSum;
    }
    public ArrayList<Tile> getTiles(){
        return tiles;
    }
    
    public void setMaxTiles(int t){
        max_tiles = t;
    }
    
    public void incrementOwnedTiles(int o){
        if(o == 1){
            p1_owned++;
            p2_owned--;
        }
        else{
            p2_owned++;
            p1_owned--;
        }
    }
    
    public int getOwnedTiles(int o){
        if(o == 1)
            return p1_owned;
        else
            return p2_owned;
    }
    
    public int getMaxTiles(){
        return p1_owned + p2_owned;
    }
    
    public void regenerateGrid(){
        num_tiles = 5;
        p1_tiles = 0;
        p2_tiles = 0;
        p1_owned = 0;
        p2_owned = 0;
        totalDice1 = 0;
        totalDice2 = 0;
        diceFactor = 0;
        owner = 0;
        diceSum = 0;
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 20; j++){
                grid[i][j] = false;
            }
        }
        grid[4][9] = true;
        generateGrid(4, 9);
        mapTiles();
        distributeDice();
    }
}
