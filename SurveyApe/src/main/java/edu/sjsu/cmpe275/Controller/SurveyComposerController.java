package edu.sjsu.cmpe275.Controller;

import edu.sjsu.cmpe275.Domain.Survey;
import edu.sjsu.cmpe275.Domain.SurveyQuestion.*;
import edu.sjsu.cmpe275.Domain.User;
import edu.sjsu.cmpe275.Service.SurveryService;
import edu.sjsu.cmpe275.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class SurveyComposerController {
    @Autowired
    private UserService userService;
    @Autowired
    private SurveryService surveryService;
    private Survey tmpSurvey;
    private MultipleChoice tmpMC;

    @RequestMapping(value="/compose_survey", method= RequestMethod.GET)
    public ModelAndView composeSurvey(ModelAndView modelAndView, HttpSession session){
        //create a new survey
        //TODO: add a flag to reset the attribute and clear up Survey when trigger
        if(session.getAttribute("tmpSurvey")==null){
        //System.out.println("Create new Survey");
            tmpSurvey = new Survey();
            session.setAttribute("tmpSurvey", tmpSurvey);
        }
        else{
        //System.out.println("Retrieve old Survey");
            tmpSurvey = (Survey)session.getAttribute("tmpSurvey");
        }
        modelAndView.addObject("survey", tmpSurvey);
        modelAndView.addObject("survey_question_factory", new SurveyQuestionFactory());
        modelAndView.setViewName("compose_survey");
        addCommonFields(modelAndView);
        return modelAndView;
    }

    @RequestMapping(value="/compose_survey", method = RequestMethod.POST,  params="action=submit")
    public ModelAndView createSurvey(ModelAndView modelAndView, @Valid Survey survey, BindingResult bindingResult, Principal principal, HttpSession session){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.findByEmail(name);
        tmpSurvey = (Survey)session.getAttribute("tmpSurvey");
        tmpSurvey.setSurveyToken(UUID.randomUUID().toString());
        tmpSurvey.setName(survey.getName());
        tmpSurvey.setType(survey.getType());
        session.setAttribute("tmpSurvey", tmpSurvey);

        List<Survey> surveys = user.getSurveyList();
        for(Survey s:surveys){
            System.out.println("Survey Name:" + s.getName());
            System.out.println("Survey Type:" + s.getType());
            System.out.println("Survey Questions:");
            for(SurveyQuestion q:s.getQuestions()){
                System.out.println("=====>" + q.getQuestion());
            }
        }

        //**Save the result
//        surveys.add(tmpSurvey);
//        user.setSurveyList(surveys);
//        userService.saveUser(user);
        modelAndView.addObject("surveyMessage", "Success!");
        //values that have to be added later
        addCommonFields(modelAndView);
        //reset tmp survey
//        session.setAttribute("tmpSurvey", new Survey());
        modelAndView.setViewName("redirect:produce_survey");
        return modelAndView;
    }

//    @RequestMapping(value="/compose_survey", method = RequestMethod.POST,  params="action=addquestion")
//    public ModelAndView createSurvey_add(ModelAndView modelAndView, @Valid SurveyQuestionFactory factory, BindingResult bindingResult, Principal principal){
//        modelAndView.addObject("add_question", "Question is add toggle");
//        addCommonFields(modelAndView);
//        return modelAndView;
//    }


    @RequestMapping(value="/add_question",  method = RequestMethod.GET)
    public ModelAndView getQuestion(ModelAndView modelAndView, @RequestParam("question_type") String question_type, HttpSession session){
        if(session.getAttribute("tmpSurvey")!=null){
            System.out.println("Get the Survey!");
        }
        modelAndView.addObject("question_type", question_type);
        SurveyQuestion question = SurveyQuestionFactory.getSurveyQuestion(question_type);
        if(question instanceof ShortAnswer){
            modelAndView.addObject("shortanswer", question);
        }
        else if(question instanceof Rating){
            modelAndView.addObject("rating", question);
        }
        else if(question instanceof YesNo){
            modelAndView.addObject("yesno", question);
        }
        else if(question instanceof DateTime){
            modelAndView.addObject("datetime", question);
        }
        else if(question instanceof MultipleChoice){
            MultipleChoice q = (MultipleChoice)question;
            if(session.getAttribute("tmpMC")==null){
                tmpMC = new MultipleChoice();
                session.setAttribute("tmpMC", tmpMC);
            }
            else{
                tmpMC = (MultipleChoice) session.getAttribute("tmpMC");
            }
            modelAndView.addObject("choice", new Choice());
            modelAndView.addObject("tmpMC", tmpMC);
            modelAndView.addObject("multiplechoice", q);
        }
        modelAndView.setViewName("add_question");
        return modelAndView;
    }

    @RequestMapping(value="/add_question",  method = RequestMethod.POST, params = "action=add datetime")
    public ModelAndView addDateTime(ModelAndView modelAndView, @Valid DateTime q, HttpSession session){
        System.out.println(q.getQuestion());
        addQuestionToSurvey(q, session);
        modelAndView.setViewName("redirect:compose_survey");
        return modelAndView;
    }

    @RequestMapping(value="/add_question",  method = RequestMethod.POST, params = "action=add multiplechoice")
    public ModelAndView addMultipleChoice(ModelAndView modelAndView, @Valid MultipleChoice q, HttpSession session){
        System.out.println(q.getQuestion());
        tmpMC = (MultipleChoice) session.getAttribute("tmpMC");
        if(tmpMC!=null){
            q.setChoices(tmpMC.getChoices());
        }
        addQuestionToSurvey(q, session);
        tmpMC = null; //reset mc
        session.setAttribute("tmpMC", tmpMC);
        modelAndView.setViewName("redirect:compose_survey");
        return modelAndView;
    }

    @RequestMapping(value="/add_question",  method = RequestMethod.POST, params = "action=add short answer")
    public ModelAndView addShortAnswer(ModelAndView modelAndView, @Valid ShortAnswer q, HttpSession session){
        //System.out.println(factory.getType());
        System.out.println(q.getQuestion());
        //modelAndView.setViewName("add_question");
        addQuestionToSurvey(q, session);
        modelAndView.setViewName("redirect:compose_survey");
        return modelAndView;
    }

    @RequestMapping(value="/add_question",  method = RequestMethod.POST, params = "action=add rating")
    public ModelAndView addRating(ModelAndView modelAndView, @Valid Rating q, HttpSession session){
        //System.out.println(factory.getType());
        System.out.println(q.getQuestion());
        //modelAndView.setViewName("add_question");
        addQuestionToSurvey(q, session);
        modelAndView.setViewName("redirect:compose_survey");
        return modelAndView;
    }

    @RequestMapping(value="/add_question",  method = RequestMethod.POST, params = "action=add yesno")
    public ModelAndView addYesNo(ModelAndView modelAndView, @Valid YesNo q, HttpSession session){
        //System.out.println(factory.getType());
        System.out.println(q.getQuestion());
        //modelAndView.setViewName("add_question");
        addQuestionToSurvey(q, session);
        modelAndView.setViewName("redirect:compose_survey");
        return modelAndView;
    }

    @RequestMapping(value="/add_choice",  method = RequestMethod.POST, params = "action=add choice")
    public ModelAndView addChoice(ModelAndView modelAndView, @Valid Choice choice, HttpSession session, RedirectAttributes redirectAttributes){
        //System.out.println(factory.getType());
        System.out.println(choice.getChoice());
        tmpMC = (MultipleChoice) session.getAttribute("tmpMC");
        if(tmpMC!=null){
            tmpMC.getChoices().add(choice);
        }
        session.setAttribute("tmpMC", tmpMC);
        redirectAttributes.addAttribute("question_type", "MultipleChoice");
        //modelAndView.setViewName("add_question");
        modelAndView.setViewName("redirect:add_question");
        return modelAndView;
    }

    @RequestMapping(value="/delete",  method = RequestMethod.GET)
    public ModelAndView deleteSurvey(ModelAndView modelAndView, @RequestParam("token") String token){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.findByEmail(name);
        surveryService.deleteBySurveyToken(token, name);
        modelAndView.setViewName("delete");
        return modelAndView;
    }


    private void addQuestionToSurvey(SurveyQuestion q, HttpSession session){
        tmpSurvey = (Survey)session.getAttribute("tmpSurvey");
        tmpSurvey.getQuestions().add(q);
        q.setOwner(tmpSurvey);
        session.setAttribute("tmpSurvey", tmpSurvey);
    }

    private static void addCommonFields(ModelAndView modelAndView){
        modelAndView.addObject("survey_types" , Survey.getSurveyTypes());
        modelAndView.addObject("question_types", SurveyQuestion.getQuestionTypes());
    }
}
