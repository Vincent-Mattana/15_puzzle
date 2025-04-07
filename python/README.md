# N-Puzzle Solver API (Python Version)

This directory contains a Python implementation of the N-Puzzle solver, exposed as a Flask web API. The solver logic is based on the original Java code found in the `../15_puzzle` directory.

## Project Context

### The N-Puzzle Problem

The N-Puzzle is a classic sliding tile puzzle. It consists of a grid (typically square, N rows by N columns) with numbered tiles (from 1 to N*N - 1) and one empty space (represented by 0). The goal is to arrange the tiles into sequential order (1, 2, 3, ..., 0) by sliding tiles into the empty space.

Common examples include the 8-puzzle (3x3 grid) and the 15-puzzle (4x4 grid).

### Algorithm Used

This solver uses the **A* search algorithm** to find the shortest sequence of moves from a given starting configuration to the goal state. 

*   **Heuristic:** The heuristic function (`h`) used to estimate the cost from the current state to the goal is the **Manhattan distance**. This is calculated by summing the horizontal and vertical distances of each numbered tile from its goal position.
*   **Cost:** The cost function (`g`) represents the number of moves taken from the start state to reach the current state.
*   **Evaluation Function:** The A* algorithm prioritizes exploring nodes with the lowest `f` value, where `f = g + h`.

### Implementation Details

*   `app.py`: The Flask web application that exposes the `/solve` endpoint.
*   `n_puzzle.py`: Contains the main `NPuzzle` class, implementing the A* logic, grid manipulation, heuristic calculation.
*   `node.py`: Defines the `Node` class used to represent states in the search tree, storing the grid, costs (g, h, f), and parent pointer.
*   **Priority Queue:** Python's built-in `heapq` module is used to efficiently manage the open list (priority queue) for the A* search, prioritizing nodes with lower `f` values.
*   `requirements.txt`: Lists the necessary Python packages (Flask).

## API Usage

1.  **Install Dependencies:**
    Navigate to the `python` directory in your terminal and install the required packages:
    ```bash
    pip install -r requirements.txt
    ```

2.  **Run the API Server:**
    Start the Flask development server:
    ```bash
    python app.py 
    ```
    The API will be running at `http://127.0.0.1:5000` (or a similar address indicated by Flask).

3.  **Send a Solve Request:**
    Make a POST request to the `/solve` endpoint with a JSON body containing the puzzle size (`n`) and the starting `grid` configuration.

    **Endpoint:** `POST /solve`

    **Request Body (JSON):**
    ```json
    {
      "n": 3, 
      "grid": [
        [1, 2, 3],
        [4, 0, 5],
        [7, 8, 6]
      ]
    }
    ```

    **Example using `curl`:**
    ```bash
    curl -X POST http://127.0.0.1:5000/solve \
         -H "Content-Type: application/json" \
         -d '{
               "n": 3, 
               "grid": [
                 [1, 2, 3],
                 [4, 0, 5],
                 [7, 8, 6]
               ]
             }'
    ```

    **Success Response (200 OK):**
    The API will return a JSON object containing the solution path (a list of grid states from start to goal) and the number of moves.
    ```json
    {
      "solution": [
        [[1, 2, 3], [4, 0, 5], [7, 8, 6]],
        [[1, 2, 3], [4, 5, 0], [7, 8, 6]],
        [[1, 2, 3], [4, 5, 6], [7, 8, 0]]
      ],
      "moves": 2,
      "solver_log": "Goal found! Processed 15 nodes in 0.01 seconds."
    }
    ```

    **Error Response (e.g., 400 Bad Request, 422 Unprocessable Entity, 500 Internal Server Error):**
    If there's an issue with the input or the solver, an error message will be returned.
    ```json
    {
      "error": "Grid must contain numbers from 0 to n*n-1 exactly once"
    }
    ```
    ```json
    {
      "error": "No solution found",
      "solver_log": "Search completed. Processed 50000 nodes. No solution found."
    }
    ```

## Original Java Code

The original Java code can be found in the `../15_puzzle` directory. 