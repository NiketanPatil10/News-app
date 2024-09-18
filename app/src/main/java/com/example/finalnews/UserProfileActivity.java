package com.example.finalnews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;

public class UserProfileActivity extends AppCompatActivity {

    TextView textViewWelcome,textViewUserFullName,textViewuserEmail,textViewUserBirthday,textViewUserGender,textViewUserMobileNo;
    private ProgressBar progressBar;
    private String fullname,email,dob,gender,mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;

    // Firebase storage reference
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setTitle("Profile");
        progressBar = findViewById(R.id.progressbar);

        textViewWelcome = findViewById(R.id.TextViewWelcome);
        textViewUserFullName = findViewById(R.id.User_full_name);
        textViewuserEmail = findViewById(R.id.User_Email);
        textViewUserBirthday = findViewById(R.id.User_Birthday);
        textViewUserGender = findViewById(R.id.User_Gender);
        textViewUserMobileNo = findViewById(R.id.User_mobile_no);

        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");


        // Set OnClickListener on Imageview to Open UploadProfilePicActivity
        imageView = findViewById(R.id. imageview_profile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this,UploadProfilePicActivity.class);
                startActivity(intent);
            }
        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(UserProfileActivity.this, "Something went wrong! User's details are not available at the moment.", Toast.LENGTH_SHORT).show();
        }else {
            checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

      // User coming to UserProfileActivity after successful registration
    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification next time");

        // Open email app if user click continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //to email app in new window and not within  our app
                startActivity(intent);
            }
        });
        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();
        //   Show the alert dialog
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Extracting user Reference from Database for "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               ReadWriteuserDetails readuserDetails = snapshot.getValue(ReadWriteuserDetails.class);
               if (readuserDetails != null){
                   fullname = firebaseUser.getDisplayName();
                   email = firebaseUser.getEmail();
                   dob = readuserDetails.doB;
                   gender = readuserDetails.gender;
                   mobile = readuserDetails.mobile;

                   textViewWelcome.setText("Welcome, "+fullname);
                   textViewUserFullName.setText(fullname);
                   textViewuserEmail.setText(email);
                   textViewUserBirthday.setText(dob);
                   textViewUserGender.setText(gender);
                   textViewUserMobileNo.setText(mobile);

                   // profile picture set in imageView
                   StorageReference imageRef = storageReference.child("image.jpg");
                   final File localFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"download_image.jpg");
                   imageRef.getFile(localFile)
                           .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                   Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                   imageView.setImageBitmap(bitmap);
                                   progressBar.setVisibility(View.GONE);

                               }
                           });

               }

               progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    // Creating ActionBar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
          // when any item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_refresh){
            //Refresg Activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);

        }else if(id == R.id.menu_profile){
            Toast.makeText(this, "Your are already there", Toast.LENGTH_SHORT).show();
        }else if(id == R.id.menu_update_profile){
            Intent intent = new Intent(UserProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UserProfileActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UserProfileActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UserProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfileActivity.this,LoginActivity.class);
             // Clear stack to prevent user comming back to UserProfileActivity on presing back button after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}