package com.student.ramirez.quizplusplus;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabQuizzes extends Fragment {

    private Button btnQuizArchive;
    private Button btnOnlineQuiz;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference TypeRef;
    private String type;

    public TabQuizzes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_quizzes, container, false);

        // Get the name and email of the current user
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        btnQuizArchive = (Button) view.findViewById(R.id.btnQuizArchive);
        btnQuizArchive.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), QuizActivity.class);
                        startActivity(intent);
                    }
                }
        );

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

        btnOnlineQuiz = (Button) view.findViewById(R.id.btnOnlineQuiz);
        btnOnlineQuiz.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), QuizOnlineActivity.class);
                        startActivity(intent);
                    }
                }
        );

        return view;
    }

}
