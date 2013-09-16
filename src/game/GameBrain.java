package game;

import gui.GameFrame;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class GameBrain {

	private GameFrame gui;
	private boolean server; public boolean isServer() { return server; }
	private GameLogic logic;


	//server = red plaer
	//server = player 1 //client = player 2
	private final Color RED_COLOR = new Color(180,0,0);
	private final Color BLUE_COLOR = new Color(0,0,180);

	private String myFullName;
	private String opFullName = "Opponent";

	Socket socket;
	BufferedReader input;
	PrintWriter output;

	public static final int SERVER_PLAYER = 1;
	public static final int CLIENT_PLAYER = 2;

	private int clientScore = 0;
	private int serverScore = 0;

	private int turn = 1;
	private boolean networkState = true;

	public GameBrain(Socket s, boolean server) throws IOException {
		this.server = server;
		if(server) {
			logic = new GameLogic();
		}
		else {
		}
		try {
			Runtime rt = Runtime.getRuntime();
	        Process pr = rt.exec("finger "+System.getProperty("user.name"));
	        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	        String line=input.readLine();
	        input.close();
	        int i = line.indexOf("Name: ");
	        myFullName = line.substring(i+6);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

		int[][] temp = new int[20][20];
		gui = new GameFrame(this, temp);
		if(server)
			System.out.println("Server Started...");
		else
			System.out.println("Client Started....");
		socket = s;
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(socket.getOutputStream());
		new NetworkListener(input, this);
		myFullName = getMyFullName();
		output.println(NAME_PREFIX+myFullName);
		output.flush();
		gui.setInputMode(server);
		if(server) {
			output.println(STATUS_PREFIX+"Waiting for "+getMyName()+" to moxe...");
			output.flush();
			gui.setStatus("Waiting for input...");
		}
	}


	public Color getMyColor() {if(server) return RED_COLOR; else return BLUE_COLOR;}
	public Color getOpColor() {if(server) return BLUE_COLOR; else return RED_COLOR;}

	public boolean isMyTurn() {
		if(server && turn == 1) return true;
		if(!server && turn == 2) return true;
		return false;
	}
	public String getMyName() { return myFullName.split(" ")[0]; }
	public String getMyFullName() { return myFullName; }
	public String getOpName() { return opFullName.split(" ")[0]; }
	public String getOpFullName() { return opFullName; }

	public String getMyStatus() {
		if((server && turn == 1) || (!server && turn == 2)) {
			return "It's your turn. Waiting for input...";
		}
		return "Waiting for "+getOpName()+" to moxe...";
	}
	public void input(int column) {
		gui.setStatus("Doing complicated stuff...");
		gui.setInputMode(false);
		if(server) {
			makeMove(1, column);
		}
		else {
			output.println(MOVE_PREFIX+column);
			output.flush();
		}
	}
	public void makeMove(int player, int col) {
		if(server) {
			int result = logic.makeMove(player, col);
			if(result == GameLogic.INVALID) {
				if(player == 1) {
					gui.setStatus("Invalid move, try again...");
					gui.setInputMode(true);
				}
				else {
					output.println(INVALID_MESSAGE);
					output.flush();
				}
				return;
			}

			if(turn == SERVER_PLAYER) turn = CLIENT_PLAYER;
			else turn = SERVER_PLAYER;
			if(result == GameLogic.NORMAL && !logic.fullBoard()) {
				output.println(TURN_PREFIX+turn);
				output.flush();
				sendBoard();
				if(turn == CLIENT_PLAYER) {
					output.println(STATUS_PREFIX+"It's your turn. Waiting for input...");
					output.flush();
					gui.setStatus("Waiting for "+getOpName()+" to moxe...");
				}
				else {
					output.println(STATUS_PREFIX+"Waiting for "+getMyName()+" to moxe...");
					output.flush();
					gui.setStatus("It's your turn. Waiting for input...");
				}
				return;
			}
			if(result == GameLogic.NORMAL && logic.fullBoard()) {
				sendBoard();
				output.println(GAME_PREFIX+logic.getPlayerOneScore()+"#"+logic.getPlayerTwoScore());
				output.flush();
				gui.setStatus("Game Over...");
				gameOver(logic.getPlayerOneScore()+"#"+logic.getPlayerTwoScore());
				return;
			}
			if(turn == SERVER_PLAYER) turn = CLIENT_PLAYER;
			else turn = SERVER_PLAYER;
			if(result == GameLogic.WIN && !logic.fullBoard()) {
				//win animate continue
				output.println(TURN_PREFIX+turn);
				output.flush();
				sendBoard();
				int[] point = logic.getWinCoords();
				output.println(ANI_PREFIX+point[0]+"#"+point[1]+"#"+point[2]+"#"+point[3]);
				output.flush();
				gui.animate(point[0], point[1], point[2], point[3]);
				output.println(SCORE_PREFIX+logic.getPlayerOneScore()+"#"+logic.getPlayerTwoScore());
				sendBoard();
				return;
			}
			if(result == GameLogic.WIN && logic.fullBoard()) {
				//game over win animate win message
				output.println(TURN_PREFIX+turn);
				output.flush();
				sendBoard();
				int[] point = logic.getWinCoords();
				output.println(ANI_PREFIX+point[0]+"#"+point[1]+"#"+point[2]+"#"+point[3]);
				output.flush();
				sendBoard();
				output.println(GAME_PREFIX+logic.getPlayerOneScore()+"#"+logic.getPlayerTwoScore());
				output.flush();
				gameOver(logic.getPlayerOneScore()+"#"+logic.getPlayerTwoScore());
				return;
			}
		}
	}
	public void gameOver(String message) {
		//show cool message
		String[] nums = message.split("#");
		int server = Integer.parseInt(nums[0]);
		int client = Integer.parseInt(nums[1]);
		if(this.server) {
			if(server > client) {
				JOptionPane.showMessageDialog(null, "Congratz, you win", "Game Over", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(null, "You lose, bad luck", "Game Over", JOptionPane.INFORMATION_MESSAGE);
			}
			networkState = false;
			output.println(NetworkListener.KILL_MSG);
			output.flush();
			output.close();
		}
		else {
			if(server < client) {
				JOptionPane.showMessageDialog(null, "Congratz, you win", "Game Over", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(null, "You lose, bad luck", "Game Over", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	public void resign() {
		networkState = false;
		output.println(RESIGN_MESSAGE);
		output.flush();
		output.println(NetworkListener.KILL_MSG);
		output.flush();
		output.close();
	}
	public void save() {
		resign();
	}
	private static final String CHAT_PREFIX = "#CHAT#";
	public void chat(String message) {
		output.println(CHAT_PREFIX+getMyName()+": "+message);
		output.flush();
		gui.chat(getMyName()+": "+message);
		System.out.println("Server("+server+") Sending Chat: "+message);
		if(server) {
			//chat logs????
		}
	};
	private static final String ANI_PREFIX = "#ANI#";
	private static final String BOARD_PREFIX = "#BOARD#"; //hardcoded elsewhere as well
	private static final String STATUS_PREFIX = "#STATUS#";
	private static final String NAME_PREFIX = "#NAME#";
	private static final String MOVE_PREFIX = "#MOVE#";
	private static final String TURN_PREFIX = "#TURN#";
	private static final String GAME_PREFIX = "#GAME#"; //game over prefix #GAME#serverscore#clientscore
	private static final String SCORE_PREFIX = "#SCORE#"; //#SCORE#serverscore#clientscore

	private static final String INVALID_MESSAGE = "#INVALIDMOVE#";
	private static final String RESIGN_MESSAGE = "#RESIGN#";

	//deal with incomming messages
	public void remoteMessage(String message) {
		System.out.println("Server("+server+") Recive Message: "+message);
		if(message.equals(NetworkListener.KILL_MSG)) {
			try {
				System.out.println("Closing input");
				input.close();
			} catch (IOException e1) {}
			if(networkState) {
				output.println(NetworkListener.KILL_MSG);
				System.out.println("Bounce Term Message");
				output.flush();
				output.close();
			}
			try {
//				try {
//					Thread.sleep(1); //need to wait until he reads terminate message before I can close
//					//is there a better way?
//				} catch (InterruptedException e) {}
				socket.close();
			} catch (IOException e) {}
			System.out.println("Exiting");
			if(resign) {
				JOptionPane.showMessageDialog(null, "Opponent Resgined", "Resign", JOptionPane.ERROR_MESSAGE);
			}
			System.exit(0);
			return;
		}
		if(message.startsWith(CHAT_PREFIX)) {
			gui.chat(message.substring(CHAT_PREFIX.length()));
			return;
		}
		if(message.startsWith(ANI_PREFIX)) {
			String[] nums = message.substring(ANI_PREFIX.length()).split("#");
			int[] ints = new int[nums.length]; //this will be 4 long
			for(int i = 0; i < nums.length; i++) ints[i] = Integer.parseInt(nums[i]);
			gui.animate(ints[0], ints[1], ints[2], ints[3]);
			return;
		}
		if(message.startsWith(BOARD_PREFIX)) {
			String[] nums = message.substring(BOARD_PREFIX.length()).split("#");
			int[] ints = new int[nums.length];
			for(int i = 0; i < nums.length; i++) ints[i] = Integer.parseInt(nums[i]);
			int[][] board = new int[ints[0]][ints[1]];
			for(int i = 2,x = 0,y = 0; i < ints.length; i++,x++) {
				if(x == board.length) {
					x = 0;
					y++;
				}
				board[x][y] = ints[i];
			}
			gui.updateBoard(board);
		}
		if(message.startsWith(STATUS_PREFIX)) {
			gui.setStatus(message.substring(STATUS_PREFIX.length()));
			return;
		}
		if(message.startsWith(NAME_PREFIX)) {
			opFullName = message.substring(NAME_PREFIX.length());
			gui.renameOp(getOpName());
			return;
		}
		if(message.startsWith(MOVE_PREFIX)) {
			makeMove(2, Integer.parseInt(message.substring(MOVE_PREFIX.length())));
			return;
		}
		if(message.equals(INVALID_MESSAGE)) {
			gui.setStatus("Invalid move, try again...");
			gui.setInputMode(true);
			return;
		}
		if(message.startsWith(TURN_PREFIX)) {
			turn = Integer.parseInt(message.substring(TURN_PREFIX.length()));
			System.out.println("Set turn to "+turn);
			return;
		}
		if(message.equals(RESIGN_MESSAGE)) {
			//JOptionPane.showMessageDialog(null, "Opponent Resgined", "Resign", JOptionPane.ERROR_MESSAGE);
			resign = true;
			return;
		}
		if(message.startsWith(GAME_PREFIX)) {
			gameOver(message.substring(GAME_PREFIX.length()));
			return;
		}
		if(message.startsWith(SCORE_PREFIX)) {
			String[] temp = message.substring(SCORE_PREFIX.length()).split("#");
			serverScore = Integer.parseInt(temp[0]);
			clientScore = Integer.parseInt(temp[1]);
		}
	}
	private boolean resign = false;
	private void sendBoard() {
		int[][] board = logic.getBoard();
		gui.updateBoard(board);
		int[] ints = new int[2+board.length*board[0].length];
		for(int i = 2,x = 0,y = 0; i < ints.length; i++,x++) {
			if(x == board.length) {
				x = 0;
				y++;
			}
			ints[i] = board[x][y];
		}
		ints[0] = board.length;
		ints[1] = board[0].length;
		String s = "#BOARD";
		for(int i = 0; i < ints.length; i++) {
			s = s + "#" + ints[i];
		}
		output.println(s);
		output.flush();
	}

	public static void main(String[] args) {

		try {
			InetAddress localMachine = InetAddress.getLocalHost();
			String address = localMachine.getCanonicalHostName();
			if(!address.contains("ecs.vuw.ac.nz")) {
				System.out.println("This can only be run on ecs machines");
				System.exit(1);
			}
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		if(args.length == 1) {
			try {
				Socket socket = new Socket(args[0].trim(), 42567);
				new GameBrain(socket, false);
			} catch (UnknownHostException e) {
				System.out.println("No such address "+args[0]);
			} catch (IOException e) {
				System.out.println("No server running at "+args[0]);
			}
		}
		else if(args.length == 0) {
			try {
				System.out.println("Waiting for Client...");
				ServerSocket s = new ServerSocket(42567);
				Socket socket = s.accept();
				s.close();
				new GameBrain(socket, true);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("Error, only takes 1 argument");
			System.exit(1);
		}
	}

	public int getMyScore() {
		if(server) return logic.getPlayerOneScore();
		return clientScore;
	}
	public int getOpScore() {
		if(server) return logic.getPlayerTwoScore();
		return serverScore;
	}

}
class NetworkListener extends Thread {
	BufferedReader scan;
	GameBrain game;
	String lastMessage = "";
	public static final String KILL_MSG = "#TERMINATE#";
	public NetworkListener(BufferedReader input, GameBrain game) {
		scan = input;
		this.game = game;
		start();
	}
	public void run() {
		while(!lastMessage.equals(KILL_MSG)) {
			try {
				lastMessage = scan.readLine();
				if(lastMessage==null)
					lastMessage = KILL_MSG;
				game.remoteMessage(lastMessage);
			} catch (IOException e1) {
				e1.printStackTrace();
				lastMessage = KILL_MSG;
				break;
			}
		}
	}
}
