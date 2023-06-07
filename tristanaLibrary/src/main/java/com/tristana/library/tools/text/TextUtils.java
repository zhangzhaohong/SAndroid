package com.tristana.library.tools.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    public static boolean checkPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^1[0-9]{10}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static boolean checkEmpty(String content) {
        return content != null && !content.equals("");
    }

}
