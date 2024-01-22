package com.example.health;

import static com.example.health.R.id.nav_analytics;
import static com.example.health.R.id.nav_home;
import static com.example.health.R.id.nav_profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.health.databinding.ActivityMainBinding;
import com.example.health.databinding.ActivityUserProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class UserProfileActivity extends AppCompatActivity {
    //ViewPager2 viewPager2;
    //ViewPageAdapter_bottomnav viewPageAdapter_bottomnav;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        //viewPager2=findViewById(R.id.view_pager2);
        //viewPageAdapter_bottomnav=new ViewPageAdapter_bottomnav(this);
        //viewPager2.setAdapter(viewPageAdapter_bottomnav);
        replaceFragment(new HomeFragment());
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id== nav_home){
                    replaceFragment(new HomeFragment());
                    return true;
                }
                else if(id== nav_analytics){
                    replaceFragment(new AnalyticsFragment());
                    return true;
                }
                else if(id== nav_profile){
                    replaceFragment(new ProfileFragment());
                    return true;
                }
                return true;
            }
        });




    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout,fragment);
        transaction.commit();
    }

}