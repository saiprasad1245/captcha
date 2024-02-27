package com.captcha.utils;

import org.apache.commons.lang3.RandomStringUtils;

import com.captcha.models.RandomString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StringGenerator {

    private static final String LOWER_CASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_CASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!\"#$%&'()*+,-./:;<=>?@[\\]^`{|}~";

    public List<RandomString> generate(RandomStringOptions options) {

        String allowedCharacters = "";
        if (options.upperCase) {
            allowedCharacters += UPPER_CASE_CHARS;
        }
        if (options.lowerCase) {
            allowedCharacters += LOWER_CASE_CHARS;
        }
        if (options.digits) {
            allowedCharacters += DIGITS;
        }
        if (options.specialCharacters) {
            allowedCharacters += SPECIAL_CHARACTERS;
        }

        List<RandomString> result = new ArrayList<>();

        for (int i = 0; i < options.length; i++) {
        	
            String randomString = RandomStringUtils.random(options.size, allowedCharacters);
            RandomString rs = new RandomString(randomString);
            rs.setCreatedTime(new Date());
            //randomString.setCurrentTime(null);
            rs.setTime(60);
            result.add(rs);
        }

        return result;
    }
}
