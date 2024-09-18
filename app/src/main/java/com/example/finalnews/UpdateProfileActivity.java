package com.example.finalnews;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

   private EditText editTextUpdateName,editTextUpdateEmail,editTextUpdateDoB,editTextUpdateMobile;
   private RadioGroup radioGroupUpdateGender;
   private RadioButton radioButtonUpdateGenderSelected;
   private String textFullName,textEmail,textDoB,textGender,textMobile;
   private FirebaseAuth authProfile;
   private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);

       // getSupportActionBar().setTitle("Update Profile Details");

        editTextUpdateName = findViewById(R.id.editTextUpdateName);
        editTextUpdateEmail = findViewById(R.id.editTextUpdateEmail);
        editTextUpdateDoB = findViewById(R.id.editTextUpdateDoB);
        editTextUpdateMobile = findViewById(R.id.editTextUpdateMobile);
        progressBar = findViewById(R.id.progressBar);
        radioGroupUpdateGender = findViewById(R.id.radioGroupUpdateGender);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //Show Profile Data
        showProfile(firebaseUser);

        //Upload Profile Data
        Button buttonUploadProfilePic = findViewById(R.id.updateProfilePicturebtn);

        buttonUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this,UploadProfilePicActivity.class);
                startActivity(intent);
                finish();
            }
        });


       // setting up date picker on editText
        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Extracting saved dd, m, yyyy into different variables by creating an array delimited by
                String textsDoB[] = textDoB.split("/");

                int day = Integer.parseInt(textsDoB[0]);
                int month = Integer.parseInt(textsDoB[1]) - 1;  // to take care of month inde starting  from 0
                int year = Integer.parseInt(textsDoB[2]);

                DatePickerDialog picker;

                //Date Picker Dialog
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth + "/" + (month + 1) + "/" +year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        // update profile button
        Button UpdateProfilebutton = findViewById(R.id.updateProfilebtn);

        UpdateProfilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });


    }
          // Update profile
    private void updateProfile(FirebaseUser firebaseUser) {

        int selectdGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectdGenderID);

        // Valid mobile number using Matchar and pattern (Register Expression)
        String mobileRegex = "[6-9][0-9]{9}";     // First no. can be (6,8,9) and rest 9 no . can be any no
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);

        if(TextUtils.isEmpty(textFullName)){
            Toast.makeText(UpdateProfileActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            editTextUpdateName.setError("Full name is required");
            editTextUpdateName.requestFocus();
        }
        else if(TextUtils.isEmpty(textEmail)){
            Toast.makeText(UpdateProfileActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
            editTextUpdateEmail.setError("Email is required");
            editTextUpdateEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
            Toast.makeText(UpdateProfileActivity.this, "Please reenter email", Toast.LENGTH_SHORT).show();
            editTextUpdateEmail.setError("Valid email is required");
            editTextUpdateEmail.requestFocus();
        } else if (TextUtils.isEmpty(textDoB)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
            editTextUpdateDoB.setError("Date of birth is required");
            editTextUpdateDoB.requestFocus();
        }else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())){
            Toast.makeText(UpdateProfileActivity.this, "Please enter your gender", Toast.LENGTH_SHORT).show();
            radioButtonUpdateGenderSelected.setError("Gender is required");
            radioButtonUpdateGenderSelected.requestFocus();
        }else if(TextUtils.isEmpty(textMobile)){
            Toast.makeText(UpdateProfileActivity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
            editTextUpdateMobile.setError("mobile number is required with 10 digit");
            editTextUpdateMobile.requestFocus();
        }else if (!mobileMatcher.find()) {
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter your mobile number", Toast.LENGTH_SHORT).show();
            editTextUpdateMobile.setError("Mobile No. is not valid");
            editTextUpdateMobile.requestFocus();
        } else {
            // Obtain the data entered by user
            textFullName = editTextUpdateName.getText().toString();
            textEmail =  editTextUpdateEmail.getText().toString();
            textDoB = editTextUpdateDoB.getText().toString();
            textGender = radioButtonUpdateGenderSelected.getText().toString();
            textMobile = editTextUpdateMobile.getText().toString();

             // Enter user data into the firebase realtime database. set up dependencies
            ReadWriteuserDetails writeuserDetails = new ReadWriteuserDetails(textFullName,textEmail,textDoB,textGender,textMobile);

            // Extract user reference from database for "Registered User"
            DatabaseReference referenceProfile  = FirebaseDatabase.getInstance().getReference("Registered Users");

            String userID = firebaseUser.getUid();

            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeuserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        // setting new dispaly Name
                       UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().
                            setDisplayName(textFullName).build();
                       firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateProfileActivity.this, "Update successful", Toast.LENGTH_LONG).show();

                        // Stop user for returning to UpdateProfileActivity on pressing button and close activity
                        Intent intent = new Intent(UpdateProfileActivity.this,UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        try {
                            throw  task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.VISIBLE);

                }
            });
        }
    }

    // fetch data from firebase and display
    private void showProfile(FirebaseUser firebaseUser) {
        String userID0fRegistered = firebaseUser.getUid();

        // Extracting user Reference from Database for "Registered Users
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userID0fRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteuserDetails readUserDetails = snapshot.getValue(ReadWriteuserDetails.class);
                if (readUserDetails != null){

                    //textFullName = readUserDetails.fullName;
                    textFullName = firebaseUser.getDisplayName();
                    textEmail = readUserDetails.email;
                    textDoB = readUserDetails.doB;
                    textMobile = readUserDetails.mobile;
                    textGender = readUserDetails.gender;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateEmail.setText(textEmail);
                    editTextUpdateDoB.setText(textDoB);
                    editTextUpdateMobile.setText(textMobile);

                    // show Gender through Radio Button
                    if (textGender.equals("Male")){
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
                    } else {
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_female);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                }else {
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}