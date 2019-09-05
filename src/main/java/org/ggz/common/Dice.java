package org.ggz.common;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * A class for rolling dice
 */
class Dice {

  private final Random random;        // Randomizer
  private final int sides;            // Number of sides

  Dice(int sides) {
    this.sides = sides;
    this.random = new Random();
  }

  static Dice d20() {
    return new Dice(20);
  }

  static Dice d8() {
    return new Dice(8);
  }

  static Dice d6() {
    return new Dice(6);
  }


  Dice setSeed(long seed) {
    random.setSeed(seed);
    return this;
  }

  /**
   * Roll the dice
   *
   * @return a number in the range [1, ..., n]
   */
  int roll() {
    return random.nextInt(sides) + 1;
  }

  /**
   * Roll the dice a given number of times
   *
   * @param count number to roll
   * @return sum of `count` rolls
   */
  int roll(int count) {
    return IntStream.range(0, count).map(i -> roll()).sum();
  }

}
