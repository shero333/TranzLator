package com.hammad.tranzlator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class TranslateHomeFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    TextView textViewTranslTransferFragment;
    ImageView imageViewSwapLang;
    Animation animation;
    RecyclerView recyclerViewHistory;
    TranslationHistoryAdapter translationHistoryAdapter;

    //material textviews for selecting languages
    private MaterialTextView materialLang1, materialLang2;

    SharedPreferences mPreference;
    SharedPreferences.Editor mEditor;

    //string variables for storing source & target languages and codes
    String srcLang, trgtLang, srcLangCode, trgtLangCode;

    //integer variable for handling the condition in onSharedPreferenceChanged() listener
    int sharedPrefChangedChecker = 0;

    //this variable is used for conditioning the update preferences function
    int prefDecrement=0;

    //list of languages entity class
    List<TranslatedDataEntity> languageDataList=new ArrayList<>();

    //room database class
    TranslationRoomDB database;

    //translated data list
    List<TranslatedDataEntity> entityDataList =new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_translate_home, container, false);

        //initialize preference
        mPreference = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEditor = mPreference.edit();

        textViewTranslTransferFragment = view.findViewById(R.id.textview_enter_text);

        imageViewSwapLang = view.findViewById(R.id.img_btn_swapping);

        //initializing the animation for image view click
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.img_button_animation);

        //initializing material textview which are used to select languages from & to translate
        materialLang1 = view.findViewById(R.id.lang_selector_1);
        materialLang2 = view.findViewById(R.id.lang_selector_2);

        //initializing history recyclerview
        recyclerViewHistory = view.findViewById(R.id.recyclerview_history);

        //initializing room DB
        database =TranslationRoomDB.getInstance(getContext());

        //swap languages function
        swapLanguages();

        //navigate to fragment translation
        navigateToFragmentTranslation(view);

        //setting the recyclerview
        setRecyclerview();

        //function for handling the language selection tab clicks
        languageSelectionHome();

        //function for displaying the source & target languages from list
        checkSharePreference();

        return view;
    }

    public void languageSelectionHome() {
        //click listener for lang 1
        materialLang1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LanguageListActivity.class);
            intent.putExtra("value", "Lang1");
            startActivity(intent);
        });

        //click listener for lang 2
        materialLang2.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LanguageListActivity.class);
            intent.putExtra("value", "Lang2");
            startActivity(intent);
        });
    }

    public void setRecyclerview() {
        //setting the layout for recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewHistory.setLayoutManager(linearLayoutManager);

        //getting the list from database
        entityDataList=database.translationHistoryDao().getAllTranslatedData();

        //setting the adapter to the recyclerview
        translationHistoryAdapter = new TranslationHistoryAdapter(getActivity(), entityDataList);
        recyclerViewHistory.setAdapter(translationHistoryAdapter);

    }

    public void navigateToFragmentTranslation(View view) {
        textViewTranslTransferFragment.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_bottom_nav_translation_to_fragmentTranslation));
    }

    public void swapLanguages() {
        imageViewSwapLang.setOnClickListener(v -> {
            imageViewSwapLang.startAnimation(animation);
            reserveTranslationLanguages();
        });
    }

    public void checkSharePreference() {
        srcLang = mPreference.getString(getString(R.string.lang_one), "English");
        srcLangCode = mPreference.getString(getString(R.string.lang_one_code), "en");
        trgtLang = mPreference.getString(getString(R.string.lang_two), "Urdu");
        trgtLangCode = mPreference.getString(getString(R.string.lang_two_code), "ur");

        materialLang1.setText(srcLang);

        materialLang2.setText(trgtLang);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /*
        this 'if' condition here is used to check if there is any update/changed in the shared preference values.
         This if condition is true when reverseTranslationLanguages() function is called
        */
        if (sharedPrefChangedChecker >= 1) {
            updateSharedPreferences();
        } else {
            checkSharePreference();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        LanguageListActivity.registerPreference(getActivity(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LanguageListActivity.unregisterPreference(getActivity(), this);
    }

    public void reserveTranslationLanguages() {
        //reversing the values of source language & code with target language & code
        String tempLang, tempLangCode;
        tempLang = srcLang;
        tempLangCode = srcLangCode;

        srcLang = trgtLang;
        srcLangCode = trgtLangCode;

        trgtLang = tempLang;
        trgtLangCode = tempLangCode;

        //incrementing this integer variable here to handle the onSharedPreferenceChanged() interface conditions
        sharedPrefChangedChecker++;

        /*
          onSharedPreferenceChanged() listener is called when we add/remove values from shared preference
          The putString function below will call onSharedPreferenceChanged() listener,
          in which we have conditions to execute a particular piece of code
         */
        mEditor.putString(getString(R.string.lang_one), srcLang).apply();

        //setting the changed source & target translation languages
        materialLang1.setText(srcLang);
        materialLang2.setText(trgtLang);

        //condition to reset the prefDecrement & sharedPrefChangedChecker values so that the updatePreference function can execute properly
        //to see more details on this condition, go to FragmentTranslation class, and then go to reserveTranslationLanguages() function
        if(prefDecrement <=4)
        {
            sharedPrefChangedChecker=0;
            prefDecrement=0;
        }
    }

    public void updateSharedPreferences() {
        //updates the prefDecrement variable value so that we can reset its values in reserveTranslationLanguages()
        prefDecrement++;

        //mEditor.putString(getString(R.string.lang_one), srcLang).apply();
        mEditor.putString(getString(R.string.lang_one_code), srcLangCode).apply();
        mEditor.putString(getString(R.string.lang_two), trgtLang).apply();
        mEditor.putString(getString(R.string.lang_two_code), trgtLangCode).apply();
    }

}
