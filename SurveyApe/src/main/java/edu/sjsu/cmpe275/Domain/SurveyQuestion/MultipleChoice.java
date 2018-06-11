package edu.sjsu.cmpe275.Domain.SurveyQuestion;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Proxy(lazy=false)
@Entity
@DiscriminatorValue("MultipleChoice")
public class MultipleChoice extends SurveyQuestion {
    private static final String[] CHOICE_TYPES = {"text", "image"};
    private static final String[] ANSWER_TYPES = {"single", "multiple"};
    private static final String[] VISUAL_STYLES = {"dropdown", "radio", "checkbox"};
    @Column
    private String choiceType; //text, image
    @Column
    private String answerType; //single, multple selection
    @Column
    private String visualStyle; //dropdown, radio, checkbox
    private String chosen;
//    @Column
//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "mcquestion_choice", joinColumns = @JoinColumn(name = "mcquestion_id"), inverseJoinColumns = @JoinColumn(name = "choice_id"))
@ElementCollection(fetch = FetchType.EAGER)
@Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(
        name="multiplechoce_choices",
        joinColumns=@JoinColumn(name="mc_id")
    )
    private List<Choice> choices;

    public MultipleChoice(){
        choices = new ArrayList<>();
    }
    public MultipleChoice(String question){
        super(question);
    }

    public String getChoiceType() {
        return choiceType;
    }

    public void setChoiceType(String choiceType) {
        this.choiceType = choiceType;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public String getVisualStyle() {
        return visualStyle;
    }

    public void setVisualStyle(String visualStyle) {
        this.visualStyle = visualStyle;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public static String[] getChoiceTypes() {
        return CHOICE_TYPES;
    }

    public static String[] getAnswerTypes() {
        return ANSWER_TYPES;
    }

    public static String[] getVisualStyles() {
        return VISUAL_STYLES;
    }

    public String getChosen() {
        return chosen;
    }

    public void setChosen(String chosen) {
        this.chosen = chosen;
    }
}
