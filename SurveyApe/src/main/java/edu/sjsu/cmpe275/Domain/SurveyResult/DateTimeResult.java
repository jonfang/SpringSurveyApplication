package edu.sjsu.cmpe275.Domain.SurveyResult;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Proxy(lazy=false)
@Entity
@DiscriminatorValue("DateTimeResult")
public class DateTimeResult extends SurveyResult{
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(
            name="datetime_results",
            joinColumns=@JoinColumn(name="datetime_id")
    )
    @Column(name="datetime_result")
    private List<String> dateTimeResults;

    public DateTimeResult(){
        dateTimeResults = new ArrayList<>();
    }

    public List<String> getDateTimeResults() {
        return dateTimeResults;
    }

    public void setDateTimeResults(List<String> dateTimeResults) {
        this.dateTimeResults = dateTimeResults;
    }
}
