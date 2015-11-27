package com.rabbiajahangir.android.quizr;

import android.content.Context;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Umer on 11/25/2015.
 */
public class InputValidation {
    private final static int PASS_LEN = 6;

    public static void makeFieldEmptyToast(Context context, String fieldName) {
        Toast toast = Toast.makeText(context, "Please enter your " + fieldName, Toast.LENGTH_SHORT);
        toast.setGravity(0, Gravity.CENTER | Gravity.LEFT, 0);
        toast.show();
    }

    public static boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(CharSequence pass) {
        if (pass.length() < 6) {
            return false;
        }
        return true;
    }

    public static void errInvalidPassword(EditText editText) {
        editText.setError("Your password should be atleast " + PASS_LEN + " long!");
    }

    public static void errInvalidEmail(EditText editText) {
        editText.setError("Your email is in incorrect format!");
    }

    public static void errFieldEmpty(EditText editText) {
        editText.setError("This field cannot be left empty!");
    }

    public static boolean isSamePassword(String pass1, String pass2) {
        if (pass1.equals(pass2) || pass2.equals(pass1))
            return true;
        return false;
    }

    public static void errNotSamePassword(EditText editText1, EditText editText2) {
        editText1.setError("Passwords do not match");
        editText2.setError("Passwords do not match");
    }

    public static void removeAllErrors(EditText... editText) {
        for (EditText edt : editText) {
            edt.setError(null);
        }

    }

}
