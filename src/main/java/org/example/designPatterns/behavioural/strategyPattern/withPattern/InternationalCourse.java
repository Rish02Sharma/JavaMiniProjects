package org.example.designPatterns.behavioural.strategyPattern.withPattern;

import org.example.designPatterns.behavioural.strategyPattern.withPattern.courseFeeStrategy.LuxuryTaxStrategy;

public class InternationalCourse extends Course {
    public InternationalCourse() {
        super(new LuxuryTaxStrategy());
    }

    @Override
    public double getCourseFee(){
        double fee = 150d;
        return fee + fee*this.getTaxOnCourse();
    }
}
