package edu.sjsu.cmpe275.Service;

import edu.sjsu.cmpe275.Domain.Survey;
import edu.sjsu.cmpe275.Domain.User;
import edu.sjsu.cmpe275.Repository.SurveyQuestionRepository;
import edu.sjsu.cmpe275.Repository.SurveyRepository;
import edu.sjsu.cmpe275.Repository.SurveyResultRepository;
import edu.sjsu.cmpe275.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service("surveyService")
public class SurveryService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    SurveyQuestionRepository surveyQuestionRepository;
    @Autowired
    SurveyResultRepository surveyResultRepository;

    public List<Survey> findByUser(String email){
        User user = userRepository.findByEmail(email);
        return user.getSurveyList();
    }

    public Survey findBySurveyToken(String surveyToken){
        return surveyRepository.findBySurveyToken(surveyToken);
    }

    public void saveSurvey(Survey survey){
        surveyRepository.save(survey);
    }

    public void deleteBySurveyToken(String surveyToken, String email){
        User user = userRepository.findByEmail(email); //delete entry in
        Survey s = surveyRepository.findBySurveyToken(surveyToken);
        Iterator<Survey> itr = user.getSurveyList().iterator();
        while(itr.hasNext()) {
            String str = itr.next().getSurveyToken();
            if (str.equals(surveyToken)) {
                itr.remove();
                break;
            }
        }
        user.getAvailableSurveyMap().remove(surveyToken); //clean up available surveys
        surveyQuestionRepository.deleteByOwner(s);
        surveyResultRepository.deleteByOwner(s);
        userRepository.save(user);
        surveyRepository.deleteBySurveyToken(surveyToken);
    }

    public void deleteById(Long id){
        surveyRepository.deleteById(id);
    }
}
