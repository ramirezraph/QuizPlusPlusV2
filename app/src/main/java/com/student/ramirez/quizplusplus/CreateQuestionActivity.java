package com.student.ramirez.quizplusplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateQuestionActivity extends AppCompatActivity {

    List<Question> questionList;
    private Button btnAddQuestion, btnBack, btnFinish;
    private TextView txtQuizTitle, txtQN;
    private int quizType;
    private QuestionAdapter questionAdapter;
    private ProgressDialog progressDialog;

    private DatabaseReference QuizRef, QuestionRef, TypeRef;
    private FirebaseAuth mFirebaseAuth;

    protected static int numberquestion = 0;
    private String descExtra;
    private String quizTimer;
    private String type;

    // Dialogs
    private Dialog MultipleChoiceInstruction;
    private Dialog TrueFalseInstruction;

    // Room
    private String RoomID;
    private String RoomTitle;
    private String RoomType;
    private String RoomPassword;
    private int RoomSize;
    private boolean fromRoomCreate;

    private DatabaseReference NameRef, mDatabaseRoom;
    private String name;

    QuizOnlineActivity quizOnlineActivity;

    public CreateQuestionActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        // Firebase
        QuizRef = FirebaseDatabase.getInstance().getReference().child("Quizzes");
        QuestionRef = FirebaseDatabase.getInstance().getReference().child("Quizzes");
        mDatabaseRoom = FirebaseDatabase.getInstance().getReference().child("Room");
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Widgets
        txtQuizTitle = findViewById(R.id.txtScore);
        btnBack = findViewById(R.id.btnBack);
        txtQN = findViewById(R.id.txtQuestion);
        btnFinish = findViewById(R.id.btnFinish);
        progressDialog = new ProgressDialog(this);

        // Setup Dialog Instruction
        MultipleChoiceInstruction = new Dialog(this);
        TrueFalseInstruction = new Dialog(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String from = extras.getString("FROM");
            String titleExtra;
            Log.e("USERFROM", from);
            switch (from){
                case "QuizActivity":
                    // User from Create Quiz
                    fromRoomCreate = false;
                    quizType = extras.getInt("QUIZ_TYPE");
                    titleExtra = extras.getString("QUIZ_TITLE");
                    descExtra = extras.getString("QUIZ_DESC");
                    quizTimer = extras.getString("QUIZ_TIMER");
                    txtQuizTitle.setText(String.valueOf(titleExtra));
                    switch (quizType){
                        case 1:
                            ShowTrueFalseInstruction();
                            break;
                        case 2:
                            ShowMultipleChoiceInstruction();
                            break;
                        case 3:
                            break;
                    }
                    break;
                case "RoomActivity":
                    // User from Create Room
                    fromRoomCreate = true;
                    RoomID = extras.getString("ROOMID");
                    RoomTitle = extras.getString("ROOMTITLE");
                    RoomType = extras.getString("ROOMTYPE");
                    RoomPassword = extras.getString("ROOMPASSWORD");
                    RoomSize = extras.getInt("ROOMSIZE");
                    CreateRoomCreationLog(RoomID, RoomTitle, RoomType, RoomPassword, RoomSize);

                    quizType = extras.getInt("QUIZ_TYPE");
                    titleExtra = extras.getString("QUIZ_TITLE");
                    descExtra = extras.getString("QUIZ_DESC");
                    quizTimer = extras.getString("QUIZ_TIMER");
                    txtQuizTitle.setText(String.valueOf(titleExtra));
                    switch (quizType){
                        case 1:
                            ShowTrueFalseInstruction();
                            break;
                        case 2:
                            ShowMultipleChoiceInstruction();
                            break;
                        case 3:
                            break;
                    }
                    break;

            }
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mFirebaseAuth.getCurrentUser();
        NameRef = FirebaseDatabase.getInstance().getReference().child("Accounts").child(user.getUid()).child("Name");
        NameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        TypeRef = FirebaseDatabase.getInstance().getReference().child("Accounts").child(user.getUid()).child("Type");
        TypeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                type = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Add Question
        questionList = new ArrayList<>();
        final RecyclerView recyclerViewQuestion = (RecyclerView) findViewById(R.id.recyclerViewQuestion);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewQuestion.setItemAnimator(new DefaultItemAnimator());
        recyclerViewQuestion.setHasFixedSize(true);
        recyclerViewQuestion.setLayoutManager(layoutManager);

        questionAdapter = new QuestionAdapter(questionList, new QuestionAdapter.QuestionAdapterListener() {
            @Override
            public void DeleteButtonOnClicked(View view, int position) {
                Log.e("WhichButtonDelete", "Button at position " +position );
                questionAdapter.removeAt(position);
                numberquestion = numberquestion - 1;
                txtQN.setText(String.valueOf(questionList.size()));
            }
        });

        recyclerViewQuestion.setAdapter(questionAdapter);
        // Add
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        btnAddQuestion.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (quizType){
                            case 1: // True or False
                                String choiceA = "True";
                                String choiceB = "False";
                                AddQuestion(questionAdapter.getItemCount(), "", "", quizType, choiceA, choiceB, "", "");
                                break;
                            case 2: // Multiple Choice
                                AddQuestion(questionAdapter.getItemCount(), "", "", quizType, "", "", "", "");
                                break;
                            case 3: // Identification
                                break;
                        }
                        recyclerViewQuestion.smoothScrollToPosition(questionAdapter.getItemCount());
                        recyclerViewQuestion.setItemViewCacheSize(questionAdapter.getItemCount()); // This single line of code saves this entire project
                    }
                }
        );

        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }
        );

        btnFinish.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isSuccess = true;
                        if (questionAdapter.getItemCount() != 0){
                            for (int i = 0; i < questionList.size(); i++){
                                if (questionList.get(i).getAnswer().isEmpty() || questionList.get(i).getQuestion().isEmpty() || questionList.get(i).getAnswer().equals("None")){
                                    isSuccess = false;
                                }
                            }
                            if (isSuccess){
                                progressDialog.setMessage("Finishing ..");
                                progressDialog.setCancelable(true);
                                progressDialog.show();
                                final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                                final String uniqueKey = QuizRef.push().getKey();
                                HashMap<String, String> QuizInformation = new HashMap<String, String>();
                                QuizInformation.put("quiz_title", txtQuizTitle.getText().toString());
                                QuizInformation.put("quiz_desc", descExtra);
                                QuizInformation.put("quiz_owner", currentUser.getEmail());
                                QuizInformation.put("quiz_type", String.valueOf(quizType));
                                QuizInformation.put("quiz_id", String.valueOf(uniqueKey));
                                QuizInformation.put("quiz_questionsize", String.valueOf(questionList.size()));
                                QuizInformation.put("quiz_timer", quizTimer);
                                if (fromRoomCreate){
                                    // User from Room
                                    // Change Reference
                                    QuizRef = FirebaseDatabase.getInstance().getReference().child("OnlineQuizzes");
                                    QuestionRef = FirebaseDatabase.getInstance().getReference().child("OnlineQuizzes");
                                    final DatabaseReference RoomRef = FirebaseDatabase.getInstance().getReference().child("Room");

                                    QuizRef.child(uniqueKey).setValue(QuizInformation).addOnCompleteListener(
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        for (int i = 0; i < questionList.size(); i++)
                                                        {
                                                            Log.e("FinishEvent", String.valueOf(i));
                                                            Question question = questionList.get(i);
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("answer").setValue(question.getAnswer());
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("question").setValue(question.getQuestion());
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("quiz_id").setValue(i);
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("type").setValue(question.getType());
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("choiceA").setValue(question.getChoiceA());
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("choiceB").setValue(question.getChoiceB());
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("choiceC").setValue(question.getChoiceC());
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("choiceD").setValue(question.getChoiceD());
                                                        }

                                                        // Add Room to Database
                                                        Room room = new Room();
                                                        room.setRoomId(RoomID);
                                                        room.setRoomTitle(RoomTitle);
                                                        room.setRoomOwner(currentUser.getEmail());
                                                        room.setRoomPassword(RoomPassword);
                                                        room.setUserListID(uniqueKey);
                                                        room.setRoomQuizID(uniqueKey);
                                                        room.setOwner_name(name);
                                                        switch (RoomType){
                                                            case "Public":
                                                                room.setPrivate(false);
                                                                break;
                                                            case "Private":
                                                                room.setPrivate(true);
                                                                break;
                                                        }
                                                        room.setRoomNumberOfUser(0);
                                                        room.setRoomSize(RoomSize);
                                                        RoomRef.child(RoomID).setValue(room);
                                                        progressDialog.dismiss();

                                                        CreateQuizActivity.createQuizActivity.finish();
                                                        finish();
                                                        Intent intent = new Intent(CreateQuestionActivity.this, QuizOnlineActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(CreateQuestionActivity.this, "Quiz created successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(CreateQuestionActivity.this, "Quiz creation Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                    );
                                } else if (!fromRoomCreate){
                                    // User from Quiz
                                    QuizRef.child(uniqueKey).setValue(QuizInformation).addOnCompleteListener(
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){

                                                        for (int i = 0; i < questionList.size(); i++)
                                                        {
                                                            Log.e("FinishEvent", String.valueOf(i));
                                                            Question question = questionList.get(i);
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("answer").setValue(question.getAnswer());
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("question").setValue(question.getQuestion());
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("quiz_id").setValue(i);
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("type").setValue(question.getType());
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("choiceA").setValue(question.getChoiceA());
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("choiceB").setValue(question.getChoiceB());
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("choiceC").setValue(question.getChoiceC());
                                                            QuestionRef.child(uniqueKey).child("Questions").child(String.valueOf(i)).child("choiceD").setValue(question.getChoiceD());
                                                        }
                                                        progressDialog.dismiss();
                                                        CreateQuizActivity.createQuizActivity.finish();
                                                        finish();
                                                        Intent intent = new Intent(CreateQuestionActivity.this, QuizActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(CreateQuestionActivity.this, "Quiz created successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(CreateQuestionActivity.this, "Quiz creation Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                    );
                                }
                            } else {
                                Toast.makeText(CreateQuestionActivity.this, "Red Question Found.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(CreateQuestionActivity.this, "No Question Found", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

    }

    private void AddQuestion(int id, String question, String answer, int type, String choiceA, String choiceB, String choiceC, String choiceD){
        questionList.add(
                new Question(
                        id,
                        question,
                        answer,
                        type,
                        choiceA,
                        choiceB,
                        choiceC,
                        choiceD
                ));
        numberquestion++;
        txtQN.setText(String.valueOf(questionList.size()));
        Toast.makeText(this, "Question Added", Toast.LENGTH_SHORT).show();
    }
    public void ShowMultipleChoiceInstruction(){

        MultipleChoiceInstruction.setContentView(R.layout.dialog_instruction_mc);
        MultipleChoiceInstruction.show();
        MultipleChoiceInstruction.setCancelable(true);

        TextView btnHide = (TextView) MultipleChoiceInstruction.findViewById(R.id.btnHideInstruction);
        btnHide.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MultipleChoiceInstruction.dismiss();
                    }
                }
        );
    }
    public void ShowTrueFalseInstruction(){

        TrueFalseInstruction.setContentView(R.layout.dialog_instruction_tf);
        TrueFalseInstruction.show();
        TrueFalseInstruction.setCancelable(true);

        TextView btnHide = (TextView) TrueFalseInstruction.findViewById(R.id.btnHideInstruction);
        btnHide.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TrueFalseInstruction.dismiss();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        if (questionList.size() != 0){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmation");
            builder.setMessage("This will delete all questions you created, Do you still want to continue?");
            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CreateQuestionActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else {
            super.onBackPressed();
        }
    }
    public void CreateRoomCreationLog(String id, String title, String type, String password, int size){
        Log.e("ROOMID", id);
        Log.e("ROOMTITLE", title);
        Log.e("ROOMTYPE", type);
        Log.e("ROOMPASSWORD", password);
        Log.e("ROOMSIZE", String.valueOf(size));
    }
}
