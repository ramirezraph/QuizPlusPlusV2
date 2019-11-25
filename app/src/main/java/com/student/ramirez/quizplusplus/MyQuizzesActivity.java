package com.student.ramirez.quizplusplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class MyQuizzesActivity extends AppCompatActivity {

    RecyclerView recyclerOwnedQuiz;
    DatabaseReference mDatabase, TypeRef;
    FirebaseAuth mFirebaseAuth;
    private String type;
    // Widgets
    private Button btnBack, btnCreateQuiz;
    private Dialog MyQuizDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_quizzes);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Quizzes");
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MyQuizzesActivity.this, MainActivity.class));
                    }
                }
        );
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
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
        btnCreateQuiz = (Button) findViewById(R.id.btnCreateQuiz);
        btnCreateQuiz.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MyQuizzesActivity.this, CreateQuizActivity.class);
                        intent.putExtra("fromRoomCreate", false);
                        intent.putExtra("TYPE", type);
                        startActivity(intent);
                    }
                }
        );
        // Creating a Recycler View for Quizzes
        recyclerOwnedQuiz = (RecyclerView) findViewById(R.id.recyclerOwnedQuiz);
        recyclerOwnedQuiz.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerOwnedQuiz.setLayoutManager(linearLayoutManager);
        MyQuizDialog = new Dialog(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseLoadMyQuizzes();
    }
    public void FirebaseLoadMyQuizzes(){
        // Select Query
        Query firebaseSearchQuery = mDatabase.orderByChild("quiz_owner").equalTo(mFirebaseAuth.getCurrentUser().getEmail());
        FirebaseRecyclerAdapter<Quiz, MyQuizzesActivity.OwnedQuizViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Quiz, MyQuizzesActivity.OwnedQuizViewHolder>(
                Quiz.class,
                R.layout.quiz_list_item_owned,
                MyQuizzesActivity.OwnedQuizViewHolder.class,
                firebaseSearchQuery
        ) {
            @Override
            protected void populateViewHolder(MyQuizzesActivity.OwnedQuizViewHolder viewHolder, Quiz model, final int position) {
                final String key = getRef(position).getKey();
                viewHolder.setQuizTitle(model.getQuiz_title());
                viewHolder.setQuizDescription(model.getQuiz_desc());
                viewHolder.setQuizID(model.getQuiz_id());
                ImageButton btnEditInformation = (ImageButton) viewHolder.mView.findViewById(R.id.btnEditInformation);
                btnEditInformation.setOnClickListener(
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
                                                DisplayMyQuizDialog(title, key, desc, quiztype, size, timer);
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Toast.makeText(MyQuizzesActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
                            }
                        }
                );
            }
        };
        recyclerOwnedQuiz.setAdapter(firebaseRecyclerAdapter);
    }
    public void DisplayMyQuizDialog(final String title, final String id, final String desc, final String type, String size, final String timer){
        final TextView txtTitle, txtDesc, txtNoQuiz, txtType, txtQuizID, txtTimerLimit;
        final Button btnEditQuiz, btnDeleteQuiz, btnClose;
        // Layout
        MyQuizDialog.setContentView(R.layout.dialog_my_quiz_info);
        // Widgets
        txtTitle = (TextView) MyQuizDialog.findViewById(R.id.txtTitle);
        txtDesc = (TextView) MyQuizDialog.findViewById(R.id.txtQuizDesc);
        txtNoQuiz = (TextView) MyQuizDialog.findViewById(R.id.txtNoQuestion);
        txtType = (TextView) MyQuizDialog.findViewById(R.id.txtQuizType);
        txtQuizID = (TextView) MyQuizDialog.findViewById(R.id.txtQuizID);
        txtTimerLimit = (TextView) MyQuizDialog.findViewById(R.id.txtTimerLimit);
        btnEditQuiz = (Button) MyQuizDialog.findViewById(R.id.btnEditQuiz);
        btnDeleteQuiz = (Button) MyQuizDialog.findViewById(R.id.btnDeleteQuiz);
        btnClose = (Button) MyQuizDialog.findViewById(R.id.btnCloseDialog);
        // Events / Values
        txtTitle.setText(title);
        txtQuizID.setText(id);
        txtDesc.setText(desc);
        txtType.setText(type);
        txtNoQuiz.setText(size);
        txtTimerLimit.setText(timer);
        btnEditQuiz.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MyQuizzesActivity.this, EditQuizActivity.class);
                        intent.putExtra("QUIZID", id);
                        intent.putExtra("QUIZTITLE", title);
                        intent.putExtra("QUIZDESC", desc);
                        intent.putExtra("QUIZTYPE", type);
                        intent.putExtra("QUIZTIMER", timer);
                        intent.putExtra("FROM", "EDIT");
                        startActivity(intent);
                    }
                }
        );
        btnDeleteQuiz.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfirmDeleteDialog(title, id);
                    }
                }
        );
        btnClose.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyQuizDialog.dismiss();
                    }
                }
        );
        MyQuizDialog.show();
    }
    public void ConfirmDeleteDialog(String title, final String id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage("You are deleting a quiz. Please confirm.");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyQuizDialog.dismiss();
                mDatabase.child(id).removeValue();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    public static class OwnedQuizViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public OwnedQuizViewHolder(View itemView) {
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
