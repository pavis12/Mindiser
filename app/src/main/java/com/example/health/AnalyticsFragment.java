package com.example.health;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnalyticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnalyticsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AnalyticsFragment() {
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
    public static AnalyticsFragment newInstance(String param1, String param2) {
        AnalyticsFragment fragment = new AnalyticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    private FirebaseAuth authProfile;

    private List<String> xValues = Arrays.asList(" "," "," "," "," ");
    private int awesome=0,good=0,okay=0,bad=0,terrible=0;
    private BarChart barChart;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_analytics, container, false);
        super.onCreate(savedInstanceState);

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        if(firebaseUser==null){

            Toast.makeText(getActivity(),"Something went wrong! User's details are not available at the  moment",Toast.LENGTH_SHORT).show();
        }
        else{



            String userId=firebaseUser.getUid();
            //extract usr reference data from database for "Registered Users"
            DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
            //referenceProfile.child(userId).child("awesome").getValue()
            referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteUserDetails readUserdetails=snapshot.getValue(ReadWriteUserDetails.class);
                    if(readUserdetails!=null){
                        //fullname=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        //fullname=firebaseUser.getDisplayName();



                        awesome=readUserdetails.awesome;
                        good=readUserdetails.good;
                        okay=readUserdetails.okay;
                        bad=readUserdetails.bad;
                        terrible=readUserdetails.terrible;
                        Toast.makeText(getActivity(),"aewsome "+awesome,Toast.LENGTH_LONG).show();
                        BarChart barChart = v.findViewById(R.id.chart);
                        barChart.getAxisRight().setDrawLabels(false);

                        ArrayList <BarEntry> entries = new ArrayList<>();
                        entries.add(new BarEntry(0,awesome));
                        entries.add(new BarEntry(1,good));
                        entries.add(new BarEntry(2,okay));
                        entries.add(new BarEntry(3,bad));
                        entries.add(new BarEntry(4,terrible));

                        YAxis yAxis = barChart.getAxisLeft();
                        yAxis.setAxisMaximum(0f);
                        yAxis.setAxisMaximum(30f);
                        yAxis.setAxisLineWidth(1f);
                        yAxis.setAxisLineColor(Color.BLACK);
                        yAxis.setLabelCount(3);
                        barChart.getAxisRight().setDrawGridLines(false);
                        barChart.getAxisLeft().setDrawGridLines(false);
                        barChart.getXAxis().setDrawGridLines(false);

                        BarDataSet dataSet = new BarDataSet(entries, "Subjects");

                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        dataSet.setDrawValues(false);

                        BarData barData = new BarData(dataSet);

                        barChart.setData(barData);

                        barChart.getDescription().setEnabled(false);
                        barChart.invalidate();
                        barChart.getLegend().setEnabled(false);
                        //barChart.setRenderer(new (barChart, barChart.getAnimator(), barChart.getViewPortHandler()));


                        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
                        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        barChart.getXAxis().setGranularity(1f);
                        barChart.getXAxis().setGranularityEnabled(true);




                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(),"Something went wrong!",Toast.LENGTH_LONG).show();

                }
            });
        }



        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //getSupportActionBar().setTitle("Your Analytic");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        /*BarChart barChart = v.findViewById(R.id.chart);
        barChart.getAxisRight().setDrawLabels(false);

        ArrayList <BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0,20f));
        entries.add(new BarEntry(1,good));
        entries.add(new BarEntry(2,okay));
        entries.add(new BarEntry(3,bad));
        entries.add(new BarEntry(4,terrible));

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMaximum(0f);
        yAxis.setAxisMaximum(30f);
        yAxis.setAxisLineWidth(1f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(3);

        BarDataSet dataSet = new BarDataSet(entries, "Subjects");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        barChart.getDescription().setEnabled(false);
        barChart.invalidate();

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);*/
        //barChart = v.findViewById(R.id.chart);

        //setData();


        return v;
    }

    /*private void setData() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        awesome=15;
        barEntries.add(new BarEntry(0, awesome));
        barEntries.add(new BarEntry(1, good));
        barEntries.add(new BarEntry(2, okay));
        barEntries.add(new BarEntry(3, bad));
        barEntries.add(new BarEntry(4, terrible));

        BarDataSet barDataSet = new BarDataSet(barEntries, "User Activity");
        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.invalidate(); // Refresh

        // Align X-axis labels with bars
        barChart.getXAxis().setLabelCount(barEntries.size());

        // Other chart customizations
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setEnabled(false);

        barChart.setTouchEnabled(false);

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Mon");
        labels.add("Tue");
        labels.add("Wed");
        labels.add("Thu");
        labels.add("Fri");
        labels.add("Sat");
        labels.add("Today");
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getAxisLeft().setAxisMinValue(0);
        barChart.getAxisLeft().setAxisMaxValue(30);

        barChart.getDescription().setEnabled(false);
    }*/



    @Override


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