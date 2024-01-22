package com.example.health;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

public class UpdateEmailActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthenticate;
    private String userOldEmail,userNewEmail,userpwd;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail,editTextpwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        progressBar=findViewById(R.id.show_progress_bar_update_email);
        editTextpwd=findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail=findViewById(R.id.editText_update_email_new);
        textViewAuthenticate=findViewById(R.id.textView_update_email_authenticated);
        buttonUpdateEmail=findViewById(R.id.button_update_email);
        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();
        userOldEmail=firebaseUser.getEmail();
        TextView textViewOldEmail =findViewById(R.id.textView_update_email_old);
        textViewOldEmail.setText(userOldEmail);
        if(firebaseUser.equals("")){
            Toast.makeText(UpdateEmailActivity.this,"Something went wrong! User's details not available",Toast.LENGTH_LONG).show();

        }
        else{
            reAuthenticate(firebaseUser);
        }
    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonverifyUser=findViewById(R.id.button_authenticate_user);
        buttonverifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userpwd=editTextpwd.getText().toString();
                if(TextUtils.isEmpty(userpwd)){
                    Toast.makeText(UpdateEmailActivity.this,"Password is needed to continue",Toast.LENGTH_LONG).show();
                    editTextpwd.setError("Please enter your password for authentication");
                    editTextpwd.requestFocus();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    AuthCredential credential= EmailAuthProvider.getCredential(userOldEmail,userpwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(UpdateEmailActivity.this,"Password has been verified"+"You can update email now.",Toast.LENGTH_LONG).show();
                                //set text view to show that user is authenticated
                                textViewAuthenticate.setText("You are authenticated. You can update your email now");
                                //disable edit text
                                editTextNewEmail.setEnabled(true);
                                editTextpwd.setEnabled(true);
                                buttonverifyUser.setEnabled(false);
                                buttonUpdateEmail.setEnabled(true);

                                //change btn color
                                buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,R.color.dark_green));
                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userNewEmail=editTextNewEmail.getText().toString();
                                        if(TextUtils.isEmpty(userNewEmail)){
                                            Toast.makeText(UpdateEmailActivity.this,"New email is required",Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Please enter new email");
                                            editTextNewEmail.requestFocus();
                                        }
                                        else if(!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()){
                                            Toast.makeText(UpdateEmailActivity.this,"Please enter valid email",Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Please provide valid new email");
                                            editTextNewEmail.requestFocus();
                                        }
                                        else if(userOldEmail.matches(userNewEmail)){
                                            Toast.makeText(UpdateEmailActivity.this,"Old email cannot be new email",Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Please provide valid new email");
                                            editTextNewEmail.requestFocus();
                                        }
                                        else{
                                            progressBar.setVisibility(View.VISIBLE);
                                            updatemail(firebaseUser);
                                        }
                                    }
                                });
                            }
                            else{
                                try{
                                    throw task.getException();
                                }
                                catch (Exception e){
                                    Toast.makeText(UpdateEmailActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
                                }
                            }

                        }
                    });
                }
            }
        });
    }

    private void updatemail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //send email verification link to user
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmailActivity.this,"Email has been updated.Please verify your Email",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(UpdateEmailActivity.this,UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    try{
                        throw task.getException();
                    }
                    catch(Exception e){
                        Toast.makeText(UpdateEmailActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}