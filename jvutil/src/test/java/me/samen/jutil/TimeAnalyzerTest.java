package me.samen.jutil;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (c) 2016 Newshunt. All rights reserved.
 */
public class TimeAnalyzerTest {
  PerfMeasureHelper<String> perfMeasureHelper;
  TimeAnalyzer.Matcher<String, Integer> matcher;
  TimeAnalyzer<String, Integer> timeAnalyzer;
  @Before
  public void setUp() throws Exception {
    String a = "a", a1 = "a1", b = "b";
    perfMeasureHelper=new PerfMeasureHelper<>();
    perfMeasureHelper.start(a);
    perfMeasureHelper.start(a1);
    perfMeasureHelper.start(b);
    waitFor(100);
    perfMeasureHelper.stop(a);
    waitFor(100);
    perfMeasureHelper.stop(a1);
    waitFor(100);
    perfMeasureHelper.stop(b);

    matcher = new TimeAnalyzer.Matcher<String, Integer>() {
      @Override
      public Integer getType(String s) {
        return s.startsWith("a") ? 0 : 1;
      }
    };
    timeAnalyzer = new TimeAnalyzer<>(perfMeasureHelper, matcher);
  }

  @Test
  public void testNumOfKeys() throws Exception {
    assertEquals(2, timeAnalyzer.getKeys().size());
  }

  @Test
  public void testKeys() throws Exception {
    ArrayList<Integer> keys = new ArrayList<>(timeAnalyzer.getKeys());
    Collections.sort(keys);
    assertEquals(2, keys.size());
    assertEquals(Integer.valueOf(0), keys.get(0));
    assertEquals(Integer.valueOf(1), keys.get(1));
  }

  @Test
  public void testAggregate() throws Exception {
    assertEquals(Long.valueOf(300), timeAnalyzer.getTotalTime(0));
    assertEquals(Long.valueOf(300), timeAnalyzer.getTotalTime(1));
  }


  private void waitFor(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}