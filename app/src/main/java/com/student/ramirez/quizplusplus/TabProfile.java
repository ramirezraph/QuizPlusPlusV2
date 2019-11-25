package com.student.ramirez.quizplusplus;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabProfile extends Fragment {

    private Button btnSignOut, btnUpdateInfo, btnMyQuizzes, btnCreateAccountAdmin, btnRecords;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference NameRef, EmailRef, PictureRef, TypeRef, mDatabase;
    private StorageReference mStorage;
    private TextView txtUserFullname, txtUserEmail;
    private CircleImageView imgProfilePicture;
    private String name, email, type;

    public TabProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_tab_profile, container, false);
        mStorage = FirebaseStorage.getInstance().getReference();

        txtUserFullname = (TextView) view.findViewById(R.id.txtUserFullname);
        txtUserEmail = (TextView) view.findViewById(R.id.txtUserEmail);
        imgProfilePicture = (CircleImageView) view.findViewById(R.id.imgProfilePicture);
        btnMyQuizzes = (Button) view.findViewById(R.id.btnMyQuizzes);
        btnRecords = (Button) view.findViewById(R.id.btnRecords);


        // Get the name and email of the current user
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        NameRef = FirebaseDatabase.getInstance().getReference().child("Accounts").child(user.getUid()).child("Name");
        NameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue().toString();
                txtUserFullname.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        EmailRef = FirebaseDatabase.getInstance().getReference().child("Accounts").child(user.getUid()).child("Email");
        EmailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email = dataSnapshot.getValue().toString();
                txtUserEmail.setText(email);
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
                if (type.equals("Admin")){
                    btnRecords.setVisibility(View.VISIBLE);
                    btnCreateAccountAdmin.setVisibility(View.VISIBLE);
                    btnMyQuizzes.setVisibility(View.VISIBLE);
                } else if (type.equals("Teacher")){
                    btnRecords.setVisibility(View.VISIBLE);
                    btnMyQuizzes.setVisibility(View.VISIBLE);
                    btnCreateAccountAdmin.setVisibility(View.INVISIBLE);
                } else if (type.equals("Student")){
                    btnRecords.setVisibility(View.GONE);
                    btnCreateAccountAdmin.setVisibility(View.GONE);
                    btnMyQuizzes.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        PictureRef = FirebaseDatabase.getInstance().getReference().child("Accounts").child(user.getUid()).child("account_picture");
        PictureRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String account_picture = dataSnapshot.getValue().toString();
                            if (!TextUtils.isEmpty(account_picture)){
                                Picasso.with(getContext())
                                        .load(account_picture)
                                        .error(R.drawable.defaultmale)
                                        .into(imgProfilePicture);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        btnMyQuizzes.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), MyQuizzesActivity.class));
                    }
                }
        );

        btnSignOut = (Button) view.findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finishAffinity();
                        mFirebaseAuth.signOut();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);

                    }
                }
        );

        btnUpdateInfo = (Button) view.findViewById(R.id.btnUpdateInfo);
        btnUpdateInfo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), UpdateInfoActivity.class);
                        startActivity(intent);
                    }
                }
        );
        btnCreateAccountAdmin = (Button) view.findViewById(R.id.btnCreateAccountAdmin);
        btnCreateAccountAdmin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), CreateAccountActivity.class);
                        startActivity(intent);
                    }
                }
        );
        btnRecords.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), RecordActivity.class);
                        startActivity(intent);
                    }
                }
        );

        return view;
    }

}
