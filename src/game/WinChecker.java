package game;

public class WinChecker {

	public int[][] board;
	public int size;
	public int x1;
	public int y1;
	public int x2;
	public int y2;


	public WinChecker(int[][] myGrid, int size){
		this.board = myGrid;
		this.size = size;
		this.x1 = 0;
		this.x2 = 0;
		this.y1 = 0;
		this.y2 = 0;
	}

	public boolean checkWin(int x , int y){
		return horizontalCheck(x,y) || verticalCheck(x,y) || diagonalChecks(x,y);

	}

	public boolean validSquare(int x, int y){

		boolean valid = true;
		if( x < 0 || x > size-1 || y < 0 || y > size-1 || board[x][y] == 3  || board[x][y] == 4){
			valid = false;
		}

		return valid;
	}

	public boolean horizontalCheck(int x, int y) {
		int playerNumber = board[x][y];
		int counter = 0;
		boolean addCount = true;

		for (int subX = x - 3, i = 0  ; i < 8; i++, subX++) {

			if(validSquare(subX,y)){
				if(board[subX][y] != playerNumber){
					addCount = false;
					counter = 0;
				}
			}else{
				addCount = false;
				counter = 0;
			}

			if(addCount){
				counter++;
			}else{

			}
			addCount = true;
			if(counter ==4){
				x2 = subX;
				y2 = y;
				x1 = subX - 3;
				y1 = y;
				return true;
			}
		}
		return false;
	}

	public boolean verticalCheck(int x, int y) {
		int playerNumber = board[x][y];
		int counter = 0;
		boolean addCount = true;

		for (int subY = y - 3, i = 0  ; i < 8; i++, subY++) {

			if(validSquare(x,subY)){
				if(board[x][subY] != playerNumber){
					addCount = false;
					counter = 0;
				}
			}else{
				addCount = false;
				counter = 0;
			}

			if(addCount){
				counter++;
			}
			addCount = true;
			if(counter ==4){
				x2 = x;
				y2 = subY;
				x1 = x;
				y1 = subY -3;
				return true;
			}
		}
		return false;
	}

	public boolean diagonalChecks(int x, int y) {
		int playerNumber = board[x][y];
		int counterRightDiagonal = 0;
		int counterLeftDiagonal = 0;
		boolean addCountLeft = true;
		boolean addCountRight = true;

		//loop for down right.
		for (int subY = y - 3, subXRight = x - 3, subXLeft = x + 3, i = 0  ; i < 8; i++, subY++, subXRight++, subXLeft--) {

			if(validSquare(subXRight,subY)){
				if(board[subXRight][subY] != playerNumber){
					addCountRight = false;
					counterRightDiagonal = 0;
				}
			}else{
				addCountRight = false;
				counterRightDiagonal = 0;
			}

			if(validSquare(subXLeft,subY)){
				if(board[subXLeft][subY] != playerNumber){
					addCountLeft = false;
					counterLeftDiagonal = 0;
				}
			}else{
				addCountLeft = false;
				counterLeftDiagonal = 0;
			}

			if(addCountRight){
				counterRightDiagonal++;
			}
			if(addCountLeft){
				counterLeftDiagonal++;
			}

			addCountRight = true;
			addCountLeft = true;

			if(counterRightDiagonal ==4 ){
				x2 = subXRight;
				y2 = subY;
				x1 = subXRight-3;
				y1 = subY -3;
				return true;
			}else if(counterLeftDiagonal ==4){
				x2 = subXLeft;
				y2 = subY;
				x1 = subXLeft +3;
				y1 = subY -3;
				return true;
			}
		}
		return false;
	}
}
