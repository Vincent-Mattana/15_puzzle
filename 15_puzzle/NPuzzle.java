//777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777
//                               			V.W.F Mattana 21128707
//777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777
import java.awt.*;
import javax.swing.*;
import java.util.*;
//======================================================================================================
//                             					NPuzzle PUZZLE
//======================================================================================================
public class NPuzzle
{
	//******************************************************************************************************
	//											   DECLARATIONS
	//******************************************************************************************************
	private int ROWS = 3;
	private int COLUMNS = 3;
	private int TOTAL_SQUARESS = ROWS*COLUMNS;
	public int[][] grid = new int[ROWS][COLUMNS];
	public int[][] solution = new int[ROWS][COLUMNS];
	public int[][] start = new int[ROWS][COLUMNS];
	public Heap openList;
	public ArrayList<Node> closedList;

	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                         DEFAULT CONSTRUCTOR
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public NPuzzle()
	{
		startText();
	}

	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                            Start Text	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public void startText()
	{
		System.out.println("");
		System.out.println("**************************************");
		System.out.println("            N-PUZZLE SOLVER");
		System.out.println("**************************************");
		System.out.println("         V.W.F Mattana 2013");
		System.out.println("\n");
		System.out.println("Please select the N: \n(The number or rows and columns in the puzzle)");
		Scanner sc = new Scanner(System.in);
		int n=sc.nextInt();
		// try{
			// Integer.parseInt(n);
		// }
		// catch(NumberFormatException ex){
			// System.out.println("Its not a valid Integer");
		// }
		int square = (n*n)-1;
		System.out.println("You have selected a "+n+"x"+n+" puzzle. This is known as the "+square+"-Puzzle.");
		System.out.println("");
		ROWS = n;
		COLUMNS = n;
		TOTAL_SQUARESS = ROWS*COLUMNS;
		grid = new int[ROWS][COLUMNS];
		solution = new int[ROWS][COLUMNS];
		start = new int[ROWS][COLUMNS];
		System.out.println("The grid looks as follows before you shuffle the grid:");
		setupGrid();
		display(grid);
		System.out.println("This is also the goal state.");
		System.out.println("");
		System.out.println("Please select the number of random moves to use while shuffling the game:");
		int shuffles=sc.nextInt();
		System.out.println("Shuffling...");
		shuffle(shuffles, grid);
		System.out.println("Your starting state looks like this:");
		display(grid);
		System.out.println("");
		System.out.println("Hit the 'return' key to solve the puzzle.");
		Scanner waiter = new Scanner(System.in);
		String wait = waiter.nextLine();
		System.out.println("Solving...");
		solver();
		traceRoute();
		System.out.println("Fin!");
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                            GAME SETUP	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public void setupGrid()
	{
		int count=1;
		for (int i = 0; i < ROWS; i++)
		{
			for (int j = 0; j < COLUMNS; j++)
			{
				grid[i][j] = count;
				count++;
			}
		}
		grid[ROWS-1][COLUMNS-1]=0;
		copy(grid, solution);
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                           COPY ARRAY	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public void copy(int[][] arrayA, int[][] arrayB) //Copy A to B
	{
		for(int i = 0; i < arrayA.length; i++)
		{
			int[] arrayA1 = arrayA[i];
			int   aLength = arrayA1.length;
			arrayB[i] = new int[aLength];
			System.arraycopy(arrayA1, 0, arrayB[i], 0, aLength);
		}
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                           FIND NUMBER	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public Coordinate findNumber(int x, int[][] array)
	{
		Coordinate location = new Coordinate();
		for (int i = 0; i < ROWS; i++)
		{
			for (int j = 0; j < COLUMNS; j++)
			{
				if(array[i][j] == x)
				{
					location.setCoordinate(i, j);
					break;
				}
			}
		}
		return location;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                           FIND NUMBER	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public Coordinate findNumberInSolution(int x)
	{
		Coordinate locationSolution = new Coordinate();
		for (int i = 0; i < ROWS; i++)
		{
			for (int j = 0; j < COLUMNS; j++)
			{
				if(solution[i][j] == x)
				{
					locationSolution.setCoordinate(i, j);
					break;
				}
			}
		}
		return locationSolution;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                          CHECK MOVEABLE	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public boolean checkMoveable(int z, int[][] array )
	{
		Coordinate location = new Coordinate();
		int x,y;
		location = findNumber(z, array);
		x = location.getX();
		y = location.getY();
		if (x==0 && y==0) // TOP LEFT CORNER
		{
			if(array[x][y+1] == 0) // RIGHT
				return true;
			if (array[x+1][y] == 0) //DOWN
				return true;
		}
		else if (x==0 && y==(COLUMNS-1)) // TOP RIGHT CORNER
		{
			if(array[x][y-1] == 0) // LEFT
				return true;
			if (array[x+1][y] == 0) //DOWN
				return true;
		}
		else if (x==(ROWS-1) && y==0) // BOTTOM LEFT CORNER
		{
			if(array[x-1][y] == 0) //UP
				return true;
			if(array[x][y+1] == 0) // RIGHT
				return true;
		}
		else if (x==(ROWS-1) && y==(COLUMNS-1)) // BOTTOM RIGHT CORNER
		{
			if(array[x-1][y] == 0) //UP
				return true;
			if(array[x][y-1] == 0) // LEFT
				return true;
		}
		else if (x == 0)
		{
			if (array[x+1][y] == 0) //DOWN
				return true;
			if(array[x][y-1] == 0) // LEFT
				return true;
			if(array[x][y+1] == 0) // RIGHT
				return true;
		}
		else if (x == (ROWS-1))
		{
			if(array[x-1][y] == 0) //UP
				return true;
			if(array[x][y-1] == 0) // LEFT
				return true;
			if(array[x][y+1] == 0) // RIGHT
				return true;
		}
		else if (y == 0)
		{
			if(array[x-1][y] == 0) //UP
				return true;
			if (array[x+1][y] == 0) //DOWN
				return true;
			if(array[x][y+1] == 0) // RIGHT
				return true;
		}
		else if (y == (COLUMNS-1))
		{
			if(array[x-1][y] == 0) //UP
				return true;
			if (array[x+1][y] == 0) //DOWN
				return true;
			if(array[x][y-1] == 0) // LEFT
				return true;
		}
		else
		{
			if(array[x-1][y] == 0) //UP
				return true;
			if (array[x+1][y] == 0) //DOWN
				return true;
			if(array[x][y-1] == 0) // LEFT
				return true;
			if(array[x][y+1] == 0) // RIGHT
				return true;
		}
		
		return false;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             	MOVE	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public void move(int z, int[][] array)
	{
		Coordinate location = new Coordinate();
		Coordinate location0 = new Coordinate();
		int x, y, x0, y0;

		if(checkMoveable(z, array)==true)
		{
			location = findNumber(z, array);
			x = location.getX();
			y = location.getY();
			location0 = findNumber(0, array);
			x0 = location0.getX();
			y0 = location0.getY();
			array[x][y]=0;
			array[x0][y0]=z;
		}
		else
		{
			System.out.println("No move available.");
		}
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             	MOVE STATE
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public int[][] moveSim(int z, int[][] newArray)
	{
		int[][] array = new int[ROWS][COLUMNS];
		copy(newArray, array);
		Coordinate location = new Coordinate();
		Coordinate location0 = new Coordinate();
		int x, y, x0, y0;

		if(checkMoveable(z, array)==true)
		{
			location = findNumber(z, array);
			x = location.getX();
			y = location.getY();
			location0 = findNumber(0, array);
			x0 = location0.getX();
			y0 = location0.getY();
			array[x][y]=0;
			array[x0][y0]=z;
			return array;
		}
		else
		{
			System.out.println("No move available.");
			return array;
		}
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                          FIND MOVEABLE	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public int[] findMoveable(int[][] array)
	{
		Coordinate location = new Coordinate();
		int x, y;
		int[] moveable;
		location = findNumber(0, array);
		x = location.getX();
		y = location.getY();
		
		if (x==0 && y==0) // TOP LEFT CORNER
		{
			moveable = new int[2];
			moveable[0] = (array[x][y+1]); // RIGHT
			moveable[1] = (array[x+1][y]); //DOWN

		}
		else if (x==0 && y==(COLUMNS-1)) // TOP RIGHT CORNER
		{
			moveable = new int[2];
			moveable[0] = (array[x][y-1]); // LEFT
			moveable[1] = (array[x+1][y]); //DOWN
		}
		else if (x==(ROWS-1) && y==0) // BOTTOM LEFT CORNER
		{
			moveable = new int[2];
			moveable[0] = (array[x-1][y]); //UP
			moveable[1] = (array[x][y+1]); // RIGHT
		}
		else if (x==(ROWS-1) && y==(COLUMNS-1)) // BOTTOM RIGHT CORNER
		{
			moveable = new int[2];
			moveable[0] = (array[x-1][y]); //UP
			moveable[1] = (array[x][y-1]); // LEFT
		}
		else if (x == 0)
		{
			moveable = new int[3];
			moveable[0] = (array[x+1][y]); //DOWN
			moveable[1] = (array[x][y-1]); // LEFT
			moveable[2] = (array[x][y+1]); // RIGHT
		}
		else if (x == (ROWS-1))
		{
			moveable = new int[3];
			moveable[0] = (array[x-1][y]); //UP
			moveable[1] = (array[x][y-1]); // LEFT
			moveable[2] = (array[x][y+1]); // RIGHT
		}
		else if (y == 0)
		{
			moveable = new int[3];
			moveable[0] = (array[x-1][y]); //UP
			moveable[1] = (array[x+1][y]); //DOWN
			moveable[2] = (array[x][y+1]); // RIGHT
		}
		else if (y == (COLUMNS-1))
		{
			moveable = new int[3];
			moveable[0] = (array[x-1][y]); //UP
			moveable[1] = (array[x+1][y]); //DOWN
			moveable[2] = (array[x][y-1]); // LEFT
		}
		else
		{
			moveable = new int[4];
			moveable[0] = (array[x-1][y]); //UP
			moveable[1] = (array[x+1][y]); //DOWN
			moveable[2] = (array[x][y-1]); // LEFT
			moveable[3] = (array[x][y+1]); // RIGHT
		}
		return moveable;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             SHUFFLE	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public void shuffle(int x, int[][] array)
	{
		Random rnd1 = new Random();
		int rndX;
		for(int i =0;i<x;i++)
		{
			rndX = rnd1.nextInt(findMoveable(array).length);
			move(findMoveable(array)[rndX], array);
		}
		copy(array, start);
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             DISPLAY	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public void display(int[][] array)
	{
		for (int i =0; i < ROWS; i++) 
		{
			for (int j = 0; j < COLUMNS; j++) 
			{
				System.out.print(" " + array[i][j]);
			}
			System.out.println("");
		}
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             DISPLAY NODE
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public void displayNode(Node inputNode)
	{
		display(inputNode.getArray());
		System.out.println(" H = "+inputNode.getH()+", G = "+inputNode.getG()+", F = "+inputNode.getF());
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                              GET GRID	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public int[][] getGrid()
	{
		return grid;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                              GET ROWS	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public int getROWS()
	{
		return ROWS;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                              GET COLUMNS	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public int getCOLUMNS()
	{
		return COLUMNS;
	}
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             FIND H
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public int findH(int z, int[][] array)
	{
		Coordinate location = new Coordinate();
		Coordinate locationSolution = new Coordinate();
		int x, y, xSol, ySol, diff;
		location = findNumber(z, array);
		x = location.getX();
		y = location.getY();
		locationSolution = findNumber(z, solution);
		xSol = locationSolution.getX();
		ySol = locationSolution.getY();
		diff = (Math.abs((x-xSol)))+(Math.abs((y-ySol)));
		return diff;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             STATE H
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public int stateH(int[][] array)
	{
		int sum=0;
		for (int i = 0; i < ROWS; i++)
		{
			for (int j = 0; j < COLUMNS; j++)
			{
				sum += findH(array[i][j], array);
			}
		}
		return sum;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             FIND G	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public void findG(Node stateNode)
	{
		
	}

	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             FIND F
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public void findF(Node stateNode)
	{
		
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             TO NODE
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public Node toNode(int[][] array)
	{
		Node newNode = new Node(array);
		newNode.setH(stateH(array));
		return newNode;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                      		SOLVER
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public void solver()
	{
		int count = 0;
		Node startNode = new Node(start);
		Node goalNode = new Node(solution);
		startNode.setG(0);
		startNode.setH(stateH(start));
		startNode.setF();
		openList = new Heap();
		closedList = new ArrayList<Node>();
		
		openList.add(startNode);
		
		while(!openList.isEmpty())
		{
			Node currentNode = new Node();
			Node copiedNode = new Node();
			currentNode.copyNode(openList.pop());
			//1
			copiedNode.copyNode(currentNode);
			closedList.add(copiedNode);
			//1
			int numberofchildren = findSuccessors(currentNode).size();
			ArrayList<Node> children = findSuccessors(currentNode);
			for(int i=0;i<numberofchildren;i++)
			{	
				int openListIndex = searchOpenList(children.get(i));
				int closedListIndex = searchClosedList(children.get(i));
				Node copiedChild = new Node();
				
				if(closedListIndex != -1) // if(children.get(i).getArray() is on closed list, ignore it and continue.
					continue;
				if(openListIndex == -1) // if(children.get(i).getArray() isn't on the open list, Make currentNode the parent of this child. Also record F, G, & H values for this child, & add it to the open list. 
				{
					copiedChild.copyNode(children.get(i));
					copiedChild.setG(currentNode.getG()+1);
					copiedChild.setH(stateH(children.get(i).getArray()));
					copiedChild.setF();
					copiedChild.setParent(currentNode);
					currentNode.addChild(copiedChild);
					openList.add(copiedChild);
				}
				if((openListIndex != -1) && (children.get(i)).getG() > (openList.get(openListIndex)).getG()) // if(children.get(i).getArray() is on the open list already, compare the G of the node on the open list to the G of this child. If the node on the open list wins (is smaller or equal), 
				{																								// move currentNode to the parent of the winning Node, and recalculate G and F of the winning Node. Resort openList.
					currentNode.copyNode(openList.get(openListIndex).getParent());
					copiedChild.setG(currentNode.getG()+1);
					copiedChild.setH(stateH(openList.get(openListIndex).getParent().getArray()));
					copiedChild.setF();
					openList = openList.resortHeap();
				}
			}
			//2
			//copiedNode.copyNode(currentNode);
			//closedList.add(copiedNode);
			//2
			if(achievedGoal(currentNode.getArray()))
			{
				System.out.println("Success after "+count+" iterations.");
				System.out.println("The solution is "+currentNode.getG()+" moves long.");
				break;
			}
			count++;
		}
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             TRACE ROUTE
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void traceRoute()
	{
		Node current = new Node();
		Node startNode = new Node();
		startNode.copyNode(closedList.get(closedList.size()-1));
		current.copyNode(closedList.get(closedList.size()-1));
		displayNode(current);
		int numberofMoves = current.getG();
		Node[] trace = new Node[numberofMoves];
		for(int j=0;j<numberofMoves;j++)
		{
			trace[j] = new Node();
		}
		for(int i=1;i<numberofMoves;i++)
		{
			trace[numberofMoves-i].copyNode(current);
			current.copyNode(current.getParent());
		}
		trace[0].copyNode(current);
		System.out.println("Your solution was solved by following this path: ");
		System.out.println("(Hit 'return' to move to the next state)");
		System.out.println("Starting Position:");
		displayNode(current.getParent());
		for(int j=0;j<numberofMoves;j++)
		{
			Scanner sc = new Scanner(System.in);
			String choice = sc.nextLine();
			displayNode(trace[j]);
		}
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             SEARCH OPENLIST
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public int searchOpenList(Node inputNode)
	{
		for(int i = 1; i<openList.size();i++)
		{
			if (inputNode.equalsArray(openList.get(i)))
				return i;
		}
		return -1;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             SEARCH CLOSEDLIST
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public int searchClosedList(Node inputNode)
	{
		for(int i = 0; i<closedList.size();i++)
		{
			if (inputNode.equalsArray(closedList.get(i)))
				return i;
		}
		return -1;
	}


	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             ACHIEVED GOAL
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public boolean achievedGoal(int[][] array)
	{
		return Arrays.deepEquals(array, solution);
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             FIND SUCCESSORS
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public ArrayList<Node> findSuccessors(Node inputNode)
	{
		Node workingNode = new Node();
		int[][] tmpArray = new int[ROWS][COLUMNS];
		copy(inputNode.getArray(), tmpArray);
		workingNode.setArray(tmpArray);

		ArrayList<Node> successors = new ArrayList<Node>();
		int numberOfMoves = findMoveable(tmpArray).length;
		int[] possibleMoves = findMoveable(tmpArray);
		int parentG = inputNode.getG();
		for(int i = 0; i < numberOfMoves; i++)
		{
			successors.add(toNode(moveSim(possibleMoves[i], tmpArray)));
			(successors.get(i)).setF();
			(successors.get(i)).setG(parentG+1);
		}
		return successors;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                             PURGE OF
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public void purgeOf(Node element)
	{
		openList.remove(element);
		closedList.remove(element);
	}
		
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                           	 MAIN
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public static void main(String[] Args)
	{
		NPuzzle puzzle = new NPuzzle();
	}
}