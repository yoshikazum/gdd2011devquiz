/**
 * 
 */
package dev.puzzle.sample;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author "Yoshikazu Miyoshi <yoshikazum@gmail.com>"
 *
 */
public class MapQueue extends AbstractQueue<StringBoard> {
  
  ArrayList<StringBoard> list;
  
  public MapQueue() {
    list = new ArrayList<StringBoard>();
  }
  
  public boolean offer(StringBoard e) {
    return list.add(e);
  }

  public StringBoard poll() {
    StringBoard map = list.get(0);
    list.remove(0);
    return map;
  }

  public StringBoard peek() {
    return list.get(0);
  }

  @Override
  public Iterator<StringBoard> iterator() {
    return list.iterator();
  }

  @Override
  public int size() {
    return list.size();
  }
  
  public boolean removeAll(){
    list = null;
    list = new ArrayList<StringBoard>();
    return true;
  }

}
