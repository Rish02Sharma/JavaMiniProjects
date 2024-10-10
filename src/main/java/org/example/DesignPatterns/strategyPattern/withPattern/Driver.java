package org.example.DesignPatterns.strategyPattern.withPattern;

public class Driver{

    public static void main(String[] args){
        Course course = new InternationalCourse();
        System.out.println("Course fee" + course.getCourseFee());
    }
}