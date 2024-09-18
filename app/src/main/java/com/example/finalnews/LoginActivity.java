package com.example.finalnews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editText_login_email, editText_login_password;
    private TextView signup_text;
    private Button login_btn;
    private ProgressBar progressbar;
    private FirebaseAuth authProfile;
    public TextView forgotepassword;
    private CheckBox login_check_box;

    private ImageView imageViewShowHidePwd;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        editText_login_email = findViewById(R.id.editText_login_email);
        editText_login_password = findViewById(R.id.editText_login_password);
        login_btn = findViewById(R.id.login_btn);
        progressbar = findViewById(R.id.progressbar);
        signup_text = findViewById(R.id.signup_text);
        forgotepassword = findViewById(R.id.login_forgote_password);
        login_check_box = findViewById(R.id.login_check_box);

         authProfile = FirebaseAuth.getInstance();

        if (!isConnected()) {
            showNoInternetDialog();
        }else{

            //show hide password using eyes icon
            imageViewShowHidePwd = findViewById(R.id.hide_password);
            imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);

            imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editText_login_password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                        // if password is  visible then hide it
                        editText_login_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        // change icon
                        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                    } else {
                        editText_login_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                    }
                }
            });

            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String textEmail = editText_login_email.getText().toString();
                    String textPwd = editText_login_password.getText().toString();

                    if (TextUtils.isEmpty(textEmail)) {
                        Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                        editText_login_email.setError("Email is required");
                        editText_login_email.requestFocus();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                        Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                        editText_login_email.setError("Valid email is required");
                        editText_login_email.requestFocus();
                    } else if (TextUtils.isEmpty(textPwd)) {
                        Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                        editText_login_password.setError("Password  is required");
                        editText_login_password.requestFocus();
                    } else if (!login_check_box.isChecked()) {
                        Toast.makeText(LoginActivity.this, "Please Click here", Toast.LENGTH_SHORT).show();
                        login_check_box.setError("Checked  is required");
                        login_check_box.requestFocus();
                    } else {
                        progressbar.setVisibility(View.VISIBLE);
                        loginUser(textEmail, textPwd);
                    }
                }
            });
               //Signup text
            signup_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });

            // Reset Password
            forgotepassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                }
            });

        }

    }



    private void loginUser (String email, String pwd){
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    // Get insance of the current user
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    // Check if email is verified before user can access their profile
                    if (firebaseUser.isEmailVerified()) {

                        //Open user profile
                        // Start the user profile
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();  // close Loin Activity

                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();   // Sign out user
                        ShowAlertDialog();
                    }

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        editText_login_email.setError("User does not exists or is no longer valid. Please register again.");
                        editText_login_email.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editText_login_email.setError("Invalid credentials. kindly check and re-enter.");
                        editText_login_email.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressbar.setVisibility(View.GONE);
            }
        });

    }


    private void ShowAlertDialog () {
        // setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification");

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


    // Check if user is already logged in. In such case, straightway take the user to the user's profile
    @Override
    protected void onStart () {
        super.onStart();
        if (authProfile.getCurrentUser() != null) {
            // Start the user profile
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();  // close Loin Activity
        }
    }


            //  check internet is connected to the application
    private boolean isConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    // when internet is not connected to the application then show no internet connection dialog
    private void showNoInternetDialog() {
       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please check your internet settings.");
        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show(); */


        new AwesomeInfoDialog(this)
                .setTitle(R.string.No_Internet)
                .setMessage(R.string.message)
                .setColoredCircle(R.color.dialogcirclecolor)
                //.setDialogIconAndColor(R.color.white)
                .setCancelable(true)
                .setPositiveButtonText(getString(R.string.Open_Settings))
                .setPositiveButtonbackgroundColor(R.color.opensettings)
                .setPositiveButtonTextColor(R.color.black)

                .setNegativeButtonText(getString(R.string.Cancel))
                .setNegativeButtonbackgroundColor(R.color.cancelbackcolor)
                .setNegativeButtonTextColor(R.color.black)

                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        //click
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        //click
                    }
                })
                .show();
    }

}