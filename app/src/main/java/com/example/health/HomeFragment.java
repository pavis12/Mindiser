package com.example.health;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/
    private TextView welcome;
     FirebaseAuth authProfile;

    private String Dob,gender,fullname,mobile,last_login,userId,date;
    private ImageView imageViewAwesome,imageViewGood,imageViewOkay,imageViewBad,imageViewTerrible;
    private int awesome,good,okay,bad,terrible;
    private RelativeLayout dailyScreeningemojiLayout,dailyScreeningcheckAnalytics;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_home, container, false);
        //for navigating to screening page on expanding



        welcome=getActivity().findViewById(R.id.TextView_Welcome);
        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        if(firebaseUser==null){

            Toast.makeText(getActivity(),"Something went wrong! User's details are not available at the  moment",Toast.LENGTH_SHORT).show();
        }
        else {
            //checkifEmailVerified(firebaseUser);
            Button screening_expand = v.findViewById(R.id.screening_tool);
            screening_expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), QuizActivity.class);
                    startActivity(intent);
                }
            });

            imageViewAwesome = getActivity().findViewById(R.id.daily_awesome);
            imageViewGood = getActivity().findViewById(R.id.daily_good);
            imageViewOkay = getActivity().findViewById(R.id.daily_okay);
            imageViewBad = getActivity().findViewById(R.id.daily_bad);
            imageViewTerrible = getActivity().findViewById(R.id.daily_terrible);
            dailyScreeningemojiLayout = getActivity().findViewById(R.id.daily_screening_relative_layout);
            dailyScreeningcheckAnalytics = getActivity().findViewById(R.id.dailyScreening_check_analytics);


            userId = firebaseUser.getUid();
            //extract usr reference data from database for "Registered Users"
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
            //referenceProfile.child(userId).child("awesome").setValue(awesome);
            referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteUserDetails readUserdetails = snapshot.getValue(ReadWriteUserDetails.class);
                    if (readUserdetails != null) {
                        //fullname=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        //fullname=firebaseUser.getDisplayName();

                        last_login = readUserdetails.last_login;
                        //check if last logindate i.e the daily screening is alreday done today else make visible and then invisible.
                        //checklastlogin(last_login_date);
                        fullname = readUserdetails.fullname;
                        //Dob = readUserdetails.dob;
                        //gender = readUserdetails.gender;
                        //mobile = readUserdetails.mobile;
                        //welcome.setText("Welcome " + fullname + " !");
                        date = getCurrentDate();
                        //Toast.makeText(getActivity(), "today's date:" + date, Toast.LENGTH_LONG).show();
                        if (!(last_login.equals(date))||last_login.equals("") ) {
                            //dailyScreeningemojiLayout.setVisibility(View.VISIBLE);



                            awesome = readUserdetails.awesome;
                            good = readUserdetails.good;
                            okay = readUserdetails.okay;
                            bad = readUserdetails.bad;
                            terrible = readUserdetails.terrible;
                            int total_days = awesome + good + okay + bad + terrible + 1;
                            if (total_days > 30) {
                                awesome = 0;
                                good = 0;
                                okay = 0;
                                bad = 0;
                                terrible = 0;
                            }

                            //for upadting daily screening value

                            imageViewAwesome.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    awesome++;
                                    dailyScreeningemojiLayout.setVisibility(View.GONE);
                                    dailyScreeningcheckAnalytics.setVisibility(View.VISIBLE);
                                    referenceProfile.child(userId).child("awesome").setValue(awesome);


                                }
                            });
                            imageViewGood.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    good++;
                                    dailyScreeningemojiLayout.setVisibility(View.GONE);
                                    dailyScreeningcheckAnalytics.setVisibility(View.VISIBLE);
                                    referenceProfile.child(userId).child("good").setValue(good);

                                }
                            });
                            imageViewOkay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    okay++;
                                    dailyScreeningemojiLayout.setVisibility(View.GONE);
                                    dailyScreeningcheckAnalytics.setVisibility(View.VISIBLE);
                                    referenceProfile.child(userId).child("okay").setValue(okay);

                                }
                            });
                            imageViewBad.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bad++;
                                    dailyScreeningemojiLayout.setVisibility(View.GONE);
                                    dailyScreeningcheckAnalytics.setVisibility(View.VISIBLE);
                                    referenceProfile.child(userId).child("bad").setValue(bad);

                                }
                            });
                            imageViewTerrible.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    terrible++;
                                    dailyScreeningemojiLayout.setVisibility(View.GONE);
                                    dailyScreeningcheckAnalytics.setVisibility(View.VISIBLE);
                                    referenceProfile.child(userId).child("terrible").setValue(terrible);

                                }
                            });
                            referenceProfile.child(userId).child("last_login").setValue(date);

                        }
                        else{
                            dailyScreeningcheckAnalytics.setVisibility(View.VISIBLE);}




                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();

                }
            });
        }






        return v;
    }

    //@Override
    public void onResume() {
        super.onResume();

        welcome=getActivity().findViewById(R.id.TextView_Welcome);
        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        if(firebaseUser==null){

            Toast.makeText(getActivity(),"Something went wrong! User's details are not available at the  moment",Toast.LENGTH_SHORT).show();
        }
        else {
            //checkifEmailVerified(firebaseUser);
            Button screening_expand = getActivity().findViewById(R.id.screening_tool);
            screening_expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), QuizActivity.class);
                    startActivity(intent);

                }
            });

            imageViewAwesome = getActivity().findViewById(R.id.daily_awesome);
            imageViewGood = getActivity().findViewById(R.id.daily_good);
            imageViewOkay = getActivity().findViewById(R.id.daily_okay);
            imageViewBad = getActivity().findViewById(R.id.daily_bad);
            imageViewTerrible = getActivity().findViewById(R.id.daily_terrible);
            dailyScreeningemojiLayout = getActivity().findViewById(R.id.daily_screening_relative_layout);
            dailyScreeningcheckAnalytics = getActivity().findViewById(R.id.dailyScreening_check_analytics);


            userId = firebaseUser.getUid();
            //extract usr reference data from database for "Registered Users"
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
            //referenceProfile.child(userId).child("awesome").setValue(awesome);
            referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteUserDetails readUserdetails = snapshot.getValue(ReadWriteUserDetails.class);
                    if (readUserdetails != null) {
                        //fullname=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        //fullname=firebaseUser.getDisplayName();

                        last_login = readUserdetails.last_login;
                        //check if last logindate i.e the daily screening is alreday done today else make visible and then invisible.
                        //checklastlogin(last_login_date);
                        fullname = readUserdetails.fullname;
                        Dob = readUserdetails.dob;
                        gender = readUserdetails.gender;
                        mobile = readUserdetails.mobile;
                        welcome.setText("Welcome " + fullname + " !");
                        date = getCurrentDate();
                        //Toast.makeText(getActivity(), "today's date:" + date, Toast.LENGTH_LONG).show();
                        if (!(last_login.equals(date))||last_login == null ) {
                            dailyScreeningemojiLayout.setVisibility(View.VISIBLE);



                        awesome = readUserdetails.awesome;
                        good = readUserdetails.good;
                        okay = readUserdetails.okay;
                        bad = readUserdetails.bad;
                        terrible = readUserdetails.terrible;
                        int total_days = awesome + good + okay + bad + terrible + 1;
                        if (total_days > 30) {
                            awesome = 0;
                            good = 0;
                            okay = 0;
                            bad = 0;
                            terrible = 0;
                        }

                        //for upadting daily screening value

                        imageViewAwesome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                awesome++;
                                dailyScreeningemojiLayout.setVisibility(View.GONE);
                                dailyScreeningcheckAnalytics.setVisibility(View.VISIBLE);
                                referenceProfile.child(userId).child("awesome").setValue(awesome);


                            }
                        });
                        imageViewGood.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                good++;
                                dailyScreeningemojiLayout.setVisibility(View.GONE);
                                dailyScreeningcheckAnalytics.setVisibility(View.VISIBLE);
                                referenceProfile.child(userId).child("good").setValue(good);

                            }
                        });
                        imageViewOkay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                okay++;
                                dailyScreeningemojiLayout.setVisibility(View.GONE);
                                dailyScreeningcheckAnalytics.setVisibility(View.VISIBLE);
                                referenceProfile.child(userId).child("okay").setValue(okay);

                            }
                        });
                        imageViewBad.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bad++;
                                dailyScreeningemojiLayout.setVisibility(View.GONE);
                                dailyScreeningcheckAnalytics.setVisibility(View.VISIBLE);
                                referenceProfile.child(userId).child("bad").setValue(bad);

                            }
                        });
                        imageViewTerrible.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                terrible++;
                                dailyScreeningemojiLayout.setVisibility(View.GONE);
                                dailyScreeningcheckAnalytics.setVisibility(View.VISIBLE);
                                referenceProfile.child(userId).child("terrible").setValue(terrible);

                            }
                        });
                        referenceProfile.child(userId).child("last_login").setValue(date);

                        }
                        else{
                        dailyScreeningcheckAnalytics.setVisibility(View.VISIBLE);}



                        //for navigating to screening page on expanding


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();

                }
            });
        }

    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Get the current date
        Date today = new Date();

        // Format the date and return it as a string
        return dateFormat.format(today);
    }

    /*private boolean checklastlogin(String lastLoginDate) {


    }*/

    //users coming to userprofile activity after successful registrartion
    private void checkifEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now.You can login without email verification next time.");
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
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/


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