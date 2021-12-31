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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

public class TranslateHomeFragment extends Fragment {
    TextView textViewTranslTransferFragment;
    ImageView imageViewSwapLang;
    Animation animation;
    RecyclerView recyclerViewHistory;
    TranslationHistoryAdapter translationHistoryAdapter;

    //material textviews for selecting languages
    private MaterialTextView materialLang1, materialLang2;

    SharedPreferences mPreference;
    SharedPreferences.Editor mEditor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_translate_home, container, false);

        //initialize preference
        mPreference= PreferenceManager.getDefaultSharedPreferences(getContext());
        mEditor=mPreference.edit();

        textViewTranslTransferFragment = view.findViewById(R.id.textview_enter_text);

        imageViewSwapLang=view.findViewById(R.id.img_btn_swapping);

        //initializing the animation for image view click
        animation= AnimationUtils.loadAnimation(getActivity(),R.anim.img_button_animation);

        //initializing material textview which are used to select languages from & to translate
        materialLang1 =view.findViewById(R.id.lang_selector_1);
        materialLang2 =view.findViewById(R.id.lang_selector_2);

        //initializing history recyclerview
        recyclerViewHistory=view.findViewById(R.id.recyclerview_history);

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

    public void languageSelectionHome()
    {
        //click listener for lang 1
        materialLang1.setOnClickListener(v-> {
            Intent intent=new Intent(getActivity(),LanguageListActivity.class);
            intent.putExtra("value","Lang1");
            startActivity(intent);
        });

        //click listener for lang 2
        materialLang2.setOnClickListener(v->{
            Intent intent=new Intent(getActivity(),LanguageListActivity.class);
            intent.putExtra("value","Lang2");
            startActivity(intent);
        });
    }

    public void setRecyclerview()
    {
        //setting the layout for recyclerview
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerViewHistory.setLayoutManager(linearLayoutManager);

        //setting the adapter to the recyclerview
        translationHistoryAdapter=new TranslationHistoryAdapter(getActivity());
        recyclerViewHistory.setAdapter(translationHistoryAdapter);
    }

    public void navigateToFragmentTranslation(View view)
    {
        textViewTranslTransferFragment.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_bottom_nav_translation_to_fragmentTranslation);

        });
    }

    public void swapLanguages()
    {
        imageViewSwapLang.setOnClickListener(v ->imageViewSwapLang.startAnimation(animation));
    }

    public void checkSharePreference(){
        String srcLang,trgtLang,srcLangCode,trgtLangCode;

        srcLang=mPreference.getString(getString(R.string.lang_one),"Lang 1");
        srcLangCode=mPreference.getString(getString(R.string.lang_one_code),"Lang 1 Code");
        trgtLang=mPreference.getString(getString(R.string.lang_two),"Lang 2");
        trgtLangCode=mPreference.getString(getString(R.string.lang_two_code),"Lang 2 Code");

        materialLang1.setText(srcLang);
        Toast.makeText(getActivity(), "Src Code: "+srcLangCode, Toast.LENGTH_SHORT).show();

        materialLang2.setText(trgtLang);
        Toast.makeText(getActivity(), "Target Code: "+trgtLangCode, Toast.LENGTH_SHORT).show();
    }

}
