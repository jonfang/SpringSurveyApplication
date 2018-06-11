package edu.sjsu.cmpe275.Domain.SurveyQuestion;

import org.hibernate.annotations.Proxy;

import javax.persistence.*;

//@Entity
//@Table(name = "choice")
@Proxy(lazy=false)
@Embeddable
public class Choice {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "choice_id")
//    private int id;
    @Column(name="choice_string")
    private String choice;
    //add an image later
    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }
}
