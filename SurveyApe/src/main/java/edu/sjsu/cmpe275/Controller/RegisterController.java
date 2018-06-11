package edu.sjsu.cmpe275.Controller;

import java.util.Map;
import java.util.UUID;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import edu.sjsu.cmpe275.Domain.User;
import edu.sjsu.cmpe275.Service.EmailService;
import edu.sjsu.cmpe275.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;
    private EmailService emailService;

    @Autowired
    public RegisterController(BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService, EmailService emailService) {

        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.emailService = emailService;
    }

    // Return registration form template
    @RequestMapping(value="/register", method = RequestMethod.GET)
    public ModelAndView showRegistrationPage(ModelAndView modelAndView, User user){
        modelAndView.addObject("user", user);
        modelAndView.setViewName("register");
        return modelAndView;
    }

    // Process form input data
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView processRegistrationForm(ModelAndView modelAndView, @Valid User user, BindingResult bindingResult, HttpServletRequest request, @RequestParam Map requestParams) {

        // Lookup user in database by e-mail
        User userExists = userService.findByEmail(user.getEmail());
        System.out.println(userExists);
        if (userExists != null) {
            System.out.println("User already exists!");
            modelAndView.addObject("alreadyRegisteredMessage", "Oops!  There is already a user registered with the email provided.");
            modelAndView.setViewName("register");
            bindingResult.reject("email");
        }

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("register");
        } else {
            // new user so we create user and send confirmation e-mail
            // Disable user until they click on confirmation link in email
            user.setEnabled(false);
            // Generate random 36-character string token for confirmation link
            user.setConfirmationToken(UUID.randomUUID().toString());
            // Set new password
            user.setPassword(bCryptPasswordEncoder.encode((CharSequence)(requestParams.get("password"))));
            userService.saveUser(user);
            String appUrl = request.getScheme() + "://" + request.getServerName();
            String port = ":8080";
            SimpleMailMessage registrationEmail = new SimpleMailMessage();
            registrationEmail.setTo(user.getEmail());
            registrationEmail.setSubject("Registration Confirmation");
//            registrationEmail.setText("Thank you for registering with Survey Ape. To confirm your e-mail address, please click the link below:\n"
//                    + appUrl + port + "/confirm?token=" + user.getConfirmationToken());
            registrationEmail.setText("Thank you for registering with Survey Ape. To activate your account, please enter the code: "
            + user.getConfirmationToken() );
            registrationEmail.setFrom("noreply@domain.com");

            emailService.sendEmail(registrationEmail);
            modelAndView.addObject("confirmationMessage", "A confirmation e-mail has been sent to " + user.getEmail());
            modelAndView.setViewName("register");
        }
        return modelAndView;
    }
    @RequestMapping(value="/activate", method = RequestMethod.GET)
    public ModelAndView activate(ModelAndView modelAndView, User user){
        modelAndView.addObject("user", user);
        modelAndView.setViewName("activate");
        return modelAndView;
    }

    @RequestMapping(value="/activate", method = RequestMethod.POST)
    public ModelAndView activateUser(ModelAndView modelAndView, User user){
        String token = user.getConfirmationToken();
        User dbUser = userService.findByConfirmationToken(token);
        if(dbUser!=null){
            dbUser.setEnabled(true);
            System.out.println("Authenticate and enable user");
            userService.saveUser(dbUser);
            modelAndView.addObject("confirmationMessage", dbUser.getEmail() + " has been activated");
        }
        else{
            modelAndView.addObject("confirmationMessage", "Invalid token");
        }
        return modelAndView;
    }

    // Process confirmation link
    @RequestMapping(value="/confirm", method = RequestMethod.GET)
    public ModelAndView showConfirmationPage(ModelAndView modelAndView, @RequestParam("token") String token) {

        User user = userService.findByConfirmationToken(token);

        if (user == null) { // No token found in DB
            modelAndView.addObject("invalidToken", "Oops!  This is an invalid confirmation link.");
        } else { // Token found
            modelAndView.addObject("confirmationToken", user.getConfirmationToken());
        }
        modelAndView.setViewName("confirm");
        return modelAndView;
    }

    // Process confirmation link
    @RequestMapping(value="/confirm", method = RequestMethod.POST)
    public ModelAndView processConfirmationForm(ModelAndView modelAndView, BindingResult bindingResult, @RequestParam Map requestParams, RedirectAttributes redir) {

        System.out.println("Processing...");
        modelAndView.setViewName("confirm");

//        Zxcvbn passwordCheck = new Zxcvbn();
//
//        Strength strength = passwordCheck.measure((String)requestParams.get("password"));
//        if (strength.getScore() < 3) {
//            bindingResult.reject("password");
//
//            redir.addFlashAttribute("errorMessage", "Your password is too weak.  Choose a stronger one.");
//
//            modelAndView.setViewName("redirect:confirm?token=" + requestParams.get("token"));
//            System.out.println(requestParams.get("token"));
//            return modelAndView;
//        }

        // Find the user associated with the reset token
        User user = userService.findByConfirmationToken((String)requestParams.get("token"));

        // Set new password
        user.setPassword(bCryptPasswordEncoder.encode((CharSequence)(requestParams.get("password"))));
        //user.setPassword((String)(requestParams.get("password")));

        // Set user to enabled
        user.setEnabled(true);

        // Save user
        System.out.println("Authenticate and enable user");
        userService.saveUser(user);

        modelAndView.addObject("successMessage", "Your password has been set!");
        return modelAndView;
    }

}