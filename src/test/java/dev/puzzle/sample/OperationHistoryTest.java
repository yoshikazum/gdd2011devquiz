package dev.puzzle.sample;

import java.util.ArrayList;

import junit.framework.TestCase;

public class OperationHistoryTest extends TestCase {

  OperationHistory his1;
  public OperationHistoryTest(String name) {
    super(name);
    his1 = new OperationHistory("abcdefg");
  }

  protected void setUp() throws Exception {
    super.setUp();
    his1.addHistory("miyoshi");
  }


  public void testSearchHistory() {
    String rearchString = "zxcf";
    boolean result = his1.searchHistory(rearchString);
    assertFalse(result);
    
    his1.setDepth(1);
    result = his1.searchHistory(rearchString);
    assertFalse(result);
    
    rearchString = "a";
    result = his1.searchHistory(rearchString);
    assertTrue(result);
    
    rearchString = "ace";
    result = his1.searchHistory(rearchString);
    assertTrue(result);
    
    rearchString = "abcdefg";
    his1.setDepth(20);
    result = his1.searchHistory(rearchString);
    assertTrue(result);
    
    rearchString = "abcde";
    his1.setDepth(20);
    result = his1.searchHistory(rearchString);
    assertFalse(result);
    
    rearchString = "miyoshi";
    his1.setDepth(20);
    result = his1.searchHistory(rearchString);
    assertTrue(result);
  }

}
