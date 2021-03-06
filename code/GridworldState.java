// Michael Gabilondo
// GridworldState is a state in the grid search problem; it contains the
// knowledge of generating new valid states and computes/stores the h, g and f
// values for the state; these are the nodes in the Open and Closed lists

import java.util.Vector;

public class GridworldState implements Comparable<GridworldState>
{
  public Gridworld world;

  public int row;
  public int column;
  public int g_value;

  GridworldState came_from;

  public static int OPERATOR_UP =    0;
  public static int OPERATOR_DOWN =  1;
  public static int OPERATOR_RIGHT = 2;
  public static int OPERATOR_LEFT =  3;

  public boolean is_blocked()
  {
    return world.world[row][column] == Gridworld.BLOCKED;
  }

  public boolean is_unblocked()
  {
    return world.world[row][column] == Gridworld.UNBLOCKED;
  }

  // to be called after the state is generated, returns whether the state is
  // valid; a state is valid if it is within bounds of the grid and this node
  // is not BLOCKED and our agent knows about it. if the node is BLOCKED but
  // the agent has not seen it, it returns it is valid and the agent will run
  // up against a wall later
  public boolean is_valid() {
    if (row < 0 || row >= world.world.length) {
      return false;
    }
    if (column < 0 || column >= world.world[0].length) {
      return false;
    }
    if (world.visible[row][column] && world.world[row][column] == world.BLOCKED) {
      return false;
    }
    return true;
  }

  // auxiliary of apply_valid_operators, generates a new state of unknown
  // validity by applying op. the new states generated have g-values that is
  // one more than the g-value of this state, sinc the cost from going to
  // another state is 1. the new states also have back pointers to this state.
  public GridworldState apply_operator(int op)
  {
    if (op == OPERATOR_UP) {
      return new GridworldState(world, row-1, column, g_value+1, this);
    }
    else if (op == OPERATOR_DOWN) {
      return new GridworldState(world, row+1, column, g_value+1, this);
    }
    else if (op == OPERATOR_LEFT) {
      return new GridworldState(world, row, column-1, g_value+1, this);
    }
    else {
      return new GridworldState(world, row, column+1, g_value+1, this);
    }
  }

  // returns a vector of all valid states generated by applying all valid
  // operators
  public Vector<GridworldState> apply_valid_operators()
  {
    Vector<GridworldState> states = new Vector<GridworldState>();

    for (int i = 0; i < 4; i++) {
      GridworldState gw_state = apply_operator(i);
      if (gw_state.is_valid()) {
        states.add(gw_state);
      }
    }
    return states;
  }


  public String toString()
  {
    if (world.show_headers)
      return "(" + (row+1) + ", " + (char)('A' + column) + ")";
    return "(" + row + ", " + column + ")";
  }

  public int f()
  {
    return g() + h();
  }

  // Manhattan distance heuristic that is admissible in the gridworld
  public int h()
  {
    return Math.abs(world.goal_state.row - row) + Math.abs(world.goal_state.column - column);
  }

  public int g()
  {
    return g_value;
  }

  // f() compare, for determining better states using the heuristic
  public int compareTo(GridworldState other)
  {
    int f_diff = this.f() - other.f();

    if (f_diff == 0) {
      if (world.tie_breaker == Gridworld.PREFER_G) {
        int g_diff = this.g() - other.g();
        if (g_diff != 0) {
          return g_diff;
        }
        return this.h() - other.h();
      }

      else {
        int h_diff = this.h() - other.h();
        if (h_diff != 0) {
          return h_diff;
        }
        return this.g() - other.g();
      }
    }

    return f_diff;
  }

  // returns true if both states have the same x,y coordinates
  public boolean equals(GridworldState other)
  {
    return row == other.row && column == other.column;
  }

  GridworldState(Gridworld world, int row, int column, int g, GridworldState parent)
  {
    this.world = world;
    this.row = row;
    this.column = column;
    this.g_value = g;
    this.came_from = parent;
  }
}

