package edu.sjsu.cmpe275.Domain.SurveyResult;

import edu.sjsu.cmpe275.Domain.SurveyQuestion.*;

public class SurveyResultFactory {
    //"YesNo", "DateTime", "MultipleChoice", "Rating", "ShortAnswer
    public static SurveyResult getSurveyQuestion(SurveyQuestion q){
        if(q instanceof ShortAnswer){
            return new ShortAnswerResult();
        }
        else if(q instanceof Rating){
            return new RatingResult();
        }
        else if(q instanceof YesNo){
            return new YesNoResult();
        }
        else if(q instanceof MultipleChoice){
            return new MultipleChoiceResult();
        }
        else if(q instanceof DateTime){
            return new DateTimeResult();
        }
        else{
            return null;
        }
    }
}