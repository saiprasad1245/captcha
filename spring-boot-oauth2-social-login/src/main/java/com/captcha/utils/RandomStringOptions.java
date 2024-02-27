package com.captcha.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RandomStringOptions {

    public RandomStringOptions() { }

    public RandomStringOptions(Integer length, Integer size, Boolean upperCase, Boolean lowerCase, Boolean digits, Boolean specialCharacters) {
        this.length = length;
        this.size = size;
        this.upperCase = upperCase;
        this.lowerCase = lowerCase;
        this.digits = digits;
        this.specialCharacters = specialCharacters;
    }

    @JsonProperty("length")
    public Integer length;

    @JsonProperty("size")
    public Integer size;

    @JsonProperty("upperCase")
    public Boolean upperCase;

    @JsonProperty("lowerCase")
    public Boolean lowerCase;

    @JsonProperty("digits")
    public Boolean digits;

    @JsonProperty("specialCharacters")
    public Boolean specialCharacters;
}
