package gui;

import game.GameBrain;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

public class GameFrame implements TimerFinish {

	JFrame frame = new JFrame();
	JLabel statusLabel;
	JLabel myScore;
	JLabel opponentName;
	JLabel opponentScore;
	JLabel moveLabel;
	JTextArea chatPane;
	JTextField chatField;
	JButton chatButton;

	BoardGUI display;
	GameBrain game;
	int[][] board;

	private boolean gameInProgress = true;

	public static final int HEIGHT = 675, WIDTH = 850;
	public static final int INPUT = 0, WATCHING = 1;
	public static int STATE = 0;

	public GameFrame(GameBrain gameMaster, int[][] board) {
		this.game = gameMaster;
		this.board = board;
		frame.setTitle("The Awesome Game");
		int x = (int) ((java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2.0)-WIDTH/2);
		int y = (int) ((java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2.0)-HEIGHT/2);//-HEIGHT/8);
		frame.setBounds(x, y, WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JMenuBar menuBar = setupMenuBar();
		frame.setJMenuBar(menuBar);
		frame.setLayout(null);
		frame.setResizable(false);
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {resignQuit();}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});

		statusLabel = new JLabel("Game loading...");
		statusLabel.setVerticalTextPosition(JLabel.CENTER);
		statusLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		frame.add(statusLabel);

		JLabel myName = new JLabel("Me");
		myName.setHorizontalAlignment(JLabel.CENTER);
		myName.setFont(new Font("Comic Sans MS", Font.BOLD, 34));
		myName.setForeground(new Color(0,0,0));
		frame.add(myName);

		myScore = new JLabel("0");
		myScore.setVerticalTextPosition(JLabel.CENTER);
		myScore.setHorizontalAlignment(JLabel.CENTER);
		myScore.setFont(new Font("Comic Sans MS", Font.BOLD, 34));
		myScore.setForeground(game.getMyColor());
		frame.add(myScore);

		opponentName = new JLabel(game.getOpName());
		opponentName.setHorizontalAlignment(JLabel.CENTER);
		opponentName.setFont(new Font("Comic Sans MS", Font.BOLD, 34));
		opponentName.setForeground(new Color(0,0,0));
		if(game.getOpFullName().equals("Andrew Davies") || game.getOpFullName().equals("Peter Riley")) {
			opponentName.setForeground(new Color(255,20,147));
		}
		frame.add(opponentName);


		opponentScore = new JLabel("0");
		opponentScore.setVerticalTextPosition(JLabel.CENTER);
		opponentScore.setHorizontalAlignment(JLabel.CENTER);
		opponentScore.setFont(new Font("Comic Sans MS", Font.BOLD, 34));
		opponentScore.setForeground(game.getOpColor());
		frame.add(opponentScore);

		moveLabel = new JLabel("It's Players Move");
		moveLabel.setVerticalTextPosition(JLabel.CENTER);
		moveLabel.setHorizontalAlignment(JLabel.CENTER);
		moveLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		if(game.isMyTurn()) {
			moveLabel.setForeground(game.getMyColor());
			moveLabel.setText("It's your move");
		}
		else {
			moveLabel.setForeground(game.getOpColor());
			moveLabel.setText("It's "+game.getOpName()+"'s move");
		}
		frame.add(moveLabel);


		chatPane = new JTextArea();
		chatPane.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		chatPane.setForeground(new Color(0,0,0));
		JScrollPane scroll = new JScrollPane(chatPane);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		frame.add(scroll);
		chatPane.setText(game.getMyName()+" has joined the game...\n"+game.getOpName()+" has joined the game...");
		chatPane.setEditable(false);

		chatField = new JTextField();
		chatField.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		chatField.setForeground(new Color(0,0,0));
		frame.add(chatField);
		chatField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = chatField.getText();
				if(text.trim().equals("")) {
					statusLabel.setText("You cannot send an empty message...");
					chatField.setText("");
					chatField.requestFocus();
					return;
				}
				if(text.contains("#")) {
					statusLabel.setText("You cannot use the '#' character...");
					chatField.setText("");
					chatField.requestFocus();
					return;
				}
				game.chat(text.trim());
				chatField.setText("");
				chatField.requestFocus();
			}
		});

		chatButton = new JButton("Say");
		chatButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
		chatButton.setForeground(new Color(0,0,0));
		frame.add(chatButton);
		chatButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String text = chatField.getText();
				if(text.trim().equals("")) {
					statusLabel.setText("You cannot send an empty message...");
					chatField.setText("");
					chatField.requestFocus();
					return;
				}
				if(text.contains("#")) {
					statusLabel.setText("You cannot use the '#' character...");
					chatField.setText("");
					chatField.requestFocus();
					return;
				}
				game.chat(text.trim());
				chatField.setText("");
				chatField.requestFocus();
			}
		});
		display = new BoardGUI(board);

		display.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {
				//detect col
				if(game.isMyTurn()) {
					int col = display.getColumnNumber(e.getX());
					display.setInputMode(false);
					game.input(col);
				}
			}
		});

		frame.add(display);

		myName.setBounds(606, 20, 244, 50);
		myScore.setBounds(606, 70, 244, 50);
		opponentName.setBounds(606, 150, 244, 50);
		opponentScore.setBounds(606, 200, 244, 50);
		moveLabel.setBounds(606, 260, 244, 20);
		scroll.setBounds(610, 300, 234, 280);
		chatField.setBounds(610, 586, 180, 20);
		chatButton.setBounds(792, 586, 49, 20);

		display.setBounds(5, 5, 601, 601);
		statusLabel.setBounds(10, 606, 596, 20);
		frame.setVisible(true);
		chatField.requestFocus();
	}
	private JMenuBar setupMenuBar() {
		JMenuBar bar = new JMenuBar();
		frame.setJMenuBar(bar);
		JMenu fileMenu = new JMenu("File");
		bar.add(fileMenu);
		JMenuItem saveItem = new JMenuItem("Save & Quit");
		JMenuItem aboutItem = new JMenuItem("About Game");
		JMenuItem resignItem = new JMenuItem("Resign & Quit");
		JMenuItem resign2Item = new JMenuItem("Resign & Play");
		fileMenu.add(saveItem);
		fileMenu.add(aboutItem);
		fileMenu.addSeparator();
		fileMenu.add(resign2Item);
		fileMenu.add(resignItem);

		resignItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resignQuit();
			}
		});
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveQuit();
			}
		});
		return bar;
	}
	private void resignQuit() {
		if(gameInProgress) {
			int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to resign?", "Resign Game",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(answer == JOptionPane.YES_OPTION) {
				frame.dispose();
				game.resign();
			}
		}
		else {
			System.exit(0);
		}
	}
	private void saveQuit() {
		if(game.isServer()) {
			game.save();
		}
		else {
			JOptionPane.showMessageDialog(null, "Cannot save game when you are not the server", "Save Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	private boolean animating = false;
	@Override
	public void animateFinish() {
		animating = false;
		display.updateBoard(board);
		setStatus(game.getMyStatus());
	}
	public void updateBoard(int[][] board) {
		if(!animating) {display.updateBoard(board);}
		this.board = board;
		myScore.setText(String.valueOf(game.getMyScore()));
		opponentScore.setText(String.valueOf(game.getOpScore()));
	}
	public void setStatus(String status) {
		if(game.isServer()) {
			statusLabel.setText("<html><font color=\"#B40000\">[Server]</font> "+status+"</html>");
		}
		else {
			statusLabel.setText("<html><font color=\"#0000B4\">[Client]</font> "+status+"</html>");
		}
		display.setInputMode(game.isMyTurn());
		if(game.isMyTurn()) {
			moveLabel.setForeground(game.getMyColor());
			moveLabel.setText("It's your move");
		}
		else {
			moveLabel.setForeground(game.getOpColor());
			moveLabel.setText("It's "+game.getOpName()+"'s move");
		}
		myScore.setText(String.valueOf(game.getMyScore()));
		opponentScore.setText(String.valueOf(game.getOpScore()));
	}
	public void animate(int x, int y, int x2, int y2) {
		animating = true;
		display.animate(x, y, x2, y2, this);
	}
	public void chat(String message) {
		chatPane.append("\n"+message);
	}

	private boolean inputWaiting = false;
	public void setInputMode(boolean input) {
		inputWaiting = input;
		display.setInputMode(input);
	}
	public void gameOver(boolean localWin) {
		gameInProgress = false;
	}
	public void renameOp(String name) {
		opponentName.setText(name);
		chatPane.setText(game.getMyName()+" has joined the game...\n"+game.getOpName()+" has joined the game...");
	}
}
