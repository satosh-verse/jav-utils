package me.samen.jutil;

import java.util.HashMap;
import java.util.Set;

/**
 * @author satosh.dhanyamraju
 */
public class TimeAnalyzer<T,K> {
  private PerfMeasureHelper<T> perfMeasureHelper;
  private Matcher<T,K> matcher;
  private HashMap<K, Long> aggTimesMap;

  public TimeAnalyzer(PerfMeasureHelper<T> perfMeasureHelper,
                      Matcher<T, K> matcher) {
    if (matcher == null || perfMeasureHelper == null) {
      throw new IllegalArgumentException("all params required");
    }
    this.perfMeasureHelper = perfMeasureHelper;
    this.matcher = matcher;
    aggTimesMap = new HashMap<>();
    aggregate();
  }

  /**
   * each key in perfMeasure helper will be categorized as per matcher.
   * Sum of times each category is maintained.
   * @return
   */
  private boolean aggregate() {
    Set<T> keys = perfMeasureHelper.getKeys();
    for (T t : keys) {
      K typeT = matcher.getType(t);
      Long cur = perfMeasureHelper.getTotalTime(t), total = 0L;
      if (aggTimesMap.containsKey(typeT)) {
        total = aggTimesMap.get(typeT);
      }
      aggTimesMap.put(typeT, cur + total);
    }
    return true;
  }

  public String getDump() {
    StringBuilder builder = new StringBuilder();
    for (K k : aggTimesMap.keySet()) {
      builder.append(k.toString()).append("=").append(aggTimesMap.get(k)).append(",\n");
    }
    return builder.toString();
  }

  public Long getTotalTime(K k) {
    return aggTimesMap.get(k);
  }

  public Set<K> getKeys() {
    return aggTimesMap.keySet();
  }



  public interface Matcher<T,K> {
    K getType(T t);
  }
}
