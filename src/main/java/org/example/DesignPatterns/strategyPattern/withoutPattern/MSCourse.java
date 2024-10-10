package org.example.DesignPatterns.strategyPattern.withoutPattern;

public class MSCourse implements Course{
    @Override
    public void getCourseFee() {
        int fee = 100;
        double tax = 0.28;

        System.out.println("Course fee for Certificate: " + (fee+tax));
    }
}