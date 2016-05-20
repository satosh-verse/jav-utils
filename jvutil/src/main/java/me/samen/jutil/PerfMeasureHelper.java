package me.samen.jutil;

// TODO: 5/19/16 copyright
/**
 *
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * For measuring performance, counting time diffs and logging
 * Class is tolerant to wrong invocation order. Returns false for failures/errors
 *
 * @author satosh.dhanyamraju
 */
public class PerfMeasureHelper<T> {

  private HashMap<T, Entry> map;

  public PerfMeasureHelper() {
    this.map = new HashMap<>();
  }

  /**
   * starts timer for key t
   * @param t key
   * @return true if success, false if error
   */
  public boolean start(T t) {
    Entry entry = map.get(t);
    if (entry == null) {
      entry = new Entry(t.toString());
    }
    boolean ret = entry.start();
    map.put(t, entry);
    return ret;
  }

   /**
   * stops timer for key t
   * @param t key
   * @return true if success, false if error
   */
  public boolean stop(T t) {
    Entry entry = map.get(t);
    if (entry == null) {
      return false;
    }
    boolean ret = entry.stop();
    map.put(t, entry);
    return ret;
  }

  /**
   * sum of all (stopTime-startTime) for key t
   * @param t key
   * @return - time in millsec, -1 if error
   */
  public long getTotalTime(T t) {
    return getTotalTime(t, TimeUnit.MILLISECONDS);
  }


  /**
   * sum of all (stopTime-startTime) for key t
   * @param t key
   * @param unit timeunit
   * @return - time in unit, -1 if error
   */
  public long getTotalTime(T t, TimeUnit unit) {
     Entry entry = map.get(t);
    if (entry == null) {
      return -1;
    }
    return unit.convert(entry.totalTime(), TimeUnit.NANOSECONDS);
  }

  public String getDump(TimeUnit unit) {
    StringBuilder builder = new StringBuilder();

    for (T t : map.keySet()) {
      long totalTime = map.get(t).totalTime();
      long converted = unit.convert(totalTime, TimeUnit.NANOSECONDS);
      builder.append(t.toString()).append("=").append(converted).append(",\n");
    }
    return builder.toString();
  }

  /**
   * @return String listing all entries. Time in millisec
   */
  public String getDump() {
    return getDump(TimeUnit.MILLISECONDS);
  }

  public Set<T> getKeys() {
    return map.keySet();
  }

  private  class Entry {
    String name;
    LinkedList<Long> strt, stp;

    public Entry(String name) {
      this.name = name;
      strt = new LinkedList<>();
      stp = new LinkedList<>();

    }

    Entry() {
      this("Entry");
    }

    boolean start() {
      int r = strt.size();
      int p = stp.size();
      if (r < p || Math.abs(r - p) > 1) {
        return false;
      } else if (r == p + 1) {
        //replace last start entry
        strt.removeLast();
        strt.addLast(time());
        return true;
      }
      //add another start entry, r==p
      strt.addLast(time());
      return true;
    }

    boolean stop() {
      int r = strt.size();
      int p = stp.size();
      if (r != (p + 1)) {
        return false;//ERROR
      }
      stp.addLast(time());
      return true;
    }


    long totalTime() {
      int r = strt.size();
      int p = stp.size();
      if (p == 0) {
        return 0;
      } else if (r < p || Math.abs(r - p) > 1) {
        return -1; //ERROR
      }
      // p == r or r-1
      long sum = 0;
      for (int i = 0; i < p; i++) {
        long diff = stp.get(i) - strt.get(i);
        if (diff < 0) {
          //return  -1?
          continue;
        }
        sum = sum + diff;
      }
      return sum;
    }

    private long time() {
      return System.nanoTime();
    }
  }

}