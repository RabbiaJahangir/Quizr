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

public class QuizrActivity extends AppCompatActivity {
    private static final String TAG = "QuizrActivity";
    private Button mLoginButton;
    private Button mBecomeMember;
    private EditText emailEdt;
    private EditText passEdt;
    Context thisContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizr);

        mLoginButton = (Button) findViewById(R.id.loginBtn);
        mBecomeMember = (Button) findViewById(R.id.becomeMemberBtn);
        emailEdt = (EditText) findViewById(R.id.emailText);
        passEdt = (EditText) findViewById(R.id.passText);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login Button has been clicked");
                boolean isConnected = ConnectivityChecker.checkConnectivity(v, thisContext);
                InputValidation.removeAllErrors(emailEdt, passEdt);
                if (isConnected) {
                    final String email = emailEdt.getText().toString().trim();
                    final String password = passEdt.getText().toString().trim();
                    if (email.isEmpty()) {
                        InputValidation.errFieldEmpty(emailEdt);
                    } else if (!InputValidation.isValidEmail(email)) {
                        InputValidation.errInvalidEmail(emailEdt);
                    } else if (password.isEmpty()) {
                        InputValidation.errFieldEmpty(passEdt);
                    } else if (!InputValidation.isValidPassword(password)) {
                        InputValidation.errInvalidPassword(passEdt);
                    } else {
                        Log.d(TAG, "inside request else");
                        ServerRequest requestTask = new ServerRequest(new ServerRequest.TaskHandler() {
                            @Override
                            public boolean task() {
                                Server comm = new Server();
                                return comm.postLogin(email, password);
                            }
                        });
                        try {
                            boolean authenticated = requestTask.execute().get();
                            if (authenticated != true) {
                                Toast.makeText(QuizrActivity.this, "invalid login, try again!", Toast.LENGTH_SHORT).show();
                            } else {
//                                Toast.makeText(QuizrActivity.this, "Logged In!", Toast.LENGTH_SHORT).show();
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

        mBecomeMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupActivity = new Intent(v.getContext(), SignupActivity.class);
                startActivity(signupActivity);
            }
        });
    }
}
