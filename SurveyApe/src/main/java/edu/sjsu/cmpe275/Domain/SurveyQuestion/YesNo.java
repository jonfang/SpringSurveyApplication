package edu.sjsu.cmpe275.Domain.SurveyQuestion;

import org.hibernate.annotations.Proxy;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Proxy(lazy=false)
@Entity
@DiscriminatorValue("YesNo")
public class YesNo extends SurveyQuestion{
    private boolean yes;

    public YesNo(){}
    public YesNo(String question){
        super(question);
    }

    public boolean isYes() {
        return yes;
    }

    public void setYes(boolean yes) {
        this.yes = yes;
    }

}
