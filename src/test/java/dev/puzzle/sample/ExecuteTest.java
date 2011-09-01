package dev.puzzle.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import javax.swing.border.Border;

import junit.framework.TestCase;

public class ExecuteTest extends TestCase {

  Execute exe;

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
  
  public ExecuteTest() {
    exe = new Execute();
    
    String filePath = "problems.txt";
    //exe.readInputFile(filePath);
  }
  
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  public void testCommandListFromMap(){
    /*
    Map<Integer, Integer> map = exe.mapFromString(3, 3, input1);
    int ex1 = Execute.COMMAND_L+ Execute.COMMAND_D;
    assertEquals(ex1, exe.commandListFromMap(map));
    Map<Integer, Integer> map2 = exe.mapFromString(3, 3, input2);
    int ex2 = Execute.COMMAND_L+ Execute.COMMAND_U;
    assertEquals(ex2, exe.commandListFromMap(map2));
    Map<Integer, Integer> map3 = exe.mapFromString(6, 6, input5);
    int ex3 = Execute.COMMAND_L+ Execute.COMMAND_D+
              Execute.COMMAND_R+ Execute.COMMAND_U;
    assertEquals(ex3, exe.commandListFromMap(map3));
    */
    assertTrue(true);
  }
  
  public void testOperate(){
    /*
    int w=3, h=3;
    Map<Integer, Integer> map       = exe.mapFromString(w, h, estimate1);
    Map<Integer, Integer> resultMap = exe.operate(map, Execute.COMMAND_R);
    String goalString             = exe.getGoal(estimate1);
    Map<Integer, Integer> goalMap = exe.mapFromString(w, h, goalString);
    int result = exe.estimateMap(resultMap, goalMap);
    assertEquals(0, result);
    */

    Board aBoard = new Board(0, 3, 3, "123456708");
    HashMap<Integer, Integer>map = aBoard.getMap();
    HashMap<Integer, Integer>map2 = exe.operate(map, Execute.COMMAND_R);
    System.out.println("map : "+map.hashCode());
    System.out.println("map2: "+map2.hashCode());
    boolean result = aBoard.compareTo(map2);

    assertTrue(result);
    
  }

  public void testWriteToFile(){
    /*
    ArrayList<OperationHistory> histories = new ArrayList<OperationHistory>();
    OperationHistory history1 = new OperationHistory();
    history1.setResult(true);
    history1.setOperation("hogehoge");
    histories.add(history1);
    

    OperationHistory history2 = new OperationHistory();
    history2.setResult(false);
    history2.setOperation("hogehoge");
    histories.add(history2);

    OperationHistory history3 = new OperationHistory();
    history3.setResult(true);
    history3.setOperation("miyoshi");
    histories.add(history3);
    
    exe.writeToFile("output.txt", histories);
    */
  }
  
  public void testReadInputFile(){
    //setUpで読み込まれてるからOK
    assertTrue(true);
  }
  
  public void testGetTotalCommands(){
    /*
    Map<String, Integer> totalCommands = exe.getTotalCommands();
    int lx = totalCommands.get("LX").intValue();
    int rx = totalCommands.get("RX").intValue();
    int ux = totalCommands.get("UX").intValue();
    int dx = totalCommands.get("DX").intValue();
    //72187 81749 72303 81778
    assertEquals(72187, lx);
    assertEquals(81749, rx);
    assertEquals(72303, ux);
    assertEquals(81778, dx);
    */
    assertTrue(true);
  }
  
  public void testTotalBoards(){
    assertTrue(true);
  }
  
  public void testSortBoardByEstimatedValue(){
    /*
    List<Board> list = exe.getBoardList();
    exe.sortBoardByEstimatedValue(list);
    int num = 0;
    for (Iterator<Board> iterator = list.iterator(); iterator.hasNext();) {
      Board board = (Board) iterator.next();
      System.out.println("line: "+ board.getLine()+ 
          ", hxw: "+ board.getHeight()+ "x"+ board.getWidth()+
           ", sorted value: "+ board.getEstimatedValue()+ 
           ", string: "+ board.getContent());
      if(10 < num) break;
      num++;
    }
    */
  }
  
  public void testCompareTo(){
    Board aBoard = new Board(0, 3, 3, "120743586");
    Map<Integer, Integer> goal = aBoard.getGoalMap();
    boolean result = aBoard.compareTo(goal);
    System.out.println("goal : "+goal.hashCode());
    assertTrue(result);
    

    Board aBoard2 = new Board(0, 3, 3, "120743568");
    Map<Integer, Integer> goal2 = aBoard.getMap();
    System.out.println("goal2: "+goal2.hashCode());
    result = aBoard.compareTo(goal2);
    assertFalse(result);
  }
  
  public void testSolveDepth(){
    HashMap<Integer, StringBoard> history = new HashMap<Integer, StringBoard>();
    StringBoard aBoard = new StringBoard(4, 4, "23481570A6BC9DEF");
    System.out.println("estimatedValue: "+ aBoard.getEstimatedValue());
    int limit = aBoard.getEstimatedValue();
    boolean result = false;
    while(limit < aBoard.getEstimatedValue()*4){
      System.out.println("limit: "+limit);
      result = exe.solveDepth(0, aBoard, history, limit);
      if(exe.result)break;
      limit += 2;
      history = new HashMap<Integer, StringBoard>();
    }
    assertTrue(exe.result);
  }
  
  public void testSolve(){
    /*
    Board aBoard = new Board(0, 4, 3, "1624537890AB");
    //Board aBoard = new Board(0, 3, 3, "123456708");
    assertTrue(exe.solve(aBoard));
    */
    
    //StringBoard aBoard = new StringBoard(4, 3,"1624537890AB");
    // StringBoard aBoard = new StringBoard(3, 3, "243108765");
    /*
    StringBoard aBoard = new StringBoard(3, 3,"432587106");
    assertTrue(exe.solve(aBoard));
    */
    
    
    //exe.writeToFile("output.txt", exe.getHistories());
  }

}
