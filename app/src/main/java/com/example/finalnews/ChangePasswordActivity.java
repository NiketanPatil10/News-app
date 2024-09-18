package com.example.finalnews;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText currentpassword,newpassword,confirmpassword;
  //  private TextView textviewVerify;
    private Button buttonchangepassword,buttonverify;
    private ProgressBar progressBar;
    String userPwdCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        currentpassword = findViewById(R.id.editText_change_pwd_current);
        newpassword = findViewById(R.id.editText_change_pwd_new);
        confirmpassword = findViewById(R.id.editText_change_pwd_new_confirm);

        progressBar = findViewById(R.id.progressBar);

       // textviewVerify = findViewById(R.id.currentpassword);

        buttonchangepassword = findViewById(R.id.button_change_pwd);
        buttonverify = findViewById(R.id.button_authenticate_user);

        // Disable edittext for new password , confirm new passeord and make change pwd button till user is authenticate.
        newpassword.setEnabled(false);
        confirmpassword.setEnabled(false);
        buttonchangepassword.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")){
            Toast.makeText(this, "Something went wrong! User details not available", Toast.LENGTH_LONG).show();
           Intent intent = new Intent(ChangePasswordActivity.this,UserProfileActivity.class);
           startActivity(intent);
           finish();
        }else{
            reAuthenticateUser(firebaseUser);
        }
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurrent = currentpassword.getText().toString();
                if (TextUtils.isEmpty(userPwdCurrent)){
                    Toast.makeText(ChangePasswordActivity.this, "Password is needed", Toast.LENGTH_SHORT).show();
                    currentpassword.setError("Please enter your current password to verify");
                    currentpassword.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);

                    //ReVerify User now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),userPwdCurrent);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //prgressBar.setVisibility(view.GONE);

                                // Disable editText  for current password. Enable edittext for new password and confirm password
                                currentpassword.setEnabled(false);
                                newpassword.setEnabled(true);
                                confirmpassword.setEnabled(true);

                                //Enable change Password button, Disable verify button
                                buttonverify.setEnabled(false);
                                buttonchangepassword.setEnabled(true);

                                //set TextView or toast to show user is authenticated /verified
                                //textViewVerify.setText("You are verify."+ You can change password now. ");
                                Toast.makeText(ChangePasswordActivity.this, "Password has been verified" +"Change password now", Toast.LENGTH_LONG).show();

                                //update color Change password button
                                buttonchangepassword.setBackgroundTintList(ContextCompat.getColorStateList(
                                        ChangePasswordActivity.this,R.color.cancelbackcolor));


                                buttonchangepassword.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        changePassword(firebaseUser);
                                    }
                                });

                            }else {
                                 try {
                                     task.getException();
                                 }catch (Exception e){
                                     Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                 }
                            }
                            //prgressBar.setVisibility(view.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePassword(FirebaseUser firebaseUser) {

        String usernewPassword = newpassword.getText().toString();
        String userconfirmPassword = confirmpassword.getText().toString();

        if (TextUtils.isEmpty(usernewPassword)){
            Toast.makeText(this, "New Password is needed", Toast.LENGTH_LONG).show();
            newpassword.setError("Please enter your password");
            newpassword.requestFocus();
        }else if (TextUtils.isEmpty(userconfirmPassword)){
            Toast.makeText(this, "Please confirm your new password", Toast.LENGTH_LONG).show();
            confirmpassword.setError("Please re-enter your password");
            confirmpassword.requestFocus();
        } else if (!usernewPassword.matches(userconfirmPassword)) {
            Toast.makeText(this, "Passwod did not match", Toast.LENGTH_LONG).show();
            confirmpassword.setError("Please re-enter same password");
            confirmpassword.requestFocus();
        } else if (userPwdCurrent.matches(usernewPassword)) {
            Toast.makeText(this, "New password can not be same as old password", Toast.LENGTH_LONG).show();
            newpassword.setError("Please enter a new password");
            newpassword.requestFocus();
        }else {
              //prgressBar.setVisibility(view.VISIBLE);

            firebaseUser.updatePassword(usernewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "Password has been changed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePasswordActivity.this,UserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    //prgressBar.setVisibility(view.GONE);

                }
            });
        }
    }

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
            Intent intent = new Intent(ChangePasswordActivity.this,UserProfileActivity.class);
            startActivity(intent);
        } else if(id == R.id.menu_update_profile){
            Intent intent = new Intent(ChangePasswordActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(ChangePasswordActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(ChangePasswordActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            authProfile = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = authProfile.getCurrentUser();
            authProfile.signOut();
            Intent intent = new Intent(ChangePasswordActivity.this,LoginActivity.class);
            // Clear stack to prevent user comming back to UserProfileActivity on presing back button after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}