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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

/**
 * スライドパズル
 * 
 * @author "Yoshikazu Miyoshi <yoshikazum@gmail.com>"
 * 
 */

@SuppressWarnings("unchecked")
public class Execute {

  public static void main(String[] args) {
    System.out.println("main");
     DFSThread();
    //BFS();
    //AStarThread();
  }

  public static void DFSThread() {
    int idNum = 1;
    int limit = 38;
    Execute exe = new Execute();
    while (limit < 400) {
      ExecutorService exec = Executors.newFixedThreadPool(2);

      for (Iterator iterator = exe.getSolvedDataFromId(idNum).iterator(); iterator
          .hasNext();) {
        StringBoard board = (StringBoard) iterator.next();
        if (board.getEstimatedValue() > limit
            || board.getOperationHistory().length() < limit
            /*|| (board.getOperationHistory().length() != 0
                && board.getOperationHistory().length() < limit - 3)*/)
          continue;
        
        exec.execute(new DFSExecutor(board, exe, limit));
      }

      exec.shutdown();
      try {
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      limit += 2;
    }
  }

  public static void AStarThread() {
    int idNum = 175;//174までやった
    Execute exe = new Execute();
    ExecutorService exec = Executors.newFixedThreadPool(2);
    for (Iterator iterator = exe.getSolvedDataFromId(idNum).iterator(); iterator
        .hasNext();) {
      StringBoard board = (StringBoard) iterator.next();
      exec.execute(new Executor(board, exe));
    }

    exec.shutdown();
    try {
      exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
  }

  public static void AStar() {
    Execute exe = new Execute();
    int idNum = 4600;
    for (Iterator iterator = exe.getSolvedDataFromId(idNum).iterator(); iterator
        .hasNext();) {
      StringBoard board = (StringBoard) iterator.next();
      StringBoard result = exe.solveByAStar(board);
      try {
        System.out.println("AStar id:" + board.id + " length:"
            + result.getOperationHistory().length() + " result: "
            + result.getOperationHistory());
        StringBoard storedBoard = exe.getBoardFromId(board.id);
        if (storedBoard.getOperationHistory().length() == 0
            || storedBoard.getOperationHistory().length() > result
                .getOperationHistory()
                .length()) {
          exe.updateOperation(board.id, result.getOperationHistory());
          System.out.println("stored History");
        }

      } catch (Exception e) {
        System.out.println("id: " + board.id + " Exception occered!!");
      }
    }
  }

  public static void DFS() {
    Execute exe = new Execute();
    ArrayList<StringBoard> histories;
    int idNum = 1;
    // for (Iterator iterator = exe.getUnsolvedData().iterator(); iterator
    // for (Iterator iterator = exe.getUnsolvedDataFromId(idNum).iterator();
    // iterator
    for (Iterator iterator = exe.getSolvedDataFromId(idNum).iterator(); iterator
        .hasNext();) {
      StringBoard board = (StringBoard) iterator.next();
      int limit = 60; // デフォルト200
      if (!board.getOperationHistory().equalsIgnoreCase("")) {
        // もし結果がnullでなければ回答の長さを取得し、それをリミットとする
        limit = board.getOperationHistory().length();
      }
      System.out.println("limit: " + limit);
      StringBoard resultBoard = exe.solveOneBoard(board, limit);
      histories = new ArrayList<StringBoard>();

      if (resultBoard == null) {
        System.out.println("failed");
        continue;
      }
      if (!resultBoard.getOperationHistory().trim().equalsIgnoreCase("")) {
        System.out.println("id: " + resultBoard.id + ", opeLength: "
            + resultBoard.getOperationHistory().length());

        if (limit > resultBoard.getOperationHistory().length()) {
          exe
              .updateOperation(
                  resultBoard.id,
                  resultBoard.getOperationHistory());
        }
      } else {
        System.out.println("failed");
      }
    }
  }

  public static void BFS() {

    Execute exe = new Execute();
    int idNum = 207; // 207まで最短解
    for (Iterator iterator = exe.getSolvedDataFromId(idNum).iterator(); iterator
        .hasNext();) {
      StringBoard board = (StringBoard) iterator.next();
      StringBoard result = exe.solveByBFS(board);
      try {
        System.out.println("BFS id:" + board.id + " length:"
            + result.getOperationHistory().length() + " result: "
            + result.getOperationHistory());
        StringBoard storedBoard = exe.getBoardFromId(board.id);
        if (storedBoard.getOperationHistory().length() == 0
            || storedBoard.getOperationHistory().length() > result
                .getOperationHistory()
                .length()) {
          exe.updateOperation(board.id, result.getOperationHistory());
          System.out.println("stored History");
        }

      } catch (Exception e) {
        System.out.println("id: " + board.id + " Exception occered!!");
      }
    }
  }

  /**
   * コンストラクター
   */
  public Execute() {
    histories = new ArrayList<StringBoard>();
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

  private List<StringBoard> boardList;

  public List<StringBoard> getBoardList() {
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
    boardList = new ArrayList<StringBoard>();
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
        StringBoard content = new StringBoard(h, w, stringBoard[2].trim());
        boardList.add(content);
        // this.insertData(line, w, h, stringBoard[2].trim());
      } catch (IOException e) {
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

  public void writeToFile(String filePath) {

    // ArrayList<StringBoard> allresults = this.getAllDataFromDB();
    ArrayList<StringBoard> allresults = this.getAnswerableAllData();

    BufferedWriter fileWriter = null;
    try {

      fileWriter =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              "output.txt"), "UTF-8"));

      for (Iterator iterator = allresults.iterator(); iterator.hasNext();) {
        StringBoard board = (StringBoard) iterator.next();
        // System.out.println("result: " + board.getOperationHistory());
        // System.out.println("ope: " + operationHistory.getResult());
        if (board.getOperationHistory() != null) {
          fileWriter.write(board.getOperationHistory());
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

  private ArrayList<StringBoard> histories;

  public ArrayList<StringBoard> getHistories() {
    return histories;
  }

  public void setHistories(ArrayList<StringBoard> histories) {
    this.histories = histories;
  }

  public boolean solve(StringBoard aBoard) {
    StringBoard map = aBoard.clone();
    exeQueue = new MapQueue();
    exeQueue.offer(map);
    searchedMap = new HashMap<Integer, StringBoard>();

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
      searchedMap = new HashMap<Integer, StringBoard>();
      searchedMap.put(new Integer(oldMap.hashCode()), oldMap);
      j++;
    }
    if (result != null)
      System.out.println("result: " + result.getOperationHistory());

    return true;
  }

  MapQueue exeQueue;
  HashMap<Integer, StringBoard> searchedMap;
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
        boolean alreadySearched = searchedMap.containsKey(hash);

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
  private StringBoard resultStringBoard;
  StringBoard bestScoreBoard;

  public StringBoard solveDepth(int i, StringBoard aBoard, int limit) {
    if (aBoard.compareTo(aBoard)) {
      return aBoard;
    }

    StringBoard copy = aBoard.clone();
    PriorityQueue<StringBoard> open =
        new PriorityQueue<StringBoard>(1, new StringComparator());
    copy.setOperationHistory("");
    open.add(copy);

    HashMap<String, StringBoard> closed = new HashMap<String, StringBoard>();

    while (!open.isEmpty()) {
      StringBoard newBoard = open.poll();
      closed.put(newBoard.getStringMap(), newBoard);

      int operationList = newBoard.getOperableList();
      // operationList -= newBoard.getLastOperation();
      int operation = operationList;

      for (int j = 0; j < 4; j++) {
        if (operation % 2 != 1) {
          operation = operation >> 1;
          continue;
        }
        operation = operation >> 1;
        StringBoard opeBoard;
        try {
          if (j == 0) {
            opeBoard = newBoard.operate(COMMAND_L);
            opeBoard.setOperationHistory(newBoard.getOperationHistory() + "L");
          } else if (j == 1) {
            opeBoard = newBoard.operate(COMMAND_R);
            opeBoard.setOperationHistory(newBoard.getOperationHistory() + "R");
          } else if (j == 2) {
            opeBoard = newBoard.operate(COMMAND_U);
            opeBoard.setOperationHistory(newBoard.getOperationHistory() + "U");
          } else {
            opeBoard = newBoard.operate(COMMAND_D);
            opeBoard.setOperationHistory(newBoard.getOperationHistory() + "D");
          }

          if (closed.containsKey(opeBoard.getStringMap())) {
            continue;
          }

          if (opeBoard.getOperationHistory().length()
              + opeBoard.getEstimatedValue() > limit) {
            continue;
          }

          if (opeBoard.compareTo(opeBoard)) {
            // if(opeBoard.getOperationHistory().length() <
            // aBoard.getOperationHistory().length())
            return opeBoard.clone();
          }

          if (opeBoard.getOperationHistory().length() < limit) {
            open.add(opeBoard.clone());
          }
        } catch (CloneNotSupportedException e) {
          e.printStackTrace();
        }
      }
    }

    return null;
  }

  public StringBoard solveOneBoard(StringBoard aBoard, int maxLimit) {
    System.out.println("solving: " + aBoard.width + "x" + aBoard.height + ": "
        + aBoard.getStringMap() + ", estimatedValue: "
        + aBoard.getEstimatedValue() + ", size: "
        + aBoard.getOperationHistory().length());

    int limit = aBoard.getEstimatedValue();
    StringBoard result = null;
    while (result == null) {
      result = this.solveDepth(0, aBoard, limit);
      limit++;
    }

    return null;
  }

  public StringBoard solveByAStar(StringBoard aBoard) {
    PriorityQueue<StringBoard> open =
        new PriorityQueue<StringBoard>(1, new StringComparator());
    HashMap<String, StringBoard> closed = new HashMap<String, StringBoard>();
    StringBoard copy = aBoard.clone();
    open.add(copy);
    int limit = 500;
    String opeHistory = aBoard.getOperationHistory();
    if (opeHistory != null && opeHistory.length() != 0) {
      limit = opeHistory.length();
    }

    // 制限時間
    Calendar calendar = Calendar.getInstance();
    long start = calendar.getTimeInMillis();

    while (!open.isEmpty()) {

      // 10分立っていたら諦める
      Calendar now = Calendar.getInstance();
      long current = now.getTimeInMillis();
      if ((current - start) > 60 * 10 * 1000/* 3分 */) {
        System.out.println("id=" + aBoard.id + ":タイムアウト");
        return null;
      }

      StringBoard n = open.poll(); // 取り出し
      closed.put(n.getStringMap(), n);
      if (n.compareTo(n)) {
        System.out.println("found");
        if (n.getOperationHistory().length() < limit)
          return n;
      }

      int operationList = n.getOperableList();
      // operationList -= n.getLastOperation();
      int operation = operationList;
      for (int i = 0; i < 4; i++) {
        if (operation % 2 != 1) {
          operation = operation >> 1;
          continue;
        }
        operation = operation >> 1;
        StringBoard next = null;
        try {
          if (i == 0) {
            next = n.operate(COMMAND_L);
            next.setOperationHistory(n.getOperationHistory() + "L");
          }
          if (i == 1) {
            next = n.operate(COMMAND_R);
            next.setOperationHistory(n.getOperationHistory() + "R");
          }
          if (i == 2) {
            next = n.operate(COMMAND_U);
            next.setOperationHistory(n.getOperationHistory() + "U");
          }
          if (i == 3) {
            next = n.operate(COMMAND_D);
            next.setOperationHistory(n.getOperationHistory() + "D");
          }
        } catch (CloneNotSupportedException e) {
          e.printStackTrace();
        }
        if (next == null)
          continue;

        int nextScore =
            next.getOperationHistory().length() + next.getEstimatedValue();
        if (nextScore > limit) {
          //System.out.println("next score:" + nextScore);
          //closed.put(next.getStringMap(), next);
          continue;
        }

        StringBoard past = closed.get(next.getStringMap());
        if (past != null) {
          if (next.calculateSequenseScore() < past.calculateSequenseScore()) {
            closed.remove(past.getStringMap());
            open.add(next);
          } else {
            continue;
          }
        } else {
          open.add(next);
        }
      }
    }
    return null;
  }

  @SuppressWarnings("unused")
  public StringBoard solveByBFS(StringBoard aBoard) {

    StringBoard copy = aBoard.clone();
    copy.setOperationHistory("");

    HashMap<String, StringBoard> close = new HashMap<String, StringBoard>();
    LinkedList<StringBoard> queue = new LinkedList<StringBoard>();
    queue.add(copy);

    HashMap<String, StringBoard> closedGoal =
        new HashMap<String, StringBoard>();
    LinkedList<StringBoard> goalQueue = new LinkedList<StringBoard>();
    String goalString = copy.getGoal();
    StringBoard goal = new StringBoard(copy.height, copy.width, goalString);
    goalQueue.add(goal);
    while (!queue.isEmpty()) {

      // ***from start***//
      StringBoard newBoard = queue.poll();
      close.put(newBoard.getStringMap(), newBoard);

      // System.out.println("closed:"+close.size());
      if (close.size() > 800000) {
        // メモリの限界
        close = null;
        queue = null;
        closedGoal = null;
        goalQueue = null;
        return null;
      }
      int operationList = newBoard.getOperableList();
      int operation = operationList;
      String history = newBoard.getOperationHistory();
      for (int i = 0; i < 4; i++) {
        if (operation % 2 != 1) {
          operation = operation >> 1;
          continue;
        }
        operation = operation >> 1;
        StringBoard nextBoard = null;
        try {
          if (i == 0) {
            nextBoard = newBoard.operate(COMMAND_L);
            nextBoard.setOperationHistory(history + "L");
          } else if (i == 1) {
            nextBoard = newBoard.operate(COMMAND_R);
            nextBoard.setOperationHistory(history + "R");
          } else if (i == 2) {
            nextBoard = newBoard.operate(COMMAND_U);
            nextBoard.setOperationHistory(history + "U");
          } else {
            nextBoard = newBoard.operate(COMMAND_D);
            nextBoard.setOperationHistory(history + "D");
          }

          if (nextBoard == null)
            continue;

          if (close.containsKey(nextBoard.getStringMap()))
            continue;

          if (nextBoard.getEstimatedValue() > newBoard.getEstimatedValue() * 2)
            continue;

          if (nextBoard.compareTo(nextBoard)) {
            close = null;
            queue = null;
            closedGoal = null;
            goalQueue = null;
            return nextBoard;
          }

          queue.add(nextBoard.clone());

        } catch (CloneNotSupportedException e) {
          // TODO 自動生成された catch ブロック
          e.printStackTrace();
        }

      }

      // ***from goal***//
      StringBoard newGoal = goalQueue.poll();
      closedGoal.put(newGoal.getStringMap(), newGoal);
      operationList = newGoal.getOperableList();
      operation = operationList;
      String goalhistory = newGoal.getOperationHistory();
      for (int i = 0; i < 4; i++) {

        if (operation % 2 != 1) {
          operation = operation >> 1;
          continue;
        }
        operation = operation >> 1;
        StringBoard nextGoal = null;
        try {
          if (i == 0) {
            nextGoal = newGoal.operate(COMMAND_L);
            nextGoal.setOperationHistory("R" + goalhistory);
          } else if (i == 1) {
            nextGoal = newGoal.operate(COMMAND_R);
            nextGoal.setOperationHistory("L" + goalhistory);
          } else if (i == 2) {
            nextGoal = newGoal.operate(COMMAND_U);
            nextGoal.setOperationHistory("D" + goalhistory);
          } else {
            nextGoal = newGoal.operate(COMMAND_D);
            nextGoal.setOperationHistory("U" + goalhistory);
          }

          if (nextGoal == null)
            continue;

          if (closedGoal.containsKey(nextGoal.getStringMap()))
            continue;

          if (close.containsKey(nextGoal.getStringMap())) {
            System.out.println("found route");
            StringBoard result = close.get(nextGoal.getStringMap());
            result.setOperationHistory(result.getOperationHistory()
                + nextGoal.getOperationHistory());
            close = null;
            queue = null;
            closedGoal = null;
            goalQueue = null;
            return result;
          }

          goalQueue.add(nextGoal.clone());

        } catch (CloneNotSupportedException e) {
          // TODO 自動生成された catch ブロック
          e.printStackTrace();
        }
      }
    }

    return null;
  }

  public void createDB() {
    try {
      Class.forName("org.sqlite.JDBC");

      Connection conn = DriverManager.getConnection("jdbc:sqlite:results.db");
      Statement stmt = conn.createStatement();
      stmt
          .execute("create table results( id integer primary key, width integer, height integer, map text, operation text )");

      conn.close();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
  }

  public boolean insertData(int id, int width, int height, String map) {
    Connection conn = null;

    try {

      Class.forName("org.sqlite.JDBC");

      conn = DriverManager.getConnection("jdbc:sqlite:results.db");
      Statement stmt = conn.createStatement();
      stmt.execute("insert into results values (" + id + "," + width + ","
          + height + ",'" + map + "', null)");

      conn.close();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public ArrayList<StringBoard> getUnsolvedData() {
    Connection conn = null;

    try {

      Class.forName("org.sqlite.JDBC");

      conn = DriverManager.getConnection("jdbc:sqlite:results.db");
      Statement stmt = conn.createStatement();
      ResultSet rs =
          stmt.executeQuery("select * from results where operation IS NULL");

      ArrayList<StringBoard> list = new ArrayList<StringBoard>();
      for (ResultSet iterator = rs; iterator.next();) {
        int id = rs.getInt(1);
        int width = rs.getInt(2);
        int height = rs.getInt(3);
        String map = rs.getString(4);
        StringBoard board = new StringBoard(id, height, width, map);
        list.add(board);
      }

      conn.close();

      return list;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public ArrayList<StringBoard> getUnsolvedDataFromId(int idNum) {
    Connection conn = null;

    try {

      Class.forName("org.sqlite.JDBC");

      conn = DriverManager.getConnection("jdbc:sqlite:results.db");
      Statement stmt = conn.createStatement();
      ResultSet rs =
          stmt
              .executeQuery("select * from results where operation IS NULL AND id >= "
                  + idNum);

      ArrayList<StringBoard> list = new ArrayList<StringBoard>();
      for (ResultSet iterator = rs; iterator.next();) {
        int id = rs.getInt(1);
        int width = rs.getInt(2);
        int height = rs.getInt(3);
        String map = rs.getString(4);
        StringBoard board = new StringBoard(id, height, width, map);
        list.add(board);
      }

      conn.close();

      return list;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public ArrayList<StringBoard> getSolvedDataFromId(int idNum) {
    Connection conn = null;

    try {

      Class.forName("org.sqlite.JDBC");

      conn = DriverManager.getConnection("jdbc:sqlite:results.db");
      Statement stmt = conn.createStatement();
      ResultSet rs =
          stmt.executeQuery("select * from results where id >= " + idNum);

      ArrayList<StringBoard> list = new ArrayList<StringBoard>();
      for (ResultSet iterator = rs; iterator.next();) {
        int id = rs.getInt(1);
        int width = rs.getInt(2);
        int height = rs.getInt(3);
        String map = rs.getString(4);
        StringBoard board = new StringBoard(id, height, width, map);
        String ope = (rs.getString(5) != null) ? rs.getString(5) : "";
        board.setOperationHistory(ope);
        list.add(board);
      }

      conn.close();

      return list;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public ArrayList<StringBoard> getAnswerableAllData() {
    Connection conn = null;

    try {

      Class.forName("org.sqlite.JDBC");

      conn = DriverManager.getConnection("jdbc:sqlite:results.db");
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("select * from results;");

      ArrayList<StringBoard> list = new ArrayList<StringBoard>();
      for (ResultSet iterator = rs; iterator.next();) {
        int id = rs.getInt(1);
        int width = rs.getInt(2);
        int height = rs.getInt(3);
        String map = rs.getString(4);
        StringBoard board = new StringBoard(id, height, width, map);
        if (rs.getString(5) != null)
          board.setOperationHistory(rs.getString(5));
        list.add(board);
      }

      // 回答文字列が少ない順にソートする
      Collections.sort(list, new Comparator<StringBoard>() {

        @Override
        public int compare(StringBoard o1, StringBoard o2) {
          int num1 = o1.getOperationHistory().length();
          int num2 = o2.getOperationHistory().length();
          return num1 - num2;
        }
      });

      int countL = 0;
      int countR = 0;
      int countU = 0;
      int countD = 0;

      // 72187 81749 72303 81778
      int LX = 72187;
      int RX = 81749;
      int UX = 72303;
      int DX = 72303;

      int answeredCount = 0;
      // 始めの回答から文字列をカウントし、制限を超えたら後はlistから外す
      ArrayList<StringBoard> newList = new ArrayList<StringBoard>();
      for (Iterator<StringBoard> iterator = list.iterator(); iterator.hasNext();) {
        StringBoard stringBoard = (StringBoard) iterator.next();

        char[] chars = stringBoard.getOperationHistory().toCharArray();
        for (int i = 0; i < chars.length; i++) {
          char c = chars[i];
          if (c == 'L')
            countL++;
          if (c == 'R')
            countR++;
          if (c == 'U')
            countU++;
          if (c == 'D')
            countD++;
        }

        if (LX < countL || RX < countR || UX < countU || DX < countD) {
          // 文字列制限超えていたら空文字を入れる
          stringBoard.setOperationHistory("");
        } else {
          // 答えられる回答をカウントする
          if (stringBoard.getOperationHistory().length() != 0)
            answeredCount++;
        }
        newList.add(stringBoard);
      }
      System.out.println("AnsweredCount: " + answeredCount);

      // 元のid順に整列させる
      Collections.sort(newList, new Comparator<StringBoard>() {

        @Override
        public int compare(StringBoard o1, StringBoard o2) {
          int num1 = o1.id;
          int num2 = o2.id;
          return num1 - num2;
        }
      });

      conn.close();

      return newList;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public StringBoard getBoardFromId(int id_) {
    Connection conn = null;

    try {

      Class.forName("org.sqlite.JDBC");

      conn = DriverManager.getConnection("jdbc:sqlite:results.db");
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("select * from results where id=" + id_);

      if (rs == null)
        return null;

      int id = rs.getInt(1);
      int width = rs.getInt(2);
      int height = rs.getInt(3);
      String map = rs.getString(4);
      StringBoard board = new StringBoard(id, height, width, map);
      if (rs.getString(5) != null)
        board.setOperationHistory(rs.getString(5));

      conn.close();

      return board;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public ArrayList<StringBoard> getAllDataFromDB() {
    Connection conn = null;

    try {

      Class.forName("org.sqlite.JDBC");

      conn = DriverManager.getConnection("jdbc:sqlite:results.db");
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("select * from results");

      ArrayList<StringBoard> list = new ArrayList<StringBoard>();
      for (ResultSet iterator = rs; iterator.next();) {
        int id = rs.getInt(1);
        int width = rs.getInt(2);
        int height = rs.getInt(3);
        String map = rs.getString(4);
        StringBoard board = new StringBoard(id, height, width, map);
        if (rs.getString(5) != null)
          board.setOperationHistory(rs.getString(5));
        list.add(board);
        // System.out.println("ope: " + board.getOperationHistory());
      }

      conn.close();

      return list;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  synchronized public boolean updateOperation(int id, String operation) {
    Connection conn = null;

    try {
      //wait();
      Class.forName("org.sqlite.JDBC");

      conn = DriverManager.getConnection("jdbc:sqlite:results.db");
      Statement stmt = conn.createStatement();

      stmt.execute("update results set operation = '" + operation
          + "' where id = " + id);

      conn.close();
      //notifyAll();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      //notifyAll();
      return false;
    }
  }

  public int[] countAnswer() {
    Connection conn = null;
    try {
      Class.forName("org.sqlite.JDBC");

      conn = DriverManager.getConnection("jdbc:sqlite:results.db");
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("select * from results;");

      int countL = 0;
      int countR = 0;
      int countU = 0;
      int countD = 0;
      int countAnsered = 0;
      for (ResultSet iterator = rs; iterator.next();) {
        String operation = rs.getString(5);
        if (operation == null)
          continue;
        char[] chars = operation.toCharArray();
        for (int i = 0; i < chars.length; i++) {
          char c = chars[i];
          if (c == 'L')
            countL++;
          if (c == 'R')
            countR++;
          if (c == 'U')
            countU++;
          if (c == 'D')
            countD++;
        }
        countAnsered++;
      }
      conn.close();
      int[] counters = { countL, countR, countU, countD, countAnsered };
      return counters;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
