package edu.sjsu.cmpe275.Domain;

import edu.sjsu.cmpe275.Domain.SurveyQuestion.SurveyQuestion;
import edu.sjsu.cmpe275.Domain.SurveyResult.SurveyResult;
import org.hibernate.annotations.Proxy;
import org.springframework.context.annotation.Bean;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Proxy(lazy=false)
@Entity
@Table(name = "survey")
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "survey_id")
    private int id;
    @Column
    private String name; //survey name
    @Column
    private String type; //type is General, Closed, Open
    @Column
    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval=true)
    private List<SurveyQuestion> questions;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<SurveyResult> questionResults;
//    @Column
//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "survey_question", joinColumns = @JoinColumn(name = "survey_id"), inverseJoinColumns = @JoinColumn(name = "question_id"))
//    private List<SurveyQuestion> questions;
//    @Column
//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "survey_result", joinColumns = @JoinColumn(name = "survey_id"), inverseJoinColumns = @JoinColumn(name = "result_id"))
//    private List<SurveyResult> questionResults;
    @Column(name = "survey_token")
    private String surveyToken;
    @Column(name="participation")
    private Integer participation=0;
//    @Column(name="participation_completed")
//    private Integer participation_completed=0;
    private final static String[] SURVEY_TYPES = {"General", "Closed", "Open"};

    public Survey(){
        name = "Survey Name";
        type = "Survey Type";
        questions = new ArrayList<SurveyQuestion>();
        questionResults = new ArrayList<SurveyResult>();
        surveyToken = UUID.randomUUID().toString();
    }

    public Survey(String name, String type, String surveyToken){
        this.name = name;
        this.type = type;
        this.surveyToken = surveyToken;
//        participation=0;
//        participation_completed=0;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<SurveyQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestion> questions) {
        this.questions = questions;
    }

    public List<SurveyResult> getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults(List<SurveyResult> questionResults) {
        this.questionResults = questionResults;
    }

    public String getSurveyToken() {
        return surveyToken;
    }

    public void setSurveyToken(String surveyToken) {
        this.surveyToken = surveyToken;
    }

    public static String[] getSurveyTypes(){
        return SURVEY_TYPES;
    }

    public Integer getParticipation() {
        return participation;
    }

    public void setParticipation(Integer participation) {
        this.participation = participation;
    }
}
