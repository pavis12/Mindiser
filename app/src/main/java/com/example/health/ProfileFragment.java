package com.example.health;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnalyticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        setHasOptionsMenu(true);

    }
    private TextView textViewFullName,textViewEmail,textViewdob,textViewgender,textViewMobile;
    private String fullname,email,dob,gender,mobile;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_profile, container, false);
        textViewFullName=v.findViewById(R.id.textView_show_full_name);
        textViewEmail=v.findViewById(R.id.textView_show_email);
        textViewdob=v.findViewById(R.id.textView_show_dob);
        textViewgender=v.findViewById(R.id.textView_show_gender);
        textViewMobile=v.findViewById(R.id.textView_show_mobile);
        progressBar=v.findViewById(R.id.show_progress_bar);
        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        if(firebaseUser==null){

            Toast.makeText(getActivity(),"Something went wrong! User's details are not available at the  moment",Toast.LENGTH_SHORT).show();
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
        //for creating action bar inside a fragment


        // Your fragment code goes here
        return v;
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userId=firebaseUser.getUid();
        //extract usr reference data from database for "Registered Users"
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserdetails=snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserdetails!=null) {
                    //fullname=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    //fullname=firebaseUser.getDisplayName();
                    fullname = readUserdetails.fullname;
                    email = firebaseUser.getEmail();
                    dob = readUserdetails.dob;
                    gender = readUserdetails.gender;
                    mobile = readUserdetails.mobile;

                    textViewFullName.setText(fullname);
                    textViewEmail.setText(email);
                    textViewdob.setText(dob);
                    textViewgender.setText(gender);
                    textViewMobile.setText(mobile);

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"Something went wrong!",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
        //creating ActionBar


    }

    //@Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_settings,menu);
        //menu.findItem(R.id.menu_refresh).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }
    //when any menu item is selected
    //@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
         if(id==R.id.menu_update_profile){
            Intent intent=new Intent(getActivity(),UpdateProfileActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.menu_update_email){
            Intent intent=new Intent(getActivity(),UpdateEmailActivity.class);
             startActivity(intent);
        }

        else if(id==R.id.menu_settings){
            Intent intent=new Intent(getActivity(),SettingsActivity.class);
             startActivity(intent);
        }
        else if(id==R.id.menu_change_password){
            Intent intent=new Intent(getActivity(),ChangepasswordActivity.class);
             startActivity(intent);
        }
        else if(id==R.id.menu_delete_profile){
            Intent intent=new Intent(getActivity(),DeleteprofileActivity.class);
             startActivity(intent);
        }
        else if(id==R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(getActivity(),"You are logged out",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getActivity(),MainActivity.class);
            //clear stack to prevent user coming back to Userprofileactivity after pressing back button
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP|intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //finish();
        }

        return super.onOptionsItemSelected(item);
    }



}