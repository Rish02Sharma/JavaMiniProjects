package org.example.DesignPatterns.strategyPattern.withPattern.courseFeeStrategy;

public class LuxuryTaxStrategy implements TaxStrategy {
    @Override
    public double getCourseTax() {
        return 0.28;
    }
}
