package pipair.common;

import java.io.*;
import java.util.List;

public class Result {
  public int bugFunc;
  public int partner;
  public List<Integer> callersMissing = null;
  public int support;
  public double confidence;

  public Result(
      int bugFunc, int partner, List<Integer> callersMissing, int support, double confidence) {
    this.bugFunc = bugFunc;
    this.partner = partner;
    this.callersMissing = callersMissing;
    this.support = support;
    this.confidence = confidence;
  }
}
