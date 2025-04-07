import copy
import functools

@functools.total_ordering
class Node:
    """Represents a node in the A* search tree for the N-Puzzle.

    Stores the puzzle state (grid), cost (g), heuristic (h), total cost (f),
    and a reference to the parent node.
    """
    def __init__(self, grid, g=0, h=0, parent=None):
        """Initializes a Node.

        Args:
            grid (list[list[int]]): The puzzle state for this node.
            g (int): Cost from the start node to this node (number of moves).
            h (int): Heuristic estimate from this node to the goal.
            parent (Node, optional): The parent node in the search path. Defaults to None.
        """
        # Deep copy the grid to ensure this node owns its state
        self.grid = copy.deepcopy(grid)
        self.parent = parent
        self.g = g  # Cost from start
        self.h = h  # Heuristic estimate to goal
        self.f = self.g + self.h # Total estimated cost

    def __eq__(self, other):
        """Checks if two nodes have the same grid state.
        Corresponds to equalsArray in Java Node.
        """
        if not isinstance(other, Node):
            return NotImplemented
        return self.grid == other.grid

    def __lt__(self, other):
        """Compares nodes based on their f value (total cost).
        Used for priority queue (heap) ordering in A*.
        Lower f value means higher priority.
        """
        if not isinstance(other, Node):
            return NotImplemented
        # If f values are equal, we can use h as a tie-breaker (optional but common)
        if self.f == other.f:
            return self.h < other.h 
        return self.f < other.f

    def __hash__(self):
        """Allows nodes to be added to sets or used as dictionary keys.
        Hashing based on the grid state.
        """
        # Convert the 2D list to a tuple of tuples for hashing
        return hash(tuple(map(tuple, self.grid)))

    def __str__(self):
        """String representation for debugging."""
        # Basic string representation, could be enhanced to print grid
        return f"Node(f={self.f}, g={self.g}, h={self.h})"

    def __repr__(self):
        """More detailed representation."""
        return f"Node(grid={self.grid}, f={self.f}, g={self.g}, h={self.h}, parent_id={id(self.parent) if self.parent else None})" 