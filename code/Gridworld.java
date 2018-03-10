// Michael Gabilondo
// Gridworld stores the global data about the world

import java.io.*;

public class Gridworld
{
  public static void main(String[] args) throws IOException
  {
    Gridworld gw = new Gridworld(args[0]);
    gw.print();

  }

  public static int PREFER_G = 0;
  public static int PREFER_H = 1;
  public int tie_breaker = PREFER_G;

  public static char BLOCKED   = 'x';
  public static char UNBLOCKED = '_';
  public static char START     = 's';
  public static char GOAL      = 'g';
  public static char TRAVERSED = '0';

  public char[][] world;
  public boolean[][] visible;

  public GridworldState start_state;
  public GridworldState goal_state;

  public boolean show_headers = false;

  public void print()
  {
    if (show_headers) {
      System.out.printf("       ");
      for (int i = 0; i < world[0].length; i++) 
        System.out.printf("%c ", 'A' + i);
      System.out.println("");

      System.out.printf("       ");
      for (int i = 0; i < world[0].length; i++) 
        System.out.printf("# ");
      System.out.println("");
    }

    for (int i = 0; i < world.length; i++) {
      if (show_headers)
        System.out.printf("%4d # ", i+1);
      for (int j = 0; j < world[i].length; j++) {
        System.out.printf("%c ", world[i][j]);
      }
      System.out.println("");
    }
  }

  public void make_omniscient()
  {
    for (int i = 0; i < visible.length; i++) {
      for (int j = 0; j < visible[0].length; j++) {
        visible[i][j] = true;
      }
    }
  }

  public void change_start_location(GridworldState state)
  {
    start_state = state;
  }

  public void mark_traversed(GridworldState state)
  {
    world[state.row][state.column] = Gridworld.TRAVERSED;
    set_adjacent_visible(state);
  }

  public void set_adjacent_visible(GridworldState state)
  {
    visible[state.row][state.column] = true;
    if (state.row-1 >= 0) visible[state.row-1][state.column] = true;
    if (state.row+1 < visible.length) visible[state.row+1][state.column] = true;
    if (state.column-1 >= 0) visible[state.row][state.column-1] = true;
    if (state.column+1 < visible[0].length) visible[state.row][state.column+1] = true;
  }

  Gridworld(String gridworld_filename) throws IOException
  {
    BufferedReader fin = new BufferedReader(new FileReader(gridworld_filename));
    int rows = new Integer(fin.readLine()).intValue();
    int columns = new Integer(fin.readLine()).intValue();
    world = new char[rows][columns];
    visible = new boolean[rows][columns];

    for (int i = 0; i < rows; i++) {
      String[] grid_locations = fin.readLine().split(" ");
      for (int j = 0; j < grid_locations.length; j++) {
        char grid_spot = grid_locations[j].charAt(0);
        world[i][j] = grid_spot;

        if (grid_spot == Gridworld.START) {
          start_state = new GridworldState(this, i, j, 0, null);
          set_adjacent_visible(start_state);
        }
        else if (grid_spot == Gridworld.GOAL) {
          goal_state = new GridworldState(this, i, j, Integer.MAX_VALUE, null);
        }
      }
    }
  }

}
