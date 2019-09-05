package org.ggz.pf2.components;


import org.ggz.pf2.Debug;

public class Shield {

  private final String name;
  private final int brokenThreshold;
  private final int acBonus;
  private final int hardness;
  private int hits;
  private boolean isRaised;
  private Boolean providingCover;                       // Null means "impossible"

  public static Shield none() {
    return new Shield("none", 0, 0, 0);          // essentially the same as broken shield
  }

  public static Shield wooden() {
    return new Shield("wooden", 2, 3, 6);
  }

  public static Shield steel() {
    return new Shield("steel", 2, 5, 10);
  }

  public static Shield tower() {
    Shield tower = new Shield("tower", 2, 5, 10);
    tower.providingCover = Boolean.FALSE;               // Possible, but not initially ready
    return tower;
  }

  /**
   * Call at start of turn when shield is lowered
   */
  public void reset() {
    isRaised = false;
    if (providingCover != null) {
      providingCover = Boolean.FALSE;
    }
  }

  /**
   * Raise shield; returns number of actions used (one or zero)
   */
  public int raise(Combatant actor) {
    if (isBroken() || isRaised) {
      return 0;
    } else {
      actor.debug().write("  %s raises their %s shield%n", actor, name);
      isRaised = true;
      return 1;
    }
  }

  /**
   * Take cover behind shield; returns number of actions used (one or zero)
   */
  public int takeCover(Combatant actor) {
    // Only if providing cover is possible and false, and shield currently raised
    if (Boolean.FALSE.equals(providingCover) && isRaised) {
      actor.debug().write("  %s takes cover behind their %s shield%n", name, actor);
      providingCover = true;
      return 1;
    } else {
      return 0;
    }
  }

  /**
   * Return the AC bonus currently provided
   */
  public int ac() {
    if (Boolean.TRUE.equals(providingCover)) {
      return acBonus + 2;
    } else if (isRaised) {
      return acBonus;
    } else {
      return 0;
    }
  }

  /**
   * Returns the amount of damage absorbed
   */
  public int blockDamage(int damage, Debug debug) {
    if (isRaised) {
      int damageTaken = damage - hardness;
      if (damageTaken > 0) {
        hits += damageTaken;
        if (isBroken()) {
          debug.write("    %s shield blocks, taking %d hits and is broken%n", name, damageTaken);
          reset();
        } else {
          debug.write("    %s shield blocks, taking %d hits%n", name, damageTaken);
        }
      } else {
        debug.write("    %s shield blocks all the damage%n", name);
      }
      return Math.min(damage, hardness);
    } else {
      return 0;
    }
  }

  private boolean isBroken() {
    return hits >= brokenThreshold;
  }


  private Shield(String name, int acBonus, int hardness, int brokenThreshold) {
    this.name = name;
    this.acBonus = acBonus;
    this.hardness = hardness;
    this.brokenThreshold = brokenThreshold;
  }

  public boolean isRaised() {
    return isRaised;
  }
}
