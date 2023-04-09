package game;

import java.awt.Color;

public class BlobGoal extends Goal{

	public BlobGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		int score = 0;
		Color[][] flattenBoard = board.flatten();
		int boardSize = flattenBoard.length;
		boolean[][] visited = new boolean[boardSize][boardSize];

		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				score = Math.max(score, undiscoveredBlobSize(i,j,flattenBoard,visited));
			}
		}
		return score;
	}

	@Override
	public String description() {
		return "Create the largest connected blob of " + GameColors.colorToString(targetGoal) 
		+ " blocks, anywhere within the block";
	}

	public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
		if (unitCells[i][j] != targetGoal || visited[i][j]) {			//base case - not at target colour
			return 0;
		}
		int targetCount = 1;		//already at target colour so start 1
		visited[i][j] = true;		//already visited this block so visited is true

		if ((i-1) >= 0 && unitCells[i-1][j].equals(targetGoal) && !visited[i-1][j]) {
			targetCount += undiscoveredBlobSize(i-1, j, unitCells, visited);
		}
		if ((i+1 <= unitCells.length-1) && unitCells[i+1][j].equals(targetGoal) && !visited[i+1][j]) {
			targetCount += undiscoveredBlobSize(i+1, j, unitCells, visited);
		}
		if ((j-1 >= 0) && unitCells[i][j-1].equals(targetGoal) && !visited[i][j-1]) {
			targetCount += undiscoveredBlobSize(i,j-1, unitCells, visited);
		}
		if ((j+1 <= unitCells.length-1) && unitCells[i][j+1].equals(targetGoal) && !visited[i][j+1]) {
			targetCount += undiscoveredBlobSize(i, j+1, unitCells, visited);
		}
	return targetCount;
	}
}
