package edu.sjsu.cmpe275.Domain.SurveyQuestion;

public class SurveyQuestionFactory {
    //"YesNo", "DateTime", "MultipleChoice", "Rating", "ShortAnswer
    public static SurveyQuestion getSurveyQuestion(String type){
        if(type.equals("ShortAnswer")){
            return new ShortAnswer();
        }
        else if(type.equals("Rating")){
            return new Rating();
        }
        else if(type.equals("YesNo")){
            return new YesNo();
        }
        else if(type.equals("MultipleChoice")){
            return new MultipleChoice();
        }
        else if(type.equals("DateTime")){
            return new DateTime();
        }
        else{
            return null;
        }
    }
}
