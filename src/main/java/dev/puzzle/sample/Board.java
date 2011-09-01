/**
 * 
 */
package dev.puzzle.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author "Yoshikazu Miyoshi <yoshikazum@gmail.com>"
 * 
 */
public class Board {

  private int line;

  private int width;
  private int height;
  private String content;

  private String goalContent;
  private HashMap<Integer, Integer> map;
  private HashMap<Integer, Integer> goalMap;
  private int estimatedValue;
  private ArrayList<HashMap<Integer, Integer>> history;

  private static HashMap<String, Integer> elementMap = BoardElement
      .getInstance()
      .getElementMap();

  public Board(int line, int w, int h, String content) {
    this.line = line;
    this.width = w;
    this.height = h;
    this.content = content;

    this.goalContent = this.getGoal(this.content);
    this.map = mapFromString(height, width, this.content);
    this.goalMap = mapFromString(height, width, goalContent);
    this.history = new ArrayList<HashMap<Integer, Integer>>();

    this.estimatedValue = estimateMap(map, goalMap);
  }

  public String getGoal(String input) {

    char[] charArray = input.toCharArray();
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

  /**
   * 文字列をMapに変換する
   * 
   * @param height
   * @param width
   * @param input
   * @return
   */
  public static HashMap<Integer, Integer> mapFromString(
      int height,
      int width,
      String input) {

    int w = width;
    int h = height;
    char[] arry = input.toCharArray();

    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    int k = 0;
    for (int i = 0; i < h + 2; i++) {
      for (int j = 0; j < w + 2; j++) {
        Integer key = new Integer(i * 10 + j);
        if (i == 0 || j == 0 || i > h || j > w) {
          Integer value = new Integer(-1);
          map.put(key, value);
        } else {
          Integer value = elementMap.get(String.valueOf(arry[k]));
          map.put(key, value);

          if (value.intValue() == 0) {
            // "0"（空白）の場所（キー）を記憶しておくキー
            map.put(BoardElement.ZEROPOSITIONKEY, key);
          }

          k++;
        }
      }
    }
    map.put(BoardElement.HEIGHTKEY, new Integer(height));
    map.put(BoardElement.WIDTHKEY, new Integer(width));

    for (int i = 0; i < h + 2; i++) {
      StringBuilder sBuilder = new StringBuilder();
      for (int j = 0; j < w + 2; j++) {
        Integer key = new Integer(i * 10 + j);
        Integer value = map.get(key);
        sBuilder.append(String.valueOf(value.intValue()) + ",");
      }
      // System.out.println(new String(sBuilder));
    }
    return map;
  }

  public static 
      int
      estimateMap(Map<Integer, Integer> test, Map<Integer, Integer> goal) {

    Map<Integer, Integer> goalMapForSearch = new HashMap<Integer, Integer>();
    for (Iterator<Integer> iterator = goal.keySet().iterator(); iterator
        .hasNext();) {
      Integer key = (Integer) iterator.next();

      // 0以下のものはプロパティ用なので除く
      if (key.intValue() < 0)
        continue;
      Integer value = (Integer) goal.get(key);
      goalMapForSearch.put(value, key);
    }

    int ix = test.get(BoardElement.HEIGHTKEY);
    int jx = test.get(BoardElement.WIDTHKEY);
    int resultSum = 0;
    for (int i = 1; i < ix + 1; i++) {
      for (int j = 1; j < jx + 1; j++) {
        int testKey = i * 10 + j;
        Integer testValue = test.get(new Integer(testKey));
        if (testValue < 1)
          continue; // 0(空白)と-1(壁)の見積もりはやらない
        Integer goalPosition = goalMapForSearch.get(testValue);
        if(goalPosition==null)
          continue;
        int diffOfTen =
            Math.abs((int) goalPosition.intValue() / 10 - (int) testKey / 10);
        int diff = Math.abs(goalPosition.intValue() % 10 - testKey % 10);
        resultSum += diffOfTen + diff;
        // System.out.print("sum: "+resultSum+ ", ");
      }
    }
    // System.out.println("estimated value: "+ resultSum);
    return resultSum;
  }

  public boolean compareTo(Map<Integer, Integer> inputMap) {
    if (inputMap == null)
      return false;
    return (inputMap.hashCode() == goalMap.hashCode()) ? true : false;
    /*
     * for (Iterator iterator = inputMap.entrySet().iterator();
     * iterator.hasNext();) { Map.Entry<Integer, Integer> testEntry =
     * (Map.Entry<Integer, Integer>) iterator.next(); Integer testKey =
     * testEntry.getKey(); Integer test = testEntry.getValue(); Integer goal =
     * goalMap.get(testKey); boolean result = (test==goal)? true: false;
     * if(result){ continue; }else{ return false; } }
     * 
     * return true;
     */
  }

  public boolean searchHistory(Map<Integer, Integer> target) {
    for (int i = 0; i < history.size(); i++) {
      Map<Integer, Integer> map = history.get(i);
      if (map == null) {
        return false;
      }
      if (map.hashCode() == target.hashCode())
        return true;
    }
    return false;
  }

  public void addHistory(HashMap<Integer, Integer> newMap) {
    this.history.add(newMap);
  }

  public int getLine() {
    return line;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public String getContent() {
    return content;
  }

  public String getGoalContent() {
    return goalContent;
  }

  public HashMap<Integer, Integer> getMap() {
    return map;
  }

  public HashMap<Integer, Integer> getGoalMap() {
    return goalMap;
  }

  public int getEstimatedValue() {
    return estimatedValue;
  }

  public void removeHistoryAfter(int index) {
    for (int j = 0; j < history.size(); j++) {
      if (j < index)
        continue;

      history.remove(j);
    }
  }

}
