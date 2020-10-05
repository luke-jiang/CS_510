package pipair.common;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallGraph {
  private Map<Integer, Function> functions = new HashMap<>();
  private List<String> functionNames = new ArrayList<>();

  public CallGraph() {}

  public void addFunction(Function f) {
    functions.put(f.getIndex(), f);
  }

  public Function getFunction(int ind) {
    return functions.get(ind);
  }

  public Map<Integer, Function> getFunctions() {
    return functions;
  }

  public String getFuncName(int i) {
    return functionNames.get(i);
  }

  public int indexOf(String name) {
    int ind = functionNames.indexOf(name);
    if (ind != -1) {
      return ind;
    } else {
      functionNames.add(name);
      return functionNames.size() - 1;
    }
  }

  public static CallGraph createCallGraphFromFile(File file) throws IOException {
    CallGraph callGraph = new CallGraph();

    BufferedReader br = new BufferedReader(new FileReader(file));

    String st;

    while ((st = br.readLine()) != null) {

      if (st.isEmpty()) continue;
      String[] tokens = st.split("\'");

      if (tokens.length <= 1) {
        // This node is 'null function'. Ignore entirely.
        while (!br.readLine().isEmpty()) {}
      } else {
        // If not null function.
        // token[1] is function names.
        Function func = new Function(callGraph.indexOf(tokens[1]));
        boolean isExternal = false;
        // System.out.println("root\t" + tokens[1]);

        while (!(st = br.readLine()).isEmpty()) {
          tokens = st.split("\'");
          if (tokens.length <= 1) continue;
          func.addCallee(callGraph.indexOf(tokens[1]));
          // System.out.println("child\t" + tokens[1]);
        }
        callGraph.addFunction(func);
      }
    }

    // Set functions' callers
    for (Function f : callGraph.getFunctions().values()) {
      int xInd = f.getIndex();
      for (int yInd : f.getCallees()) {
        callGraph.getFunction(yInd).addCaller(xInd);
      }
    }

    //  System.out.println("Call Graph is Loaded.");
    //  int max = 0;
    //  for (Function f : callGraph.getFunctions().values()) {
    //    int tmp = f.getCallees().size();
    //    System.out.println(
    //        f.getIndex()
    //            + " "
    //            + callGraph.getFuncName(f.getIndex())
    //            + " "
    //            + f.getCallees()
    //            + " "
    //            + f.getCallers());
    //    if (tmp > max) max = tmp;
    //  }

    //  System.out.println("#FUNCTIONS = " + callGraph.getFunctions().size());
    //  System.out.println("MAX_#CALLEES = " + max);

    return callGraph;
  }
}
