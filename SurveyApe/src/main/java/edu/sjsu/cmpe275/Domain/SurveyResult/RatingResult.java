package edu.sjsu.cmpe275.Domain.SurveyResult;

import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Proxy(lazy=false)
@Entity
@DiscriminatorValue("RatingResult")
public class RatingResult extends SurveyResult{
    @Column
    private int one;
    @Column
    private int two;
    @Column
    private int three;
    @Column
    private int four;
    @Column
    private int five;

    RatingResult(){
        one=0;two=0;three=0;four=0;five=0;
    }

    public int getOne() {
        return one;
    }

    public void setOne(int one) {
        this.one = one;
    }

    public int getTwo() {
        return two;
    }

    public void setTwo(int two) {
        this.two = two;
    }

    public int getThree() {
        return three;
    }

    public void setThree(int three) {
        this.three = three;
    }

    public int getFour() {
        return four;
    }

    public void setFour(int four) {
        this.four = four;
    }

    public int getFive() {
        return five;
    }

    public void setFive(int five) {
        this.five = five;
    }
}
