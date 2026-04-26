package com.decade.practice.users.utils;

public class GenderUtils {

    public static final float MALE = 1;
    public static final float FEMALE = 2;
    public static final float OTHER = 3;

    public static String inspect(float gender) {
        if (gender == MALE) {
            return "Male";
        } else if (gender == FEMALE) {
            return "Female";
        }
        return "Others";
    }
}
