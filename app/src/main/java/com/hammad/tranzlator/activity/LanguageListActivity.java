package com.hammad.tranzlator.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.tranzlator.adapter.LanguagesAdapter;
import com.hammad.tranzlator.R;

public class LanguageListActivity extends AppCompatActivity implements LanguagesAdapter.OnLanguageSelectionListener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    LanguagesAdapter languagesAdapter;
    SharedPreferences mPreference;
    SharedPreferences.Editor mEditor;
    boolean bLangOnePressed, bLangTwoPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_list);

        //initializing toolbar
        toolbar = findViewById(R.id.toolbar_language_activity);
        setSupportActionBar(toolbar);

        //sets the back button on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //initializing recyclerview
        recyclerView = findViewById(R.id.recyclerview_languages);

        //setting default values for boolean variables
        bLangOnePressed = false;
        bLangTwoPressed = false;

        //initializing shared preferences
        initializePreference();

        //this function sets the toolbar title text based on item clicked from FragmentTranslation
        setTitleBarText();

        //this function setup the recyclerview for supported languages
        setupRecyclerview();

    }

    public void setupRecyclerview() {
        //setting the layout for recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //setting the adapter to recyclerview
        languagesAdapter = new LanguagesAdapter(this, this, bLangOnePressed, bLangTwoPressed);
        recyclerView.setAdapter(languagesAdapter);
    }

    //this handles the back pressed button on toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //handling the searchview of toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.language_search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_lang_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();

        //setting the options to done on soft keyboard so that it can disappears
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        //setting the searchview properties
        searchView.setQueryHint("Search Language");
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                languagesAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void initializePreference() {
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreference.edit();
    }

    public void setTitleBarText() {
        //getting the intent from FragmentTranslation to set the title text
        Intent intent = getIntent();
        String buttonPressed = intent.getStringExtra("value");
        if (buttonPressed.equals("Lang1")) {
            toolbar.setTitle("Translate From");
            bLangOnePressed = true;
        } else if (buttonPressed.equals("Lang2")) {
            toolbar.setTitle("Translate To");
            bLangTwoPressed = true;
        }
    }

    //overridden function of OnLanguageSelectionListener interface
    @Override
    public void onLanguageSelection(String lang, String langCode, boolean btnOnePressed, boolean btnTwoPressed) {
        if (btnOnePressed) {
            mEditor.putString(getString(R.string.lang_one), lang);
            mEditor.apply();
            mEditor.putString(getString(R.string.lang_one_code), langCode);
            mEditor.apply();
        }
        else if (btnTwoPressed) {
            mEditor.putString(getString(R.string.lang_two), lang);
            mEditor.apply();
            mEditor.putString(getString(R.string.lang_two_code), langCode);
            mEditor.apply();
        }
        finish();

    }

    //functions for preference change listeners
    public static void registerPreference(Context context,SharedPreferences.OnSharedPreferenceChangeListener listener)
    {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterPreference(Context context,SharedPreferences.OnSharedPreferenceChangeListener listener)
    {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

}