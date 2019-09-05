package org.ggz.pf2.components;


import static org.ggz.pf2.SimFight.roll;

import org.ggz.pf2.Debug;

public class Attack {

  private final String name;
  private final int attackBonus;
  private final int damageDiceCount;
  private final int damageDiceSize;
  private final int damageBonus;
  private final int iterativePenalty;
  private int attacksPerformed;

  private Attack(String name, int attackBonus, int damageDiceCount, int damageDiceSize,
      int damageBonus, int iterativePenalty) {
    this.name = name;
    this.attackBonus = attackBonus;
    this.damageDiceCount = damageDiceCount;
    this.damageDiceSize = damageDiceSize;
    this.damageBonus = damageBonus;
    this.iterativePenalty = iterativePenalty;
  }

  public static Attack greatsword(int baseAttackBonus, int baseDamageBonus, int plus) {
    return new Attack("greatsword", baseAttackBonus, 1 + plus, 12, baseDamageBonus + plus, 5);
  }

  public static Attack longsword(int baseAttackBonus, int baseDamageBonus, int plus) {
    return new Attack("longsword", baseAttackBonus, 1 + plus, 8, baseDamageBonus + plus, 5);
  }

  public static Attack shortsword(int baseAttackBonus, int baseDamageBonus, int plus) {
    return new Attack("shortsword", baseAttackBonus, 1 + plus, 6, baseDamageBonus + plus, 4);
  }

  public void reset() {
    attacksPerformed = 0;
  }

  /**
   * Attack! This also resolves damage on the target
   *
   * @param attacker who is attacking
   * @param target who to the target
   * @return number of actions consumed (one)
   */
  public int resolve(Combatant attacker, Combatant target) {
    Debug debug = attacker.debug();
    int roll = roll(20);
    int attack = roll + attackBonus;

    if (attacksPerformed > 1) {
      attack -= 2 * iterativePenalty;
    } else if (attacksPerformed > 0) {
      attack -= iterativePenalty;
    }

    boolean isCritical = roll == 20 || attack >= target.AC() + 10;
    int potentialDamage = isCritical ? 2 * damage() : damage();
    String attackMessage = isCritical ? "critically hits" : "hits";

    if (attack >= target.AC()) {
      debug.write("  %s %s %s with their %s for %d damage (roll=%d, attack=%d)%n",
          attacker, attackMessage, target, name, potentialDamage, roll, attack);
      if (target.canReact() && target.shield().isRaised()) {
        potentialDamage -= target.reacts().shield().blockDamage(potentialDamage, debug);
      }
      target.status().take(potentialDamage, isCritical);
    } else {
      debug.write("  %s misses %s with their %s (roll=%d, attack=%d)%n",
          attacker, target, name, roll, attack);
    }

    attacksPerformed++;
    return 1;                   // Always takes one action
  }

  private int damage() {
    int result = damageBonus;
    for (int i = 0; i < damageDiceCount; i++) {
      result += roll(damageDiceSize);
    }
    return result;
  }

}
