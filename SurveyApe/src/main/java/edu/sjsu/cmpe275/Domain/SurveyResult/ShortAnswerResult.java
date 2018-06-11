package edu.sjsu.cmpe275.Domain.SurveyResult;

import edu.sjsu.cmpe275.Domain.SurveyQuestion.ShortAnswer;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Proxy(lazy=false)
@Entity
@DiscriminatorValue("ShortAnswerResult")
public class ShortAnswerResult extends SurveyResult{
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="shortanswer_results",
            joinColumns=@JoinColumn(name="shortanswer_id")
    )
    @Column(name="answer_result")
    private List<String> answerResults;

    public ShortAnswerResult(){
            answerResults = new ArrayList<>();
    }

    public List<String> getAnswerResults() {
        return answerResults;
    }

    public void setAnswerResults(List<String> answerResults) {
        this.answerResults = answerResults;
    }
}
