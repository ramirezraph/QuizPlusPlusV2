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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateAccountActivity extends AppCompatActivity {

    private Button btnBack, btnRegister;
    private EditText etFullName, etBirthdate, etEmail, etPassword, etConPassword;
    private RadioGroup rgType;
    private RadioButton rbType;
    private ProgressDialog progressDialog;

    // Firebase
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Accounts");

        // Widgets
        etFullName = (EditText) findViewById(R.id.etFullName);
        etBirthdate = (EditText) findViewById(R.id.etBirthdate);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etRoomPassword);
        etConPassword = (EditText) findViewById(R.id.etConPassword);
        rgType = (RadioGroup) findViewById(R.id.rgType);

        progressDialog = new ProgressDialog(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        //startActivity(intent);
                    }
                }
        );
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String name = etFullName.getText().toString().trim();
                        final String birthdate = etBirthdate.getText().toString().trim();
                        final String email = etEmail.getText().toString().trim();
                        final String password = etPassword.getText().toString().trim();
                        final String conpassword = etConPassword.getText().toString().trim();

                        if (TextUtils.isEmpty(name)){
                            ShowMessageDialog("Failed: Empty Text", "Please fill out the form completely");
                            return; // stop the function if empty
                        }
                        if (TextUtils.isEmpty(birthdate)){
                            ShowMessageDialog("Failed: Empty Text", "Please fill out the form completely");
                            return; // stop the function if empty
                        }
                        if (TextUtils.isEmpty(email)){
                            ShowMessageDialog("Failed: Empty Text", "Please fill out the form completely");
                            return; // stop the function if empty
                        }
                        if (TextUtils.isEmpty(password)){
                            ShowMessageDialog("Failed: Empty Text", "Please fill out the form completely");
                            return; // stop the function if empty
                        }
                        if (TextUtils.isEmpty(conpassword)){
                            ShowMessageDialog("Failed: Empty Text", "Please fill out the form completely");
                            return; // stop the function if empty
                        }

                        // get selected gender
                        int selectedType = rgType.getCheckedRadioButtonId();
                        rbType = (RadioButton) findViewById(selectedType);

                        if (selectedType == -1){
                            ShowMessageDialog("Registration Failed", "Please select what type of user you want to create");
                            return; // stop the function if empty
                        }

                        final String type = rbType.getText().toString();

                        if (!password.equals(conpassword)){
                            ShowMessageDialog("Registration Failed", "Password does not match");
                            return;
                        }

                        progressDialog.setMessage("Registering");
                        progressDialog.show();

                        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            String key = task.getResult().getUser().getUid();
                                            SaveUserData(key, name, birthdate, email, type);
                                        } else {
                                            // User failed to register
                                            ShowMessageDialogInvalid("Registration Failed", "Something isn't right. Please check the following:");
                                            progressDialog.dismiss();
                                        }
                                    }
                                });

                    }
                }
        );

    }
    public void SaveUserData(String key, String name, String birthdate, String email, String gender){
        // Save
        final User user = new User(name, birthdate, email, gender);

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

        // Used to store multiple value in one entry
        HashMap<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("Name", user.getFullName());
        dataMap.put("Birthdate", user.getBirthdate());
        dataMap.put("Email", user.getEmail());
        dataMap.put("Type", user.getType());
        mDatabaseRef.child(key).setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CreateAccountActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    ShowMessageDialogSuccess("Registered Successfully", "The account: "+user.getEmail()+" is now in use");
                    // Stops the progress dialog
                    progressDialog.dismiss();
                } else {
                    // User failed to register
                    ShowMessageDialogInvalid("Registration Failed", "Something isn't right. Please check the following:");
                    progressDialog.dismiss();
                }
            }
        });
    }
    public void ShowMessageDialogSuccess(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT); //THEME_DEVICE_DEFAULT_DARK
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Return the user to Login Activity
                Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
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
    public void ShowMessageDialogInvalid(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT); //THEME_DEVICE_DEFAULT_DARK
        builder.setCancelable(true);
        builder.setTitle(title);
        StringBuffer buffer = new StringBuffer();
        buffer.append(message +"\n");
        buffer.append("- Invalid Email Address \n");
        buffer.append("- Invalid Birthdate \n");
        buffer.append("- Email Address Already in Use");
        builder.setMessage(buffer);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
