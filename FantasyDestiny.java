/** Fantasy Destiny
  * ICS3U Final Project Game
  * @Authors: Dylan Thai and Kevin Chen
  * @Version Date: 6/9/2019
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*; 
import java.awt.image.*;
import javax.imageio.*;
import java.util.Scanner;

class FantasyDestiny {
  public static void main(String[] args) throws Exception { 
     // Open a file called 'Rules.txt'
    File instructionsFile = new File("instructions.txt");
    
    // Create a Scanner and associate it with the file
    Scanner instructions = new Scanner(instructionsFile); 
     
    // Read and print rules from file
    while (instructions.hasNext()) {
      System.out.println(instructions.nextLine());
    }
    System.out.println();
    
    GameWindow game = new GameWindow();
    game.setVisible(true);
    instructions.close();
  } 
}

//This class represents the game window
class GameWindow extends JFrame {
  private static final long serialVersionUID = 1L; // To remove the warning
  private static final int WINDOW_WIDTH = 1100; // 11 * 100 (Board is 11x11)
  private static final int WINDOW_HEIGHT = 825; // 11 * 75
  
  //Window constructor
  public GameWindow() { 
    setTitle("Fantasy Destiny");
    //setSize(WINDOW_WIDTH, WINDOW_HEIGHT);  // set the size of my window to 400 by 400 pixels
    setResizable(false);  // set my window to allow the user to resize it
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // set the window up to end the program when closed
    getContentPane().add( new GamePanel());
    pack(); //makes the frame fit the contents
  }

// An inner class representing the panel on which the game takes place
  static class GamePanel extends JPanel implements KeyListener, MouseListener{
    private static final long serialVersionUID = 2L; // To remove the warning
    
    Player[] players;
    Map map;
    int dice1;
    int dice2;
    int sumOfDice;
    int screenNum;
    String turn;
    
    int titleScreen = 0;
    int gameScreen = 1;
    int controlScreen = 2;
    int endScreen = 3;
    int nothing = 0;
    int blockCard = 1;
    int blocked = 1;
    
    //constructor
    public GamePanel() { 
      setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
      addKeyListener(this);
      addMouseListener(this);
      this.requestFocus();
      setFocusable(true);
      requestFocusInWindow();
      turn = "";
      
      screenNum = titleScreen;// 0 - title screen  // 1 - game screen  // 2 - controls screen
      
      map = new Map(WINDOW_WIDTH, WINDOW_HEIGHT);
      Player.map = map; // Give Player the map
      
      // create the players
      
      players = new Player[4];
      for (int i = 0; i < 4; i++){
        players[i] = new Player(i);
      }
    } // end game panel
    
    public void rollDice(int player) {
      turn = "Its player's " + player + " turn";
      dice1 = (int) (Math.random() * 5 + 1); // 1 to 6
      dice2 = (int) (Math.random() * 5 + 1); // 1 to 6
      sumOfDice = dice1 + dice2;
      System.out.printf("Player %d rolled a %d\n", player, sumOfDice); 
      
      for (int i = 0; i < sumOfDice; i++) {
        players[player].move(1);
        repaint();
        delay(200);
      }
      
      if(players[player].teleport()){
        turn = "The player " + player + " has landed go to 20, Go to 20 then!";
        repaint();
        delay(1000);
      }
      
      if(players[player].position == 5 || players[player].position == 25){
        turn = "The player " + player + " has landed on roll again, time to roll again!";
        repaint();
        delay(1000);
        rollDice(player);
        
      } else if (players[player].position == 15 || players[player].position == 35){
        turn = "The player " + player + " has landed on Item: Blocked, Player gains a block card!";
        players[player].item = blockCard;
        repaint();
        delay(1000);
      }
      
      if(! players[player].inPlay){
        screenNum = endScreen;
      }
      
      delay(500);
      
      if (player == 3 && players[0].item == blockCard){
        turn = "Click on players icon to use item on";
      } else {
        turn = "";
      }
      repaint();
    } // end rollDice
    
    public void paintComponent(Graphics g) { 
      super.paintComponent(g); //required to ensure the panel is correctly redrawn
      // update the content
      // draw the screen
      if (screenNum == gameScreen){
        map.drawMap(g);
        drawDice(g);
        drawStats(g);
      }else if (screenNum == endScreen){
        drawEndScreen(g);
      } else if (screenNum == titleScreen){
        drawTitleScreen(g);
      } else if (screenNum == controlScreen){
        drawControlScreen(g);
      }
    }
    
    public void setBigFont(Graphics g){
      int fontSize = 30;
      g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
      g.setColor(Color.BLACK);
    }
    
    public void drawControlScreen(Graphics g){
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
      setBigFont(g);
      g.drawString(" Controls (empty for now)", 100, 100);
      g.drawRect(500, 525, 100, 75);
      g.drawString("Title Screen", 511, 570);
    }
    
    public void drawEndScreen(Graphics g) {
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
      setBigFont(g);
      g.drawString(" Congratulations!", 500, 500);
    }
    
    public void drawTitleScreen(Graphics g) {
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
      setBigFont(g);
      g.drawString(" Final destiny ", 500, 100);
      g.drawRect(500, 525, 150, 75);
      g.drawString("Play", 550, 570);
      g.drawRect(500, 400, 150, 75);
      g.drawString("Controls", 525, 445);
    }
    
    public void drawDice(Graphics g){
      int fontSize = 20;
      g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
      g.setColor(Color.BLACK);
      g.drawString("Dice 1: " + dice1, 510, 400);
      g.drawString("Dice 2: " + dice2, 510, 450);
      g.drawString("Dice sum: " + sumOfDice, 510, 500);
    }
    //  private static final int[] xOffset = { 15, 55, 15, 55 };
    // private static final int[] yOffset = { 5, 5, 40, 40 };
    
    public void drawStats(Graphics g){
      players[0].draw(g, 135, 140);
      players[1].draw(g, 95, 190);
      players[2].draw(g, 135, 205);
      players[3].draw(g, 95, 255);
      
      g.drawString("Laps Completed" , 200, 130);
      g.drawString("Items" , 500, 130);
      g.drawString("Status" , 600, 130);
      g.drawString(turn , 150, 350); 
      
      for(int i = 0; i < 4; i ++){
        g.drawString(" " + players[i].laps + "/" + players[i].lapsToWin, 200, i * 50 + 170);
        if (players[i].item == blockCard){
          g.drawString(" Block Card ", 500, i * 50 + 170);
        } else {
          g.drawString(" Nothing ", 500, i * 50 + 170);
        }
        if (players[i].status == blocked){
          g.drawString(" Blocked ", 600, i * 50 + 170);
        } else {
          g.drawString(" Nothing ", 600, i * 50 + 170);
        }
      }
    } // end drawStats
    
    public void mousePressed(MouseEvent e) {}
    
    public void mouseReleased(MouseEvent e) {
      int mxp = e.getX();
      int myp = e.getY();
      int button = e.getButton();

      if (button == 1 && (mxp>500 && mxp<600) && (myp>525 && myp<600)&& screenNum == gameScreen){
        for (int i = 0; i <= 3; i ++){
          if (players[i].status == blocked){
            turn = "Player" + i + "was blocked";
            repaint();
            players[i].status = nothing;
            delay(1000);
          } else {
            rollDice(i);
          }
        }
        
        }else if (button == 1 && screenNum == gameScreen && players[0].item == blockCard){ // is second condition needed?
          if ((mxp > 150 && mxp < 182) && (myp > 145 && myp < 177)){
            players[0].status = blocked;
          }else if((mxp > 150 && mxp < 182) && (myp > 195 && myp < 227)){
            players[1].status = blocked;
            turn = "Player 1 blocked by Player 0!";
          }else if((mxp > 150 && mxp < 182) && (myp > 245 && myp < 277)){
            players[2].status = blocked;
            turn = "Player 2 blocked Player 0!";
          }else if((mxp > 150 && mxp < 182) && (myp > 295 && myp < 327)){
            players[3].status = blocked;
            turn = "Player 3 blocked Player 4!";
          }
          
          players[0].item = nothing;
          repaint();
          delay(5000);
       
//////////////////////////// 
        
      } else if (button == 1 && (mxp > 500 && mxp < 600) && (myp > 525 && myp < 600) && screenNum == titleScreen) {
        screenNum = gameScreen;
        repaint();
      }else if (button == 1 && (mxp > 500 && mxp < 600) && (myp > 400 && myp < 475) && screenNum == titleScreen) {
        screenNum = controlScreen;
        repaint();
      }else if (button == 1 && (mxp > 500 && mxp < 600) && (myp > 525 && myp < 600) && screenNum == controlScreen) {
        screenNum = titleScreen;
        repaint();
      }
    }
    
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {
      
    } // end key release
    
    // repaints immediately for animation
    public void repaint() {
      this.paintImmediately(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
    } // end repaint immediately
    
    // delay for animation
    public void delay(int ms) {
      try{Thread.sleep(ms);    //These two lines add a small delay
      } catch (Exception exc){}
    } // end delay
  } // end Game Panel
} // end Game Window

class Player {
  private static final int[] xOffset = { 15, 55, 15, 55 };
  private static final int[] yOffset = { 5, 5, 40, 40 };
  private static final String[] sprites = { "dragon.png", "wizard.png", "unicorn.png", "ghost.png" };
  static int lapsToWin = 3;
  static Map map;
  static boolean inPlay = true;
  
  int number; // 0 to 3, 0: TL, 1: TR, 2: BL, 3: BR
  int position;
  int laps;
  int status;
  int item;

  boolean moveAgain = true;
  BufferedImage image;
  Tile tile;
  
  public Player(int number) { 
    this.number = number;
    this.position = 0;
    this.moveAgain = true;
    this.laps = 0;
    this.status = 0;
    this.item = 0;
    
    loadSprite();
    map.movePlayer(this, 0);
  }
  
  public void loadSprite() { 
    try {
      image = ImageIO.read(new File(sprites[this.number]));
    } catch(Exception e) {
      System.out.println("error loading sprite");
    };
  } // end load sprite

  public void draw(Graphics g, int x, int y) {
    g.drawImage(image, x + xOffset[number], y + yOffset[number], null);
  } // end draw
  
  public void move(int direction) { // direction = -1 or +1
    this.tile.removePlayer(this.number);
    
    this.position = (this.position + direction) % 40; // to loop back to 0
    if (this.position < 0) { // negative case (to loop back to position 39)
      this.position += 40;
    }
    if(this.position == 0){
      laps++;
      System.out.println(laps);
      if (laps == lapsToWin){
        inPlay = false;
      }
    }
    map.movePlayer(this, this.position);
    // teleport();
  } // end move
  
//  public int getWinner() {
//    if (this.laps == lapsToWin){
//      return (int) this.number;
//    } else {
//      return -1;
//    }

  //} // end get-winner
  
  public boolean teleport() {
    if(this.position == 10 || this.position == 30){
      this.tile.removePlayer(this.number);
      this.position = 20;
      map.movePlayer(this, this.position);
      return true;
    } else {
      return false;
    }
  } // end teleport
} // end player class

  class Map { 
    int boxWidth, boxHeight;
    int map[][]= { 
      {0,0,0,0,0,0,0,0,0,0,0},
      {0,1,1,1,1,1,1,1,1,1,0},
      {0,1,1,1,1,1,1,1,1,1,0},
      {0,1,1,1,1,1,1,1,1,1,0},
      {0,1,1,1,1,1,1,1,1,1,0},
      {0,1,1,1,1,1,1,1,1,1,0},
      {0,1,1,1,1,1,1,1,1,1,0},
      {0,1,1,1,1,2,1,1,1,1,0},
      {0,1,1,1,1,1,1,1,1,1,0},
      {0,1,1,1,1,1,1,1,1,1,0},
      {0,0,0,0,0,0,0,0,0,0,0}};  // this can be loaded from a file
    
    Tile worldMap[][];
    Tile walkableMap[];
    
    public Map(int xResolution,int yResolution) { 
      boxWidth = xResolution / 11; //The size of each square
      boxHeight = yResolution / 11;  //it would be best to choose a res that has a common divisor
      createWorldMap();
      createWalkableMap();
    }
    
    public void createWorldMap() { 
      worldMap = new Tile[map.length][map[0].length]; // 11 x 11 Tiles
      
      for (int j=0;j<worldMap.length;j++){
        for (int i=0;i<worldMap[j].length;i++) {
          if (map[j][i]==0) {
            worldMap[j][i] = new Tile(Color.BLACK, i * boxWidth, j * boxHeight, boxWidth, boxHeight, true);
          } else if(map[j][i]==2) {
            worldMap[j][i] = new Tile(Color.GREEN, i * boxWidth, j * boxHeight, boxWidth, boxHeight, true);
          } else {
            worldMap[j][i] = new Tile(Color.WHITE, i * boxWidth, j * boxHeight, boxWidth, boxHeight, false);
          }
        }
      }
    }
    
    public void createWalkableMap() {
      walkableMap = new Tile[2 * map.length + 2 * (map[0].length - 2)]; // 40 Tiles
      int walkableIndex = 0;
      
      // top row
      for (int i = 0; i < worldMap[0].length; i++) {
        walkableMap[walkableIndex] = worldMap[0][i];
        worldMap[0][i].tileNum = walkableIndex;
        walkableIndex++;
      }
      // right column
      for (int j = 1; j < worldMap.length; j++) {
        walkableMap[walkableIndex] = worldMap[j][worldMap[j].length - 1];
        worldMap[j][worldMap[j].length - 1].tileNum = walkableIndex;
        walkableIndex++;
      }
      // bottom row
      for (int i = worldMap[0].length - 2; i >= 0; i--) {
        walkableMap[walkableIndex] = worldMap[worldMap.length - 1][i];
        worldMap[worldMap.length - 1][i].tileNum = walkableIndex;
        walkableIndex++;
      }
      // left column
      for (int j = worldMap.length - 2; j > 0; j--) {
        walkableMap[walkableIndex] = worldMap[j][0];
        worldMap[j][0].tileNum = walkableIndex;
        walkableIndex++;
      }
    } // End createWalkableMap()
    
    public void movePlayer(Player player, int position){
      walkableMap[position].addPlayer(player);
      player.tile = walkableMap[position];
    } // end movePlayer
    
    public void drawMap(Graphics g) { 
      for (int j = 1; j < map.length - 1; j++) {
        for (int i = 1; i < map[1].length - 1; i++) {
          worldMap[j][i].draw(g, i, j);
        }
      }
      
      for (int j = 0; j < map.length; j++) {
        for (int i = 0; i< map[j].length; i++) {
          if (worldMap[j][i].border) {
            worldMap[j][i].draw(g, i, j);
          }
        }
      }
      
      int fontSize = 20;
      g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
      g.setColor(Color.BLACK);
      g.drawString("Roll Dice", 511, 570);
      
    } // end drawMap
  } // end map class
  
  class Tile { 
    int xPosMap, yPosMap;
    int width,height;
    boolean border;
    Color colour;
    Player players[];
    int tileNum;
    
    public Tile(Color c, int x, int y, int w, int h, boolean b) { 
      colour = c;
      xPosMap = x;
      yPosMap = y;
      width = w;
      height = h;
      border = b;
      players = new Player[4];
    }
    
    public void addPlayer(Player player) {
      players[player.number] = player;
    }
    
    public void removePlayer(int number) {
      players[number] = null;
    }
    
    public void draw(Graphics g, int xScreen, int yScreen) { 
      g.setColor(colour);
        
      int x = xScreen * width;
      int y = yScreen * height;
      
      if (!border){
        g.fillRect(x, y, width, height);
      } else {
        g.drawRect(x, y, width, height);  //-1 to see edges
        int fontSize = 20;
        g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
        g.setColor(Color.BLACK);
        g.drawString("" + this.tileNum, x + 10, y + 20);
        if (this.tileNum == 10 || this.tileNum == 30){
          g.drawString("Go to 20", x + 10, y + 50);
        } else if (this.tileNum == 5 || this.tileNum == 25){
          g.drawString("Roll Again", x + 10, y + 50);
        }else if (this.tileNum == 15 || this.tileNum == 35){
          g.drawString("Item: Block", x + 10, y + 50);
        }
      }
      
      // draw players on this tile
      for(int i = 0; i < 4; i++){
        if (players[i] != null){
          players[i].draw(g, x, y);
        }
      }
    } // end draw
  } // end Tile class
  