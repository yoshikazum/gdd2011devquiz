package dev.puzzle.sample;

import java.util.HashMap;

public class BoardElement {

  private static BoardElement instance = new BoardElement();
  private HashMap<String, Integer> elementMap;

  static char[] elements = { '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
      'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
      'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

  static Integer ZEROPOSITIONKEY = new Integer(-100);
  static Integer WIDTHKEY = new Integer(-200);
  static Integer HEIGHTKEY = new Integer(-300);

  /**
   * コンストラクター
   */
  private BoardElement() {
  }
  
  public void createElementMap(){
    elementMap = new HashMap<String, Integer>();
    for (int i = 0; i < elements.length; i++) {
      String key = String.valueOf(elements[i]);
      Integer value = new Integer(i + 1);
      elementMap.put(key, value);
    }
    elementMap.put("0", new Integer(0));
    elementMap.put("=", new Integer(-1));
  }

  public static BoardElement getInstance() {
    return instance;
  }

  public HashMap<String, Integer> getElementMap() {
    if(elementMap==null){
      createElementMap();
    }
    return elementMap;
  }

  public char[] getElement() {
    return elements;
  }

}
