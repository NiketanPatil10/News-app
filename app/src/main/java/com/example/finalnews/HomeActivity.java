package com.example.finalnews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.example.finalnews.newsmaterial.EntertainmentFragment;
import com.example.finalnews.newsmaterial.BusinessFragment;
import com.example.finalnews.newsmaterial.HomeFragment;
import com.example.finalnews.newsmaterial.ScienceFragment;
import com.example.finalnews.newsmaterial.SportsFragment;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iammert.library.readablebottombar.ReadableBottomBar;

public class HomeActivity extends AppCompatActivity {

    ReadableBottomBar readableBottomBar;
   private LinearProgressIndicator progressIndicator;

    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);



        if (!isConnected()) {
            showNoInternetDialog();
        }else{

            readableBottomBar  = findViewById(R.id.readableBottomBar);
            progressIndicator = findViewById(R.id.progress_bar);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content,new HomeFragment());
            fragmentTransaction.commit();


            readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
                @Override
                public void onItemSelected(int i) {

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                    switch (i){
                        case 0:
                            fragmentTransaction.replace(R.id.content,new HomeFragment());
                            fragmentTransaction.commit();
                            break;
                        case 1:
                            fragmentTransaction.replace(R.id.content,new ScienceFragment());
                            fragmentTransaction.commit();
                            break;
                        case 2:
                            fragmentTransaction.replace(R.id.content,new SportsFragment());
                            fragmentTransaction.commit();
                            break;
                        case 3:
                            fragmentTransaction.replace(R.id.content,new BusinessFragment());
                            fragmentTransaction.commit();
                            break;
                        case 4:
                            fragmentTransaction.replace(R.id.content,new EntertainmentFragment());
                            fragmentTransaction.commit();
                            break;
                    }
                }
            });

            //getSupportActionBar().setTitle("Home");
            authProfile = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = authProfile.getCurrentUser();

            if (firebaseUser == null){
                Toast.makeText(HomeActivity.this, "Something went wrong! User's details are not available at the moment.", Toast.LENGTH_SHORT).show();
            }else {
                checkIfEmailVerified(firebaseUser);
                //progressBar.setVisibility(View.VISIBLE);

            }
        }
    }

    // User coming to UserProfileActivity after successful registration
    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }
    private void showAlertDialog() {

      /*  AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
        alertDialog.show();  */


        new AwesomeInfoDialog(this)
                .setTitle("Email Not Verified")
                .setMessage("Please verify your email now. You can not login without email verification next time")
                .setColoredCircle(R.color.dialogcirclecolor)
                //.setDialogIconAndColor(R.color.white)
                .setCancelable(true)
                .setPositiveButtonText("Continue")
                .setPositiveButtonbackgroundColor(R.color.primary)
                .setPositiveButtonTextColor(R.color.black)

                .setNegativeButtonText("Cancel")
                .setNegativeButtonbackgroundColor(R.color.primary3)
                .setNegativeButtonTextColor(R.color.black)

                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //to email app in new window and not within  our app
                        startActivity(intent);
                    }
                })
                .setNegativeButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        int id;
                        //click
                        dismissDialog(1);
                    }
                })
                .show();

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
            Intent intent = new Intent(HomeActivity.this,UserProfileActivity.class);
            startActivity(intent);
        } else if(id == R.id.menu_update_profile){
            Intent intent = new Intent(HomeActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }  else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(HomeActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(HomeActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            authProfile = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = authProfile.getCurrentUser();
            authProfile.signOut();
            Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
            // Clear stack to prevent user comming back to UserProfileActivity on presing back button after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(HomeActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
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

      /*  AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        dialog.show();  */

        new AwesomeInfoDialog(this)
                .setTitle(R.string.No_Internet)
                .setMessage(R.string.message)
                .setColoredCircle(R.color.dialogcirclecolor)
                //.setDialogIconAndColor(R.color.white)
                .setCancelable(true)
                .setPositiveButtonText(getString(R.string.Open_Settings))
                .setPositiveButtonbackgroundColor(R.color.opensetting)
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


    void changeInProgress(boolean show){
        if (show){
            progressIndicator.setVisibility(View.VISIBLE);
        }else {
            progressIndicator.setVisibility(View.INVISIBLE);
        }
    }
}