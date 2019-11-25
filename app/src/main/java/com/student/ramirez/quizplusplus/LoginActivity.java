package com.student.ramirez.quizplusplus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mFirebaseAuth;
    // Widgets
    private Button btnLogin;
    private TextView btnTxtSignUp;
    private ProgressDialog progressDialog;
    private EditText etEmail, etPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Firebase Authentication
        mFirebaseAuth = FirebaseAuth.getInstance();
        // Widgets
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etRoomPassword);
        progressDialog = new ProgressDialog(this);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = etEmail.getText().toString().trim();
                        String password = etPassword.getText().toString().trim();
                        if (TextUtils.isEmpty(email)){
                            //Toast.makeText(getApplicationContext(), "Please enter your email.", Toast.LENGTH_SHORT).show();
                            ShowMessageDialog("Login Failed", "Please enter your email");
                            return; // stop the function if empty
                        }
                        if (TextUtils.isEmpty(password)){
                            //Toast.makeText(getApplicationContext(), "Please enter your password.", Toast.LENGTH_SHORT).show();
                            ShowMessageDialog("Login Failed", "Please enter your password");
                            return; // stop the function if empty
                        }
                        progressDialog.setMessage("Signing In");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        // Validation Success
                        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            //Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            ShowMessageDialog("Login Failed", "Invalid email or password");
                                        }
                                    }
                                });
                    }
                }
        );

        btnTxtSignUp = (TextView) findViewById(R.id.btnTxtSignUp);
        btnTxtSignUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }
    public void ShowMessageDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT); //THEME_DEVICE_DEFAULT_DARK
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
