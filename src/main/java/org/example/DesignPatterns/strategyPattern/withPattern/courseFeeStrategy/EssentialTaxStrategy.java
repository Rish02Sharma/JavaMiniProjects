package org.example.DesignPatterns.strategyPattern.withPattern.courseFeeStrategy;

public class EssentialTaxStrategy implements TaxStrategy {
    @Override
    public double getCourseTax() {
        return 0d;
    }
}
