package com.example.health;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteprofileActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private EditText editTextUserpwd;
    private TextView textViewauthenticated;
    private ProgressBar progressBar;
    private String userPwd;
    private Button buttonReauthenticate,buttonDeleteUser;
    private final static String TAG="DeletePrfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleteprofile);
        progressBar=findViewById(R.id.show_progress_bar_delete_profile);
        editTextUserpwd=findViewById(R.id.editText_delete_user_pwd);
        textViewauthenticated=findViewById(R.id.textView_delete_user_authenticated);
        buttonDeleteUser=findViewById(R.id.button_delete_user);
        buttonReauthenticate=findViewById(R.id.button_delete_user_authenticate);
        buttonDeleteUser.setEnabled(false);
        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();
        if(firebaseUser.equals("")){
            Toast.makeText(DeleteprofileActivity.this,"Something went wrong! User's details not available",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(DeleteprofileActivity.this,UserProfileActivity.class);
            startActivity(intent);
            finish();

        }
        else{
            reAuthenticate(firebaseUser);
        }
    }
    private void reAuthenticate(FirebaseUser firebaseUser) {
        buttonReauthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwd=editTextUserpwd.getText().toString();
                if(TextUtils.isEmpty(userPwd)){
                    Toast.makeText(DeleteprofileActivity.this,"Password is needed to continue",Toast.LENGTH_LONG).show();
                    editTextUserpwd.setError("Please enter your password for authentication");
                    editTextUserpwd.requestFocus();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(),userPwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                editTextUserpwd.setEnabled(false);
                                buttonReauthenticate.setEnabled(false);
                                buttonDeleteUser.setEnabled(true);
                                textViewauthenticated.setText("You are authenticated/verified."+" You can delete your profile and related data now!");
                                Toast.makeText(DeleteprofileActivity.this,"Password has been verified."+"Change Password now",Toast.LENGTH_LONG).show();
                                buttonDeleteUser.setBackgroundTintList(ContextCompat.getColorStateList(DeleteprofileActivity.this,R.color.dark_green));
                                buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAlertDialog();

                                    }
                                });
                            }
                            else{
                                try{
                                    throw task.getException();
                                }
                                catch(Exception e){
                                    Toast.makeText(DeleteprofileActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }
    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(DeleteprofileActivity.this);
        builder.setTitle("Delete User and Related Data?");
        builder.setMessage("Do you really want to delete your profile and related data? This action is irreversible");
        //open email apps if user clicks/taps continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserData(firebaseUser);
            }
        });
        //negative-cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(DeleteprofileActivity.this,UserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //create the alertDialog
        AlertDialog alertDialog=builder.create();
        //change color of continue btn;
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });
        //show the dialog box
        alertDialog.show();

    }

    private void deleteUser() {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    authProfile.signOut();
                    Toast.makeText(DeleteprofileActivity.this,"User has been deleted!",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(DeleteprofileActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    try{
                        throw task.getException();
                    }
                    catch(Exception e){
                        Toast.makeText(DeleteprofileActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void deleteUserData(FirebaseUser firebaseUser) {

        //deletedata from realtime db
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG,"On Success: User data deleted");
                //delete user data
                deleteUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,e.getMessage());
                Toast.makeText(DeleteprofileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


}