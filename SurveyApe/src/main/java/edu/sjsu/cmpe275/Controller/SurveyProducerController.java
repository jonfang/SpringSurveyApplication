package edu.sjsu.cmpe275.Controller;

import com.sun.org.apache.xpath.internal.operations.Mult;
import edu.sjsu.cmpe275.Domain.Survey;
import edu.sjsu.cmpe275.Domain.SurveyQuestion.*;
import edu.sjsu.cmpe275.Domain.SurveyResult.*;
import edu.sjsu.cmpe275.Domain.User;
import edu.sjsu.cmpe275.Service.EmailService;
import edu.sjsu.cmpe275.Service.SurveryService;
import edu.sjsu.cmpe275.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Controller
public class SurveyProducerController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SurveryService surveyService;
    private Survey tmpSurvey;

    @RequestMapping(value="/produce_survey", method= RequestMethod.GET)
    public ModelAndView produceSurvey(ModelAndView modelAndView, HttpSession session,HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.findByEmail(name);
//        1.Receive the temporary survey
        if(session.getAttribute("tmpSurvey")==null){
            System.out.println("Create new Survey");
            tmpSurvey = new Survey();
            session.setAttribute("tmpSurvey", tmpSurvey);
        }
        else{
            System.out.println("Retrieve old Survey");
            tmpSurvey = (Survey)session.getAttribute("tmpSurvey");
        }
//        2.Create the corresponding survey results:
        List<SurveyResult> questionResults = new ArrayList<>();
        for(SurveyQuestion q:tmpSurvey.getQuestions()){
            SurveyResult qr = SurveyResultFactory.getSurveyQuestion(q);
            qr.setQuestion(q.getQuestion());
            if(qr instanceof MultipleChoiceResult){
                HashMap<String, Integer> tmpMap = new HashMap<>();
                for(Choice c:((MultipleChoice)q).getChoices()){
                    tmpMap.put(c.getChoice(),0);
                }
                ((MultipleChoiceResult) qr).setChoiceMap(tmpMap);
            }
            questionResults.add(qr);
            qr.setOwner(tmpSurvey);
        }
        tmpSurvey.setQuestionResults(questionResults);
        //save survey
        if(ifExist(tmpSurvey, user)){ //if an existing one, update correspondingly
            List<Survey> surveyList = user.getSurveyList();
            for(Survey s:surveyList){
                if(s.getId()==tmpSurvey.getId()){
                       String token = s.getSurveyToken();
                       Survey survey = surveyService.findBySurveyToken(token);
                       for(SurveyQuestion q:tmpSurvey.getQuestions()){
                           if(!containsQ(survey.getQuestions(), q)){
                               survey.getQuestions().add(q);
                           }
                       }
                       for(SurveyResult r:tmpSurvey.getQuestionResults()){
                           if(!containsR(survey.getQuestionResults(), r)){
                               survey.getQuestionResults().add(r);
                           }
                       }
                       tmpSurvey.setSurveyToken(survey.getSurveyToken());
                       surveyService.saveSurvey(survey);
                       break;
                }
            }
        }
        else{
            user.getSurveyList().add(tmpSurvey);
            userService.saveUser(user);
        }

        printSurvey(tmpSurvey);

//        3.create corresponding url to deliver survey
        String accessUrl = "//////";
        if(tmpSurvey.getType().equals("General")){ //general access by anyone
            accessUrl = "/general?token=";
        }
        else if(tmpSurvey.getType().equals("Closed")){ //closed need extra mechanism to send email
            accessUrl = "/closed?token=";
        }
        else if(tmpSurvey.getType().equals("Open")){ //open need to have all user registered
            accessUrl = "/open?token=";
        }
        //if general and open, just create url
        //if closed, have email service
        String appUrl = request.getScheme() + "://" + request.getServerName();
        String urlText = appUrl + ":8080" + accessUrl + tmpSurvey.getSurveyToken();
        addSurveyToUsers(tmpSurvey.getSurveyToken(), urlText); //add the newly created survey to each user
        modelAndView.addObject("type", tmpSurvey.getType());
        modelAndView.addObject("produceMessage", "The survey is created and can be access at: " + urlText);
        session.setAttribute("produceMessage", "The survey is created and can be access at: " + urlText);
        modelAndView.addObject("survey", tmpSurvey);
        modelAndView.addObject("user", new User());
        modelAndView.setViewName("produce_survey");
        session.setAttribute("tmpSurvey", null);
        return modelAndView;
    }

    @RequestMapping(value="/closed_survey", method= RequestMethod.GET)
    public ModelAndView closedSurvey(ModelAndView modelAndView, HttpSession session){
        String message = (String)session.getAttribute("produceMessage");
        modelAndView.addObject("produceMessage", message);
        modelAndView.setViewName("closed");
        return modelAndView;
    }

    @RequestMapping(value="/closed_survey", method=RequestMethod.POST, params="action=submit")
    public ModelAndView closedSurvey(ModelAndView modelAndView, User user, HttpSession session, RedirectAttributes redirectAttributes){
        String message = (String)session.getAttribute("produceMessage");
        System.out.println(user.getEmail());
        System.out.println(message);
        if(!user.getEmail().contains(",")) {
            SimpleMailMessage registrationEmail = new SimpleMailMessage();
            registrationEmail.setTo(user.getEmail());
            registrationEmail.setSubject("New Survey");
            registrationEmail.setText(message);
            registrationEmail.setFrom("noreply@domain.com");
            emailService.sendEmail(registrationEmail);
        }
        else{
            String[] emails = user.getEmail().split(",");
            for(String email:emails){
                SimpleMailMessage registrationEmail = new SimpleMailMessage();
                registrationEmail.setTo(email);
                registrationEmail.setSubject("New Survey");
                registrationEmail.setText(message);
                registrationEmail.setFrom("noreply@domain.com");
                emailService.sendEmail(registrationEmail);
            }
        }
        redirectAttributes.addAttribute("produceMessage", "Message has been sent to " + user.getEmail());
        modelAndView.setViewName("redirect:closed_survey");
        return modelAndView;
    }

    private void printSurvey(Survey tmpSurvey){
        System.out.println("=====================================================");
        System.out.println("Survey Name:" + tmpSurvey.getName());
        System.out.println("Survey Type:" + tmpSurvey.getType());
        System.out.println("Survey Token:" + tmpSurvey.getSurveyToken());
        System.out.println("Survey Questions:");
        for(SurveyQuestion q:tmpSurvey.getQuestions()){
            System.out.println(q.getClass().getName());
            System.out.println("=====>" + q.getQuestion());
        }
        for(SurveyResult r:tmpSurvey.getQuestionResults()){
            System.out.println(r.getClass().getName());
            System.out.println("---->" + r.getQuestion());
            if(r instanceof YesNoResult){
                System.out.println("Yes: " + ((YesNoResult) r).getYes());
                System.out.println("No:" + ((YesNoResult) r).getNo());
            }
            else if(r instanceof RatingResult){
                List<String> tmp_list = new ArrayList<>();
                tmp_list.add(String.valueOf(((RatingResult) r).getOne()));
                tmp_list.add(String.valueOf(((RatingResult) r).getTwo()));
                tmp_list.add(String.valueOf(((RatingResult) r).getThree()));
                tmp_list.add(String.valueOf(((RatingResult) r).getFour()));
                tmp_list.add(String.valueOf(((RatingResult) r).getFive()));
                System.out.println(String.join(",", tmp_list));
            }
        }
        System.out.println("=====================================================");
    }

    private void addSurveyToUsers(String token, String survey_url){
        List<User> users = userService.findAll();
        for(User user:users){
            user.getAvailableSurveyMap().put(token, survey_url);
            userService.saveUser(user);
        }
    }

    private boolean ifExist(Survey survey, User user){
        String token = survey.getSurveyToken();
        System.out.println("Token:" + token);
        System.out.println("ID:" + survey.getId());
        List<Survey> surveyList = user.getSurveyList();
        if(token!=null && !token.equals("")){
            for(Survey s:surveyList){
                if(s.getId()==survey.getId()){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsQ(List<SurveyQuestion> qlist, SurveyQuestion question){
        for(SurveyQuestion q:qlist){
            if(q.getType().equals(question.getType()) && q.getQuestion().equals(question.getQuestion())){
                return true;
            }
        }
        return false; //new question
    }

    private boolean containsR(List<SurveyResult> rlist, SurveyResult result){
        for(SurveyResult r:rlist){
            if(r.getType().equals(result.getType()) && r.getQuestion().equals(result.getQuestion())){
                return true;
            }
        }
        return false; //new result
    }
}
