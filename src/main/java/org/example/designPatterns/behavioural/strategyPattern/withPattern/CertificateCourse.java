package org.example.designPatterns.behavioural.strategyPattern.withPattern;

import org.example.designPatterns.behavioural.strategyPattern.withPattern.courseFeeStrategy.EssentialTaxStrategy;

public class CertificateCourse extends Course {

    public CertificateCourse() {
        super(new EssentialTaxStrategy());
    }

    @Override
    public double getCourseFee(){
        double fee = 50d;
        return fee + fee*this.getTaxOnCourse();
    }
}
