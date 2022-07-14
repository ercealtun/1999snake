import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JPanel;

/**
 * 
 */

/**
 * @author ercealtun
 *
 */
public class GamePanel extends JPanel implements ActionListener {
	static final int SCREEN_WIDTH=900;
	static final int SCREEN_HEIGHT=700;
	static final int UNIT_SIZE=25;
	static final int GAME_UNITS=(SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
	static int DELAY=100;
	final int x[]=new int[GAME_UNITS];
	final int y[]=new int[GAME_UNITS];
	int bodyParts=6; // Game begin with six body parts on the snake
	int applesEaten;
	int appleX; // X coordinate of where the Apple is located
	int appleY; // Y coordinate of where the Apple is located
	char direction='R'; // R for Right, L for Left, U for Up, D for Down
	boolean running = false;
	int highest;
	Timer timer;
	Random random;
	
	GamePanel(){
		random=new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		StartGame();
	}
	
	public void StartGame() {
		newApple();
		running=true;
		timer=new Timer(DELAY, this);
		timer.start();
		
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			draw(g);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics g) throws IOException {
		if(running) {
			
			// If commented section will remove, grids will be appear
			
			/*
			for(int i=0;i<SCREEN_HEIGHT/UNIT_SIZE;i++) {
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH,  i*UNIT_SIZE);
			}
			*/
		
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
			for(int i=0; i<bodyParts; ++i) {
				if(i==0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else {
					g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255))); // RGB Snake
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			
			g.setColor(Color.red);
			g.setFont(new Font("Monospaced",Font.BOLD,40));
			FontMetrics metrics=getFontMetrics(g.getFont());
			g.drawString("Score: "+applesEaten, (SCREEN_WIDTH-metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
		}
		else {
			checkHighestScore();
			gameOver(g);
		}
	}
	
	public void newApple() {
		appleX=random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		appleY=random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
	}
	
	public void move() {
		for(int i=bodyParts;i>0;i--) {
			x[i]=x[i-1];
			y[i]=y[i-1];
		}
		
		switch(direction) {
			case 'R':
				x[0]=x[0]+UNIT_SIZE;
				break;
			case 'L':
				x[0]=x[0]-UNIT_SIZE;
				break;
			case 'U':
				y[0]=y[0]-UNIT_SIZE;
				break;
			case 'D':
				y[0]=y[0]+UNIT_SIZE;
				break;
		}
	}
	
	public void checkApple() {
		int makeDifficult;
		if( (x[0]==appleX) && (y[0]==appleY) ) {
			bodyParts++;
			applesEaten++;
			makeDifficult=applesEaten;
			newApple();
			
			if(makeDifficult==10) {
				DELAY--; // Reducing time to make difficult
			}
		}
	}
	
	public void checkCollisions() {
		//checks if head collides with body
		for(int i=bodyParts;i>0;--i) {
			if( (x[0]==x[i]) && (y[0]==y[i]) ) {
				running=false;
				
			}
		}
		
		//check if head touches left border
		if(x[0]<0) {
			running=false;
		}
		
		//check if head touches right border
		if(x[0]>SCREEN_WIDTH) {
			running=false;
		}
		
		//check if head touches top border
		if(y[0]<0) {
			running=false;
		}
		
		//check if head touches bottom border,
		if(x[0]>SCREEN_HEIGHT) {
			running=false;
		}
		
		if(!running) {
			timer.stop();
		}
	}
	
	public void gameOver(Graphics g) {
		// GameOver text
		g.setColor(Color.red);
		g.setFont(new Font("Monospaced",Font.BOLD,75));
		FontMetrics metrics1=getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH-metrics1.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2); // to center the Game Over text
		
		// Score text
		g.setColor(Color.red);
		g.setFont(new Font("Monospaced",Font.BOLD,40));
		FontMetrics metrics2=getFontMetrics(g.getFont());
		g.drawString("Your Score: "+applesEaten+" / Highest Score: "+highest, (SCREEN_WIDTH-metrics2.stringWidth("Your Score: "+applesEaten+"Highest Score: "+highest))/2, g.getFont().getSize());
	}
	
	public void checkHighestScore() throws IOException {
		// Read the highest score
        File myFile = new File("HIGHEST_SCORE.txt");
        Scanner myReader = new Scanner(myFile);
        highest=myReader.nextInt();
        System.out.println(highest);  
        
        // check if the current score has passed the highest score
		if(highest<applesEaten) {
			highest=applesEaten;
	        try{    
             FileWriter myfw=new FileWriter("HIGHEST_SCORE.txt");    
             myfw.write(String.valueOf(highest));    
             myfw.close();    
           }catch(Exception e){System.out.println(e);}    
        	 System.out.println("Success...");    
	       }   
	}	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(running) {
			move();
			checkApple();
			checkCollisions();
			
		}
		repaint();
		
	}
	
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					if(direction!='R') {
						direction='L';
					}
					break;
					
				case KeyEvent.VK_RIGHT:
					if(direction!='L') {
						direction='R';
					}
					break;
				case KeyEvent.VK_UP:
					if(direction!='D') {
						direction='U';
					}
					break;
				case KeyEvent.VK_DOWN:
					if(direction!='U') {
						direction='D';
					}
					break;
			}
		}
	}
}
