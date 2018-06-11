package edu.sjsu.cmpe275.Domain.SurveyQuestion;

import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Proxy(lazy=false)
@Entity
@DiscriminatorValue("ShortAnswer")
public class ShortAnswer extends SurveyQuestion  {
    @Column
    private String shortAnswer;

    public ShortAnswer(){}

    public ShortAnswer(String question){
        super(question);
    }

    public String getShortAnswer() {
        return shortAnswer;
    }

    public void setShortAnswer(String shortAnswer) {
        this.shortAnswer = shortAnswer;
    }
}
