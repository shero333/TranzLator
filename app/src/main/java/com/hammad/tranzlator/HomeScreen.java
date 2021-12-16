package com.hammad.tranzlator;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class HomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        //code for menu button on top left side of toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //setting the onClick for navigation view items
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
        these lines of code are used to set the checked item to dark mode menu option &
        execute the action associated with the menu item.
        now we can handles the onClicks directly on toggle switch of Dark Mode
        */
        navigationView.setCheckedItem(R.id.nav_drawer_dark_mode);
        navigationView.getMenu().performIdentifierAction(R.id.nav_drawer_dark_mode, 0);

        //Bottom Navigation View
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //setting up the navigation component
        NavController navController = Navigation.findNavController(this, R.id.fragment_container);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //bottom navigation view click listener
        //bottomNavigationView.setOnItemSelectedListener(bottomNav);

        //sets the selected item to translation when application starts
        // bottomNavigationView.setSelectedItemId(R.id.bottom_nav_translation);

        //By default the home fragement (translate fragment here) will be displayed
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container,new TranslateHomeFragment())
//                .commit();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    //the overridden method which will handles the click events of navigation view
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_drawer_dark_mode:
                Switch modeSwitch = (Switch) item.getActionView();

                modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (modeSwitch.isChecked()) {
                            Toast.makeText(HomeScreen.this, "Switch checked", Toast.LENGTH_SHORT).show();
                        } else if (!modeSwitch.isChecked()) {
                            Toast.makeText(HomeScreen.this, "Switch un-checked", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case R.id.nav_drawer_remove_ads:
                Toast.makeText(this, "Remove ads", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_drawer_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_drawer_clear_history:
                Toast.makeText(this, "Clear History", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_drawer_privacy_policy:
                Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show();
                break;

        }

        return true;
    }

    //click listener for bottom navigation view items

/*
    private NavigationBarView.OnItemSelectedListener bottomNav=new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment=null;

            switch (item.getItemId())
            {
                case R.id.bottom_nav_translation:
//                    NavController navController= Navigation.findNavController(HomeScreen.this,R.id.fragment_container);
                   // selectedFragment=new TranslateHomeFragment();
                    break;

                case R.id.bottom_nav_conversation:
//                    NavController navController= Navigation.findNavController(HomeScreen.this,R.id.fragment_container);
//                    Toast.makeText(getApplicationContext(), "Conversation", Toast.LENGTH_SHORT).show();
                    //selectedFragment=new ConversationFragment();

                    break;

                case R.id.bottom_nav_dictionary:
                    Toast.makeText(getApplicationContext(), "Dictionary", Toast.LENGTH_SHORT).show();
                   // selectedFragment=new DictionaryFragment();
                    break;
            }


//            getSupportFragmentManager().beginTransaction().
//                    replace(R.id.fragment_container,selectedFragment).
//                    commit();

            return true;
        }
    };
*/
}
