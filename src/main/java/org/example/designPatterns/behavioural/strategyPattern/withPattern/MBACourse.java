package org.example.designPatterns.behavioural.strategyPattern.withPattern;

import org.example.designPatterns.behavioural.strategyPattern.withPattern.courseFeeStrategy.NormalTaxStrategy;

public class MBACourse extends Course {

    public MBACourse() {
        super(new NormalTaxStrategy());
    }

    @Override
    public double getCourseFee(){
        double fee = 75d;
        return fee + fee*this.getTaxOnCourse();
    }
}

