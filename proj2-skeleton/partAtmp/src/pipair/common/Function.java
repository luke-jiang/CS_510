package pipair.common;

import java.util.ArrayList;
import java.util.List;

public class Function {
  private int index;
  private List<Integer> callees = new ArrayList<>();
  private List<Integer> callers = new ArrayList<>();

  Function(int ind) {
    this.index = ind;
  }

  public int getIndex() {
    return index;
  }

  public void addCallee(int calleeIndex) {
    if (!callees.contains(calleeIndex)) {
      callees.add(calleeIndex);
    }
  }

  //  public void deleteCallee(int calleeIndex) {
  //    callees.remove(calleeIndex);
  //  }

  public List<Integer> getCallees() {
    return callees;
  }

  public boolean calls(int funcIndex) {
    return callees.contains(funcIndex);
  }

  public void addCaller(int callerIndex) {
    callers.add(callerIndex);
  }

  public List<Integer> getCallers() {
    return callers;
  }
}
