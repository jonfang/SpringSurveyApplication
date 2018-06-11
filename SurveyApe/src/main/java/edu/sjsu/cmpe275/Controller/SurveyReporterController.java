package edu.sjsu.cmpe275.Controller;

import edu.sjsu.cmpe275.Domain.Survey;
import edu.sjsu.cmpe275.Domain.SurveyQuestion.Rating;
import edu.sjsu.cmpe275.Domain.SurveyResult.*;
import edu.sjsu.cmpe275.Domain.User;
import edu.sjsu.cmpe275.Service.SurveryService;
import edu.sjsu.cmpe275.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SurveyReporterController {

    @Autowired
    private SurveryService surveryService;
    @Autowired
    private UserService userService;

    @RequestMapping(value="/report", method= RequestMethod.GET)
    public ModelAndView reportSurvey(ModelAndView modelAndView, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.findByEmail(name);
        List<Survey> surveys = user.getSurveyList();
        modelAndView.addObject("surveys", surveys);
        modelAndView.setViewName("view_survey");
        return modelAndView;
    }

    @RequestMapping(value="/report_survey", method= RequestMethod.GET)
    public ModelAndView reportSurveyByToken(ModelAndView modelAndView, @RequestParam("token") String token, HttpSession session) {
        Survey survey = surveryService.findBySurveyToken(token);
        List<SurveyResult> results = survey.getQuestionResults();
        List<SurveyResultWrapper> wrappers = new ArrayList<>(); //wrapper class that wrap results
        for(SurveyResult r:results){
            SurveyResultWrapper wrapper = new SurveyResultWrapper();
            wrapper.setQuestion(r.getQuestion());
            wrapper.setType(r.getType());
            if(r instanceof YesNoResult){
                wrapper.setYes(((YesNoResult) r).getYes());
                wrapper.setNo(((YesNoResult) r).getNo());
            }
            else if(r instanceof RatingResult){
                wrapper.setOne(((RatingResult) r).getOne());
                wrapper.setTwo(((RatingResult) r).getTwo());
                wrapper.setThree(((RatingResult) r).getThree());
                wrapper.setFour(((RatingResult) r).getFour());
                wrapper.setFive(((RatingResult) r).getFive());
            }
            else if(r instanceof ShortAnswerResult){
                wrapper.setAnswerResults(((ShortAnswerResult) r).getAnswerResults());
            }
            else if(r instanceof MultipleChoiceResult){
                wrapper.setChoiceMap(((MultipleChoiceResult) r).getChoiceMap());
            }
            else if(r instanceof DateTimeResult){
                wrapper.setDateTimeResults(((DateTimeResult) r).getDateTimeResults());
            }
            wrappers.add(wrapper);
        }
        modelAndView.addObject("results", wrappers);
        modelAndView.addObject("survey", survey);
        modelAndView.setViewName("report");
        return modelAndView;
    }
}
