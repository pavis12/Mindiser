package com.example.health;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.nfc.Tag;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
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

public class Register extends AppCompatActivity {
    private EditText editTextRegisterFullname,editTextRegisteremail,editTextRegisterdob,editTextRegisterMobile,editTextRegisterpwd,editTextRegisterConfirmpwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegistergender;
    private RadioButton radioButtonRegisterGenderselected;
    private DatePickerDialog picker;
    private static  final String TAG="RegisterActivity";
    private int awesome,good,okay,bad,terrible;
    private String last_login="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //getSupportActionBar().setTitle("Register");
        Toast.makeText(Register.this, "You can register now", Toast.LENGTH_LONG).show();
        progressBar=findViewById(R.id.progressBar);
        editTextRegisterFullname = findViewById(R.id.editText_register_full_name);
        editTextRegisteremail = findViewById(R.id.editText_register_email);
        editTextRegisterdob = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterConfirmpwd = findViewById(R.id.editText_register_confirm_password);
        editTextRegisterpwd = findViewById(R.id.editText_register_password);
        radioGroupRegistergender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegistergender.clearCheck();
        //setting up datepicker on edit text
        editTextRegisterdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar=Calendar.getInstance();
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);
                picker=new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterdob.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });
        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedgendr = radioGroupRegistergender.getCheckedRadioButtonId();
                radioButtonRegisterGenderselected = findViewById(selectedgendr);
                String textFullName = editTextRegisterFullname.getText().toString();
                String textEmail = editTextRegisteremail.getText().toString();
                String textdob = editTextRegisterdob.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterpwd.getText().toString();
                String textConfirmpwd = editTextRegisterConfirmpwd.getText().toString();
                String textGender;
                //validate mobile number using regular expression
                String mobileregex="^\\d{10}";
                Matcher mobilematcher;
                Pattern mobilepattern=Pattern.compile(mobileregex);
                mobilematcher=mobilepattern.matcher(textMobile);

                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(Register.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullname.setError("Full Name is required");
                    editTextRegisterFullname.requestFocus();

                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(Register.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisteremail.setError("Email is required");
                    editTextRegisteremail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(Register.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisteremail.setError("Valid email is required");
                    editTextRegisteremail.requestFocus();

                } else if (TextUtils.isEmpty(textdob)) {
                    Toast.makeText(Register.this, "Please enter your dob", Toast.LENGTH_LONG).show();
                    editTextRegisterdob.setError("Date of birth is required");
                    editTextRegisterdob.requestFocus();
                } else if (radioGroupRegistergender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(Register.this, "Please enter your gender", Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderselected.setError("Gender is required");
                    radioButtonRegisterGenderselected.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(Register.this, "Please enter your mobile no.", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile no. is required");
                    editTextRegisterMobile.requestFocus();
                } else if (textMobile.length() != 10) {
                    Toast.makeText(Register.this, "Please re-enter your mobile no.", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile no. should be 10 digits");
                    editTextRegisterMobile.requestFocus();
                }
                else if(!mobilematcher.find()){
                    Toast.makeText(Register.this, "Please re-enter your mobile no.", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile no. is not valid");
                    editTextRegisterMobile.requestFocus();
                }
                else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(Register.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    editTextRegisterpwd.setError("Password is Required");
                    editTextRegisterpwd.requestFocus();
                } else if (textPwd.length() < 6) {
                    Toast.makeText(Register.this, "Password should be atleast 6 digits", Toast.LENGTH_LONG).show();
                    editTextRegisterpwd.setError("Password too weak");
                    editTextRegisterpwd.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmpwd)) {
                    Toast.makeText(Register.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmpwd.setError("Password confirmation is Required");
                    editTextRegisterConfirmpwd.requestFocus();
                } else if (!textPwd.equals(textConfirmpwd)) {
                    Toast.makeText(Register.this, "Please enter same password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmpwd.setError("Password not matched");
                    editTextRegisterConfirmpwd.requestFocus();
                    editTextRegisterConfirmpwd.clearComposingText();
                    editTextRegisterpwd.clearComposingText();
                } else {
                    textGender = radioButtonRegisterGenderselected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    awesome=0;
                    good=0;
                    okay=0;
                    bad=0;
                    terrible=0;
                    registerUser(textFullName, textEmail, textdob, textGender, textMobile, last_login,textPwd,awesome,good,okay,bad,terrible);
                }
            }


        });
    }
    private void registerUser(String textFullName, String textEmail, String textdob, String textGender, String textMobile, String last_login,String textPwd,int awesome,int good,int okay,int bad,int terrible) {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        //create user profile
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                @Override public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        FirebaseUser firebaseUser=auth.getCurrentUser();
                        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();



                        //update display name of user
                        UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileChangeRequest);
                        // enter user data into firebase database
                        ReadWriteUserDetails writeUserDetails=new ReadWriteUserDetails(textFullName,textdob,textGender,textMobile,last_login,awesome,good,okay,bad,terrible);
                        //extracting user reference from db for "Registered user"
                        DatabaseReference referenceprofile= firebaseDatabase.getReference("Registered Users");
                       referenceprofile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    firebaseUser.sendEmailVerification();
                                    Toast.makeText(Register.this,"User registered successfully.Please verify your email",Toast.LENGTH_LONG).show();

                                    //to prevent user from returning back to register activity on pressing bak button after registration
                                    Intent i=new Intent(Register.this,UserProfileActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                }
                                else{
                                    Toast.makeText(Register.this,"Registration failed.Please try again later",Toast.LENGTH_LONG).show();

                                }
                                progressBar.setVisibility(View.GONE);

                            }
                        });

                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch (FirebaseAuthWeakPasswordException e){
                            editTextRegisterpwd.setError("Your password is too weak.Kindly use a mix of alphabets,numbers and special characters.");
                            editTextRegisterpwd.requestFocus();
                        }
                        catch (FirebaseAuthInvalidCredentialsException e){
                            editTextRegisterpwd.setError("Your email is invalid or already in use.Kindly re-enter");
                            editTextRegisterpwd.requestFocus();
                        }
                        catch (FirebaseAuthUserCollisionException e){
                            editTextRegisteremail.setError("User is already registered with this email.Use another email");
                            editTextRegisteremail.requestFocus();
                            editTextRegisterpwd.requestFocus();
                        }
                        catch (Exception e){
                            Log.e(TAG,e.getMessage());
                            Toast.makeText(Register.this,e.getMessage(),Toast.LENGTH_LONG).show();

                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });

    }
}