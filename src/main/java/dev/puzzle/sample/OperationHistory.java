/**
 * 
 */
package dev.puzzle.sample;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author "Yoshikazu Miyoshi <yoshikazum@gmail.com>"
 * 
 */
public class OperationHistory {
  boolean result;
  String operation;
  ArrayList<String> operationHistories;
  int depth;
  
  public OperationHistory() {
    result = false;
    operation = "";
    operationHistories = new ArrayList<String>();
    depth = 0;
  }
  
  public OperationHistory(String history){
    result = false;
    operation = "";
    operationHistories = new ArrayList<String>();
    depth = 0;
    
    operationHistories.add(history);
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getOperation() {
    return operation;
  }

  public void addOperation(String newOperation) {
    this.operation += newOperation;
  }
  
  public void addHistory(String target){

    for (Iterator<String> iterator = operationHistories.iterator(); iterator
        .hasNext();) {
      String history = (String) iterator.next();
      if(history.equalsIgnoreCase(target)){
        return;
      }
    }
    this.operationHistories.add(target);
  }

  public boolean searchHistory(String target) {
    if(depth<1)
      return false;
    
    for (Iterator<String> iterator = operationHistories.iterator(); iterator
        .hasNext();) {
      String history = (String) iterator.next();
      int tmpDepth = (history.length() < depth)? history.length(): depth;
      String historyPrefix = (String) history.subSequence(0, tmpDepth);
      if (target.startsWith(historyPrefix)) {
        return true;
      }
    }
    return false;
  }

  public ArrayList<String> getOperationHistories() {
    return operationHistories;
  }

  public boolean getResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }
}
