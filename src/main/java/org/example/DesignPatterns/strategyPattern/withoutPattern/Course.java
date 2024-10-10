package org.example.DesignPatterns.strategyPattern.withoutPattern;

public interface Course {
    void getCourseFee();
}

/*
 Without strategy pattern getCoursefee method implementation for InternationalCourses and MSCourses both have duplicate implementation (28% GST)
 and CertificateCourses and MBACourses have duplicate implementation (18% GST)
 Thus in Parent-Child relationship when there are duplicate implementation present in Child classes, we use Strategy design pattern to prevent duplicate code.
 */
