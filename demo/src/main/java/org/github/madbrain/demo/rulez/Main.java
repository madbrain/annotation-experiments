package org.github.madbrain.demo.rulez;

import org.github.madbrain.demo.rulez.api.RuleEvaluator;

public class Main {
    public static void main(String[] args) {
        var evaluator = new RuleEvaluator(AirAviaRules.class);
        var a1 = new Account(150_000);
        var f1 = new Flight(2_419, false);

        evaluator.fireRules(a1, f1);
    }
}
