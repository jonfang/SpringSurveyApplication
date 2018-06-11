package edu.sjsu.cmpe275.Domain.SurveyQuestion;

import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Proxy(lazy=false)
@Entity
@DiscriminatorValue("Rating")
public class Rating extends SurveyQuestion {
    @Column
    private int rating;

    public Rating(){}

    public Rating(String question){
        super(question);
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
