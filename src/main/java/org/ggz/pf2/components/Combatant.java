package org.ggz.pf2.components;


import org.ggz.pf2.Debug;

public class Combatant {

  private final String name;
  private final int baseAC;
  private final Debug debug;
  private final Damage status;
  private boolean hasReaction;
  private int actions;
  private Attack attack;
  private Shield shield = Shield.none();

  public Combatant(String name, int baseAC, int maxHits, Debug debug) {
    this.name = name;
    this.baseAC = baseAC;
    this.debug = debug;
    this.status = new Damage(name, maxHits, debug);
    this.actions = 0;
    this.hasReaction = false;
  }

  public Combatant withAttack(Attack attack) {
    this.attack = attack;
    return this;
  }

  public Combatant withShield(Shield shield) {
    this.shield = shield;
    return this;
  }

  public int AC() {
    return baseAC + shield.ac();
  }

  public Attack attack() {
    return attack;
  }

  public Shield shield() {
    return shield;
  }

  public boolean canAct() {
    return actions > 0;
  }

  public boolean canReact() {
    return hasReaction;
  }

  public Combatant reacts() {
    hasReaction = false;
    return this;
  }

  public Combatant usesActions(int n) {
    actions -= n;
    return this;
  }

  /**
   * Start a turn
   *
   * @return true if they can act
   */
  public void startTurn() {
    status.reset();
    shield.reset();
    attack.reset();
    if (status.isConscious()) {
      actions = 3;
      hasReaction = true;
    } else {
      actions = 0;
      hasReaction = false;
    }
  }

  public Damage status() {
    return status;
  }

  public String toString() {
    return name;
  }

  public Debug debug() {
    return debug;
  }

  public void move() {
    if (canAct()) {
      usesActions(1);
      debug.write("  %s moves into position%n", name);
    }
  }
}
