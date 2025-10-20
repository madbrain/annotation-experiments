package org.github.madbrain.demo.rulez;

import org.github.madbrain.demo.rulez.api.Imports;
import org.github.madbrain.demo.rulez.api.Rule;
import org.github.madbrain.demo.rulez.api.RuleContext;

public class AirAviaRules {

    @Rule(
            """
            $account: Account(miles > 100000)
            """
    )
    public static void goldStatus(RuleContext context, Account account) {
        account.setStatus(Status.GOLD);
        context.modify(account);
    }

    @Rule(
            """
            $account: Account()
            $flight: Flight(miles >= 500)
            """
    )
    public static void longFlight(RuleContext context, Account account, Flight flight) {
        account.addAwardedMiles(flight.miles());
        context.modify(account);
    }

    @Rule(
            """
            $account: Account(status == Status.Gold)
            $flight: Flight(partner == false)
            """
    )
    @Imports(Status.class)
    public static void goldBonus(RuleContext context, Account account, Flight flight) {
        account.addAwardedMiles(flight.miles());
        context.modify(account);
    }

}
