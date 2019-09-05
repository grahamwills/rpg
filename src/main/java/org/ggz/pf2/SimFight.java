package org.ggz.pf2;


import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.ggz.pf2.Strategy.Action;
import org.ggz.pf2.components.Attack;
import org.ggz.pf2.components.Combatant;
import org.ggz.pf2.components.Shield;

public class SimFight {

//  // Level 1
//  private static final int BASE_AC = 16;
//  private static final int MAX_HITS = 18;
//  private static final int BASE_ATTACK_BONUS = 6;
//  private static final int BASE_DAMAGE_BONUS = 3;
//  private static final int WEAPON_LEVEL = 0;

  // Level 3
  private static final int BASE_AC = 17;
  private static final int MAX_HITS = 38;
  private static final int BASE_ATTACK_BONUS = 8;
  private static final int BASE_DAMAGE_BONUS = 3;
  private static final int WEAPON_LEVEL = 1;

  private static final int SIMULATIONS = 10000;
  private static final Random random = new Random();


  private final int simulationCount;
  private final Supplier<Collection<Combatant>> sideA;
  private final Strategy sideAStrategy;
  private final Supplier<Collection<Combatant>> sideB;
  private final Strategy sideBStrategy;
  private final Debug debug;


  public SimFight(int simulationCount, Debug debug,
      Supplier<Collection<Combatant>> sideA, Strategy sideAStrategy,
      Supplier<Collection<Combatant>> sideB, Strategy sideBStrategy) {
    this.simulationCount = simulationCount;
    this.sideA = sideA;
    this.sideAStrategy = sideAStrategy;
    this.sideB = sideB;
    this.sideBStrategy = sideBStrategy;
    this.debug = debug;
  }

  public static int roll(int diceSize) {
    return random.nextInt(diceSize) + 1;
  }

  static Combatant fighterWithGreatSword(int index, Debug out) {
    return new Combatant("GS-" + index, BASE_AC, MAX_HITS, out)
        .withAttack(Attack.greatsword(BASE_ATTACK_BONUS, BASE_DAMAGE_BONUS, WEAPON_LEVEL));

  }

  static Combatant fighterWithSwordAndSteelShield(int index, Debug out, boolean shortSword) {
    Attack attack =
        shortSword ? Attack.shortsword(BASE_ATTACK_BONUS, BASE_DAMAGE_BONUS, WEAPON_LEVEL)
            : Attack.longsword(BASE_ATTACK_BONUS, BASE_DAMAGE_BONUS, WEAPON_LEVEL);
    String name = shortSword ? "SS-" : "LS-";
    return new Combatant(name + index, BASE_AC, MAX_HITS, out)
        .withAttack(attack)
        .withShield(Shield.steel());
  }


  static Combatant fighterWithLongswordAndTowerShield(int index, Debug out) {
    return new Combatant("TW-" + index, BASE_AC, MAX_HITS, out)
        .withAttack(Attack.longsword(BASE_ATTACK_BONUS, BASE_DAMAGE_BONUS, WEAPON_LEVEL))
        .withShield(Shield.tower());
  }

//  public static void main(String[] args) {
//    Debug out = System.out::format;
//    Strategy basic = Strategy.strategy_BasicCombat(false);
//    new SimFight(3, out,
//        () -> makeGreatswordTeam(out), basic,
//        () -> makeLightDefensiveTeam(out), basic)
//        .runSimulations();
//  }


  public static void main(String[] args) {

    Debug out = (a, b) -> {
    };

    Strategy[] strategies = new Strategy[]{
        Strategy.strategy_BasicCombat(false),
        Strategy.strategy_BasicCombat(true),
    };

    List<Supplier<Collection<Combatant>>> teams = Arrays.asList(
        () -> makeGreatswordTeam(out),
        () -> makeLightDefensiveTeam(false, out),
        () -> makeLightDefensiveTeam(true, out),
        () -> makeHeavyDefensiveTeam(out)
    );

    Map<String, Double> results = new HashMap<>();
    for (Supplier<Collection<Combatant>> supplierA : teams) {
      for (Supplier<Collection<Combatant>> supplierB : teams) {

        // Don't bother with same teams using different strategies
        if (supplierA == supplierB) {
          continue;
        }

        for (Strategy strategyA : strategies) {
          for (Strategy strategyB : strategies) {
            String aName = makeName(supplierA, strategyA);
            String bName = makeName(supplierB, strategyB);
            if (aName.equals(bName) || results.containsKey(aName + " vs " + bName)) {
              continue;
            }
            SimFight fight = new SimFight(SIMULATIONS, out, supplierA, strategyA, supplierB,
                strategyB);
            double result = fight.runSimulations();
            results.put(aName + " vs " + bName, 100 * result);
            results.put(bName + " vs " + aName, 100 * (1.0 - result));
          }
        }
      }
    }

    System.out.println("OVERALL RANKINGS\n=================");
    Map<String, Double> overall = results.entrySet().stream()
        .collect(Collectors.groupingBy(
            e -> e.getKey().replaceAll("^\\[(.+)\\].+", "$1"),
            Collectors.averagingDouble(Entry::getValue)
        ));
    printMapInOrder(overall);

//    System.out.println("DETAILED MATCHUPS\n=================");
//    printMapInOrder(results);

  }

  private static void printMapInOrder(Map<String, Double> results) {
    results.entrySet().stream()
        .sorted(Comparator.comparingDouble(e -> -e.getValue()))
        .forEach(e -> System.out.format("%3.1f: %s%n", e.getValue(), e.getKey()));
  }

  private static String makeName(Supplier<Collection<Combatant>> team, Strategy strategy) {
    return "[" + nameTeam(team) + " : " + strategy + "]";
  }

  private static List<Combatant> makeHeavyDefensiveTeam(Debug out) {
    return IntStream.rangeClosed(1, 4)
        .mapToObj(k -> fighterWithLongswordAndTowerShield(k, out))
        .collect(Collectors.toList());
  }

  private static List<Combatant> makeLightDefensiveTeam(boolean shortSword, Debug out) {
    return IntStream.rangeClosed(1, 4)
        .mapToObj(k -> fighterWithSwordAndSteelShield(k, out, shortSword))
        .collect(Collectors.toList());
  }

  private static List<Combatant> makeGreatswordTeam(Debug out) {
    return IntStream.rangeClosed(1, 4)
        .mapToObj(k -> fighterWithGreatSword(k, out))
        .collect(Collectors.toList());
  }

  private double runSimulations() {

    int sideAWins = 0;
    int sideBWins = 0;

    for (int simulation = 1; simulation <= simulationCount; simulation++) {

      debug.write("SIMULATION #" + simulation + "\n=============\n\n");

      Collection<Combatant> a = sideA.get();
      Collection<Combatant> b = sideB.get();

      runCombat(a, b);

      if (activeCount(a) > 0) {
        sideAWins++;
      }
      if (activeCount(b) > 0) {
        sideBWins++;
      }
    }

//    System.out.println(sideAWins + " wins for " + nameTeam(sideA));
//    System.out.println(sideBWins + " wins for " + nameTeam(sideB));
    return ((double) sideAWins) / (sideAWins + sideBWins);
  }

  private static String nameTeam(Supplier<Collection<Combatant>> teamSupplier) {
    return teamSupplier.get().stream()
        .map(Combatant::toString)
        .map(s -> s.replaceFirst("-.+", ""))
        .collect(Collectors.joining(", "));
  }

  private void runCombat(Collection<Combatant> a, Collection<Combatant> b) {
    boolean sideAFirst = roll(2) == 1;

    final double P_MOVE_NEEDED = 0.25;         // How often a move or other action needed

    while (activeCount(a) > 0 && activeCount(b) > 0) {
      // Alternate actions between teams
      Iterator<Combatant> itA = a.iterator();
      Iterator<Combatant> itB = b.iterator();

      while (itA.hasNext()) {
        if (sideAFirst) {
          doRound(itA, sideAStrategy, a, b, random.nextDouble() < P_MOVE_NEEDED);
          doRound(itB, sideBStrategy, b, a, random.nextDouble() < P_MOVE_NEEDED);
        } else {
          doRound(itB, sideBStrategy, b, a, random.nextDouble() < P_MOVE_NEEDED);
          doRound(itA, sideAStrategy, a, b, random.nextDouble() < P_MOVE_NEEDED);
        }
      }

      debug.write("A has %d active; B has %d active%n%n", activeCount(a), activeCount(b));
    }
  }

  private void doRound(Iterator<Combatant> who, Strategy strategy,
      Collection<Combatant> allies, Collection<Combatant> opponents, boolean needMove) {
    Combatant actor = who.next();

    actor.startTurn();
    if (needMove) {
      actor.move();
    }
    for (Action action : strategy) {
      if (actor.canAct()) {
        int used = action.perform(actor, allies, opponents);
        actor.usesActions(used);
      }
    }
  }

  private int activeCount(Collection<Combatant> team) {
    return (int) team.stream().filter(combatant -> combatant.status().isConscious()).count();
  }

}
