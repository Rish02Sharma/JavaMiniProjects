package org.example.DesignPatterns.strategyPattern.withPattern;

import org.example.DesignPatterns.strategyPattern.withPattern.courseFeeStrategy.NormalTaxStrategy;

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

