package com.example.health;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangepasswordActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    //private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthenticate;
    private String userPwdCurr;
    private Button buttonChangepwd,buttonChangeAuthenticate;
    private EditText editTextpwdcurr,editTextpwdnew,editTextpwdConfirmnew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        progressBar=findViewById(R.id.show_progress_bar_change_pwd);
        textViewAuthenticate=findViewById(R.id.textView_change_pwd_authenticated);
        editTextpwdnew=findViewById(R.id.editText_change_pwd_new);
        editTextpwdcurr=findViewById(R.id.editText_change_pwd_current);
        editTextpwdConfirmnew=findViewById(R.id.editText_change_pwd_new_confirm);
        buttonChangeAuthenticate=findViewById(R.id.button_change_pwd_authenticate);
        buttonChangepwd=findViewById(R.id.button_change_pwd);
        //disable edit text for 3
        editTextpwdnew.setEnabled(false);
        editTextpwdConfirmnew.setEnabled(false);
        buttonChangepwd.setEnabled(false);
        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        if(firebaseUser.equals("")){
            Toast.makeText(ChangepasswordActivity.this,"Something went wrong! User's details not available",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(ChangepasswordActivity.this,UserProfileActivity.class);
            startActivity(intent);
            finish();

        }
        else{
            reAuthenticate(firebaseUser);
        }


    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        buttonChangeAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurr=editTextpwdcurr.getText().toString();
                if(TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(ChangepasswordActivity.this,"Password is needed to continue",Toast.LENGTH_LONG).show();
                    editTextpwdcurr.setError("Please enter your password for authentication");
                    editTextpwdcurr.requestFocus();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(),userPwdCurr);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                editTextpwdcurr.setEnabled(false);
                                editTextpwdConfirmnew.setEnabled(true);
                                editTextpwdnew.setEnabled(true);
                                buttonChangeAuthenticate.setEnabled(false);
                                buttonChangepwd.setEnabled(true);
                                textViewAuthenticate.setText("You are authenticated/verified."+" You can update your email now");
                                Toast.makeText(ChangepasswordActivity.this,"Password has been verified."+"Change Password now",Toast.LENGTH_LONG).show();
                                buttonChangepwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangepasswordActivity.this,R.color.dark_green));
                                buttonChangepwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePwd(firebaseUser);

                                    }
                                });
                            }
                            else{
                                try{
                                    throw task.getException();
                                }
                                catch(Exception e){
                                    Toast.makeText(ChangepasswordActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew=editTextpwdnew.getText().toString();
        String userPwdConfirmNew=editTextpwdConfirmnew.getText().toString();
        if(TextUtils.isEmpty(userPwdNew)){
            Toast.makeText(ChangepasswordActivity.this,"New Password is required",Toast.LENGTH_LONG).show();
            editTextpwdnew.setError("Please enter new password");
            editTextpwdnew.requestFocus();
        }
        else if(TextUtils.isEmpty(userPwdConfirmNew)){
            Toast.makeText(ChangepasswordActivity.this,"Please confirm your new Password",Toast.LENGTH_LONG).show();
            editTextpwdConfirmnew.setError("Please re-enter your new password");
            editTextpwdConfirmnew.requestFocus();
        }
        else if(!userPwdNew.matches(userPwdConfirmNew)){
            Toast.makeText(ChangepasswordActivity.this,"Password did not match",Toast.LENGTH_LONG).show();
            editTextpwdConfirmnew.setError("Please re-enter same password");
            editTextpwdConfirmnew.requestFocus();
        }
        else if(userPwdCurr.matches(userPwdConfirmNew)){
            Toast.makeText(ChangepasswordActivity.this,"Password did not match",Toast.LENGTH_LONG).show();
            editTextpwdnew.setError("Please enter a new password");
            editTextpwdnew.requestFocus();
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //send email verification link to user
                        firebaseUser.sendEmailVerification();
                        Toast.makeText(ChangepasswordActivity.this,"Password has been changed",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(ChangepasswordActivity.this,UserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch(Exception e){
                            Toast.makeText(ChangepasswordActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }


    }
}