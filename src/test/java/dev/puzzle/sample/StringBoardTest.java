/**
 * 
 */
package dev.puzzle.sample;

import junit.framework.TestCase;

/**
 * @author "Yoshikazu Miyoshi <yoshikazum@gmail.com>"
 *
 */
public class StringBoardTest extends TestCase {

  private StringBoard board1 = new StringBoard(3, 3, "136450782");
  private StringBoard board2;
  private StringBoard board3;
  /**
   * @param name
   */
  public StringBoardTest(String name) {
    super(name);
  }

  /* (非 Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    board3 = new StringBoard(4, 4, "123856749=CBDEF0");
    board3.setOperationHistory("LDRUDULDUUU");
  }

  /**
   * {@link dev.puzzle.sample.StringBoard#hashCode()} のためのテスト・メソッド。
   */
  public void testHashCode() {
    int hash = board1.hashCode();
    System.out.println("hash: "+hash);
    assertEquals(-1757203306, hash);
    
    assertNotSame(hash, board3.hashCode);
    assertEquals(board3.hashCode(), board3.hashCode());
  }

  /**
   * {@link dev.puzzle.sample.StringBoard#StringBoard()} のためのテスト・メソッド。
   */
  public void testStringBoard() {
    board2 = new StringBoard();
    assertNotNull(board2);
  }

  /**
   * {@link dev.puzzle.sample.StringBoard#StringBoard(java.lang.String)} のためのテスト・メソッド。
   */
  public void testStringBoardString() {
    board2 = new StringBoard(0, 0, "");
    int hash = board2.hashCode();
    assertEquals(0, hash);
  }

  /**
   * {@link dev.puzzle.sample.StringBoard#getStringMap()} のためのテスト・メソッド。
   */
  public void testGetStringMap() {
    String map = board3.getStringMap();
    assertEquals("123856749=CBDEF0", map);
  }

  /**
   * {@link dev.puzzle.sample.StringBoard#setStringMap(java.lang.String)} のためのテスト・メソッド。
   */
  public void testSetStringMap() {
    board3.setStringMap("1234=68907AB");
    String map = board3.getStringMap();
    assertEquals("1234=68907AB", map);
  }

  /**
   * {@link dev.puzzle.sample.StringBoard#getOperationHistory()} のためのテスト・メソッド。
   */
  public void testGetOperationHistory() {
    String operation = board3.getOperationHistory();
    assertEquals("LDRUDULDUUU", operation);
  }

  /**
   * {@link dev.puzzle.sample.StringBoard#setOperationHistory(java.lang.String)} のためのテスト・メソッド。
   */
  public void testSetOperationHistory() {
    board3.setOperationHistory("LDURUDLUDDDDULR");
    assertEquals("LDURUDLUDDDDULR", board3.getOperationHistory());
  }
  
  public void testGetOperableList(){
    int operableList = board3.getOperableList();
    assertEquals(5, operableList);
    
    StringBoard test = new StringBoard(3, 3, "=30456782");
    assertEquals(9, test.getOperableList());

    StringBoard test1 = new StringBoard(4, 4, "12345=7809ABDEFC");
    assertEquals(14, test1.getOperableList());

    StringBoard test2 = new StringBoard(4, 3, "10=4257=6AB9");
    assertEquals(9, test2.getOperableList());

    StringBoard test3 = new StringBoard(3, 5, "0123467895BC=EA");
    assertEquals(10, test3.getOperableList());

    StringBoard test4 = new StringBoard(3, 5, "7=234=0=95B==EA");
    assertEquals(0, test4.getOperableList());
  }
  
  public void testOperate(){
    StringBoard newBoard = null;
    try {
      newBoard = board3.operate(StringBoard.COMMAND_L);
    } catch (CloneNotSupportedException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
    int hash = newBoard.hashCode();
    assertNotSame(hash, board3.hashCode());
    
    StringBoard expected = new StringBoard(4, 4, "123856749=CBDE0F");
    assertEquals(expected.hashCode(), hash);
  }

}
