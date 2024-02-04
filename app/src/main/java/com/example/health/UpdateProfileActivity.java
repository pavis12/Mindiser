package com.example.health;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    private EditText editTextUpdateName,editTextupdateDob,editTextUpdatemobile;
    private RadioGroup radioGroupUpdategender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String textDob,textgender,textfullname,textmobile,last_login;
    FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private int awesome,good,okay,bad,terrible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setTitle("Update profile details");
        progressBar=findViewById(R.id.show_progress_bar_update_profile);
        editTextUpdateName=findViewById(R.id.editText_update_profile_name);
        editTextupdateDob=findViewById(R.id.editText_update_profile_dob);
        editTextUpdatemobile=findViewById(R.id.editText_update_profile_mobile);
        radioGroupUpdategender=findViewById(R.id.radio_group_update_gender);
        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        showProfileFirebase(firebaseUser);
        //update email
        Button buttonupdateEmail=findViewById(R.id.button_profile_update_email);
        buttonupdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });
        editTextupdateDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //extracing saved dd,mm,yyyy,into diff variables by creating an array delimilted by "/"
                String textSADob[]=textDob.split("/");
                int day =Integer.parseInt(textSADob[0]);
                int month=Integer.parseInt(textSADob[1])-1;
                int year=Integer.parseInt(textSADob[2]);

                DatePickerDialog picker;
                picker=new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextupdateDob.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });
        //update profile
        Button buttonUpdateprofile=findViewById(R.id.button_update_profile);
        buttonUpdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID=radioGroupUpdategender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected=findViewById(selectedGenderID);
        String mobileregex="^\\d{10}";
        Matcher mobilematcher;
        Pattern mobilepattern=Pattern.compile(mobileregex);
        mobilematcher=mobilepattern.matcher(textmobile);

        if (TextUtils.isEmpty(textfullname)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full Name is required");
            editTextUpdateName.requestFocus();


        } else if (TextUtils.isEmpty(textDob)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your dob", Toast.LENGTH_LONG).show();
            editTextupdateDob.setError("Date of birth is required");
            editTextupdateDob.requestFocus();
        } else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your gender", Toast.LENGTH_LONG).show();
            radioButtonUpdateGenderSelected.setError("Gender is required");
            radioButtonUpdateGenderSelected.requestFocus();
        } else if (TextUtils.isEmpty(textmobile)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your mobile no.", Toast.LENGTH_LONG).show();
            editTextUpdatemobile.setError("Mobile no. is required");
           editTextUpdatemobile.requestFocus();
        } else if (textmobile.length() != 10) {
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter your mobile no.", Toast.LENGTH_LONG).show();
            editTextUpdatemobile.setError("Mobile no. should be 10 digits");
           editTextUpdatemobile.requestFocus();
        }
        else if(!mobilematcher.find()){
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter your mobile no.", Toast.LENGTH_LONG).show();
            editTextUpdatemobile.setError("Mobile no. is not valid");
            editTextUpdatemobile.requestFocus();
        }
        else {
            textgender = radioButtonUpdateGenderSelected.getText().toString();
            textfullname=editTextUpdateName.getText().toString();
            textDob=editTextupdateDob.getText().toString();
            textmobile=editTextUpdatemobile.getText().toString();
            //enter user data into firebase realtime databse.
            ReadWriteUserDetails writeUserDetails=new ReadWriteUserDetails(textfullname,textDob,textgender,textmobile,last_login,awesome,good,okay,bad,terrible);
            //extract user refernce for db for registered user
            DatabaseReference referenceprofile=FirebaseDatabase.getInstance().getReference("Registered Users");
            String userId=firebaseUser.getUid();
            progressBar.setVisibility(View.VISIBLE);
            referenceprofile.child(userId).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //setting new display name
                        UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder().setDisplayName(textfullname).build();
                        firebaseUser.updateProfile(profileUpdates);
                        Toast.makeText(UpdateProfileActivity.this,"Update Successful!",Toast.LENGTH_LONG).show();
                        //stop user from returning to update profile activity on back pressing a button and close activity
                        Intent intent=new Intent(UpdateProfileActivity.this,UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                    else{
                        try{
                            throw  task.getException();
                        }
                        catch (Exception e){
                            Toast.makeText(UpdateProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });

        }
    }

    private void showProfileFirebase(FirebaseUser firebaseUser) {
        String userIdofRegistered=firebaseUser.getUid();
        DatabaseReference referenceprofile= FirebaseDatabase.getInstance().getReference("Registered Users");
        progressBar.setVisibility(View.VISIBLE);
        referenceprofile.child(userIdofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails!=null){
                    textfullname=readUserDetails.fullname;
                    textDob=readUserDetails.dob;
                    textgender=readUserDetails.gender;
                    textmobile=readUserDetails.mobile;
                    editTextUpdateName.setText(textfullname);
                    editTextupdateDob.setText(textDob);
                    editTextUpdatemobile.setText(textmobile);
                    last_login=readUserDetails.last_login;
                    awesome=readUserDetails.awesome;
                    good=readUserDetails.good;
                    okay=readUserDetails.okay;
                    bad=readUserDetails.bad;
                    terrible=readUserDetails.terrible;
                    if(textgender.equals("Male")){
                        radioButtonUpdateGenderSelected=findViewById(R.id.radio_update_male);

                    }
                    else{
                        radioButtonUpdateGenderSelected=findViewById(R.id.radio_update_female);

                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                }
                else{
                    Toast.makeText(UpdateProfileActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}