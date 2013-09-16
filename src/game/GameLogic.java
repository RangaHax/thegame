package game;

public class GameLogic {

	private int[][] board;
	public int gameSize;
	public WinChecker checker;
	public int playerNumber;
	public int playerOneScore;
	public int playerTwoScore;

	public GameLogic() {
		gameSize = 20;
		board = new int[gameSize][gameSize];
		this.checker = new WinChecker(board, gameSize);
		this.playerOneScore = 0;
		this.playerTwoScore = 0;
	}

	public static final int NORMAL = 0; // a normal move with no other outcomes
	public static final int WIN = 1; // a move wich results in a "connect 4"
	public static final int INVALID = 2;// a move which is no allowed

	/**
	 * This method will change the board with a player tile (not a win tile)
	 *
	 * @param player
	 * @param column
	 * @return
	 */
	public int makeMove(int player, int column) {
		int yValue = pickColumn(player, column);
		playerNumber = player;

		if (yValue != -1) {
			if (checker.checkWin(column, yValue)) {
				return WIN;
			}
			return NORMAL;
		} else {
			return INVALID;
		}
	}

	public int pickColumn(int player, int columnNumber) {
		if(columnNumber < 0 || columnNumber >= gameSize) {
			return -1;
		}
		for (int i = gameSize - 1; i > -1; i--) {

			if (board[columnNumber][i] == 0) {
				board[columnNumber][i] = player;
				return i;
			}
		}
		return -1;
	}

	/**
	 * This method will only be called after makeMove() has returned WIN this
	 * should return an int array of length 4 [x1,y1,x2,y2] These coords are the
	 * winning streak the player achived when he called makeMove
	 *
	 * This method will change all unclaimed squares to claimed ones (so it will
	 * remember the player number from makeMove()
	 *
	 * @return
	 */
	public int[] getWinCoords() {
		int score = lockZone(playerNumber);
		if(playerNumber == 1){
			playerOneScore += score;
		}else{
			playerTwoScore += score;
		}
		//lockZone(playerNumber);
		return new int[]{checker.x1, checker.y1, checker.x2, checker.y2};
	}
	public int getPlayerOneScore(){
		return this.playerOneScore;
	}

	public int getPlayerTwoScore(){
		return this.playerTwoScore;
	}

	public int lockZone(int winnerNumber){
		int count = 0;
		int nonWinnerNumber = ((winnerNumber*3)%2)+1;

		for (int i = 0; i < gameSize; i++) {
			for (int j = 0; j < gameSize; j++) {
				if(board[i][j] == winnerNumber || board[i][j] == nonWinnerNumber){
					count++;
					board[i][j] = winnerNumber + 2;
				}
			}
		}
		return count;
	}

	/**
	 * This will check if there are any squares available for the game to
	 * continue
	 *
	 * @return
	 */
	public boolean fullBoard() {

		for (int i = 0; i < gameSize; i++) {
			int squareValue = board[i][0];
			if (squareValue < 1) {
				return false;
			}
		}
		return true;
	}

	public int[][] getBoard() {
		return duplicate(board);
	}

	private int[][] duplicate(int[][] old) {
		int[][] newBoard = new int[old.length][old[0].length];
		for (int x = 0; x < old.length; x++) {
			for (int y = 0; y < old[0].length; y++) {
				newBoard[x][y] = old[x][y];
			}
		}
		return newBoard;
	}
}

