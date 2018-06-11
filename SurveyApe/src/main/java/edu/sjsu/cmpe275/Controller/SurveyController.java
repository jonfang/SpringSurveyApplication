package edu.sjsu.cmpe275.Controller;

import edu.sjsu.cmpe275.Domain.*;
import edu.sjsu.cmpe275.Domain.SurveyQuestion.*;
import edu.sjsu.cmpe275.Domain.SurveyResult.*;
import edu.sjsu.cmpe275.Service.SurveryService;
import edu.sjsu.cmpe275.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import sun.awt.image.ImageWatched;

import javax.jws.WebParam;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

//this will be the main survey page entry point
@Controller
public class SurveyController {
    @Autowired
    private UserService userService;
    @Autowired
    private SurveryService surveryService;
    private LinkedHashMap<String, Boolean> progress_map;
    private HashMap<String, SurveyQuestion> question_map;
    private HashMap<String, SurveyResult> result_map;
    private HashMap<String, String> completed_map;
    private String prev_url;

    @RequestMapping(value="/survey", method=RequestMethod.GET)
    public ModelAndView simpleSurvey(ModelAndView modelAndView, Principal principal){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
//        System.out.println(name);
//        User user = userService.findByEmail(name);
//        if(user!=null) {
//            //user.setSurveyList(new ArrayList<Survey>(Arrays.asList(new Survey("Sample survey", "Open", UUID.randomUUID().toString()))));
//            //userService.saveUser(user);
//            List<Survey> list = surveryService.findByUser(name);
//            System.out.println(name);
//            for(Survey s:list){
//                for(SurveyQuestion q:s.getQuestions()){
//                    System.out.println(q.getClass());
//                    if(q instanceof ShortAnswer) {
//                        System.out.println(((ShortAnswer) q).getShortAnswer());
//                    }
//                    else if(q instanceof Rating){
//                        System.out.println(((Rating) q).getRating());
//                    }
//                }
//            }
//            modelAndView.addObject("surveys", list);
//        Survey s = surveryService.findBySurveyToken("1a3b8569-079a-469a-af06-1caecb468c99");
//        ShortAnswer q1 = new ShortAnswer("Question 1");
//        q1.setShortAnswer("short answer 1");
//        Rating q2 = new Rating("Question 2");
//        q2.setRating(3);
//        List<SurveyQuestion> questions = new ArrayList<>();
//        questions.add(q1);
//        questions.add(q2);
//        s.setQuestions(questions);
//        surveryService.saveSurvey(s);
        modelAndView.setViewName("survey");
        return modelAndView;
    }

    @RequestMapping(value="/open", method=RequestMethod.GET)
    public ModelAndView openSurvey(ModelAndView modelAndView, @RequestParam("token") String token, HttpSession session){
        if(session.getAttribute("progress_map")==null && session.getAttribute("result_map")==null &&
                session.getAttribute(" question_map")==null && session.getAttribute("completed_map")==null){
            Survey survey = surveryService.findBySurveyToken(token);
//            survey.setParticipation(survey.getParticipation()+1);
//            surveryService.saveSurvey(survey);
            List<SurveyResult> r_list = survey.getQuestionResults();
            List<SurveyQuestion> q_list = survey.getQuestions();
            progress_map = new LinkedHashMap<>();
            result_map = new HashMap<>();
            question_map = new HashMap<>();
            completed_map = new HashMap<>();
            for(SurveyResult r:r_list){
                progress_map.put(r.getQuestion(), false);
                result_map.put(r.getQuestion(), r);
            }
            for(SurveyQuestion q:q_list){
                question_map.put(q.getQuestion(), q);
                completed_map.put(q.getQuestion(), "No Response");
            }

        }
        else{
            progress_map = (LinkedHashMap)session.getAttribute("progress_map");
            result_map = (HashMap)session.getAttribute("result_map");
            question_map = (HashMap)session.getAttribute(" question_map");
            completed_map = (HashMap)session.getAttribute("completed_map");
        }

        if(completed(progress_map)){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Survey survey = surveryService.findBySurveyToken(token);
//            survey.setParticipation_completed(survey.getParticipation_completed()+1);
//            surveryService.saveSurvey(survey);
            processResults(result_map, token, surveryService); //save survey results
            storeSessionResult(username, completed_map, survey);//record this session's survey result
            //reset
            progress_map=null;
            question_map=null;
            result_map=null;
            completed_map=null;
            modelAndView.addObject("surveyMessage", "Completed!");
        }
        else{
            for(String key:progress_map.keySet()){
                System.out.println(key + "->" + progress_map.get(key));
            }
            for(String key:progress_map.keySet()){
                if(!progress_map.get(key)){ //if has not been completed
                    SurveyQuestion curr = question_map.get(key);
                    if(curr instanceof YesNo){
                        YesNo q = (YesNo)curr;
                        modelAndView.addObject("yesno", q);
                        modelAndView.addObject("type", "yesno");
                    }
                    else if(curr instanceof ShortAnswer){
                        ShortAnswer q = (ShortAnswer)curr;
                        modelAndView.addObject("shortanswer", q);
                        modelAndView.addObject("type", "short");
                    }
                    else if(curr instanceof Rating){
                        Rating q = (Rating)curr;
                        modelAndView.addObject("rating", q);
                        modelAndView.addObject("type", "rating");
                    }
                    else if(curr instanceof MultipleChoice){
                        MultipleChoice q = (MultipleChoice)curr;
                        modelAndView.addObject("multiplechoice", q);
                        modelAndView.addObject("type", "multiplechoice");
                    }
                    else if(curr instanceof DateTime){
                        DateTime q = (DateTime)curr;
                        modelAndView.addObject("datetime", q);
                        modelAndView.addObject("type", "datetime");
                    }
                }
            }
        }
        modelAndView.setViewName("survey");
        session.setAttribute("progress_map", progress_map);
        session.setAttribute("result_map", result_map);
        session.setAttribute(" question_map", question_map);
        session.setAttribute("completed_map", completed_map);
        prev_url = "/open?token="+token;
        session.setAttribute("prev_url", prev_url);
        return modelAndView;
    }

    @RequestMapping(value="/closed", method=RequestMethod.GET)
    public ModelAndView closedSurvey(ModelAndView modelAndView, @RequestParam("token") String token, HttpSession session){
        if(session.getAttribute("progress_map")==null && session.getAttribute("result_map")==null &&
                session.getAttribute(" question_map")==null && session.getAttribute("completed_map")==null){
            Survey survey = surveryService.findBySurveyToken(token);
//            survey.setParticipation(survey.getParticipation()+1);
//            surveryService.saveSurvey(survey);
            List<SurveyResult> r_list = survey.getQuestionResults();
            List<SurveyQuestion> q_list = survey.getQuestions();
            progress_map = new LinkedHashMap<>();
            result_map = new HashMap<>();
            question_map = new HashMap<>();
            completed_map = new HashMap<>();
            for(SurveyResult r:r_list){
                progress_map.put(r.getQuestion(), false);
                result_map.put(r.getQuestion(), r);
            }
            for(SurveyQuestion q:q_list){
                question_map.put(q.getQuestion(), q);
                completed_map.put(q.getQuestion(), "No Response");
            }

        }
        else{
            progress_map = (LinkedHashMap)session.getAttribute("progress_map");
            result_map = (HashMap)session.getAttribute("result_map");
            question_map = (HashMap)session.getAttribute(" question_map");
            completed_map = (HashMap)session.getAttribute("completed_map");
        }


        if(completed(progress_map)){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Survey survey = surveryService.findBySurveyToken(token);
//            survey.setParticipation_completed(survey.getParticipation_completed()+1);
//            surveryService.saveSurvey(survey);
            processResults(result_map, token, surveryService); //save survey results
            storeSessionResult(username, completed_map, survey);//record this session's survey result
            //reset
            progress_map=null;
            question_map=null;
            result_map=null;
            completed_map=null;
            modelAndView.addObject("surveyMessage", "Completed!");
        }
        else{
            for(String key:progress_map.keySet()){
                System.out.println(key + "->" + progress_map.get(key));
            }
            for(String key:progress_map.keySet()){
                if(!progress_map.get(key)){ //if has not been completed
                    SurveyQuestion curr = question_map.get(key);
                    if(curr instanceof YesNo){
                        YesNo q = (YesNo)curr;
                        modelAndView.addObject("yesno", q);
                        modelAndView.addObject("type", "yesno");
                    }
                    else if(curr instanceof ShortAnswer){
                        ShortAnswer q = (ShortAnswer)curr;
                        modelAndView.addObject("shortanswer", q);
                        modelAndView.addObject("type", "short");
                    }
                    else if(curr instanceof Rating){
                        Rating q = (Rating)curr;
                        modelAndView.addObject("rating", q);
                        modelAndView.addObject("type", "rating");
                    }
                    else if(curr instanceof MultipleChoice){
                        MultipleChoice q = (MultipleChoice)curr;
                        modelAndView.addObject("multiplechoice", q);
                        modelAndView.addObject("type", "multiplechoice");
                    }
                    else if(curr instanceof DateTime){
                        DateTime q = (DateTime)curr;
                        modelAndView.addObject("datetime", q);
                        modelAndView.addObject("type", "datetime");
                    }
                }
            }
        }
        modelAndView.setViewName("survey");
        session.setAttribute("progress_map", progress_map);
        session.setAttribute("result_map", result_map);
        session.setAttribute(" question_map", question_map);
        session.setAttribute("completed_map", completed_map);
        prev_url = "/closed?token="+token;
        session.setAttribute("prev_url", prev_url);
        return modelAndView;
    }

    @RequestMapping(value="/general", method=RequestMethod.GET)
    public ModelAndView generalSurvey(ModelAndView modelAndView, @RequestParam("token") String token, HttpSession session){
        if(session.getAttribute("progress_map")==null && session.getAttribute("result_map")==null &&
                session.getAttribute(" question_map")==null && session.getAttribute("completed_map")==null){
            Survey survey = surveryService.findBySurveyToken(token);
//            survey.setParticipation(survey.getParticipation()+1);
//            surveryService.saveSurvey(survey);
            List<SurveyResult> r_list = survey.getQuestionResults();
            List<SurveyQuestion> q_list = survey.getQuestions();
            progress_map = new LinkedHashMap<>();
            result_map = new HashMap<>();
            question_map = new HashMap<>();
            completed_map = new HashMap<>();
            for(SurveyResult r:r_list){
                progress_map.put(r.getQuestion(), false);
                result_map.put(r.getQuestion(), r);
            }
            for(SurveyQuestion q:q_list){
                question_map.put(q.getQuestion(), q);
                completed_map.put(q.getQuestion(), "No Response");
            }

        }
        else{
            progress_map = (LinkedHashMap)session.getAttribute("progress_map");
            result_map = (HashMap)session.getAttribute("result_map");
            question_map = (HashMap)session.getAttribute(" question_map");
            completed_map = (HashMap)session.getAttribute("completed_map");
        }


        if(completed(progress_map)){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Survey survey = surveryService.findBySurveyToken(token);
//            survey.setParticipation_completed(survey.getParticipation_completed()+1);
//            surveryService.saveSurvey(survey);
            processResults(result_map, token, surveryService); //save survey results
            storeSessionResult(username, completed_map, survey);//record this session's survey result
            //reset
            progress_map=null;
            question_map=null;
            result_map=null;
            completed_map=null;
            modelAndView.addObject("surveyMessage", "Completed!");
        }
        else{
            for(String key:progress_map.keySet()){
                System.out.println(key + "->" + progress_map.get(key));
            }
            for(String key:progress_map.keySet()){
                if(!progress_map.get(key)){ //if has not been completed
                    SurveyQuestion curr = question_map.get(key);
                    if(curr instanceof YesNo){
                        YesNo q = (YesNo)curr;
                        modelAndView.addObject("yesno", q);
                        modelAndView.addObject("type", "yesno");
                    }
                    else if(curr instanceof ShortAnswer){
                        ShortAnswer q = (ShortAnswer)curr;
                        modelAndView.addObject("shortanswer", q);
                        modelAndView.addObject("type", "short");
                    }
                    else if(curr instanceof Rating){
                        Rating q = (Rating)curr;
                        modelAndView.addObject("rating", q);
                        modelAndView.addObject("type", "rating");
                    }
                    else if(curr instanceof MultipleChoice){
                        MultipleChoice q = (MultipleChoice)curr;
                        modelAndView.addObject("multiplechoice", q);
                        modelAndView.addObject("type", "multiplechoice");
                    }
                    else if(curr instanceof DateTime){
                        DateTime q = (DateTime)curr;
                        modelAndView.addObject("datetime", q);
                        modelAndView.addObject("type", "datetime");
                    }
                }
            }
        }
        modelAndView.setViewName("survey");
        session.setAttribute("progress_map", progress_map);
        session.setAttribute("result_map", result_map);
        session.setAttribute(" question_map", question_map);
        session.setAttribute("completed_map", completed_map);
        prev_url = "/general?token="+token;
        session.setAttribute("prev_url", prev_url);
        return modelAndView;
    }

    @RequestMapping(value="/submit_survey", method=RequestMethod.POST, params="action=submit multiplechoice")
    public ModelAndView submitMultipleChoice(ModelAndView modelAndView, @Valid MultipleChoice question, BindingResult bindingResult, HttpSession session){
        prev_url = (String)session.getAttribute("prev_url");
        prev_url = "redirect:" + prev_url;
        System.out.println(question.getClass().getName());
        System.out.println(question.getQuestion());
        System.out.println(question.getChosen());
        //record logic
        progress_map = (LinkedHashMap)session.getAttribute("progress_map");
        progress_map.put(question.getQuestion(), true);
        result_map = (HashMap)session.getAttribute("result_map");
        MultipleChoiceResult result = (MultipleChoiceResult)result_map.get(question.getQuestion());
        Map<String, Integer> tmpMap = result.getChoiceMap();
        tmpMap.put(question.getChosen(), tmpMap.get(question.getChosen())+1);
        completed_map.put(question.getQuestion(), question.getChosen());
        result.setChoiceMap(tmpMap);
        result_map.put(question.getQuestion(), result);
        session.setAttribute("progress_map", progress_map);
        session.setAttribute("result_map", result_map);
        session.setAttribute("completed_map", completed_map);
        modelAndView.setViewName(prev_url);
        return modelAndView;
    }

    @RequestMapping(value="/submit_survey", method=RequestMethod.POST, params="action=submit datetime")
    public ModelAndView submitDateTime(ModelAndView modelAndView, @Valid DateTime question, BindingResult bindingResult, HttpSession session){
        prev_url = (String)session.getAttribute("prev_url");
        prev_url = "redirect:" + prev_url;
        System.out.println(question.getClass().getName());
        System.out.println(question.getQuestion());
        System.out.println(question.getMonth());
        System.out.println(question.getDay());
        System.out.println(question.getYear());
        //record logic
        progress_map = (LinkedHashMap)session.getAttribute("progress_map");
        progress_map.put(question.getQuestion(), true);
        result_map = (HashMap)session.getAttribute("result_map");
        DateTimeResult result = (DateTimeResult)result_map.get(question.getQuestion());
        List<String> list = result.getDateTimeResults();
        String date = question.getMonth()+ "-" + question.getDay() + "-"+ question.getYear();
        list.add(date);
        completed_map.put(question.getQuestion(), date);
        result.setDateTimeResults(list);
        result_map.put(question.getQuestion(), result);
        session.setAttribute("progress_map", progress_map);
        session.setAttribute("result_map", result_map);
        session.setAttribute("completed_map", completed_map);
        modelAndView.setViewName(prev_url);
        return modelAndView;
    }

    @RequestMapping(value="/submit_survey", method=RequestMethod.POST, params="action=submit short")
    public ModelAndView submitShort(ModelAndView modelAndView, @Valid ShortAnswer question, BindingResult bindingResult, HttpSession session){
        prev_url = (String)session.getAttribute("prev_url");
        prev_url = "redirect:" + prev_url;
        System.out.println(question.getClass().getName());
        System.out.println(question.getQuestion());
        System.out.println(question.getShortAnswer());
        //record logic
        progress_map = (LinkedHashMap)session.getAttribute("progress_map");
        progress_map.put(question.getQuestion(), true);
        result_map = (HashMap)session.getAttribute("result_map");
        ShortAnswerResult result = (ShortAnswerResult)result_map.get(question.getQuestion());
        List<String> list = result.getAnswerResults();
        list.add(question.getShortAnswer());
        completed_map.put(question.getQuestion(), question.getShortAnswer());
        result.setAnswerResults(list);
        result_map.put(question.getQuestion(), result);
        session.setAttribute("progress_map", progress_map);
        session.setAttribute("result_map", result_map);
        session.setAttribute("completed_map", completed_map);
        modelAndView.setViewName(prev_url);
        return modelAndView;
    }

    @RequestMapping(value="/submit_survey", method=RequestMethod.POST, params="action=submit yesno")
    public ModelAndView submitYesNo(ModelAndView modelAndView, @Valid YesNo question, BindingResult bindingResult, HttpSession session){
        prev_url = (String)session.getAttribute("prev_url");
        prev_url = "redirect:" + prev_url;
        System.out.println(question.getClass().getName());
        System.out.println(question.getQuestion());
        System.out.println(question.isYes());
        //record logic
        progress_map = (LinkedHashMap)session.getAttribute("progress_map");
        progress_map.put(question.getQuestion(), true);
        result_map = (HashMap)session.getAttribute("result_map");
        YesNoResult result = (YesNoResult)result_map.get(question.getQuestion());
        if(question.isYes()) {
            result.setYes(result.getYes() + 1);
        }
        else{
            result.setNo(result.getNo()+1);
        }
        completed_map.put(question.getQuestion(), question.isYes()?"Yes":"No");
        result_map.put(question.getQuestion(), result);
        session.setAttribute("result_map", result_map);
        session.setAttribute("progress_map", progress_map);
        session.setAttribute("completed_map", completed_map);
        modelAndView.setViewName(prev_url);
        return modelAndView;
    }

    @RequestMapping(value="/submit_survey", method=RequestMethod.POST, params="action=submit rating")
    public ModelAndView submitRating(ModelAndView modelAndView, @Valid Rating question, BindingResult bindingResult, HttpSession session){
        prev_url = (String)session.getAttribute("prev_url");
        prev_url = "redirect:" + prev_url;
        System.out.println(question.getClass().getName());
        System.out.println(question.getQuestion());
        System.out.println(question.getRating());
        //record logic
        progress_map = (LinkedHashMap)session.getAttribute("progress_map");
        progress_map.put(question.getQuestion(), true);
        result_map = (HashMap)session.getAttribute("result_map");
        RatingResult result = (RatingResult)result_map.get(question.getQuestion());

        int rating = question.getRating();
        if(rating==1){
            System.out.println(result.getOne());
            result.setOne(result.getOne()+1);
        }
        else if(rating==2){
            result.setTwo(result.getTwo()+1);
        }
        else if(rating==3){
            result.setThree(result.getThree()+1);
        }
        else if(rating==4){
            result.setFour(result.getFour()+1);
        }
        else if(rating==5){
            result.setFive(result.getFive()+1);
        }
        completed_map.put(question.getQuestion(), String.valueOf(rating));
        result_map.put(question.getQuestion(), result);
        session.setAttribute("result_map", result_map);
        session.setAttribute("progress_map", progress_map);
        session.setAttribute("completed_map", completed_map);
        modelAndView.setViewName(prev_url);
        return modelAndView;
    }

    private void processResults(HashMap<String, SurveyResult> result_map, String token, SurveryService surveryService){
        //display current buffer results
        for(String key:result_map.keySet()){
            SurveyResult result = result_map.get(key);
            if(result instanceof YesNoResult){
                System.out.println("YesNo Result");
                System.out.println("----------------------");
                System.out.println("Yes: " + ((YesNoResult) result).getYes());
                System.out.println("No: " + ((YesNoResult) result).getNo());
            }
            else if(result instanceof ShortAnswerResult){
                System.out.println("Short Answer Result");
                System.out.println("----------------------");
                for(String s:((ShortAnswerResult) result).getAnswerResults()){
                    System.out.println(s);
                }
            }
            else if(result instanceof RatingResult){
                System.out.println("Rating Result");
                System.out.println("----------------------");
                System.out.println("1:"+((RatingResult) result).getOne() );
                System.out.println("2:" + ((RatingResult) result).getTwo());
                System.out.println("3:" + ((RatingResult) result).getThree());
                System.out.println("4:" + ((RatingResult) result).getFour());
                System.out.println("5:" + ((RatingResult) result).getFive());
            }
            else if(result instanceof MultipleChoiceResult){
                Map<String, Integer> map = ((MultipleChoiceResult) result).getChoiceMap();
                for(String k:map.keySet()){
                    System.out.println(k + " : " + map.get(k));
                }
            }
            else if(result instanceof DateTimeResult){
                System.out.println(((DateTimeResult) result).getDateTimeResults());
            }

            //record survey results in DB
            Survey survey = surveryService.findBySurveyToken(token);
            List<SurveyResult> r_list = survey.getQuestionResults();
            for(SurveyResult r:r_list){
                if(r instanceof YesNoResult){
                    ((YesNoResult) r).setYes(((YesNoResult)result_map.get(r.getQuestion())).getYes());
                    ((YesNoResult) r).setNo(((YesNoResult)result_map.get(r.getQuestion())).getNo());
                }
                else if(r instanceof RatingResult){
                    ((RatingResult) r).setOne(((RatingResult)result_map.get(r.getQuestion())).getOne());
                    ((RatingResult) r).setTwo(((RatingResult)result_map.get(r.getQuestion())).getTwo());
                    ((RatingResult) r).setThree(((RatingResult)result_map.get(r.getQuestion())).getThree());
                    ((RatingResult) r).setFour(((RatingResult)result_map.get(r.getQuestion())).getFour());
                    ((RatingResult) r).setFive(((RatingResult)result_map.get(r.getQuestion())).getFive());
                }
                else if(r instanceof ShortAnswerResult){
                    ((ShortAnswerResult) r).setAnswerResults(((ShortAnswerResult)result_map.get(r.getQuestion())).getAnswerResults());
                }
                else if(r instanceof MultipleChoiceResult){
                    ((MultipleChoiceResult) r).setChoiceMap(((MultipleChoiceResult)result_map.get(r.getQuestion())).getChoiceMap());
                }
                else if(r instanceof DateTimeResult){
                    ((DateTimeResult) r).setDateTimeResults(((DateTimeResult)result_map.get(r.getQuestion())).getDateTimeResults());
                }

            }
            if(survey.getParticipation()==null){ //add participation
                survey.setParticipation(1);
            }
            else {
                survey.setParticipation(survey.getParticipation() + 1);
            }
            surveryService.saveSurvey(survey);
        }
    }

    private void storeSessionResult(String username, HashMap<String, String> completed_map, Survey survey){
        User user = userService.findByEmail(username);
        if(user!=null){
            Completed_Survey completed_survey = new Completed_Survey();
            completed_survey.setOutput(completed_map);
            completed_survey.setOwner(user);
            completed_survey.setName(survey.getName());
            user.getCompletedSurveys().add(completed_survey);
            userService.saveUser(user);
        }
    }

    private static boolean completed(LinkedHashMap<String, Boolean> progress_map){
        int count = progress_map.size();
        for(String key:progress_map.keySet()){
            if(progress_map.get(key)){
                count--;
            }
        }
        return count==0;
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
        System.out.println("Survey Results:");
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
}
