# 15_puzzle
A solver for the NxN "15 puzzle".

## Versions

### Original Java Version

The original command-line Java implementation can be found in the `15_puzzle/` directory.

### Python API Version

A Python version implemented as a Flask web API is available in the `python/` directory.

*   **Functionality:** Accepts the puzzle size (`N`) and a number of random shuffles to generate the starting state. It then solves the puzzle using A* search and returns the solution path encoded in Base64.
*   **Usage:** For detailed setup, run instructions, and API endpoint documentation, please see the README within the Python directory: [`python/README.md`](python/README.md).
