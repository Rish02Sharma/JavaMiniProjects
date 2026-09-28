package org.example.SystemDesign.parking_lot.pricing;

import org.example.SystemDesign.parking_lot.Ticket;

public class FixedPricingStrategy implements PricingStrategy {

    @Override
    public double calculate(Ticket ticket) {
        return 100;
    }
}

