import random
import copy
import heapq # Use Python's heapq for the priority queue

# Placeholder imports for classes that need to be translated later
# from .heap import Heap # No longer needed
from node import Node # Changed from relative import
# from .coordinate import Coordinate

class NPuzzle:
    # Class variables corresponding to the Java version
    # Default size (can be changed by user input)
    _rows = 3
    _cols = 3
    _total_squares = _rows * _cols
    
    grid = []
    solution = []
    start = []
    
    # open_list: list[Node] = [] # The open list (priority queue) for A*, implemented as a heap
    # closed_list: set[Node] = set() # The closed list (set of visited states) for A*

    def __init__(self):
        """Initializes the NPuzzle instance by running the text-based setup."""
        self.start_text()

    def start_text(self):
        """Handles the initial setup and solving process via text console."""
        print("\n**************************************")
        print("            N-PUZZLE SOLVER")
        print("**************************************")
        print("         V.W.F Mattana 2013 (Python Port)")
        print("\n")
        
        while True:
            try:
                n_str = input("Please select the N (number of rows/columns):\n")
                n = int(n_str)
                if n > 1:
                    break
                else:
                    print("N must be greater than 1.")
            except ValueError:
                print("Invalid input. Please enter an integer.")

        self._rows = n
        self._cols = n
        self._total_squares = self._rows * self._cols
        square = self._total_squares - 1
        
        print(f"\nYou have selected a {n}x{n} puzzle. This is known as the {square}-Puzzle.")
        print("")

        # Initialize grids based on N
        self.grid = [[0] * self._cols for _ in range(self._rows)]
        self.solution = [[0] * self._cols for _ in range(self._rows)]
        self.start = [[0] * self._cols for _ in range(self._rows)]
        
        self.open_list = [] # Initialize open list as a list for heapq
        # Using a set for the closed list allows for efficient checking of visited states (O(1) average time)
        self.closed_list = set()    # Initialize closed list as a set

        print("The grid looks as follows before you shuffle the grid:")
        self.setup_grid()
        self.display(self.grid)
        print("This is also the goal state.")
        print("")

        while True:
            try:
                shuffles_str = input("Please select the number of random moves to use while shuffling:\n")
                shuffles = int(shuffles_str)
                if shuffles >= 0:
                    break
                else:
                    print("Number of shuffles cannot be negative.")
            except ValueError:
                print("Invalid input. Please enter an integer.")

        print("Shuffling...")
        self.shuffle(shuffles, self.grid) # Assumes shuffle method exists
        # Keep a copy of the starting state
        self.start = copy.deepcopy(self.grid) 
        
        print("Your starting state looks like this:")
        self.display(self.grid) # Assumes display method exists
        print("")
        input("Hit the 'Enter' key to solve the puzzle.") # Changed from Scanner wait
        
        print("Solving...")
        goal_node = self.solve() # Call the solver
        if goal_node:
            print(f"Goal reached in {goal_node.g} moves!")
            self._trace_route(goal_node) # Call trace_route to show the path
        else:
            print("Could not find a solution.")
            
        print("Fin!")

    # --- Helper Methods ---
    
    def _copy_grid(self, source_grid):
        """Creates a deep copy of a grid (2D list)."""
        return copy.deepcopy(source_grid)

    # --- Core Logic Methods (Translated from Java) ---

    def setup_grid(self):
        """Initializes the grid to the solved state (1 to N*N-1, with 0 at the end).
        Also sets the solution grid.
        Corresponds to setupGrid in Java.
        """
        count = 1
        for i in range(self._rows):
            for j in range(self._cols):
                self.grid[i][j] = count
                count += 1
        # Set the last cell to 0 (blank tile)
        self.grid[self._rows - 1][self._cols - 1] = 0
        # Set the solution state
        self.solution = copy.deepcopy(self.grid)


    def display(self, array):
        """Prints the puzzle grid to the console.
        Corresponds to display in Java.
        """
        # Corrected width calculation: 2 for initial "| " + 5 for each cell ("XX | ")
        line_width = 2 + self._cols * 5
        print("-" * line_width)
        for i in range(self._rows):
            print("| ", end="")
            for j in range(self._cols):
                # Print number or space for 0
                cell = str(array[i][j]) if array[i][j] != 0 else " " 
                # Basic padding for alignment (adjust if N gets very large)
                print(f"{cell:<2} |", end=" ") 
            print() # Newline at the end of the row
            print("-" * line_width)


    def shuffle(self, num_shuffles, grid):
        """Shuffles the grid by making a specified number of random valid moves.
        Starts from the solved state (which should be in grid before calling).
        Modifies the grid in place.
        Corresponds to shuffle in Java.
        """
        print(f"Performing {num_shuffles} random shuffles...")
        last_moved = -1 # Prevent immediately moving a tile back
        for _ in range(num_shuffles):
            moveable_tiles = self._find_moveable_tiles(grid)
            
            # Filter out the tile that was just moved, if possible
            possible_moves = [tile for tile in moveable_tiles if tile != last_moved]
            if not possible_moves:
                possible_moves = moveable_tiles # Use original list if filtering removed all options
            
            if not possible_moves:
                print("Warning: No moveable tiles found during shuffle? Grid state:")
                self.display(grid)
                break # Should not happen in a valid puzzle > 1x1
                
            # Choose a random tile to move
            tile_to_move = random.choice(possible_moves)
            self._move_tile(tile_to_move, grid) 
            last_moved = tile_to_move # Remember the tile just moved


    def _find_moveable_tiles(self, grid):
        """Finds all tile numbers that are adjacent to the empty spot (0).
        Corresponds to findMoveable in Java.
        Returns a list of moveable tile numbers.
        """
        moveable = []
        zero_pos = self.find_number(0, grid)
        if not zero_pos:
            return [] # Should not happen

        r, c = zero_pos
        
        # Check potential neighbors (Up, Down, Left, Right)
        potential_neighbors = []
        if r > 0: potential_neighbors.append((r - 1, c)) # Up
        if r < self._rows - 1: potential_neighbors.append((r + 1, c)) # Down
        if c > 0: potential_neighbors.append((r, c - 1)) # Left
        if c < self._cols - 1: potential_neighbors.append((r, c + 1)) # Right
            
        for nr, nc in potential_neighbors:
            tile_num = grid[nr][nc]
            moveable.append(tile_num)
            
        return moveable

    def find_number(self, num, array):
        """Finds the coordinates (row, col) of a number in the given grid.
        Returns a tuple (row, col) or None if the number is not found.
        Corresponds to findNumber in Java.
        """
        for r in range(self._rows):
            for c in range(self._cols):
                if array[r][c] == num:
                    return (r, c) # Returning tuple (row, col)
        return None # Not found

    def _is_tile_moveable(self, tile_num, grid):
        """Checks if the given tile number is adjacent to the empty spot (0).
        Corresponds to checkMoveable in Java.
        """
        if tile_num == 0: # The empty spot itself cannot be moved
            return False
            
        tile_pos = self.find_number(tile_num, grid)
        zero_pos = self.find_number(0, grid)

        if not tile_pos or not zero_pos:
            # Should not happen in a valid grid
            return False 

        # Check for adjacency (Manhattan distance of 1)
        return abs(tile_pos[0] - zero_pos[0]) + abs(tile_pos[1] - zero_pos[1]) == 1

    def _move_tile(self, tile_num, grid):
        """Swaps the specified tile number with the empty spot (0) in the grid.
        Assumes the move is valid (tile is adjacent to 0).
        Modifies the grid in place.
        Corresponds to move in Java.
        """
        tile_pos = self.find_number(tile_num, grid)
        zero_pos = self.find_number(0, grid)

        if tile_pos and zero_pos:
            # Swap values
            grid[tile_pos[0]][tile_pos[1]], grid[zero_pos[0]][zero_pos[1]] = \
                grid[zero_pos[0]][zero_pos[1]], grid[tile_pos[0]][tile_pos[1]]
        else:
            # Should not happen if move validity is checked beforehand
            print(f"Error: Cannot move tile {tile_num}. Position not found.")
            

    def _simulate_move(self, tile_num, grid):
        """Simulates moving a tile on a *copy* of the grid.
        Returns the new grid state after the move, or None if move is invalid.
        Corresponds to moveSim in Java.
        """
        if not self._is_tile_moveable(tile_num, grid):
            return None # Move is not possible
            
        # Create a copy to simulate the move on
        new_grid = self._copy_grid(grid)
        # Perform the move on the copy
        self._move_tile(tile_num, new_grid)
        return new_grid

    def is_goal_state(self, grid):
        """Checks if the given grid matches the solution state.
        Corresponds to achievedGoal in Java.
        """
        return grid == self.solution

    def get_rows(self):
        """Returns the number of rows.
        Corresponds to getROWS in Java.
        """
        return self._rows
        
    def get_cols(self):
        """Returns the number of columns.
        Corresponds to getCOLUMNS in Java.
        """
        return self._cols
        
    # --- Heuristic Calculation Methods ---

    def _find_number_in_solution(self, num):
        """Finds the coordinates (row, col) of a number in the solution grid.
        Helper for heuristic calculation. Corresponds to findNumberInSolution.
        """
        # The solution grid is static once setup, so we could potentially cache these
        # locations if performance becomes an issue for very large N.
        for r in range(self._rows):
            for c in range(self._cols):
                if self.solution[r][c] == num:
                    return (r, c)
        return None # Should not happen for valid tile numbers

    def _calculate_tile_heuristic(self, tile_num, current_grid):
        """Calculates the Manhattan distance for a single tile.
        Finds the distance between the tile's current position and its goal position.
        Corresponds to findH in Java.
        """
        if tile_num == 0: # Blank tile doesn't contribute to heuristic
            return 0

        current_pos = self.find_number(tile_num, current_grid)
        goal_pos = self._find_number_in_solution(tile_num)

        if current_pos and goal_pos:
            return abs(current_pos[0] - goal_pos[0]) + abs(current_pos[1] - goal_pos[1])
        else:
            # Should not happen with valid grids and tile numbers
            print(f"Error: Could not find position for tile {tile_num} during heuristic calculation.")
            return 0 

    def calculate_state_heuristic(self, current_grid):
        """Calculates the total Manhattan distance heuristic for the entire grid state.
        Sums the Manhattan distance for each tile (except 0) to its goal position.
        Corresponds to stateH in Java.
        """
        total_heuristic = 0
        for r in range(self._rows):
            for c in range(self._cols):
                tile_num = current_grid[r][c]
                if tile_num != 0: # Skip the blank tile
                    total_heuristic += self._calculate_tile_heuristic(tile_num, current_grid)
        return total_heuristic
        
    # --- A* Solver Methods ---
    
    def solve(self):
        """Attempts to solve the N-Puzzle using the A* algorithm.
        Returns the goal Node if a solution is found, otherwise None.
        Corresponds to solver in Java.
        """
        # Initialize the start node
        start_h = self.calculate_state_heuristic(self.start)
        start_node = Node(grid=self.start, g=0, h=start_h, parent=None)
        
        # Reset open and closed lists
        self.open_list = []
        self.closed_list = set()
        
        # Add start node to the open list (priority queue)
        heapq.heappush(self.open_list, start_node)
        
        nodes_expanded = 0
        
        while self.open_list: # While the open list is not empty
            # Get the node with the lowest f score
            current_node = heapq.heappop(self.open_list)
            nodes_expanded += 1

            # --- Progress Indicator --- 
            if nodes_expanded % 1000 == 0:
                print(f"\rExpanded {nodes_expanded} nodes... Current f={current_node.f}", end="")
            # --------------------------

            # Goal check
            if self.is_goal_state(current_node.grid):
                print(f"\nSolution found! Expanded {nodes_expanded} nodes.") # Add newline before final message
                return current_node # Goal reached!

            # Skip if already visited (handles duplicates in heap efficiently)
            if current_node in self.closed_list:
                continue
                
            # Add current node to the closed list
            self.closed_list.add(current_node)

            # Generate successors
            successors = self._generate_successors(current_node)
            
            for successor_node in successors:
                # If successor is already visited, skip
                if successor_node in self.closed_list:
                    continue
                    
                # Add valid, unvisited successor to the open list
                # heapq handles the priority queue logic based on Node comparison (__lt__)
                heapq.heappush(self.open_list, successor_node)
                
        # If the loop finishes without finding the goal
        print(f"\nNo solution found after expanding {nodes_expanded} nodes.") # Add newline
        return None

    def _generate_successors(self, current_node):
        """Generates all valid successor nodes from the current node's state.
        Corresponds to findSuccessors in Java.
        """
        successors = []
        moveable_tiles = self._find_moveable_tiles(current_node.grid)
        
        for tile_num in moveable_tiles:
            # Simulate the move on a copy of the grid
            new_grid = self._simulate_move(tile_num, current_node.grid)
            
            if new_grid:
                # Calculate costs for the new state
                g_cost = current_node.g + 1
                h_cost = self.calculate_state_heuristic(new_grid)
                # Create the successor node
                successor = Node(grid=new_grid, g=g_cost, h=h_cost, parent=current_node)
                successors.append(successor)
                
        return successors
        
    # --- Path Tracing ---
    
    def _trace_route(self, goal_node):
        """Reconstructs and prints the solution path from the goal node.
        Waits for user input between steps.
        Corresponds to traceRoute in Java.
        """
        if not goal_node:
            print("No goal node provided for tracing.")
            return
            
        path = []
        current = goal_node
        while current:
            path.insert(0, current) # Insert at the beginning to reverse the path
            current = current.parent
            
        print("\n--- Solution Path ---")
        print(f"Total moves: {goal_node.g}")
        print("(Hit 'Enter' to see the next step)")
        
        for i, node in enumerate(path):
            print(f"\nStep {i}:")
            self.display(node.grid)
            print(f"  g = {node.g}, h = {node.h}, f = {node.f}")
            if i < len(path) - 1:
                # Wait for user input before showing the next step
                input("...") 
            else:
                print("\n--- Goal Reached ---")

    # --- Main execution ---
if __name__ == "__main__":
    puzzle = NPuzzle() 