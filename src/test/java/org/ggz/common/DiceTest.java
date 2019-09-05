package org.ggz.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class DiceTest {

  private static final int RUNS = 10000;

  @Test
  void test_range_looks_good() {
    Dice dice = new Dice(5);
    long count = IntStream.range(0, RUNS).map(i -> dice.roll()).distinct().count();
    int min = IntStream.range(0, RUNS).map(i -> dice.roll()).min().orElse(-1);
    int max = IntStream.range(0, RUNS).map(i -> dice.roll()).max().orElse(-1);
    assertEquals(5, count);
    assertEquals(1, min);
    assertEquals(5, max);
  }

  @Test
  void test_setting_seed_gives_same_result_over_and_over() {
    Dice dice = new Dice(20);
    long count = IntStream.range(0, RUNS)
        .map(i -> dice.setSeed(13).roll()).distinct().count();
    assertEquals(1, count);
  }

  @Test
  void test_d20() {
    Dice dice = Dice.d20().setSeed(13);
    long count = IntStream.range(0, RUNS).map(i -> dice.roll()).distinct().count();
    assertEquals(20, count);
  }

  @Test
  void test_d8() {
    Dice dice = Dice.d8().setSeed(13);
    long count = IntStream.range(0, RUNS).map(i -> dice.roll()).distinct().count();
    assertEquals(8, count);
  }

  @Test
  void test_d6() {
    Dice dice = Dice.d6().setSeed(13);
    long count = IntStream.range(0, RUNS).map(i -> dice.roll()).distinct().count();
    assertEquals(6, count);
  }

  @Test
  void test_roll_of_multiples() {
    Dice dice = Dice.d6().setSeed(13);
    Map<Integer, Long> counts = IntStream.range(0, RUNS).map(i -> dice.roll(2)).boxed()
        .collect(Collectors.groupingBy(i -> i, Collectors.counting()));

    assertEquals(11, counts.size());                    // Should be values [2..12]
    assertTrue(counts.get(7) > 5 * counts.get(2));      // '7' is ~ 6x common than '2'
  }

}
