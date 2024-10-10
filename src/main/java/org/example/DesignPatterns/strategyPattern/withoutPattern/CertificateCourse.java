package org.example.DesignPatterns.strategyPattern.withoutPattern;

public class CertificateCourse implements Course{
    @Override
    public void getCourseFee() {
        int fee = 50;
        double tax = 0.18;

        System.out.println("Course fee for Certificate: " + (fee+tax));
    }
}
