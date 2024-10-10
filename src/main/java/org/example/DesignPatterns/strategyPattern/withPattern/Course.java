package org.example.DesignPatterns.strategyPattern.withPattern;

import org.example.DesignPatterns.strategyPattern.withPattern.courseFeeStrategy.TaxStrategy;

public class Course {

    TaxStrategy taxStrategy;
    double tax;

    public Course(TaxStrategy taxStrategy){
        this.taxStrategy=taxStrategy;
    }

    double getTaxOnCourse(){
        return taxStrategy.getCourseTax();
    }

    double getCourseFee(){
        return 0d;
    }
}

/*
In Strategy pattern we define the Strategy logic separately and fetch that within our main logic so that we dont have to write duplicate logic everytime our
child class wants to implement that type of logic.
 */
