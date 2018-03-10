// Michael Gabilondo
// PathFinder is the main executible class

import java.util.Vector;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.ListIterator;
import java.util.Iterator;
import java.io.*;

public class PathFinder
{
  public static void main(String[] args) throws IOException
  {

    // Handle arguments, instantiate
    ArgumentParser arg_parser = new ArgumentParser(args);
    String filename = arg_parser.filename;

    if (filename.length() == 0) {
      System.out.println("Arguments: [-pp0|-pp1] [-omni0|-omni1] [-g|-h] filename");
      return;
    }

    PathFinder pf = new PathFinder(new Gridworld(filename));
    LinkedList<GridworldState> full_path = new LinkedList<GridworldState>();

    pf.world.show_headers = arg_parser.pretty_print;

    if (arg_parser.prefer_g)
      pf.world.tie_breaker = Gridworld.PREFER_G;
    else
      pf.world.tie_breaker = Gridworld.PREFER_H;

    if (arg_parser.omniscient)
      pf.world.make_omniscient();

    arg_parser.print_arguments();

    // Compute the partial path, moving the agent on each try, until a goal is
    // found or no goal is reached
    while (true) {
      LinkedList<GridworldState> path = pf.compute_path();

      if (path == null) {
        System.out.println("No path");
        return;
      }

      full_path.addAll(path);
      full_path.removeLast();

      GridworldState last_state = path.peekLast();
      if (!last_state.equals(pf.world.goal_state)) {
        pf.world.change_start_location(last_state);
      }
      else {
        break;
      }
    }

    // Print stuff out
    pf.world.print();

    ListIterator<GridworldState> iter = full_path.listIterator(0);
    while (iter.hasNext()) {
      GridworldState next_state = iter.next();
      System.out.println(next_state);
    }
    System.out.println(pf.world.goal_state);
  }

  public Gridworld world;
  public PriorityQueue<GridworldState> open;
  public Vector<GridworldState> closed;

  // return true if state is in closed
  public boolean is_state_in_closed(GridworldState state)
  {
    for (int i = 0; i < closed.size(); i++) {
      if (closed.elementAt(i).equals(state)) {
        return true;
      }
    }
    return false;
  }

  // return the GridworldState in open that .equals(state) or null
  public GridworldState find_state_in_open(GridworldState state)
  {
    Iterator<GridworldState> iter = open.iterator();
    while (iter.hasNext()) {
      GridworldState next_state = iter.next();
      if (state.equals(next_state)) {
        return next_state;
      }
    }
    
    return null;
  }

  PathFinder(Gridworld world)
  {
    this.world = world;
    this.open = new PriorityQueue<GridworldState>();
    this.closed = new Vector<GridworldState>();
  }
  
  // given a goal, return the reconstructed path in-order up to the goal or
  // before it hit a blocked obstacle; if the last node is the goal, it's the
  // full path; otherwise, we need to move the agent to the end of the path and
  // compte another path from there. this also updates world.visible as the
  // agent moves along the valid part of the path.
  public LinkedList<GridworldState> reconstruct_path(GridworldState goal)
  {
    LinkedList<GridworldState> path = new LinkedList<GridworldState>();
    GridworldState state = goal;

    path.push(state);
    while ( !(state = state.came_from).equals(world.start_state)) {
      path.push(state);
    }
    path.push(state);

    
    LinkedList<GridworldState> unblocked_path = new LinkedList<GridworldState>();
    ListIterator<GridworldState> iter = path.listIterator(0);
    while (iter.hasNext()) {
      GridworldState next_state = iter.next();
      if (!next_state.is_blocked()) {
        unblocked_path.add(next_state);
        if (next_state.is_unblocked()) {
          world.mark_traversed(next_state);
        }
      }
      else {
        return unblocked_path;
      }
    }

    return unblocked_path;
  }

  // primary A* algorithm that computes the path. it only takes into account
  // world.visible in its knowledge of blocked cells, unless we are omniscient.
  // therefore the reconstructed path it returns might not be a complete path,
  // and the function needs to be called again after the agent has been moved
  // to a new start state.
  public LinkedList<GridworldState> compute_path()
  {
    this.open = new PriorityQueue<GridworldState>();
    this.closed = new Vector<GridworldState>();
    open.add(this.world.start_state);

    while (open.size() > 0) {

      GridworldState state = open.poll();

      // no state in open has a lower f-value, else it would have been expanded
      if (state.equals(world.goal_state)) {
        return reconstruct_path(state);
      }

      closed.add(state);

      Vector<GridworldState> gw_states = state.apply_valid_operators();
      for (int i = 0; i < gw_states.size(); i++) {
        GridworldState successor_state = gw_states.elementAt(i);

        if (is_state_in_closed(successor_state)) {
          continue;
        }

        GridworldState old_open_state = find_state_in_open(successor_state);
        if (old_open_state != null) {
          if (old_open_state.g() > successor_state.g()) {
            open.remove(old_open_state);
            open.add(successor_state);
          }
        }
        else {
          open.add(successor_state);
        }

      }
    }
    return null;
  }
}

