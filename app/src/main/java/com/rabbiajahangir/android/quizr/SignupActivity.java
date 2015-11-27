package com.rabbiajahangir.android.quizr;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class SignupActivity extends AppCompatActivity {
    private final static String TAG = "SignupActivity";

    private Button mSignupBtn;
    private Button mLoginScreenButton;
    private EditText emailSignEdt;
    private EditText passSignEdt;
    private EditText confPassSignEdt;
    Context thisContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mSignupBtn = (Button) findViewById(R.id.signupBtn);
        mLoginScreenButton = (Button) findViewById(R.id.loginScreenBtn);
        emailSignEdt = (EditText) findViewById(R.id.emailTextSignup);
        passSignEdt = (EditText) findViewById(R.id.passTextSignup);
        confPassSignEdt = (EditText) findViewById(R.id.confPassTextSignup);

        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isConnected = ConnectivityChecker.checkConnectivity(v, thisContext);
                if (isConnected) {
                    final String email = emailSignEdt.getText().toString().trim();
                    final String password = passSignEdt.getText().toString().trim();
                    final String confPassword = confPassSignEdt.getText().toString().trim();
                    InputValidation.removeAllErrors(emailSignEdt, passSignEdt, confPassSignEdt);
                    if (email.isEmpty()) {
                        InputValidation.errFieldEmpty(emailSignEdt);
                    } else if (!InputValidation.isValidEmail(email)) {
                        InputValidation.errInvalidEmail(emailSignEdt);
                    } else if (password.isEmpty()) {
                        InputValidation.errFieldEmpty(passSignEdt);
                    } else if (!InputValidation.isValidPassword(password)) {
                        InputValidation.errInvalidPassword(passSignEdt);
                    } else if (!InputValidation.isValidPassword(confPassword)) {
                        InputValidation.errInvalidPassword(confPassSignEdt);
                    } else if (!InputValidation.isSamePassword(password, confPassword)) {
                        InputValidation.errNotSamePassword(passSignEdt, confPassSignEdt);
                    } else {
                        Log.d(TAG, "inside signup request else");
                        ServerRequest requestTask = new ServerRequest(new ServerRequest.TaskHandler() {
                            @Override
                            public boolean task() {
                                Server comm = new Server();
                                return comm.postSignup(email, password);
                            }
                        });
                        try {
                            boolean authenticated = requestTask.execute().get();
                            if (authenticated != true) {
                                Toast.makeText(SignupActivity.this, "Couldn't Signup, user already exists", Toast.LENGTH_SHORT).show();
                            } else {
//                                Toast.makeText(SignupActivity.this, "Signed Up", Toast.LENGTH_SHORT).show();
                                Intent categoriesActivity = new Intent(v.getContext(), CategoriesActivity.class);
                                startActivity(categoriesActivity);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    ConnectivityChecker.makeNotConnectedToast(thisContext);
                }


            }
        });
        mLoginScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivity = new Intent(v.getContext(), QuizrActivity.class);
                startActivity(loginActivity);
            }
        });

    }

}
