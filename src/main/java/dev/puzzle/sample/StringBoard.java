/**
 * 
 */
package dev.puzzle.sample;

import java.util.HashMap;

/**
 * @author "Yoshikazu Miyoshi <yoshikazum@gmail.com>"
 * 
 */
public class StringBoard {
  int id;
  int width;
  int height;
  private String stringMap;
  private int hashCode;
  private String goalMap;
  private String operationHistory;
  private int zeroIndex;
  private int estimatedValue;

  public StringBoard() {
    width = 0;
    height = 0;
    stringMap = "";
    hashCode = 0;
    goalMap = "";
    operationHistory = "";
    zeroIndex = -1;
    estimatedValue = 99999;
  }
  
  public StringBoard(int id, int height, int width, String stringMap) {
    this.id = id;
    this.height = height;
    this.width = width;
    this.stringMap = stringMap;
    this.hashCode = stringMap.hashCode();
    this.goalMap = getGoal();
    zeroIndex = stringMap.indexOf("0");
    operationHistory = "";
    estimatedValue = calculateValue();
  }

  public StringBoard(int height, int width, String stringMap) {
    this.height = height;
    this.width = width;
    this.stringMap = stringMap;
    this.hashCode = stringMap.hashCode();
    this.goalMap = getGoal();
    zeroIndex = stringMap.indexOf("0");
    operationHistory = "";
    estimatedValue = calculateValue();
  }

  public StringBoard clone() {
    StringBoard newBoard =
        new StringBoard(this.id, this.height, this.width, this.stringMap);
    newBoard.setOperationHistory(this.operationHistory);
    newBoard.setEstimatedValue(this.estimatedValue);
    return newBoard;
  }

  public void setEstimatedValue(int estimatedValue) {
    this.estimatedValue = estimatedValue;
  }

  public String getStringMap() {
    return stringMap;
  }

  public void setStringMap(String stringMap) {
    this.stringMap = stringMap;
  }

  public String getOperationHistory() {
    return operationHistory;
  }

  public void setOperationHistory(String operationHistory) {
    this.operationHistory = operationHistory;
  }

  public int getZeroIndex() {
    return zeroIndex;
  }

  public void setZeroIndex(int zeroIndex) {
    this.zeroIndex = zeroIndex;
  }

  public void rehash() {
    this.hashCode = stringMap.hashCode();
  }

  /**
   * stringMapのハッシュコードを渡す
   */
  @Override
  public int hashCode() {
    return this.hashCode;
  }

  static int COMMAND_L = 01; // 1
  static int COMMAND_R = COMMAND_L << 1; // 2
  static int COMMAND_U = COMMAND_L << 2; // 4
  static int COMMAND_D = COMMAND_L << 3; // 8

  /**
   * 移動可能なリストを渡す
   * 
   * @return
   */
  public int getOperableList() {
    int rightIndex = zeroIndex + 1;
    int leftIndex = zeroIndex - 1;
    int upIndex = zeroIndex - width;
    int downIndex = zeroIndex + width;

    int stringSize = stringMap.length();

    char rightChar = '-';
    char leftChar = '-';
    char upChar = '-';
    char downChar = '-';

    if ((rightIndex % width) == 0) {
      rightChar = '=';
    }
    if ((leftIndex % width) == width - 1 || leftIndex < 0) {
      leftChar = '=';
    }
    if (upIndex < 0) {
      upChar = '=';
    }
    if (downIndex > stringSize - 1) {
      downChar = '=';
    }

    if (rightChar != '=')
      rightChar = this.stringMap.charAt(rightIndex);
    if (leftChar != '=')
      leftChar = this.stringMap.charAt(leftIndex);
    if (upChar != '=')
      upChar = this.stringMap.charAt(upIndex);
    if (downChar != '=')
      downChar = this.stringMap.charAt(downIndex);

    int operationList = 0;
    if (rightChar != '=')
      operationList += COMMAND_R;
    if (leftChar != '=')
      operationList += COMMAND_L;
    if (upChar != '=')
      operationList += COMMAND_U;
    if (downChar != '=')
      operationList += COMMAND_D;

    return operationList;
  }

  public StringBoard operate(int operation) throws CloneNotSupportedException {
    //System.out.println("w:"+ width+ " h:"+ height);
    char tmp = '-';
    char[] chars = this.stringMap.toCharArray();
    int n = 0;
    if ((operation & COMMAND_R) == COMMAND_R) {
      n = 1;
    } else if ((operation & COMMAND_L) == COMMAND_L) {
      n = -1;
    } else if ((operation & COMMAND_U) == COMMAND_U) {
      n = -width;
    } else if ((operation & COMMAND_D) == COMMAND_D) {
      n = width;
    } else {

    }
    tmp = chars[zeroIndex];
    chars[zeroIndex] = chars[zeroIndex + n];
    chars[zeroIndex + n] = tmp;
    int newZeroIndex = zeroIndex + n;
    
    //System.out.println(this.stringMap+ "->"+ new String(chars));
    StringBoard returnStringBoard =
        new StringBoard(this.id, this.height, this.width, new String(chars));

    return returnStringBoard;
  }

  public String getGoal() {

    char[] charArray = this.stringMap.toCharArray();
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < charArray.length; i++) {
      char c = charArray[i];
      if (c == '=') {
        stringBuilder.append("=");
      } else {
        if (i == charArray.length - 2) {
          char end = charArray[charArray.length - 1];
          if (end == '=') {
            stringBuilder.append("0");
            continue;
          }
        }
        if (i == charArray.length - 1) {
          stringBuilder.append("0");
        } else {
          stringBuilder.append(BoardElement.elements[i]);
        }
      }

    }

    String result = new String(stringBuilder);
    // System.out.println("goal result: " + result);
    return result;
  }

  public boolean compareTo(StringBoard stringBoard) {
    boolean result =
        stringBoard.stringMap.equalsIgnoreCase(this.goalMap) ? true : false;
    return result;
  }

  public int calculateValue() {
    HashMap<Integer, Integer> hMap =
        Board.mapFromString(height, width, this.stringMap);
    HashMap<Integer, Integer> gMap =
        Board.mapFromString(height, width, this.goalMap);
    return Board.estimateMap(hMap, gMap);
   }

  final long mask33 = Long.parseLong(
      "111" +
  		"000" +
  		"000" +
  		"000000000000000000000000000", 2);
  final long mask34 = Long.parseLong(
      "111" +
      "111" +
      "000" +
      "000" +
      "000000000000000000000000", 2);
  final long mask35 = Long.parseLong(
      "111" +
      "111" +
      "111" +
      "000" +
      "000" +
      "000000000000000000000", 2);
  final long mask36 = Long.parseLong(
      "111" +
      "111" +
      "111" +
      "111" +
      "000" +
      "000" +
      "000000000000000000", 2);
  final long mask43 = Long.parseLong(
      "1111" +
      "0000" +
      "0000" +
      "000000000000000000000000", 2);
  final long mask44 = Long.parseLong(
      "1111" +
      "1111" +
      "1000" +
      "1000" +
      "00000000000000000000", 2);
  final long mask45 = Long.parseLong(
      "1111" +
      "1111" +
      "1111" +
      "1000" +
      "1000" +
      "0000000000000000", 2);
  final long mask46 = Long.parseLong(
      "1111" +
      "1111" +
      "1111" +
      "1111" +
      "1000" +
      "1000" +
      "000000000000", 2);
  final long mask53 = Long.parseLong(
      "11111" +
      "11000" +
      "11000" +
      "100000000000000000000", 2);
  final long mask54 = Long.parseLong(
      "11111" +
      "11111" +
      "10000" +
      "10000" +
      "0000000000000000", 2);
  final long mask55 = Long.parseLong(
      "11111" +
      "11111" +
      "11111" +
      "11000" +
      "11000" +
      "00000000000", 2);
  final long mask56 = Long.parseLong(
      "11111" +
      "11111" +
      "11111" +
      "11111" +
      "11000" +
      "11000" +
      "000000", 2);
  final long mask63 = Long.parseLong(
      "111111" +
      "111000" +
      "111000" +
      "000000000000000000", 2);
  final long mask64 = Long.parseLong(
      "111111" +
      "110000" +
      "110000" +
      "110000" +
      "000000000000", 2);
  final long mask65 = Long.parseLong(
      "111111" +
      "110000" +
      "110000" +
      "110000" +
      "110000" +
      "000000", 2);
  final long mask66 = Long.parseLong(
      "111111" +
      "111111" +
      "111111" +
      "110000" +
      "110000" +
      "110000", 2);
  public long score(){
    char[] current = this.stringMap.toCharArray();
    char[] goal    = this.goalMap.toCharArray();
    
    long score = 0;
    long value = 1;
    boolean nocount = false;
    for (int i = 0; i < goal.length; i++) {
      if(current[i]=='=' || current[i]=='0')
        continue;
      if(current[i] == goal[i] && !nocount){
        score += value << (35-i);
      }else{
        nocount = true;
      }
    }
    
    //行列によってスコアマスクを変更する
    if(width==3 && height==3){
      score = score & mask33;
    }else if(width==3 && height==4){
      score = score & mask34;
    }else if(width==3 && height==5){
      score = score & mask35;
    }else if(width==3 && height==6){
      score = score & mask36;
    }else if(width==4 && height==3){
      score = score & mask43;
    }else if(width==4 && height==4){
      score = score & mask44;
    }else if(width==4 && height==5){
      score = score & mask45;
    }else if(width==4 && height==6){
      score = score & mask46;
    }else if(width==5 && height==3){
      score = score & mask53;
    }else if(width==5 && height==4){
      score = score & mask54;
    }else if(width==5 && height==5){
      score = score & mask55;
    }else if(width==5 && height==6){
      score = score & mask56;
    }else if(width==6 && height==3){
      score = score & mask63;
    }else if(width==6 && height==4){
      score = score & mask64;
    }else if(width==6 && height==5){
      score = score & mask65;
    }else if(width==6 && height==6){
      score = score & mask66;
    }
    
    return score;
  }

  public int getEstimatedValue() {
    return estimatedValue;
  }

  public int getLastOperation() {
    int index = this.operationHistory.length() - 1;
    if(index<0)return 0;
    char[] string = this.operationHistory.toCharArray();
    char operation = string[index];
    
    int result = 0;
    if(operation == 'L'){
      result = COMMAND_R;
    }else if(operation == 'R'){
      result = COMMAND_L;
    }else if(operation == 'U'){
      result = COMMAND_D;
    }else{
      result = COMMAND_U;
    }
    
    return result;
  }
}
