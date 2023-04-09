package game;

import java.awt.Color;

public class PerimeterGoal extends Goal {

	public PerimeterGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		int score = 0;
		Color[][] flatBoard = board.flatten();
		int boardSize = flatBoard.length;
		boolean[][] visited = new boolean[boardSize][boardSize];
		BlobGoal blobGoal = new BlobGoal(this.targetGoal);        //so we can call undiscoveredBlobSize

		for (int i = 1; i < boardSize - 1; i++) {                //setting all middle blocks to visited
			for (int j = 1; j < boardSize - 1; j++) {
				visited[i][j] = true;
			}
		}
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				score += blobGoal.undiscoveredBlobSize(i, j, flatBoard, visited);
				if (i == 0 && j == 0 && flatBoard[0][0].equals(targetGoal))  {
					score += 1;
				} else if (i == 0 && j == boardSize-1 && flatBoard[0][boardSize-1].equals(targetGoal)) {
					score += 1;
				} else if (i == boardSize-1 && j == 0 && flatBoard[boardSize-1][0].equals(targetGoal)) {
					score += 1;
				} else if (i == boardSize-1 && j == boardSize-1 && flatBoard[boardSize-1][boardSize-1].equals(targetGoal)) {
					score += 1;
				}
			}
		}
		return score;
	}


	@Override
	public String description() {
		return "Place the highest number of " + GameColors.colorToString(targetGoal) 
		+ " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
	}

}
