package org.example.designPatterns.behavioural.strategyPattern.withPattern;

import org.example.designPatterns.behavioural.strategyPattern.withPattern.courseFeeStrategy.LuxuryTaxStrategy;

public class MSCourse extends Course {

    public MSCourse() {
        super(new LuxuryTaxStrategy());
    }

    @Override
    public double getCourseFee(){
        double fee = 100d;
        return fee + fee*this.getTaxOnCourse();
    }
}