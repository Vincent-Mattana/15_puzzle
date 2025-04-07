import random
import copy
import heapq # Use Python's heapq for the priority queue
import time # Added for progress indicator

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
    
    # Removed grid, solution, start instance variables initialized here.
    # They will be handled within methods or constructor as needed.
    
    # open_list and closed_list will be initialized in __init__

    def __init__(self, n):
        """Initializes the NPuzzle solver for a given size N."""
        if not isinstance(n, int) or n <= 1:
            raise ValueError("N must be an integer greater than 1.")
        
        self._rows = n
        self._cols = n
        self._total_squares = self._rows * self._cols
        
        # Initialize the goal state (solution grid)
        self.solution = [[0] * self._cols for _ in range(self._rows)]
        self._setup_solution_grid() # Renamed setup_grid to avoid confusion

        # Initialize A* data structures
        self.open_list = [] # Initialize open list as a list for heapq
        self.closed_list = set()    # Initialize closed list as a set

    # Removed start_text method

    # --- Helper Methods ---
    
    def _copy_grid(self, source_grid):
        """Creates a deep copy of a grid (2D list)."""
        return copy.deepcopy(source_grid)

    # --- Core Logic Methods (Translated from Java) ---

    def _setup_solution_grid(self): # Renamed from setup_grid
        """Initializes the `self.solution` grid to the solved state."""
        count = 1
        for i in range(self._rows):
            for j in range(self._cols):
                self.solution[i][j] = count
                count += 1
        # Set the last cell to 0 (blank tile)
        self.solution[self._rows - 1][self._cols - 1] = 0


    def display(self, array):
        """Prints the puzzle grid to the console. (Kept for potential debugging)"""
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


    # Shuffle is no longer needed for the core API, but might be useful for testing/future features
    # Keep shuffle method for now, but it won't be directly called by the solve endpoint
    def shuffle(self, num_shuffles, start_grid):
        """Shuffles a given grid by making random valid moves.
        Modifies the grid in place.
        Returns the shuffled grid (same object modified in place).
        """
        print(f"Performing {num_shuffles} random shuffles...")
        grid = self._copy_grid(start_grid) # Work on a copy
        last_moved = -1 # Prevent immediately moving a tile back
        for i in range(num_shuffles):
            moveable_tiles = self._find_moveable_tiles(grid)
            
            # Filter out the tile that was just moved, if possible
            possible_moves = [tile for tile in moveable_tiles if tile != last_moved]
            if not possible_moves:
                possible_moves = moveable_tiles # Use original list if filtering removed all options
            
            if not possible_moves:
                print(f"Warning: No moveable tiles found during shuffle step {i+1}? Grid state:")
                self.display(grid)
                break 
                
            # Choose a random tile to move
            tile_to_move = random.choice(possible_moves)
            self._move_tile(tile_to_move, grid) 
            last_moved = tile_to_move # Remember the tile just moved
        return grid


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
        """Checks if the given grid state matches the solution state."""
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

    def find_number_in_solution(self, num):
        """Finds the coordinates (row, col) of a number in the solution grid."""
        # This assumes self.solution is already set up
        for r in range(self._rows):
            for c in range(self._cols):
                if self.solution[r][c] == num:
                    return (r, c)
        return None # Should not happen for numbers 1 to N*N-1

    def _calculate_tile_heuristic(self, tile_num, current_grid):
        """Calculates the Manhattan distance for a single tile."""
        if tile_num == 0:
            return 0 # Empty space contributes 0 to heuristic

        current_pos = self.find_number(tile_num, current_grid)
        goal_pos = self.find_number_in_solution(tile_num)

        if current_pos and goal_pos:
            # Manhattan distance: |x1 - x2| + |y1 - y2|
            return abs(current_pos[0] - goal_pos[0]) + abs(current_pos[1] - goal_pos[1])
        else:
            # Should not happen in a valid puzzle state
            print(f"Warning: Could not find position for tile {tile_num} in current or goal state.")
            return 0

    def calculate_state_heuristic(self, current_grid):
        """Calculates the total Manhattan distance heuristic for the given grid state."""
        total_heuristic = 0
        for r in range(self._rows):
            for c in range(self._cols):
                tile_num = current_grid[r][c]
                total_heuristic += self._calculate_tile_heuristic(tile_num, current_grid)
        return total_heuristic
        
    # --- A* Solver Methods ---
    
    def solve(self, start_grid): # Takes start_grid as input
        """Solves the N-Puzzle using A* search starting from the given grid.

        Args:
            start_grid (list[list[int]]): The initial state of the puzzle.

        Returns:
            Node: The goal node if a solution is found, otherwise None.
        """
        # Reset lists for potentially multiple solves with the same instance
        self.open_list = [] 
        self.closed_list = set()

        # Create the starting node
        start_h = self.calculate_state_heuristic(start_grid)
        start_node = Node(grid=start_grid, g=0, h=start_h, parent=None)

        # Add the starting node to the open list (priority queue)
        heapq.heappush(self.open_list, start_node)
        
        processed_nodes = 0
        start_time = time.time()

        while self.open_list:
            # Get the node with the lowest f value from the priority queue
            current_node = heapq.heappop(self.open_list)
            
            processed_nodes += 1
            if processed_nodes % 1000 == 0: # Print progress indicator
                elapsed_time = time.time() - start_time
                print(f"Processed {processed_nodes} nodes... Open list size: {len(self.open_list)}, Current f={current_node.f}, Time: {elapsed_time:.2f}s", end='\\r')


            # Check if this node represents the goal state
            if self.is_goal_state(current_node.grid):
                end_time = time.time()
                print(f"\\nGoal found! Processed {processed_nodes} nodes in {end_time - start_time:.2f} seconds.")
                return current_node # Goal reached

            # Add the current node to the closed list to avoid revisiting
            self.closed_list.add(current_node) # Relies on Node's __hash__ and __eq__

            # Generate successor nodes (neighbors)
            successors = self._generate_successors(current_node)

            for successor in successors:
                # If successor is already in closed list, skip it
                if successor in self.closed_list:
                    continue

                # Check if a node with the same state is already in the open list
                # This requires iterating or a more complex lookup structure if performance is critical
                # For heapq, we might push duplicates and rely on pulling the best one first.
                # A simple check:
                is_in_open = False
                for open_node in self.open_list:
                     if open_node == successor: # Check grid equality
                          is_in_open = True
                          # If the new path to this state is better, update the existing node
                          if successor.g < open_node.g:
                              # Update logic depends on heap implementation;
                              # Standard heapq doesn't easily support decreasing key.
                              # Easiest is often to just add the better path; the worse one
                              # will eventually be popped and ignored if already closed.
                              # Let's stick to adding it and relying on closed list check.
                              pass # Keep the better g-value already potentially in the heap
                          break # Found matching state in open list

                if not is_in_open:
                    heapq.heappush(self.open_list, successor)

        print(f"\\nSearch completed. Processed {processed_nodes} nodes. No solution found.")
        return None # No solution found


    def _generate_successors(self, current_node):
        """Generates all valid successor nodes from the current node."""
        successors = []
        moveable_tiles = self._find_moveable_tiles(current_node.grid)

        for tile in moveable_tiles:
            # Simulate the move on a copy of the grid
            new_grid = self._simulate_move(tile, current_node.grid)
            if new_grid: # If the move was valid
                # Create the successor node
                g_cost = current_node.g + 1 # Cost increases by 1 for each move
                h_cost = self.calculate_state_heuristic(new_grid)
                successor_node = Node(grid=new_grid, g=g_cost, h=h_cost, parent=current_node)
                successors.append(successor_node)
        return successors
        
    # --- Path Tracing ---
    
    def get_solution_path(self, goal_node): # Renamed from _trace_route and modified return
        """Traces the path from the goal node back to the start node.

        Args:
            goal_node (Node): The goal node returned by the solve method.

        Returns:
            list[list[list[int]]]: A list of grids representing the solution path
                                   from start to goal, or an empty list if goal_node is None.
        """
        if not goal_node:
            return []

        path = []
        current = goal_node
        while current is not None:
            path.append(current.grid) # Add the grid state
            current = current.parent
        
        return path[::-1] # Reverse the path to get start -> goal order

# Removed the if __name__ == "__main__": block 