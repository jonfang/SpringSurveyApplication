package edu.sjsu.cmpe275.Domain;

import javax.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Transient;

import java.util.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Please provide a valid e-mail")
    @NotEmpty(message = "Please provide an e-mail")
    private String email;
    @Column(name = "password")
    @Transient
    private String password;
    @Column(name = "first_name")
    @NotEmpty(message = "Please provide your first name")
    private String firstName;
    @Column(name = "last_name")
    @NotEmpty(message = "Please provide your last name")
    private String lastName;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name = "confirmation_token")
    private String confirmationToken;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_survey", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "survey_id"))
    private List<Survey> surveyList;
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(
            name="available_survey_map",
            joinColumns=@JoinColumn(name="survey_map_id")
    )
    @MapKeyJoinColumn(name="name")
    @Column(name="survey_results")
    private Map<String, String> availableSurveyMap;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<Completed_Survey> completedSurveys;

    public User(){
        surveyList = new ArrayList<>();
        availableSurveyMap = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Survey> getSurveyList() {
        return surveyList;
    }

    public void setSurveyList(List<Survey> surveyList) {
        this.surveyList = surveyList;
    }

    public Map<String, String> getAvailableSurveyMap() {
        return availableSurveyMap;
    }

    public void setAvailableSurveyMap(Map<String, String> availableSurveyMap) {
        this.availableSurveyMap = availableSurveyMap;
    }

    public List<Completed_Survey> getCompletedSurveys() {
        return completedSurveys;
    }

    public void setCompletedSurveys(List<Completed_Survey> completedSurveys) {
        this.completedSurveys = completedSurveys;
    }
}