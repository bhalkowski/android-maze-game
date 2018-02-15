package benjaminhalkowski.cs301.cs.wm.edu.amazebybenjaminhalkowski.generation;

import java.util.ArrayList;

/*
 * This class will create a maze by use of Eller's method.
 * The maze will be to specific dimensions with a solution
 * based on a distance matrix.
 * We implement runnable so we can form the maze while running
 * app. MazeFactory has a MazeBuilder that handles thread management.
 * 
 * 
 */

public class MazeBuilderEller extends MazeBuilder implements Runnable{
	
	public ArrayList<Integer> setArrays = new ArrayList<Integer>();
	
	public MazeBuilderEller() {
		super();
		System.out.println("MazeBuilderEller uses Ellers's algorithm to generate maze.");
	}
	
	public MazeBuilderEller(boolean det) {
		super(det);
		System.out.println("MazeBuilderEller uses Eller's algorithm to generate maze.");
	}
	/*
	 * Here we perform the necessary steps for the Eller Algorithm.
	 * 
	 * We will just be deleting walls as we progress down
	 * the rows of the initially fully-walled maze.
	 * 
	 * (non-Javadoc)
	 * @see generation.MazeBuilder#generatePathways()
	 */
	
	@Override
	protected void generatePathways() {
		// start with the first row
		// combine cells into sets randomly
		// randomly decide which one from each set looses bottom wall (at least one)
		// Move to next row
		
		// Use mazeFactory method (waitTillDelivered) to make sure the maze has
		// finished processing and we can begin testing.
		
		for (int cur_row = 0; cur_row < height; cur_row ++) {
			// iterate through row array twice 
			// once to combine 'cells' into sets
			// then again to destroy bottom walls

			for (int cur_col = 0; cur_col < width; cur_col ++) {
				
				int cellNum = cur_row*width + cur_col;
				int cellNeighbor = cellNum+1;
				
				deleteWallEllerOrAdd(cellNum, cellNeighbor, cur_col, CardinalDirection.East);	
			}
			int start = cur_row*width;
			int end = cur_row*width + width -1;
			if (cur_row == height - 1) {
				for(; start < end ; start ++) {
					if (!sameSet(start, start+1)) {
						Wall curWall = new Wall(start % width, start / width, CardinalDirection.East);
						cells.deleteWall(curWall);
						joinSets(start, start+1);
					}
				}
			}
			else {
				for(; start <= end ; start ++) {
					if (!sameSet(start, start+1)) {
						Wall curWall = new Wall(start % width, start / width, CardinalDirection.South);
						cells.deleteWall(curWall);
						joinSets(start, start+1);
					}
				}
			}	

		}
		
		/*
		 * Please see test for details on this method for testing my Ellers Algorithm
		 * I was not able to get junit functioning with a complete instance of a maze and so 
		 * I had to settle for this print style form of testing. It was sufficient to get a working 
		 * algorithm for the reasons outline in my test for MazeFactory.
		System.out.print(setArrays.toString());
		*/
	}
	
	/*
	 * Method to delete the wall if possible (for a given direction) and, if not, 
	 * add the wall to the aforementioned setArrys list
	 */
	private boolean deleteWallEllerOrAdd(int cellNum, int finalNum, int cur_col, CardinalDirection dir) {
		boolean canDelete = true;
		if (cur_col == width - 1 && dir == CardinalDirection.East) {
			canDelete = false;
		}
		if (sameSet(cellNum, finalNum)) {
			canDelete = false;
		}
		Wall curWall = new Wall(cellNum % width, cellNum / width, dir);
		int rand = random.nextIntWithinInterval(0, 10);
		if (rand < 5 && canDelete) {
			cells.deleteWall(curWall);
			joinSets(cellNum, finalNum);
			return true; // true if we deleted
		}
		else {
			if (!setArrays.contains(cellNum)) {
				setArrays.add(cellNum);
				setArrays.add(null);
			}
		}
		return false; // return false if we did not delete
	}
	
	/* 
	 * Method to join sets by reorganizing the setArrays and null pointers within
	 */
	private void joinSets(int x1, int x2) {
		// add the sets of x1 and x2
		// choose the lower index and remove and add from other
		int x1Index = setArrays.indexOf(x1);
		int x2Index = setArrays.indexOf(x2);
		// x1 and x2 are not in a set
		if (x1Index == -1 && x2Index == -1) {
			setArrays.add(x1); setArrays.add(x2);
			setArrays.add(null);
		}
		else if (x1Index == -1) {
			// we find the set of x2 and add x1 to it
			addToSet(x2Index , x1);
		}
		else if (x2Index == -1) {
			// we find the set of x1 and add x2, who is new to the set, to it
			addToSet(x1Index , x2);
		}
		// they are both in list, transfer higher set to lower indexed set
		else {
			if ( !sameSet(x1,x2)) {
				int lowerIndex = returnLowerIndex(x1Index, x2Index);
				int higherIndex = returnHigherIndex(x1Index, x2Index);
				int higherPrevNullIndex = 0;
				
				for (int i = higherIndex;i >= 0 ; i--) {
					if (setArrays.get(i) == null) {
						higherPrevNullIndex = i;
						break;
					}
				}
				ArrayList<Integer> higherList = new ArrayList<Integer>();
				while(true) {
					higherList.add(setArrays.get(higherPrevNullIndex+1));
					setArrays.remove(higherPrevNullIndex+1);
					if (setArrays.get(higherPrevNullIndex+1) == null) {
						setArrays.remove(higherPrevNullIndex+1);
						break;
					}
				}
				
				for (Integer temp : higherList) {
					addToSet(lowerIndex, temp);
				}
			}
		}
	}
	
	private void addToSet(int setIndex, int xValue) {
		setArrays.add(setIndex, xValue);
	}
	
	private int returnLowerIndex(int index1, int index2) {
		if (index1 < index2)
			return index1;
		else
			return index2;
	}
	private int returnHigherIndex(int index1, int index2) {
		if (index1 > index2)
			return index1;
		else
			return index2;
	}
	private boolean sameSet(int x1, int x2) {	
		if (!setArrays.contains(x1) || !setArrays.contains(x2)) {
			return false;
		}
		int lowerIndex = returnLowerIndex(setArrays.indexOf(x1) , setArrays.indexOf(x2));
		int higherIndex = returnHigherIndex(setArrays.indexOf(x1) , setArrays.indexOf(x2));
		for (int i = lowerIndex; i <= higherIndex; i ++) {
			// should always have null pointer at the end of the list
			// null pointers separate sets, if we reach null pointer, not in same set
			if (setArrays.get(i) == null) {
				return false;
			}
			else if (setArrays.get(i)== setArrays.get(higherIndex)) {
				return true;
			}
		}

		return false;
	}	
	
	public ArrayList<Integer> getSetArrays(){
		return setArrays;
	}
	
	
}
