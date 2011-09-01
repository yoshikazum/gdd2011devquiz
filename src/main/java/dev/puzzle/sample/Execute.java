package dev.puzzle.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

/**
 * スライドパズル TODO: ・ファイル読み込みとデータベース化 ・全パネル評価 ・操作ヒストリ ・操作数の集計 ・回答パネル管理 ・ファイル出力
 * 
 * @author "Yoshikazu Miyoshi <yoshikazum@gmail.com>"
 * 
 */

@SuppressWarnings("unchecked")
public class Execute {
  

  public static void main(String[] args){
    System.out.println("main");
    StringBoard aBoard = new StringBoard(3, 3, "123456708");
    Execute exe = new Execute();
    HashMap<Integer, StringBoard> history = new HashMap<Integer, StringBoard>();
    history.put(aBoard.hashCode(), aBoard);
    exe.solveDepth(0, aBoard, history, 1);
  }
  
  /**
   * コンストラクター
   */
  public Execute() {
    histories = new ArrayList<OperationHistory>();
  }

  static Integer ZEROPOSITIONKEY = new Integer(-100);
  static Integer WIDTHKEY = new Integer(-200);
  static Integer HEIGHTKEY = new Integer(-300);

  static int COMMAND_L = 01; // 1
  static int COMMAND_R = COMMAND_L << 1; // 2
  static int COMMAND_U = COMMAND_L << 2; // 4
  static int COMMAND_D = COMMAND_L << 3; // 8

  /**
   * マップを受け取り、どの操作ができるか返す
   * 
   * @param map
   * @return
   */
  public int commandListFromMap(Map<Integer, Integer> map) {
    Integer zeroPosition = map.get(ZEROPOSITIONKEY);
    Integer leftValue = map.get(zeroPosition.intValue() - 1);
    Integer rightValue = map.get(zeroPosition.intValue() + 1);
    Integer upValue = map.get(zeroPosition.intValue() - 10);
    Integer downValue = map.get(zeroPosition.intValue() + 10);

    int result = 0;

    if (leftValue != null && leftValue.intValue() > 0)
      result += COMMAND_L;
    if (rightValue != null && rightValue.intValue() > 0)
      result += COMMAND_R;
    if (upValue != null && upValue.intValue() > 0)
      result += COMMAND_U;
    if (downValue != null && downValue.intValue() > 0)
      result += COMMAND_D;

    // System.out.println("result: " + String.valueOf(result));
    return result;
  }

  public HashMap<Integer, Integer> operate(
      HashMap<Integer, Integer> inputMap,
      int operation) {
    HashMap<Integer, Integer> map =
        (HashMap<Integer, Integer>) inputMap.clone();
    Integer zeroLocation = map.get(ZEROPOSITIONKEY);
    Integer destLocation = null;
    int destLocationInt = 0;
    int tmp;
    if (operation == COMMAND_D) {
      destLocationInt = zeroLocation.intValue() + 10;
    } else if (operation == COMMAND_L) {
      destLocationInt = zeroLocation.intValue() - 1;
    } else if (operation == COMMAND_R) {
      destLocationInt = zeroLocation.intValue() + 1;
    } else if (operation == COMMAND_U) { // COMMAND_U
      destLocationInt = zeroLocation.intValue() - 10;
    } else {
      // error
    }
    destLocation = new Integer(destLocationInt);
    tmp = map.get(destLocation);
    if (tmp < 0)
      return null;
    map.remove(destLocation);
    map.put(destLocation, new Integer(0));
    map.remove(zeroLocation);
    map.put(zeroLocation, tmp);
    map.remove(ZEROPOSITIONKEY);
    map.put(ZEROPOSITIONKEY, destLocation); // 空白のポジションを記憶

    return map;
  }

  private Map<String, Integer> totalCommands;

  public Map<String, Integer> getTotalCommands() {
    return totalCommands;
  }

  private int totalBoards;

  public int getTotalBoards() {
    return this.totalBoards;
  }

  private List<Board> boardList;

  public List<Board> getBoardList() {
    return boardList;
  }

  public void readInputFile(String path) {
    File file = new File(path);
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }

    String str = null;
    try {
      // LX, RX, UX, DX: 使うことができる L, R, U, D それぞれの総数読み込み
      str = reader.readLine();
      totalCommands = new HashMap<String, Integer>();
      String[] commands = str.split(" ");
      totalCommands.put("LX", new Integer(commands[0]));
      totalCommands.put("RX", new Integer(commands[1]));
      totalCommands.put("UX", new Integer(commands[2]));
      totalCommands.put("DX", new Integer(commands[3]));

      // 総ボード数 (整数)
      str = reader.readLine();
      totalBoards = Integer.valueOf(str).intValue();
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }

    // 全ボードの読み込み
    boardList = new ArrayList<Board>();
    int line = 0;
    while (str != null) {
      line++;
      try {
        str = reader.readLine();
        if (str == null)
          continue;

        String[] stringBoard = str.split(",");
        int w = new Integer(stringBoard[0]).intValue();
        int h = new Integer(stringBoard[1]).intValue();
        String content = new String(stringBoard[2].trim());
        Board aBoard = new Board(line, w, h, content);
        boardList.add(aBoard);
      } catch (IOException e) {
        // TODO 自動生成された catch ブロック
        e.printStackTrace();
      }
    }

    try {
      reader.close();
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
  }

  public
      void
      writeToFile(String filePath, ArrayList<OperationHistory> histories) {

    BufferedWriter fileWriter = null;
    try {

      fileWriter =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              "output.txt"), "UTF-8"));

      for (Iterator iterator = histories.iterator(); iterator.hasNext();) {
        OperationHistory operationHistory = (OperationHistory) iterator.next();

        if (operationHistory.getResult()) {
          fileWriter.write(operationHistory.getOperation());
          fileWriter.newLine();
        } else {
          fileWriter.newLine();
        }
        fileWriter.flush();
      }

      fileWriter.close();
      System.out.println("書き込み完了");

    } catch (Exception e) {
      System.out.println(e);
    } finally {
    }
  }

  public void sortBoardByEstimatedValue(List<Board> list) {
    Collections.sort(list, new Comparator<Board>() {

      public int compare(Board board1, Board board2) {
        int value1 = board1.getEstimatedValue();
        int value2 = board2.getEstimatedValue();
        int result = value1 - value2;
        return result;
      }
    });
  }

  private ArrayList<OperationHistory> histories;

  public ArrayList<OperationHistory> getHistories() {
    return histories;
  }

  public void setHistories(ArrayList<OperationHistory> histories) {
    this.histories = histories;
  }

  public boolean solve(StringBoard aBoard) {
    StringBoard map = aBoard.clone();
    exeQueue = new MapQueue();
    exeQueue.offer(map);
    searchedMap = new Hashtable<Integer, StringBoard>();

    StringBoard result = null;
    lowestCostBoard = aBoard.clone();
    System.out.print("solving");

    int i = 0;
    int n = 8;
    int limit = 30;
    for (int j = 0; j < 200; j++) {
      while (result == null) {
        System.out.print(".");
        result = solveMap(map, limit);
        i++;
        if (i > n) {
          break;
        }
      }

      if (result != null) {
        System.out.println("result: " + result.getOperationHistory());
        break;
      }

      if (map.hashCode() == lowestCostBoard.hashCode()) {
        System.err.println("solving failed");
        n++;
      }
      i = 0;
      System.out.println("lowcost: " + lowestCostBoard.getEstimatedValue());
      map = lowestCostBoard.clone();
      exeQueue = new MapQueue();
      exeQueue.offer(map);

      // 直前のマップを追加
      int lastOperation = map.getLastOperation();
      StringBoard oldMap = null;
      try {
        oldMap = map.operate(lastOperation);
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
      searchedMap = new Hashtable<Integer, StringBoard>();
      searchedMap.put(new Integer(oldMap.hashCode()), oldMap);
      j++;
    }
    if (result != null)
      System.out.println("result: " + result.getOperationHistory());

    return true;
  }

  MapQueue exeQueue;
  Hashtable<Integer, StringBoard> searchedMap;
  StringBoard lowestCostBoard;

  public StringBoard solveMap(StringBoard aBoard, int limit) {

    boolean result = false;

    int commandList = 0;
    boolean search = false;
    int initialQueueSize = exeQueue.size();
    while (initialQueueSize > 0) {
      initialQueueSize--;

      StringBoard aMap = exeQueue.poll();

      if (lowestCostBoard.score() >= aMap.score()) {
        lowestCostBoard = aMap.clone();
      }

      ArrayList<StringBoard> map = new ArrayList<StringBoard>(5);
      for (int i1 = 0; i1 < 5; i1++) {
        map.add((StringBoard) aMap.clone());
      }

      String historyString = map.get(0).getOperationHistory();
      commandList = map.get(0).getOperableList();
      // System.out.println("command: " + commandList);

      try {
        if ((commandList & COMMAND_D) == COMMAND_D) {
          map.set(1, map.get(1).operate(COMMAND_D));
          result = aBoard.compareTo(map.get(1));
          if (result) {
            System.out.println("success");
            map.get(1).setOperationHistory(historyString + "D");
            return map.get(1);
          }
        }
        if ((commandList & COMMAND_L) == COMMAND_L) {
          map.set(2, map.get(2).operate(COMMAND_L));
          result = aBoard.compareTo(map.get(2));
          if (result) {
            System.out.println("success");
            map.get(2).setOperationHistory(historyString + "L");
            return map.get(2);
          }
        }
        if ((commandList & COMMAND_R) == COMMAND_R) {
          map.set(3, map.get(3).operate(COMMAND_R));
          result = aBoard.compareTo(map.get(3));
          if (result) {
            System.out.println("success");
            map.get(3).setOperationHistory(historyString + "R");
            return map.get(3);
          }
        }
        if ((commandList & COMMAND_U) == COMMAND_U) {
          map.set(4, map.get(4).operate(COMMAND_U));
          result = aBoard.compareTo(map.get(4));
          if (result) {
            System.out.println("success");
            map.get(4).setOperationHistory(historyString + "U");
            return map.get(4);
          }
        }
      } catch (CloneNotSupportedException e) {
        // TODO 自動生成された catch ブロック
        e.printStackTrace();
      }

      // ヒストリーと比較
      for (int k = 1; k < 5; k++) {
        StringBoard hashMap = map.get(k);
        if (hashMap == null)
          continue;

        int hash = hashMap.hashCode();
        boolean alreadySearched = searchedMap.contains(hash);

        if (alreadySearched) {
          continue;
        } else {
          searchedMap.put(new Integer(hashMap.hashCode()), hashMap);
        }

        // 下限法
        int newLow = hashMap.getEstimatedValue();
        int move = map.get(0).getOperationHistory().length();
        if (newLow + move > limit)
          continue;

        String operation = "";
        if (k == 1)
          operation = "D";
        if (k == 2)
          operation = "L";
        if (k == 3)
          operation = "R";
        if (k == 4)
          operation = "U";

        boolean success = exeQueue.offer(hashMap);
        if (!success)
          System.err.println("offer failed");
        hashMap.setOperationHistory(historyString + operation);
        // System.out.print(operation);
      }
    }

    return null;
  }
  boolean result = false;
  public boolean solveDepth(int i, StringBoard aBoard, HashMap<Integer, StringBoard> history, int limit) {
    //System.out.print(".");
    String goal = aBoard.getGoal();
    StringBoard goalMap = new StringBoard(aBoard.height, aBoard.width, goal);
    
    //System.out.println("test: "+ aBoard.getStringMap()+ ", goal: "+goalMap.getStringMap());
    if (aBoard.compareTo(aBoard)) {
      System.out.println("result: " + aBoard.getOperationHistory());
      this.result = true;
      return true;
    }
    //int limit = aBoard.getEstimatedValue()*3;
    if (i > limit) {
      //System.out.println(aBoard.getOperationHistory());
      //System.out.println("over 60");
      //System.out.print(".");
      return false;
    } else {
      
      int operationList = aBoard.getOperableList();
      int lastOpelation = aBoard.getLastOperation();
      //System.out.println(operationList+ "-"+ lastOpelation);
      operationList -= lastOpelation;
      String historyString = aBoard.getOperationHistory();
      int lastCommand = 0;
      for (int j = 0; j < 4; j++) {
        if (operationList == 0)
          break;
        StringBoard nextBoard = null;
        if ((operationList & COMMAND_D) == COMMAND_D) {
          try {
            nextBoard = aBoard.operate(COMMAND_D);
            nextBoard.setOperationHistory(historyString + "D");
            lastCommand = COMMAND_D;
          } catch (CloneNotSupportedException e) {
            e.printStackTrace();
          }
          operationList -= COMMAND_D;
        } else if ((operationList & COMMAND_L) == COMMAND_L) {
          try {
            nextBoard = aBoard.operate(COMMAND_L);
            nextBoard.setOperationHistory(historyString + "L");
            lastCommand = COMMAND_L;
          } catch (CloneNotSupportedException e) {
            e.printStackTrace();
          }

          operationList -= COMMAND_L;
        } else if ((operationList & COMMAND_R) == COMMAND_R) {
          try {
            nextBoard = aBoard.operate(COMMAND_R);
            nextBoard.setOperationHistory(historyString + "R");
            lastCommand = COMMAND_R;
          } catch (CloneNotSupportedException e) {
            e.printStackTrace();
          }

          operationList -= COMMAND_R;
        } else {
          try {
            nextBoard = aBoard.operate(COMMAND_U);
            nextBoard.setOperationHistory(historyString + "U");
            lastCommand = COMMAND_U;
          } catch (CloneNotSupportedException e) {
            e.printStackTrace();
          }

          operationList -= COMMAND_U;
        }
        

        // 下限法
        int newLow = nextBoard.getEstimatedValue();
        int move = aBoard.getOperationHistory().length();
        if (newLow + move > limit*2){
          continue;
        }
        
        if (history.containsKey(nextBoard.hashCode())) {

        } else {
          history.put(new Integer(nextBoard.hashCode()), nextBoard);
          solveDepth(++i, nextBoard, history, limit);
        }
      }
    }
    return false;
  }
}
