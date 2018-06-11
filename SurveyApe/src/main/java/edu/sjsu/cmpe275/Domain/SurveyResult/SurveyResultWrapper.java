package edu.sjsu.cmpe275.Domain.SurveyResult;

import javax.persistence.Column;
import java.util.List;
import java.util.Map;

public class SurveyResultWrapper {
    private String question;
    private String Type;
    //YesNo
    private int yes;
    private int no;
    //short answer
    private List<String> answerResults;
    //rating
    private int one;
    private int two;
    private int three;
    private int four;
    private int five;
    //date time
    private List<String> dateTimeResults;
    //multiple choice
    private Map<String, Integer> choiceMap;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getYes() {
        return yes;
    }

    public void setYes(int yes) {
        this.yes = yes;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public List<String> getAnswerResults() {
        return answerResults;
    }

    public void setAnswerResults(List<String> answerResults) {
        this.answerResults = answerResults;
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

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public List<String> getDateTimeResults() {
        return dateTimeResults;
    }

    public void setDateTimeResults(List<String> dateTimeResults) {
        this.dateTimeResults = dateTimeResults;
    }

    public Map<String, Integer> getChoiceMap() {
        return choiceMap;
    }

    public void setChoiceMap(Map<String, Integer> choiceMap) {
        this.choiceMap = choiceMap;
    }
}
