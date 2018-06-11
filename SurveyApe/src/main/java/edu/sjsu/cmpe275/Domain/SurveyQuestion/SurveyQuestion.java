package edu.sjsu.cmpe275.Domain.SurveyQuestion;

import edu.sjsu.cmpe275.Domain.Survey;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Proxy(lazy=false)
@Entity
@Inheritance
@DiscriminatorColumn(name="question_type")
@Table(name = "question")
public class SurveyQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "question_id")
    private int id;
    @Column
    private String question;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="owner_id")
    private Survey owner;
    private static final String[] QUESTION_TYPE = {"YesNo", "DateTime", "MultipleChoice", "Rating", "ShortAnswer"};

    public SurveyQuestion(){
        this.question = "";
    }


    public SurveyQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Survey getOwner() {
        return owner;
    }

    public void setOwner(Survey owner) {
        this.owner = owner;
    }

    public static String[] getQuestionTypes(){
        return QUESTION_TYPE;
    }

    public String getType(){
        return this.getClass().toString();
    }
}
