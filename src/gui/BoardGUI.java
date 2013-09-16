package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

public class BoardGUI extends JComponent {
	public static final int HEIGHT = 600, WIDTH = 600;
	private int[][] board;

	private int mouseX = -1, mouseY = -1;

	private boolean inputMode = false;
	public void setInputMode(boolean b) {inputMode = b;}

	public BoardGUI(int[][] board) {
		this.board = board;
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getPoint().x;
				mouseY = e.getPoint().y;
				if(inputMode) repaint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {
				mouseX = -1;
				mouseY = -1;
				repaint();
			}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {
				inputMode = false;
				repaint();
			}
		});
	}
	public void updateBoard(int[][] board) {
		this.board = board;
		this.repaint();
	}
	public void animate(int x, int y, int x2, int y2, TimerFinish t) {
		double boxWidth = WIDTH/board.length;
		double boxHeight = HEIGHT/board[0].length;
		new Timer(this,
		((double)x+1)*boxWidth-(boxWidth/2),
		((double)y+1)*boxHeight-(boxHeight/2),
		((double)x2+1)*boxWidth-(boxWidth/2),
		((double)y2+1)*boxHeight-(boxHeight/2),
		t);
	}
	private int ANI_X1 = -1, ANI_X2 = -1, ANI_Y1 = -1, ANI_Y2 = -1;
	public void uberSecretDontCall(int x1, int y1, int x2, int y2) {
		ANI_X1 = x1;
		ANI_X2 = x2;
		ANI_Y1 = y1;
		ANI_Y2 = y2;
		repaint();
	}

	public int getColumnNumber(int xCoord) {
		double boxWidth = WIDTH/board.length;
		for(int x = 0; x < board.length; x++) {
			if(xCoord > (int)(x*boxWidth) && xCoord < (int)(x*boxWidth+boxWidth)) {
				return x;
			}
		}
		return -1;
	}
    protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, WIDTH, HEIGHT);
		double boxWidth = WIDTH/board.length;
		double boxHeight = HEIGHT/board[0].length;

		//paint hover pattern
		if(inputMode) {
			for(int x = 0; x < board.length; x++) {
				if(mouseX > (int)(x*boxWidth) && mouseX < (int)(x*boxWidth+boxWidth)) {
					g2d.setColor(new Color(200,200,200));
					g2d.fillRect((int)(x*boxWidth), 0, (int) boxWidth, HEIGHT);
				}
			}
		}

		//fill boxes with color
		g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
		for(int x = 0; x < board.length; x++) {
			for(int y = 0; y < board[0].length; y++) {
				if(board[x][y] == 3) {
					g2d.setColor(new Color(180,0,0));
					g2d.fillRect((int)(x*boxWidth), (int)(y*boxHeight), (int)boxWidth, (int)boxHeight);
				}
				else if(board[x][y] == 1) {
					g2d.setColor(new Color(180,0,0));
					String s = "X"; // always a single character!
					FontMetrics fm = g2d.getFontMetrics();
					FontRenderContext frc = g2d.getFontRenderContext();
					TextLayout tl = new TextLayout(s, g2d.getFont(), frc);
					AffineTransform transform = new AffineTransform();
					transform.setToTranslation((int)(x*boxWidth), (int)((y+1)*boxHeight));
					double scaleY =
					   boxHeight / (double) (tl.getOutline(null).getBounds().getMaxY()
					                                - tl.getOutline(null).getBounds().getMinY());
					transform.scale(boxWidth / (double) fm.stringWidth(s), scaleY);
					Shape shape = tl.getOutline(transform);
					Shape temp = g2d.getClip();
					g2d.setClip(shape);
					g2d.fill(shape.getBounds());
					g2d.setClip(temp);
				}
				else if(board[x][y] == 4) {
					g2d.setColor(new Color(0,0,180));
					g2d.fillRect((int)(x*boxWidth), (int)(y*boxHeight), (int)boxWidth, (int)boxHeight);
				}
				else if(board[x][y] == 2) {
					g2d.setColor(new Color(0,0,180));
					String s = "O"; // always a single character!
					FontMetrics fm = g2d.getFontMetrics();
					FontRenderContext frc = g2d.getFontRenderContext();
					TextLayout tl = new TextLayout(s, g2d.getFont(), frc);
					AffineTransform transform = new AffineTransform();
					transform.setToTranslation((int)(x*boxWidth), (int)((y+1)*boxHeight));
					double scaleY =
					   boxHeight / (double) (tl.getOutline(null).getBounds().getMaxY()
					                                - tl.getOutline(null).getBounds().getMinY());
					transform.scale(boxWidth / (double) fm.stringWidth(s), scaleY);
					Shape shape = tl.getOutline(transform);
					Shape temp = g2d.getClip();
					g2d.setClip(shape);
					g2d.fill(shape.getBounds());
					g2d.setClip(temp);
				}
			}
		}

		//draw lines
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));
		for(int i=0; i<=board.length; i++) {
		    Line2D vline = new Line2D.Double(i*boxWidth, 0, i*boxWidth, HEIGHT);
		    g2d.draw(vline);
		}
		for(int i=0; i<=board[0].length; i++) {
		    Line2D hline = new Line2D.Double(0, i*boxHeight, WIDTH, i*boxHeight);
		    g2d.draw(hline);
		}


		//draw animate line if needed
		if(ANI_X1 != -1) {
			g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2d.setColor(new Color(255,215,0));
			Line2D line = new Line2D.Double(ANI_X1, ANI_Y1, ANI_X2, ANI_Y2);
			//System.out.println("Line: "+ANI_X1+", "+ANI_Y1+", "+ANI_X2+", "+ANI_Y2);
			g2d.draw(line);
		}
    }
}
class Timer extends Thread {
	private BoardGUI temp;
	private double startX;
	private double startY;
	private double finishX;
	private double finishY;

	private double dX;
	private double dY;

	private final int NUM_OF_STEPS = 500;
	private TimerFinish signal;

	public Timer(BoardGUI i, double x1, double y1, double x2, double y2, TimerFinish t) {
		startX = x1;
		startY = y1;
		finishX = x2;
		finishY = y2;
		temp = i;
		dX = ((double)(finishX-startX))/((double)NUM_OF_STEPS);
		dY = ((double)(finishY-startY))/((double)NUM_OF_STEPS);
		finishX = startX;
		finishY= startY;

		signal = t;

		this.start();
	}

	public void run() {

		for(int i = 0; i < NUM_OF_STEPS; i++) {
			finishX = finishX+dX;
			finishY = finishY+dY;
			temp.uberSecretDontCall((int)startX, (int)startY, (int)finishX, (int)finishY);
			try {
				Thread.sleep(4);
			} catch (InterruptedException e) {}
		}
//		signal.animateFinish();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		temp.uberSecretDontCall(-1, -1, -1, -1);
		signal.animateFinish();
	}
}
