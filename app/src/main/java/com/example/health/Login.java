package com.example.health;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText editTextLoginemail,editTextLoginpwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final  String TAG="LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextLoginemail=findViewById(R.id.editText_login_email);
        editTextLoginpwd=findViewById(R.id.editText_login_pwd);
        progressBar=findViewById(R.id.progressBarLogin);
        authProfile=FirebaseAuth.getInstance();
        TextView buttonforgotpassword=findViewById(R.id.button_forgotPassowrd);
        TextView noaccount=findViewById(R.id.noaccount);
        noaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,Register.class);
                startActivity(intent);
                finish();
            }
        });
        //reset password
        buttonforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this,"You can reset your password now!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(Login.this,ForgotPasswordActivity.class));
            }
        });

        //show hide password using eye icon
        ImageView imageViewShowhidepwd=findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowhidepwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowhidepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextLoginpwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //if pwd visible hide
                    editTextLoginpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //change icon
                    imageViewShowhidepwd.setImageResource(R.drawable.ic_hide_pwd);
                }
                else{
                    editTextLoginpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowhidepwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });
        Button buttonLogin =findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail=editTextLoginemail.getText().toString();
                String textpwd=editTextLoginpwd.getText().toString();
                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(Login.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    editTextLoginemail.setError("Email is required");
                    editTextLoginemail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(Login.this,"Please re-enter your email",Toast.LENGTH_LONG).show();
                    editTextLoginemail.setError("Valid email is required");
                    editTextLoginemail.requestFocus();
                }
                else if(TextUtils.isEmpty(textpwd)){
                    Toast.makeText(Login.this,"Please enter your Password",Toast.LENGTH_LONG).show();
                    editTextLoginpwd.setError("Password is required");
                    editTextLoginpwd.requestFocus();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textpwd);
                }
            }
        });


    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(Login.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //get the instance of the current user
                    FirebaseUser firebaseUser=authProfile.getCurrentUser();
                    //check if email is verified before user can go to their profile
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(Login.this,"You are logged in now",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this,UserProfileActivity.class));
                        finish();
                    }
                    else{
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();//signout user
                        showAlertDialog();
                    }
                }
                else{
                    try{
                        throw task.getException();
                    }
                    catch(FirebaseAuthInvalidUserException e){
                        editTextLoginemail.setError("User does not exist or is no longer valid.Please register again");
                        editTextLoginemail.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        editTextLoginemail.setError("Invalid credentials.Kindly, check and re-enter");
                        editTextLoginemail.requestFocus();
                    }
                    catch(Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Login.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now.You can login without email verification");
        //open email apps if user clicks/taps continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent =new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        //create the alertDialog
        AlertDialog alertDialog=builder.create();
        //shoe the dialog box
        alertDialog.show();

    }
    //check if user is alredy logged in.In such case,straightaway take the user to Userprofile activity
    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser()!=null){
            Toast.makeText(Login.this,"Already Logged In!",Toast.LENGTH_SHORT).show();
            //start the userprofile activity
            startActivity(new Intent(Login.this,UserProfileActivity.class));
            finish();//close login activity
        }
        else{
            Toast.makeText(Login.this,"You can login now",Toast.LENGTH_SHORT).show();
        }
    }
}