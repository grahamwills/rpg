package org.ggz.pf2.components;


import static org.ggz.pf2.SimFight.roll;

import org.ggz.pf2.Debug;

public class Damage {

  private final String name;
  private final Debug debug;
  private final int maxHits;

  private int hits;
  private int woundedLevel;
  private int dyingLevel;

  Damage(String name, int maxHits, Debug debug) {
    this.name = name;
    this.maxHits = maxHits;
    this.hits = maxHits;
    this.debug = debug;
  }

  public boolean isConscious() {
    return hits > 0;
  }

  public void reset() {
    if (dyingLevel == 0 || dyingLevel >= 4) {
      return;         // we are fine or we are dead
    }

    // Try and recover
    int deathRoll = roll(20);
    int target = 10 + dyingLevel;

    // How did we do?
    if (deathRoll == 1 || deathRoll < target - 10) {
      dyingLevel += 2;
      if (dyingLevel >= 4) {
        debug.write("  %s critically fails recovery and dies%n", name);
      } else {
        debug.write("  %s critically fails recovery (dying @ %d)%n", name, dyingLevel);
      }
    } else if (deathRoll < target) {
      dyingLevel++;
      if (dyingLevel >= 4) {
        debug.write("  %s fails recovery and dies%n", name);
      } else {
        debug.write("  %s fails recovery (dying @ %d)%n", name, dyingLevel);
      }
    } else if (deathRoll >= target + 10 || deathRoll == 20) {
      dyingLevel = Math.max(0, dyingLevel - 2);
      if (dyingLevel == 0) {
        woundedLevel++;
        debug.write("  %s critically succeeds recovery is no longer dying (wounded @ %d)%n", name,
            woundedLevel);
      } else {
        debug.write("  %s critically succeeds recovery (dying @ %d)%n", name, dyingLevel);
      }
    } else {
      dyingLevel--;
      if (dyingLevel == 0) {
        woundedLevel++;
        debug.write("  %s recovers and is no longer dying (wounded @ %d)%n", name, woundedLevel);
      } else {
        debug.write("  %s recovers (dying @ %d)%n", name, dyingLevel);
      }
    }
  }

  void take(int amount, boolean wasCritical) {
    if (dyingLevel > 0) {
      dyingLevel += wasCritical ? 2 : 1;
      debug.write("        %s continues to die (dying @ %d)%n", name, dyingLevel);
    } else if (amount >= hits) {
      dyingLevel = woundedLevel + 1;
      hits = 0;
      debug.write("        %s is taken out (dying @ %d)%n", name, dyingLevel);
    } else {
      hits -= amount;
      debug.write("        %s has %d hits left%n", name, hits);
    }
  }


  void heal(int amount) {
    hits = Math.max(hits + amount, maxHits);
    if (dyingLevel > 0) {
      dyingLevel = 0;
      woundedLevel++;
    }
  }


  public int hits() {
    return hits;
  }
}
