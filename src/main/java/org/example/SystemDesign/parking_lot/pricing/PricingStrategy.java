package org.example.SystemDesign.parking_lot.pricing;

import org.example.SystemDesign.parking_lot.Ticket;

public interface PricingStrategy {

    double calculate(Ticket ticket);
}

