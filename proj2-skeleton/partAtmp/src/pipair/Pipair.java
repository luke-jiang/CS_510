package pipair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import pipair.common.CallGraph;
import pipair.common.Function;
import pipair.common.Result;
import pipair.util.Pair;

public class Pipair {
  private final int T_SUPPORT;
  private final int T_CONFIDENCE;
  private CallGraph callGraph = new CallGraph();
  private List<Pair<Integer, Integer>> funcPairs = new ArrayList<>();
  private List<Result> results = new ArrayList<>();

  Pipair(File file, int tSupport, int tConfidence) {
    try {
      this.callGraph = CallGraph.createCallGraphFromFile(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.T_SUPPORT = tSupport;
    this.T_CONFIDENCE = tConfidence;
  }

  Pipair(File file) {
    try {
      this.callGraph = CallGraph.createCallGraphFromFile(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.T_SUPPORT = 3;
    this.T_CONFIDENCE = 65;
  }

  public static void main(String[] args) {
    String fin = args[0];
    Pipair pipair;
    if (args.length == 1) {
      pipair = new Pipair(new File(fin));
    } else {
      int tSupport = Integer.valueOf(args[1]);
      int tConfidence = Integer.valueOf(args[2]);
      pipair = new Pipair(new File(fin), tSupport, tConfidence);
    }
    pipair.computeConfidence();
    // System.out.println(pipair.results.size());
    pipair.printResults();
  }

  private void computeConfidence() {
    // Get a list of function pairs.
    // TODO(sy): Make this function parallel.
    getFunctionPairs();

    // TODO(sy): Make this loop parallel.
    for (Pair<Integer, Integer> p : funcPairs) {
      // A Pair function x and function y.
      int xInd = p.getKey();
      int yInd = p.getValue();
      Function xFunc = callGraph.getFunction(xInd);
      Function yFunc = callGraph.getFunction(yInd);

      // Iterate all the function calling function x, and then check if the caller calls y as well.
      int pairSupport = 0;
      // Functions calling X, but not calling Y
      List<Integer> callersMissingY = new ArrayList<>();
      for (int callerInd : xFunc.getCallers()) {
        Function caller = callGraph.getFunction(callerInd);
        if (caller.getCallees().contains(yInd)) {
          pairSupport++;
        } else {
          callersMissingY.add(callerInd);
        }
      }

      if (pairSupport < T_SUPPORT) continue;
      // System.out.println(xFunc.getIndex() + " - " + yFunc.getIndex());
      // System.out.println(pairSupport);

      // Confidence of X
      double xConf = (double) pairSupport * 100 / xFunc.getCallers().size();
      if (xConf >= T_CONFIDENCE) {
        // System.out.println(xConf);

        results.add(new Result(xInd, yInd, callersMissingY, pairSupport, xConf));
      }
      // Confidence of Y
      double yConf = (double) pairSupport * 100 / yFunc.getCallers().size();
      if (yConf >= T_CONFIDENCE) {
        // Get Functions calling Y, but not calling X. Similar with callersMissingY.
        List<Integer> callersMissingX = new ArrayList<>();
        for (int callerInd : yFunc.getCallers()) {
          if (!callGraph.getFunction(callerInd).getCallees().contains(xInd)) {
            callersMissingX.add(callerInd);
          }
        }
        results.add(new Result(yInd, xInd, callersMissingX, pairSupport, yConf));
      }
    }
  }

  private void getFunctionPairs() {
    for (Function f : callGraph.getFunctions().values()) {
      List<Integer> callees = f.getCallees();
      for (int i = 0; i < callees.size(); i++) {
        for (int j = i + 1; j < callees.size(); j++) {
          addFuncPair(callees.get(i), callees.get(j));
        }
      }
    }
  }

  private void addFuncPair(int x, int y) {
    Pair<Integer, Integer> p1 = new Pair<>(x, y);
    Pair<Integer, Integer> p2 = new Pair<>(y, x);
    if (!funcPairs.contains(p1) && !funcPairs.contains(p2)) {
      funcPairs.add(p1);
    }
  }

  private void printResults() {
    for (Result r : results) {
      for (int callerInd : r.callersMissing) {
        String bugFunc = callGraph.getFuncName(r.bugFunc);
        String partner = callGraph.getFuncName(r.partner);
        System.out.println(
            "bug: "
                + bugFunc
                + " in "
                + callGraph.getFuncName(callerInd)
                + ", pair: ("
                + printPair(bugFunc, partner)
                + "), support: "
                + r.support
                + ", confidence: "
                + String.format("%.2f", r.confidence)
                + "%");
      }
    }
  }

  private String printPair(String str1, String str2) {
    if (str1.compareTo(str2) < 0) {
      return str1 + ", " + str2;
    }
    return str2 + ", " + str1;
  }

  // private String pair2String(String x, String y) {
  //   if (x < y) return "(" + x + ", " + y + ")";
  //   else return "(" + y + ", " + x + ")";
  // }
}
