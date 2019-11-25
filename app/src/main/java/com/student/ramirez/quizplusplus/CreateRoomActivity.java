package com.student.ramirez.quizplusplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Random;

public class CreateRoomActivity extends AppCompatActivity {
    private Button btnBack, btnCancel, btnNextCreate;
    private EditText etRoomID, etRoomTitle, etRoomPassword, etRoomSize;
    private RadioGroup rgRoomType;
    DatabaseReference mDatabaseRoom;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        etRoomID = (EditText) findViewById(R.id.etRoomID);
        etRoomTitle = (EditText) findViewById(R.id.etRoomTitle);
        etRoomPassword = (EditText) findViewById(R.id.etRoomPassword);
        etRoomSize = (EditText) findViewById(R.id.etRoomSize);
        rgRoomType = (RadioGroup) findViewById(R.id.rgRoomType);
        mDatabaseRoom = FirebaseDatabase.getInstance().getReference().child("Room");
        progressDialog = new ProgressDialog(this);
        SetRandomQuizID();
        rgRoomType.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId){
                            case R.id.rbPublic:
                                etRoomPassword.setEnabled(false);
                                etRoomPassword.setText("");
                                break;
                            case R.id.rbPrivate:
                                etRoomPassword.setEnabled(true);
                                break;
                            default:
                                etRoomPassword.setEnabled(false);
                                break;
                        }
                    }
                }
        );
        btnNextCreate = (Button) findViewById(R.id.btnNextRoomCreate);
        btnNextCreate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String password = "";
                        String id = etRoomID.getText().toString().trim();
                        String title = etRoomTitle.getText().toString().trim();
                        int selectedbutton = rgRoomType.getCheckedRadioButtonId();
                        RadioButton selected = (RadioButton) findViewById(selectedbutton);
                        String type = selected.getText().toString();
                        switch (selectedbutton){
                            case R.id.rbPublic:
                                password = "";
                                break;
                            case R.id.rbPrivate:
                                password = etRoomPassword.getText().toString().trim();
                                break;
                        }
                        int size = Integer.valueOf(etRoomSize.getText().toString().trim());
                        CreateRoomCreationLog(id, title, type, password, size);
                        Intent intent = new Intent(CreateRoomActivity.this, CreateQuizActivity.class);
                        intent.putExtra("ROOMID", id);
                        intent.putExtra("ROOMTITLE", title);
                        intent.putExtra("ROOMTYPE", type);
                        intent.putExtra("ROOMPASSWORD", password);
                        intent.putExtra("ROOMSIZE", size);
                        intent.putExtra("fromRoomCreate", true);
                        startActivity(intent);
                    }
                }
        );
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(CreateRoomActivity.this, QuizOnlineActivity.class));
                    }
                }
        );
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(CreateRoomActivity.this, QuizOnlineActivity.class));
                    }
                }
        );
    }
    public void SetRandomQuizID(){
        Random random = new Random();
        final int number = random.nextInt(2000 - 1000) + 1000;
        Log.e("GETROOMID", String.valueOf(number));
        mDatabaseRoom.child(String.valueOf(number)).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Log.e("isAvailable?", "ID: "+number+" is not available.");
                            progressDialog.setMessage("Room ID already exists. Generating a new ID ....");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            Random random = new Random();
                            int newid = random.nextInt(2000 - 1000) + 1000;
                            etRoomID.setText(newid);
                            progressDialog.dismiss();
                        } else {
                            Log.e("isAvailable?", "ID: "+number+" is available.");
                            etRoomID.setText(String.valueOf(number));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }
    public void CreateRoomCreationLog(String id, String title, String type, String password, int size){
        Log.e("ROOMID", id);
        Log.e("ROOMTITLE", title);
        Log.e("ROOMTYPE", type);
        Log.e("ROOMPASSWORD", password);
        Log.e("ROOMSIZE", String.valueOf(size));
    }
}
