package edu.sjsu.cmpe275.Controller;

import edu.sjsu.cmpe275.Domain.Survey;
import edu.sjsu.cmpe275.Domain.User;
import edu.sjsu.cmpe275.Service.SurveryService;
import edu.sjsu.cmpe275.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SurveyTakerController {
    @Autowired
    UserService userService;
    @Autowired
    SurveryService surveyService;

    @RequestMapping(value="/take_survey", method= RequestMethod.GET)
    public ModelAndView takeSurvey(ModelAndView modelAndView, HttpSession session, HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.findByEmail(name);
        Map<String,String> availableSurveys = user.getAvailableSurveyMap();
        Map<String,String> survey_map = new HashMap<>();
        for(String token:availableSurveys.keySet()){
            Survey survey = surveyService.findBySurveyToken(token);
            if(survey!=null){
                survey_map.put(survey.getName(), availableSurveys.get(token));
            }
        }
        modelAndView.addObject("completed_surveys", user.getCompletedSurveys());
        modelAndView.addObject("available_surveys", survey_map);
        modelAndView.setViewName("take_survey");
        return modelAndView;
    }

    @RequestMapping(value="/view_completed", method= RequestMethod.GET)
    public ModelAndView viewCompleted(ModelAndView modelAndView, HttpSession session, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.findByEmail(name);
        modelAndView.addObject("completed_surveys", user.getCompletedSurveys());
        modelAndView.setViewName("view_completed");
        return modelAndView;
    }
}
