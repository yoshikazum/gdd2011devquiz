/**
 * 
 */
package dev.puzzle.sample;

import junit.framework.TestCase;

/**
 * @author "Yoshikazu Miyoshi <yoshikazum@gmail.com>"
 *
 */
public class BoardTest extends TestCase {

  /**
   * @param name
   */
  public BoardTest(String name) {
    super(name);
  }

  String input1 = "120743586";
  String input2 = "168452=30";
  String input3 = "16452730=";
  String input4 = "B61D5H=42C0398A=IPRJESKMNLQOTF";
  String input5 = "1A3B458J26EZKF09LUD7GCIOP==MNTVWXYSH";

  String goal1 = "123456780";
  String goal2 = "123456=80";
  String goal3 = "12345670=";
  String goal4 = "123456=89ABCDEF=HIJKLMNOPQRST0";
  String goal5 = "123456789ABCDEFGHIJKLMNOP==STUVWXYZ0";
  
  String estimate1 = "123456708";
  String estimate2 = "168452=30";
  String estimate4 = "B61D5H=42C0398A=IPRJESKMNLQOTF";
  String estimate5 = "123456789ABCDEFGHIJKLMNOP==STUVWXYZ0";

  /* (非 Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
  }

  /**
   * {@link dev.puzzle.sample.Board#Board(int, int, java.lang.String)} のためのテスト・メソッド。
   */
  public void testBoard() {
    assertTrue(true);
  }

  /**
   * {@link dev.puzzle.sample.Board#getGoal(java.lang.String)} のためのテスト・メソッド。
   */
  public void testGetGoal() {
    /*
    assertEquals(goal1, exe.getGoal(input1));
    assertEquals(goal2, exe.getGoal(input2));
    assertEquals(goal3, exe.getGoal(input3));
    assertEquals(goal4, exe.getGoal(input4));
    assertEquals(goal5, exe.getGoal(input5));
    */
    assertTrue(true);
  }

  /**
   * {@link dev.puzzle.sample.Board#mapFromString(int, int, java.lang.String)} のためのテスト・メソッド。
   */
  public void testMapFromString() {
    assertTrue(true);
  }

  /**
   * {@link dev.puzzle.sample.Board#estimateMap(java.util.Map, java.util.Map)} のためのテスト・メソッド。
   */
  public void testEstimateMap() {
    assertTrue(true);
  }
  
  public void testGetEstematedValue(){
    assertTrue(true);
  }

}
