package com.cperceful.beercellartracker;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {

    private static final String REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";

    private static Pattern pattern;
    private Matcher matcher;

    public EmailValidator(){
        pattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
    }

    public boolean validateEmail(String email){
        return pattern.matcher(email).matches();
    }
}
