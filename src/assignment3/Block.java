package assignment3;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {
	
	private int xCoord;
	private int yCoord;
	private int size; // height/width of the square
	private int level; // the root (outter most block) is at level 0
	private int maxDepth; 	//deepest level allowed
	private Color color;	//color of block if not subdivided
	private Block[] children; // {UR, UL, LL, LR}

	public static Random gen = new Random(2);
 
 
	/*
	 * These two constructors are here for testing purposes. 
	 */
	public Block() {}
 
	public Block(int x, int y, int size, int lvl, int  maxD, Color c, Block[] subBlocks) {
		this.xCoord=x;
		this.yCoord=y;
		this.size=size;
		this.level=lvl;
		this.maxDepth = maxD;	
		this.color=c;
		this.children = subBlocks;
	}
 
 
	/*
	 * Creates a random block given its level and a max depth. 
	 * 
	 * xCoord, yCoord, size, and highlighted should not be initialized
	 * (i.e. they will all be initialized by default)
	 */
	public Block(int lvl, int maxDepth) {
		if (lvl < 0 || maxDepth < 0) {								//TODO Ask TA why maxdepth 0 isnt allowed
			throw new IllegalArgumentException("Cannot generate blocks at a level higher than the max depth.");
		}else if (lvl > maxDepth) {
			throw new IllegalArgumentException("Cannot generate blocks at a level higher than the max depth");
		}else {
			this.level = lvl;
			this.maxDepth = maxDepth;
			if (lvl == maxDepth) {            //first potential base case
				int colourIndex = gen.nextInt(GameColors.BLOCK_COLORS.length);
				this.color = GameColors.BLOCK_COLORS[colourIndex];
				this.children = new Block[0];
			} else {
				double subDecider = gen.nextDouble();
				if (subDecider >= Math.exp(-0.25 * this.level)) {            //second base case
					int colourIndex = gen.nextInt(GameColors.BLOCK_COLORS.length);
					this.color = GameColors.BLOCK_COLORS[colourIndex];
					this.children = new Block[0];
				} else {                                                     //recursion case
					this.children = new Block[4];
					this.color = null;
					for (int i = 0; i < this.children.length; i++) {
						this.children[i] = new Block(lvl + 1, maxDepth);
					}
				}
			}
		}
	}


	/*
	  * Updates size and position for the block and all of its sub-blocks, while
	  * ensuring consistency between the attributes and the relationship of the 
	  * blocks. 
	  * 
	  *  The size is the height and width of the block. (xCoord, yCoord) are the 
	  *  coordinates of the top left corner of the block. 
	 */
	public void updateSizeAndPosition (int size, int xCoord, int yCoord) {					//TODO edge case for negative children
	  if (size <= 0 || size % 2 != 0) {												//size edge cases
		  throw new IllegalArgumentException("Invalid input for size");
	  }
	  else if (xCoord < 0 || yCoord < 0) {										//coordinate edge cases
		  throw new IllegalArgumentException("Invalid input for coordinates.");
	  }
	  else if (this.children.length == 0) {				//base case: if 0 children
		  this.size = size;
		  this.xCoord = xCoord;
		  this.yCoord = yCoord;
		  if (this.color == null) {
			  throw new IllegalArgumentException("A block with no children must have a colour");
		  }
	  }
	  else if (this.children.length != 0) {
		  if (this.color != null) {
			  throw new IllegalArgumentException("A block with children must not have a colour");
		  }
		  else if (this.children.length != 4) {
			  throw new IllegalArgumentException("A block has either 0 or 4 children");
		  }
		  for (int i = 0; i < this.children.length; i++) {
			  if (this.children[i].level != this.level + 1) {
				  throw new IllegalArgumentException("Level of child block should be one higher than the parent block");
			  }
			  else if (this.children[i].maxDepth != this.maxDepth) {
				  throw new IllegalArgumentException("Child and parent block must have the same max depth");
			  }
		  }
	  }
	  if (this.children.length == 4){									//recursion step: has 4 children
		  this.size = size;
		  this.xCoord = xCoord;
		  this.yCoord = yCoord;

		  //UR coordinates
		  this.children[0].xCoord = this.xCoord + this.size / 2;
		  this.children[0].yCoord = this.yCoord;
		  this.children[0].updateSizeAndPosition(this.size / 2, this.children[0].xCoord, this.children[0].yCoord);

		  //UL coordinates
		  this.children[1].xCoord = this.xCoord;
		  this.children[1].yCoord = this.yCoord;
		  this.children[1].updateSizeAndPosition(this.size / 2, this.children[1].xCoord, this.children[1].yCoord);

		  //LL coordinates
		  this.children[2].xCoord = this.xCoord;
		  this.children[2].yCoord = this.yCoord + this.size / 2;
		  this.children[2].updateSizeAndPosition(this.size / 2, this.children[2].xCoord, this.children[2].yCoord);

		  //LR coordinates
		  this.children[3].xCoord = this.xCoord + this.size / 2;
		  this.children[3].yCoord = this.yCoord + this.size / 2;
		  this.children[3].updateSizeAndPosition(this.size / 2, this.children[3].xCoord, this.children[2].yCoord);
	  }
	}
	/*
  	* Returns a List of blocks to be drawn to get a graphical representation of this block.
  	* 
  	* This includes, for each undivided Block:
  	* - one BlockToDraw in the color of the block
  	* - another one in the FRAME_COLOR and stroke thickness 3
  	* 
  	* Note that a stroke thickness equal to 0 indicates that the block should be filled with its color.
  	*  
  	* The order in which the blocks to draw appear in the list does NOT matter.
  	*/
	public ArrayList<BlockToDraw> getBlocksToDraw() {
		ArrayList<BlockToDraw> blockList = new ArrayList<>();
		if (this.color != null) {
			BlockToDraw myBlock = new BlockToDraw(this.color, this.xCoord, this.yCoord, this.size, 0);
			BlockToDraw frameBlock = new BlockToDraw(GameColors.FRAME_COLOR, this.xCoord, this.yCoord, this.size, 3);
			blockList.add(myBlock);
			blockList.add(frameBlock);
		} else {
			ArrayList<BlockToDraw> childOne = this.children[0].getBlocksToDraw();
			ArrayList<BlockToDraw> childTwo = this.children[1].getBlocksToDraw();
			ArrayList<BlockToDraw> childThree = this.children[2].getBlocksToDraw();
			ArrayList<BlockToDraw> childFour = this.children[3].getBlocksToDraw();
			blockList.addAll(childOne);
			blockList.addAll(childTwo);
			blockList.addAll(childThree);
			blockList.addAll(childFour);
		}
		return blockList;
	}


	/*
	 * This method is provided and you should NOT modify it. 
	 */
	public BlockToDraw getHighlightedFrame() {
		return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
	}
 
 
 
	/*
	 * Return the Block within this Block that includes the given location
	 * and is at the given level. If the level specified is lower than 
	 * the lowest block at the specified location, then return the block 
	 * at the location with the closest level value.
	 * 
	 * The location is specified by its (x, y) coordinates. The lvl indicates 
	 * the level of the desired Block. Note that if a Block includes the location
	 * (x, y), and that Block is subdivided, then one of its sub-Blocks will 
	 * contain the location (x, y) too. This is why we need lvl to identify 
	 * which Block should be returned. 
	 * 
	 * Input validation: 
	 * - this.level <= lvl <= maxDepth (if not throw exception)
	 * - if (x,y) is not within this Block, return null.
	 */
	public Block getSelectedBlock(int x, int y, int lvl) {
		/*
		 * ADD YOUR CODE HERE
		 */
		return null;
	}

	/*
	 * Swaps the child Blocks of this Block. 
	 * If input is 1, swap vertically. If 0, swap horizontally. 
	 * If this Block has no children, do nothing. The swap 
	 * should be propagate, effectively implementing a reflection
	 * over the x-axis or over the y-axis.
	 * 
	 */
	public void reflect(int direction) {
		/*
		 * ADD YOUR CODE HERE
		 */
	}
 

 
	/*
	 * Rotate this Block and all its descendants. 
	 * If the input is 1, rotate clockwise. If 0, rotate 
	 * counterclockwise. If this Block has no children, do nothing.
	 */
	public void rotate(int direction) {
		/*
		 * ADD YOUR CODE HERE
		 */
	}
 


	/*
	 * Smash this Block.
	 * 
	 * If this Block can be smashed,
	 * randomly generate four new children Blocks for it.  
	 * (If it already had children Blocks, discard them.)
	 * Ensure that the invariants of the Blocks remain satisfied.
	 * 
	 * A Block can be smashed iff it is not the top-level Block 
	 * and it is not already at the level of the maximum depth.
	 * 
	 * Return True if this Block was smashed and False otherwise.
	 * 
	 */
	public boolean smash() {
		/*
		 * ADD YOUR CODE HERE
		 */
		return false;
	}
 
 
	/*
	 * Return a two-dimensional array representing this Block as rows and columns of unit cells.
	 * 
	 * Return and array arr where, arr[i] represents the unit cells in row i, 
	 * arr[i][j] is the color of unit cell in row i and column j.
	 * 
	 * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
	 */
	public Color[][] flatten() {
		/*
		 * ADD YOUR CODE HERE
		 */
		return null;
	}

 
 
	// These two get methods have been provided. Do NOT modify them. 
	public int getMaxDepth() {
		return this.maxDepth;
	}
 
	public int getLevel() {
		return this.level;
	}


	/*
	 * The next 5 methods are needed to get a text representation of a block. 
	 * You can use them for debugging. You can modify these methods if you wish.
	 */
	public String toString() {
		return String.format("pos=(%d,%d), size=%d, level=%d", this.xCoord, this.yCoord, this.size, this.level);
	}

	public void printBlock() {
		this.printBlockIndented(0);
	}

	private void printBlockIndented(int indentation) {
		String indent = "";
		for (int i=0; i<indentation; i++) {
			indent += "\t";
		}

		if (this.children.length == 0) {
			// it's a leaf. Print the color!
			String colorInfo = GameColors.colorToString(this.color) + ", ";
			System.out.println(indent + colorInfo + this);   
		} 
		else {
			System.out.println(indent + this);
			for (Block b : this.children)
				b.printBlockIndented(indentation + 1);
		}
	}
 
	private static void coloredPrint(String message, Color color) {
		System.out.print(GameColors.colorToANSIColor(color));
		System.out.print(message);
		System.out.print(GameColors.colorToANSIColor(Color.WHITE));
	}

	public void printColoredBlock(){
		Color[][] colorArray = this.flatten();
		for (Color[] colors : colorArray) {
			for (Color value : colors) {
				String colorName = GameColors.colorToString(value).toUpperCase();
				if(colorName.length() == 0){
					colorName = "\u2588";
				}
				else{
					colorName = colorName.substring(0, 1);
				}
				coloredPrint(colorName, value);
			}
			System.out.println();
		}
	}
}