package edu.sjsu.cmpe275.Domain.SurveyResult;

import edu.sjsu.cmpe275.Domain.Survey;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Proxy(lazy=false)
@Entity
@Table(name = "result")
public class SurveyResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "result_id")
    private int id;
    @Column
    private String question;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="owner_id")
    private Survey owner;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SurveyResult(){
        this.question = "No question";
    }

    public SurveyResult(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Survey getOwner() {
        return owner;
    }

    public void setOwner(Survey owner) {
        this.owner = owner;
    }

    public String getType(){
        return this.getClass().toString();
    }
    
}
