package com.example.finalnews;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;



import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName,editTextRegisterEmail,editTextRegisterDoB,editTextRegisterMobile,editTextRegisterPassword,editTextRegisterConfirmPassword;
     ProgressBar progressBar;
    private RadioGroup RadioGroupRegisterGender;
    private RadioButton RadioBtnselected;
    private TextView text_signup_click;
    Button buttonRegister;

    private DatePickerDialog picker;
    private static final String TAG="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        editTextRegisterFullName = findViewById(R.id.etextfullname);
        editTextRegisterEmail = findViewById(R.id.etextemail);
        editTextRegisterDoB = findViewById(R.id.etextdob);
        editTextRegisterMobile = findViewById(R.id.etextmobile);
        editTextRegisterPassword = findViewById(R.id.etextpassword);
        buttonRegister = findViewById(R.id.loginbtn);
        text_signup_click = findViewById(R.id.text_signup_click);
        editTextRegisterConfirmPassword = findViewById(R.id.etextconfirmpassword);
        progressBar = findViewById(R.id.progressBar);

        // Radio button for gender
        RadioGroupRegisterGender = findViewById(R.id.radiogroup);
        RadioGroupRegisterGender.clearCheck();

        // setting up date picker on editText
        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date Picker Dialog
                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDoB.setText(dayOfMonth + "/" + (month + 1) + "/" +year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId = RadioGroupRegisterGender.getCheckedRadioButtonId();
                RadioBtnselected = findViewById(selectedGenderId);

                // enter data
                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDoB = editTextRegisterDoB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterPassword.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPassword.getText().toString();
                String textGender;

                // Valid mobile number using Matchar and pattern (Register Expression)
                String mobileRegex = "[6-9][0-9]{9}";     // First no. can be (6,8,9) and rest 9 no . can be any no
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);

                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                }
                else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(RegisterActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please reenter email", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                    editTextRegisterDoB.setError("Date of birth is required");
                    editTextRegisterDoB.requestFocus();
                }else if (RadioGroupRegisterGender.getCheckedRadioButtonId()==-1){
                    Toast.makeText(RegisterActivity.this, "Please enter your gender", Toast.LENGTH_SHORT).show();
                    RadioBtnselected.setError("Gender is required");
                    RadioBtnselected.requestFocus();
                }else if(TextUtils.isEmpty(textMobile)){
                    Toast.makeText(RegisterActivity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("mobile number is required");
                    editTextRegisterMobile.requestFocus();
                }else if(textMobile.length() != 10){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile number", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("mobile no. should be 10 digits");
                    editTextRegisterMobile.requestFocus();
                }else if (!mobileMatcher.find()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile number", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("Mobile No. is not valid");
                    editTextRegisterMobile.requestFocus();
                }else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextRegisterPassword.setError("password is required");
                    editTextRegisterPassword.requestFocus();
                } else if (textPwd.length() <7) {
                    Toast.makeText(RegisterActivity.this, "Password should be at least 7 digit", Toast.LENGTH_SHORT).show();
                    editTextRegisterPassword.setError("Strong password is required");
                    editTextRegisterPassword.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmPassword.setError("Password conformation is required");
                    editTextRegisterConfirmPassword.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please enter same password", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmPassword.setError("Password conformation is required");
                    editTextRegisterConfirmPassword.requestFocus();

                    //clear the entered passwords
                    editTextRegisterPassword.clearComposingText();
                    editTextRegisterConfirmPassword.clearComposingText();
                } else {
                    textGender = RadioBtnselected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDoB, textGender, textMobile, textPwd);
                }

            }
        });


        text_signup_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    // Register user using user credentials
    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd) {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        //  Create user profile
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser  firebaseUser = auth.getCurrentUser();

                    //  Update Display name of user
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    // Enter user data into the firebase realtime database
                    ReadWriteuserDetails writeUserDetails = new ReadWriteuserDetails(textFullName,textEmail,textDoB,textGender,textMobile);

                    //  Extracting user reference from database for "Registered Users
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                // send verificatiomn email
                                firebaseUser.sendEmailVerification();

                                Toast.makeText(RegisterActivity.this, "User registerd successfully. Please verify your email", Toast.LENGTH_LONG).show();

                            /*    // Open user profile after successful registration
                                Intent intent = new Intent(Register2Activity.this,HomeActivity.class);
                                // To prevent user from returning back to register activity on presing back after registration
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); // to close Registration activity  */
                            }else {
                                Toast.makeText(RegisterActivity.this, "User registerd failed. Please verify your email", Toast.LENGTH_LONG).show();
                            }
                            //Hide progressbar whether user credention is sucessful or failed
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                }else{
                    try {
                        throw  task.getException();
                    }catch(FirebaseAuthWeakPasswordException e){
                        editTextRegisterPassword.setError("Your password is too week. kidely use a mix of alphabets ,number and special characters");
                        editTextRegisterPassword.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        editTextRegisterPassword.setError("Your email is invalid or already in use. Kidely re-enter.");
                        editTextRegisterPassword.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e){
                        editTextRegisterPassword.setError("User is already registered with this email. Use another email.");
                        editTextRegisterPassword.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(RegisterActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    //Hide progressbar whether user credention is sucessful or failed
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}