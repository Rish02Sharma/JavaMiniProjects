package org.example.designPatterns.behavioural.strategyPattern.withPattern.courseFeeStrategy;

public class NormalTaxStrategy implements TaxStrategy {
    @Override
    public double getCourseTax() {
        return 0.18;
    }
}
