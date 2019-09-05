package org.ggz.pf2;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.ggz.pf2.Strategy.Action;
import org.ggz.pf2.components.Combatant;

public class Strategy extends ArrayList<Action> {

  private final String name;

  public Strategy(String name, Action... actions) {
    this.name = name;
    Collections.addAll(this, actions);
  }

  public String toString() {
    return name;
  }

  @FunctionalInterface
  interface Action {

    /**
     * Perform an action
     *
     * @param actor who is acting
     * @param allies people to help
     * @param enemies people to harm
     * @return number of actions consumed
     */
    int perform(Combatant actor, Collection<Combatant> allies, Collection<Combatant> enemies);
  }


  public static Strategy strategy_BasicCombat(boolean preferLowAC) {
    return new Strategy(
        "Basic targeting low " + (preferLowAC ? "AC" : "HP"),
        attackEnemy(preferLowAC),
        (actor, allies, enemies) -> actor.shield().raise(actor),
        (actor, allies, enemies) -> actor.shield().takeCover(actor),
        attackEnemy(preferLowAC),
        attackEnemy(preferLowAC)
    );
  }


  private static Action attackEnemy(boolean preferLowAC) {
    return (actor, allies, enemies) ->
        enemies.stream().filter(combatant -> combatant.status().isConscious())
            .min((a, b) -> compareTargets(a, b, preferLowAC))
            .map(target -> actor.attack().resolve(actor, target))
            .orElse(0);
  }

  private static int compareTargets(Combatant a, Combatant b, boolean preferLowAC) {
    int acDiff = a.AC() - b.AC();
    int hitsDiff = a.status().hits() - b.status().hits();
    if (preferLowAC) {
      return acDiff != 0 ? acDiff : hitsDiff;
    } else {
      return hitsDiff != 0 ? hitsDiff : acDiff;
    }
  }

}
