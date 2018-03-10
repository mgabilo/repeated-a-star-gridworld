// Michael Gabilondo
// ArgumentParser is a simple argument parser to handle options on the command
// line

public class ArgumentParser
{
  public boolean pretty_print = true;
  public boolean omniscient = false;
  public boolean prefer_g = true;
  public String filename = "";

  public void print_arguments()
  {
    System.out.println("\n=== OPTIONS ===");
    if (pretty_print) 
      System.out.println("pretty print = ON");
    else
      System.out.println("pretty print = OFF");
    if (omniscient) 
      System.out.println("omniscient = ON");
    else
      System.out.println("omniscient = OFF");
    if (prefer_g) 
      System.out.println("breaks ties with g-values, then h-values");
    else
      System.out.println("breaks ties with h-values, then g-values");
    System.out.println("map filename = " + filename);
    System.out.println("===============\n");
  }

  ArgumentParser(String[] args)
  {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-pp0")) {
        this.pretty_print = false;
      }
      else if (args[i].equals("-pp1")) {
        this.pretty_print = true;
      }

      else if (args[i].equals("-omni0")) {
        this.omniscient = false;
      }
      else if (args[i].equals("-omni1")) {
        this.omniscient = true;
      }

      else if (args[i].equals("-g")) {
        this.prefer_g = true;
      }
      else if (args[i].equals("-h")) {
        this.prefer_g = false;
      }

      else {
        this.filename = args[i];
      }
    }

  }


}
