package com.student.ramirez.quizplusplus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateInfoActivity extends AppCompatActivity {


    // Firebase
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mFirebaseRef, mFirebaseRefUpdate;

    private ViewPager mViewPager;
    private Button btnBack, btnUpdateInfo;
    private RadioGroup rguType;
    private RadioButton rbType, rbStudent, rbTeacher;
    private EditText etuFullName, etuBirthdate, etuEmail, etuPassword, etuConPassword, etuOldPassword;
    private ProgressDialog progressDialog;


    // Profile Picture
    private CircleImageView imgProfile;
    private Button btnPickPhoto;
    private static final int GALLERY_REQUEST = 1;
    private Uri imageUri = null;
    private StorageReference mStorageImage;
    private String downloadProfileUrl;

    private String typetype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        // Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        mFirebaseRef = FirebaseDatabase.getInstance().getReference().child("Accounts").child(user.getUid());
        mFirebaseRefUpdate = FirebaseDatabase.getInstance().getReference();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("account_picture");

        // Widgets
        etuFullName = (EditText) findViewById(R.id.etuFullName);
        etuBirthdate = (EditText) findViewById(R.id.etuBirthdate);
        etuEmail = (EditText) findViewById(R.id.etuEmail);
        etuPassword = (EditText) findViewById(R.id.etuPassword);
        etuConPassword = (EditText) findViewById(R.id.etuConPassword);
        etuOldPassword = (EditText) findViewById(R.id.etuOldPassword);
        btnUpdateInfo = (Button) findViewById(R.id.btnUpdateInfo);
        btnBack = (Button) findViewById(R.id.btnBack);
        rguType = (RadioGroup) findViewById(R.id.rguType);
        rbStudent = (RadioButton) findViewById(R.id.rbStudent);
        rbTeacher = (RadioButton) findViewById(R.id.rbTeacher);
        imgProfile = (CircleImageView) findViewById(R.id.imgProfilePicture);
        btnPickPhoto = (Button) findViewById(R.id.btnUpdatePicture);

        progressDialog = new ProgressDialog(this);

        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map)dataSnapshot.getValue();
                String name = map.get("Name");
                String birthdate = map.get("Birthdate");
                String email = map.get("Email");
                String type = map.get("Type");
                String downloadUri = map.get("account_picture");

                typetype = map.get("Type");

                // Display Account Information
                etuFullName.setText(name);
                etuBirthdate.setText(birthdate);
                etuEmail.setText(email);
                downloadProfileUrl = downloadUri;

                if (type.equals("Student")){
                    rbStudent.setChecked(true);
                } else if (type.equals("Teacher")){
                    rbTeacher.setChecked(true);
                }
                rbTeacher.setEnabled(false);
                rbStudent.setEnabled(false);
                // Profile Picture
                if (!TextUtils.isEmpty(downloadUri)){
                    DisplayPicture(Uri.parse(downloadUri));
                } else {
                    DisplayPicture(Uri.parse("https://firebasestorage.googleapis.com/v0/b/quizplusplusv2.appspot.com/o/account_picture%2FtxOAYudv7gf0hLsuEnl2M62tztI2?alt=media&token=53b26722-27ba-44e2-8faa-5255dcad1921"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnPickPhoto.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent();
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, GALLERY_REQUEST);
                    }
                }
        );

        ;

        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        btnUpdateInfo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(etuPassword.getText().toString()) && !TextUtils.isEmpty(etuConPassword.getText().toString()) && !TextUtils.isEmpty(etuOldPassword.getText().toString())){
                            if (etuPassword.getText().toString().equals(etuConPassword.getText().toString())){
                                String oldPassword = etuOldPassword.getText().toString().trim();
                                String newPassword = etuPassword.getText().toString().trim();
                                UpdateAccountInformation();
                                SetupProfilePicture();
                                ChangePassword(mFirebaseAuth.getCurrentUser().getEmail(), oldPassword, newPassword);
                            } else {
                                Toast.makeText(UpdateInfoActivity.this, "Password do not match", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            UpdateAccountInformation();
                            SetupProfilePicture();
                        }

                    }
                }
        );
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            imageUri = data.getData();
            Log.e("IMAGEURI", imageUri.toString());
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imgProfile.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Failed to set image");
                builder.setMessage(error.toString());
                builder.setCancelable(true);
                builder.show();
            }
        }
    }

    public void DisplayPicture(Uri imageUri){
        Picasso.with(getApplicationContext())
                .load(imageUri)
                .error(R.drawable.defaultmale)
                .into(imgProfile);
    }

    public void SetupProfilePicture(){
        if (imageUri != null){
            StorageReference filePath = mStorageImage.child(mFirebaseAuth.getCurrentUser().getUid()); //imageUri.getLastPathSegment()
            filePath.putFile(imageUri).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUri = taskSnapshot.getDownloadUrl().toString();
                            mFirebaseRef.child("account_picture").setValue(downloadUri);
                        }

                    }
            );
            filePath.putFile(imageUri).addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateInfoActivity.this, "Something wen't wrong.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

    public void UpdateAccountInformation(){
        final String newname = etuFullName.getText().toString().trim();
        String newbirthdate = etuBirthdate.getText().toString().trim();
        final String email = etuEmail.getText().toString().trim();
        String oldpassword = etuOldPassword.getText().toString().trim();

        String newtype = "";
        switch (typetype){
            case "Admin":
                 newtype = "Admin";
                break;
            case "Teacher":
                newtype = "Teacher";
                break;
            case "Student":
                newtype = "Student";
                break;
        }
        progressDialog.setMessage("Updating ...");
        progressDialog.show();

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        // Used to store multiple value in one entry
        HashMap<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("Name", newname);
        dataMap.put("Email", email);
        dataMap.put("Birthdate", newbirthdate);
        dataMap.put("Type", newtype);
        dataMap.put("account_picture", downloadProfileUrl);
        mFirebaseRefUpdate.child("Accounts").child(user.getUid()).setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                    // Stops the progress dialog
                    progressDialog.dismiss();
                    //startActivity(new Intent(UpdateInfoActivity.this, MainActivity.class));

                } else {
                    // User failed to update
                    Toast.makeText(getApplicationContext(), "Update failed, Please try again.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

    }
    public void ChangePassword(String email, String oldPassword, final String newPassword){
        final FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
        user.reauthenticate(credential).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(UpdateInfoActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                        etuOldPassword.setText("");
                                        etuPassword.setText("");
                                        etuConPassword.setText("");
                                    } else {
                                        Toast.makeText(UpdateInfoActivity.this, "Password update failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(UpdateInfoActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}
