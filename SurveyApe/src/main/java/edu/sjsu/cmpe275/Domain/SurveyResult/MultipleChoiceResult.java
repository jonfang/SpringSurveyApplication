package edu.sjsu.cmpe275.Domain.SurveyResult;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Proxy(lazy=false)
@Entity
@DiscriminatorValue("MultipleChoiceResult")
public class MultipleChoiceResult extends SurveyResult{
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(
            name="mc_choice_map",
            joinColumns=@JoinColumn(name="mc_id")
    )
    @MapKeyJoinColumn(name="name")
    @Column(name="mc_results")
    private Map<String, Integer> choiceMap;

    MultipleChoiceResult(){
        choiceMap = new HashMap<>();
    }

    public Map<String, Integer> getChoiceMap() {
        return choiceMap;
    }

    public void setChoiceMap(Map<String, Integer> choiceMap) {
        this.choiceMap = choiceMap;
    }
}
