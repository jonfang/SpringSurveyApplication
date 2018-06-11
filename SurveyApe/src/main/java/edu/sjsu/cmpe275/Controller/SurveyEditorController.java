package edu.sjsu.cmpe275.Controller;

import edu.sjsu.cmpe275.Domain.Survey;
import edu.sjsu.cmpe275.Domain.SurveyQuestion.MultipleChoice;
import edu.sjsu.cmpe275.Domain.User;
import edu.sjsu.cmpe275.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebParam;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SurveyEditorController {
    @Autowired
    private UserService userService;

    @RequestMapping(value="/edit_survey", method= RequestMethod.GET)
    public ModelAndView viewEditSurvey(ModelAndView modelAndView, Principal principal){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.findByEmail(name);
        List<Survey> surveyList = user.getSurveyList();
        modelAndView.addObject("survey", new Survey());
        modelAndView.addObject("surveyList", surveyList);
        modelAndView.setViewName("edit_survey");
        return modelAndView;
    }

    @RequestMapping(value="/edit_survey",  method = RequestMethod.POST, params = "action=edit survey")
    public ModelAndView editSurvey(ModelAndView modelAndView, Survey s, HttpSession session){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.findByEmail(name);
        List<Survey> surveyList = user.getSurveyList();
        Survey tmpSurvey = new Survey();
        for(Survey survey:surveyList){
            if(survey.getName().equals(s.getName())){
                tmpSurvey = survey;
            }
        }
        session.setAttribute("tmpSurvey", tmpSurvey);
        System.out.println(tmpSurvey.getName());
        modelAndView.setViewName("redirect:compose_survey");
        return modelAndView;
    }



}
