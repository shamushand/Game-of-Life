
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class Life extends JFrame implements KeyListener, MouseListener, MouseMotionListener
{
	private boolean[][] universe;	// Array holding current state of the game.
	private Canvas game;			// Canvas component for drawing the game.
	
	// Constructor
	public Life()
	{
		// Call JFrame constructor.
		super("Life");
		
		// Create the game canvas.
		game = new Canvas(true);
		setContentPane(game); 
		clear();
		pack();
		   
		// Set window attributes.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		 
		// Add event listeners.
		this.addKeyListener(this);
		game.addMouseListener(this);
		game.addMouseMotionListener(this);
		game.setRepaintDelay(100);
	}
	
	private final int height = 45;		// Height of the universe, in cells.
	private final int width = 75;		// Width of the universe, in cells.
	private final int scale = 15;		// Size of each cell, in pixels.
	
	// Seed the universe by bringing random cells to life.
	private void seed()
	{
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				if ((int) (10 * Math.random()) == 2)
					universe[x][y] = true;
	}
	
	// Clear the universe and pause the game.
	private void clear()
	{
		universe = new boolean[width][height];
		game.running = false;
	}
	
	// Process the next generation of the universe.
	private void tick()
	{
		boolean[][] next = new boolean[width][height];
		
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				next[x][y] = cell(x, y);
				
		universe = next.clone();
	}
		
	// Takes in two integers, x and y, coordinates of a space in the 
	// universe. Counts how many living cells reside in its Moore 
	// neighborhood, then returns a boolean representing whether
	// this cell should be alive in the next generation.
	private boolean cell(int x, int y)
	{
		int neighbors = 0;	// Number of neighbors.
		
		neighbors += toroidal(x + 1, y + 1) ? 1 : 0;	// Top right
		neighbors += toroidal(x + 0, y + 1) ? 1 : 0;	// Top center
		neighbors += toroidal(x - 1, y + 1) ? 1 : 0;	// Top left
		neighbors += toroidal(x + 1, y + 0) ? 1 : 0;	// Middle right
		neighbors += toroidal(x - 1, y + 0) ? 1 : 0;	// Middle left
		neighbors += toroidal(x + 1, y - 1) ? 1 : 0;	// Bottom right
		neighbors += toroidal(x + 0, y - 1) ? 1 : 0;	// Bottom center
		neighbors += toroidal(x - 1, y - 1) ? 1 : 0;	// Bottom left
		
		return ((neighbors == 2 && universe[x][y]) || neighbors == 3);
	}
	
	// Takes in two integers, x and y, and returns a boolean representing
	// whether a cell resides in that space. Treats the universe as a
	// toroidal array, handling any out of bounds coordinates by 
	// wrapping around to the opposite edge of the universe.
	private boolean toroidal(int x, int y)
	{
		x = (x + width) % width;
		y = (y + height) % height;
		
		// Return the cell stored at the new coordinate.
		return universe[x][y];
	}
		
	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			game.running = !game.running;
		
		if (e.isControlDown())
		{
			if (e.getKeyCode() == KeyEvent.VK_C)
				clear();
			
			else if (e.getKeyCode() == KeyEvent.VK_S)
				seed();
			
			else if (e.getKeyCode() == KeyEvent.VK_G)
				game.grid = !game.grid;
			
			else if (e.getKeyCode() == KeyEvent.VK_R)
			{
				String perSecond = JOptionPane.showInputDialog("Generations per second:");
				game.setRepaintDelay(1000 / Integer.parseInt(perSecond));
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) 
	{
		if (SwingUtilities.isLeftMouseButton(e))
			universe[e.getX()/scale][e.getY()/scale] = true;
	
		else if (SwingUtilities.isRightMouseButton(e))
			universe[e.getX()/scale][e.getY()/scale] = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		if (SwingUtilities.isLeftMouseButton(e))
			universe[e.getX()/scale][e.getY()/scale] = true;
		
		else if (SwingUtilities.isRightMouseButton(e))
			universe[e.getX()/scale][e.getY()/scale] = false;
	}
	
	@Override public void keyReleased(KeyEvent e){}
	@Override public void keyTyped(KeyEvent arg0){}
	@Override public void mouseClicked(MouseEvent arg0){}
	@Override public void mouseEntered(MouseEvent arg0){}
	@Override public void mouseExited(MouseEvent arg0){}
	@Override public void mouseReleased(MouseEvent arg0){}
	@Override public void mouseMoved(MouseEvent arg0){}
	
	// The class for handling the game canvas.
	class Canvas extends JPanel
	{
		// The game grid colors.
		private Color background = new Color(64, 87, 71);
		private Color cell = new Color(20, 30, 8);
		private Color shadow = new Color(56, 75, 56);
		
		private boolean running = false;	// Whether the game is running or paused.
		private boolean grid = true;		// Whether the grid overlay should be displayed.
		private Timer refresh;				// Swing timer for handling canvas repaints.
		
		// Constructor
		public Canvas(boolean isDoubleBuffered) 
		{
			super(isDoubleBuffered);
			this.setPreferredSize(new Dimension(width * scale + 1, height * scale + 1));
		}
		
		@Override
		public void paintComponent(Graphics element) 
		{
			super.paintComponents(element);
			
			element.setColor(background);
			element.fillRect(0, 0, width * scale + 1, height * scale + 1);
			    
			for (int x = 0; x < width; x++)
				for (int y = 0; y < height; y++)
					if (universe[x][y])
					{
						element.setColor(shadow);
						element.fillRect(x * scale + scale/3 + 1, y * scale + scale/3 + 1, scale, scale);
						element.setColor(cell);
						element.fillRect(x * scale + 2, y * scale + 2, scale - 2, scale - 2);
					}
					else
					{
						element.setColor(shadow);					
						element.fillRect(x * scale + scale/3, y * scale + scale/3, scale/3, scale/3);
					}
		}
		
		public void setRepaintDelay(int delay)
		{
			if (refresh != null)
				refresh.stop();
			
			refresh = new Timer(delay, new ActionListener() 
					{
			            public void actionPerformed(ActionEvent ae) 
			            {
			            	repaint();
			           
			            	if (running)
			            		tick();
			            }
			        });
			refresh.start();
			
			
			new Timer(1, new ActionListener() 
			{
	            public void actionPerformed(ActionEvent ae) 
	            {
	            	repaint();
	            }
	        }).start();	
		}
	}
	
	public static void main(String args[]) throws Exception
	{
		// Set UI look and feel to the current OS.
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		// Construct a new instance of the game.
		SwingUtilities.invokeLater(new Runnable()
		{
	        @Override
	        public void run()
	        {
	            new Life();
	        }
	    });
	}
}