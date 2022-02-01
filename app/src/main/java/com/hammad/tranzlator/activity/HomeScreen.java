package com.hammad.tranzlator.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.hammad.tranzlator.BuildConfig;
import com.hammad.tranzlator.R;
import com.hammad.tranzlator.entities.TranslatedDataEntity;
import com.hammad.tranzlator.TranslationRoomDB;
import com.hammad.tranzlator.adapter.TranslationHistoryAdapter;

import java.util.List;

public class HomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;

    List<TranslatedDataEntity> dataEntityList;

    //initializing database
    TranslationRoomDB database;

    //shared preference for storing the switched theme (Dark or Light) value/status
    SharedPreferences preference;
    SharedPreferences.Editor prefEditor;

    //variable for checking whether dark theme is applied or not
    boolean isDarkModeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        database = TranslationRoomDB.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        //initializing preferences for Theme switching
        initializePreference();

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

        //getting the theme saved value from preference
        isDarkModeEnabled=preference.getBoolean(getString(R.string.pref_theme),false);

        //checking theme preference value
        if(isDarkModeEnabled)
        {
            AppCompatDelegate.setDefaultNightMode((AppCompatDelegate.MODE_NIGHT_YES));
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //the overridden method which will handles the click events of navigation view
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_drawer_dark_mode:
                Switch modeSwitch = (Switch) item.getActionView();

                modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (modeSwitch.isChecked()) {
                        AppCompatDelegate.setDefaultNightMode((AppCompatDelegate.MODE_NIGHT_YES));

                        //saving the theme to preference
                        prefEditor.putBoolean(getString(R.string.pref_theme),true);
                        prefEditor.apply();

                    } else if (!modeSwitch.isChecked()) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                        //saving the theme to preference
                        prefEditor.putBoolean(getString(R.string.pref_theme),false);
                        prefEditor.apply();
                    }
                });
                break;

            case R.id.nav_drawer_remove_ads:
                Toast.makeText(this, "Remove ads", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_drawer_share:
                shareAppLink();
                break;

            case R.id.nav_drawer_clear_history:
                alertDialog();
                break;

            case R.id.nav_drawer_privacy_policy:
                privacyPolicy();
                break;

        }

        return true;
    }

    public void shareAppLink() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String subSectionLink = "Download Tranzlator app from:\n\n\t\"https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID;
        intent.putExtra(Intent.EXTRA_TEXT, subSectionLink);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    public void clearHistory() {
        //getting the data list from database
        dataEntityList = database.translationHistoryDao().getAllTranslatedData();

        //delete all data from database
        database.translationHistoryDao().deleteAllTranslatedData(dataEntityList);

        dataEntityList.clear();
        dataEntityList.addAll(database.translationHistoryDao().getAllTranslatedData());

        TranslationHistoryAdapter translationHistoryAdapter = new TranslationHistoryAdapter(this, dataEntityList);

        //calling this intent in same activity so that it can refresh the activity and history recyclerview values can be updated
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }

    public void alertDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setTitle("Clear History")
                .setMessage("want to clear History?")
                .setCancelable(false)
                .setPositiveButton("Clear", (dialog, which) -> clearHistory())
                .setNegativeButton("Cancel", (dialog, which) -> {});

        builder.show();
    }

    public void privacyPolicy()
    {
        Uri uri=Uri.parse("https://www.google.com");
        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }

    private void initializePreference() {
        preference = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = preference.edit();
    }
}
