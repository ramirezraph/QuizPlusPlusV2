package com.student.ramirez.quizplusplus;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    RecyclerView recyclerViewQuiz;

    DatabaseReference mDatabase, TypeRef;
    private String type;

    // Widgets
    private Button btnBack, btnCreateQuiz;
    private Button btnSearchQuiz;
    private EditText etSearchQuiz;
    private Dialog QuizDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        btnCreateQuiz = (Button) findViewById(R.id.btnCreateQuiz);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TypeRef = FirebaseDatabase.getInstance().getReference().child("Accounts").child(user.getUid()).child("Type");
        TypeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                type = dataSnapshot.getValue().toString();
                if (type.equals("Admin")){
                    btnCreateQuiz.setVisibility(View.VISIBLE);
                } else if (type.equals("Teacher")){
                    btnCreateQuiz.setVisibility(View.VISIBLE);
                } else if (type.equals("Student")){
                    btnCreateQuiz.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Quizzes");
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(QuizActivity.this, MainActivity.class));
                    }
                }
        );

        btnCreateQuiz.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(QuizActivity.this, CreateQuizActivity.class);
                        startActivity(intent);
                    }
                }
        );


        // Search Function
        etSearchQuiz = (EditText) findViewById(R.id.etSearchQuiz);
        btnSearchQuiz = (Button) findViewById(R.id.btnSearchQuiz);
        btnSearchQuiz.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String stringToSearch = etSearchQuiz.getText().toString();
                        FirebaseUserSearch(stringToSearch);
                    }
                }
        );

        // Creating a Recycler View for Quizzes
        recyclerViewQuiz = (RecyclerView) findViewById(R.id.recyclerViewQuiz);
        recyclerViewQuiz.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewQuiz.setLayoutManager(linearLayoutManager);

        QuizDialog = new Dialog(this);


    }

    // Set the Quiz Dialog
    public void ShowQuizDialog(final String title, final String id, String desc, String owner, final String type, String size, String timer){
        final TextView txtTitle, txtDesc, txtOwner, txtNoQuiz, txtType, txtQuizID, txtTimerLimit;
        Button btnPlay, btnClose;

        // Layout
        QuizDialog.setContentView(R.layout.dialog_quiz_info);

        // Widgets
        txtTitle = (TextView) QuizDialog.findViewById(R.id.txtTitle);
        txtDesc = (TextView) QuizDialog.findViewById(R.id.txtQuizDesc);
        txtOwner = (TextView) QuizDialog.findViewById(R.id.txtQuizCreator);
        txtNoQuiz = (TextView) QuizDialog.findViewById(R.id.txtNoQuestion);
        txtType = (TextView) QuizDialog.findViewById(R.id.txtQuizType);
        txtQuizID = (TextView) QuizDialog.findViewById(R.id.txtQuizID);
        txtTimerLimit = (TextView) QuizDialog.findViewById(R.id.txttimertimer);

        btnPlay = (Button) QuizDialog.findViewById(R.id.btnPlayQuizNow);
        btnClose = (Button) QuizDialog.findViewById(R.id.btnCloseDialog);

        // Events / Values
        txtTitle.setText(title);
        txtQuizID.setText(id);
        txtDesc.setText(desc);
        txtOwner.setText(owner);
        txtType.setText(type);
        txtNoQuiz.setText(size);
        txtTimerLimit.setText(timer);

        btnPlay.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Begin play = Intent
                        switch (type){
                            case "True or False":
                                Intent tfintent = new Intent(QuizActivity.this, QuizPlayingTFActivity.class);
                                tfintent.putExtra("QUIZTITLE", title);
                                tfintent.putExtra("QUIZKEY", id);
                                startActivity(tfintent);
                                break;
                            case "Multiple Choice":
                                Intent mcintent = new Intent(QuizActivity.this, QuizPlayingMCActivity.class);
                                mcintent.putExtra("QUIZTITLE", title);
                                mcintent.putExtra("QUIZKEY", id);
                                startActivity(mcintent);
                                break;
                            case "Identification":
                                break;
                            case "None":
                                Toast.makeText(QuizActivity.this, "Quiz Type not recognized", Toast.LENGTH_LONG).show();
                                break;
                        }


                    }
                }
        );

        btnClose.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close the dialog
                        QuizDialog.dismiss();
                    }
                }
        );

        QuizDialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUserSearch("");
    }
    public void FirebaseUserSearch(String searchText){

        // Search Query
        Query firebaseSearchQuery = mDatabase.orderByChild("quiz_title").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Quiz, QuizViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Quiz, QuizViewHolder>(
                Quiz.class,
                R.layout.quiz_list_item,
                QuizViewHolder.class,
                firebaseSearchQuery
        ) {
            @Override
            protected void populateViewHolder(QuizViewHolder viewHolder, Quiz model, final int position) {
                final String key = getRef(position).getKey();
                viewHolder.setQuizTitle(model.getQuiz_title());
                viewHolder.setQuizDescription(model.getQuiz_desc());
                viewHolder.setQuizID(model.getQuiz_id());

                Button btnPlay = (Button) viewHolder.mView.findViewById(R.id.btnPlayQuiz);
                btnPlay.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e("QuizKey", key);
                                Log.e("ClickEvent", "Button Clicked At: " +position);
                                mDatabase.child(key).addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                                String quiztype = "";
                                                String title = map.get("quiz_title").toString();
                                                String desc = map.get("quiz_desc").toString();
                                                String owner = map.get("quiz_owner").toString();
                                                String type = map.get("quiz_type").toString();
                                                String size = map.get("quiz_questionsize").toString();
                                                String timer = map.get("quiz_timer").toString();
                                                switch (type){
                                                    case "1":
                                                        quiztype = "True or False";
                                                        break;
                                                    case "2":
                                                        quiztype = "Multiple Choice";
                                                        break;
                                                    case "3":
                                                        quiztype = "Identification";
                                                        break;
                                                    default:
                                                        quiztype = "None";
                                                        break;
                                                }
                                                ShowQuizDialog(title, key, desc, owner, quiztype, size, timer);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Toast.makeText(QuizActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
                                // Show Dialog

                            }
                        }
                );
            }
        };
        recyclerViewQuiz.setAdapter(firebaseRecyclerAdapter);
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public QuizViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setQuizID(String id){
            TextView txtQN = (TextView) mView.findViewById(R.id.txtQuestion);
            txtQN.setText(id);

        }
        public void setQuizTitle(String title){
            TextView txtQuizTitle = (TextView) mView.findViewById(R.id.txtScore);
            txtQuizTitle.setText(title);
        }
        public void setQuizDescription(String desc){
            TextView txtQuizDesc = (TextView) mView.findViewById(R.id.txtQuizDesc);
            txtQuizDesc.setText(desc);
        }

    }

}
