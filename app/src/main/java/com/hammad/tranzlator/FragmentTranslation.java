package com.hammad.tranzlator;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.IOException;
import java.io.InputStream;

public class FragmentTranslation extends Fragment implements PopupMenu.OnMenuItemClickListener {

    ImageView editTextImageVolumeUp, editTextImageSpeak;
    ImageView textViewImageVolumeUp, textViewImageCopy, textViewImageMoreOptions;
    TextView textViewTranslation;
    TextInputEditText inputEditText;
    ImageView imageViewSwapLang;
    Animation animation;

    //initializing the cloud translation
    Translate translate;

    //material textviews for selecting languages
    MaterialTextView materialTxtViewLang1,materialTxtViewLang2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);

        //initializing material textview which are used to select languages from & to translate
        materialTxtViewLang1=view.findViewById(R.id.lang_selector_1);
        materialTxtViewLang2=view.findViewById(R.id.lang_selector_2);

        //initializing swap language image view
        imageViewSwapLang=view.findViewById(R.id.img_btn_swapping);

        //loading animation
        animation= AnimationUtils.loadAnimation(getActivity(),R.anim.img_button_animation);

        imageViewSwapLang.setOnClickListener(v -> imageViewSwapLang.startAnimation(animation));

        //input text initialization
        inputEditText = view.findViewById(R.id.edittext_input_layout_translation);

        //image view related to edit text initialization
        editTextImageVolumeUp = view.findViewById(R.id.edittext_imageview_volume_up);
        editTextImageSpeak = view.findViewById(R.id.edittext_imageview_speak_translation);

        //text view translated initialization
        textViewTranslation = view.findViewById(R.id.textview_translated);

        //image views related to text view translated initialization
        textViewImageVolumeUp = view.findViewById(R.id.textview_imageview_volume_up);
        textViewImageCopy = view.findViewById(R.id.textview_imageview_copy_content);
        textViewImageMoreOptions = view.findViewById(R.id.textview_imageview_more);

        inputEditText.addTextChangedListener(textWatcher);

        //popup menu for clicking on more option in translated text view
        textViewImageMoreOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            popupMenu.inflate(R.menu.textview_translation_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        });

        //this method handles the click listeners for translation language selection
        languageSelection();

        return view;
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.textview_translation_share:
                Toast.makeText(getContext(), "Share", Toast.LENGTH_SHORT).show();
                break;

            case R.id.textview_translation_fullscreen:
                Toast.makeText(getContext(), "Full Screen", Toast.LENGTH_SHORT).show();
                break;

            case R.id.textview_translation_reverse_translation:
                Toast.makeText(getContext(), "Reverse Translation", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() > 0) {
                editTextImageSpeak.setImageResource(R.drawable.ic_arrow_forward);
                editTextImageVolumeUp.setVisibility(View.VISIBLE);

                editTextImageSpeak.setOnClickListener(v -> {
                    if(checkInternetConnection())
                    {
                        //setting the translation service
                        getTranslateService();

                        //translating the text
                        textViewTranslation.setText(translate(s.toString(),"en","ur"));

                        //setting the visibility of textview where translated text is set
                        textViewTranslation.setVisibility(View.VISIBLE);

                        textViewImageVolumeUp.setVisibility(View.VISIBLE);
                        textViewImageCopy.setVisibility(View.VISIBLE);
                        textViewImageMoreOptions.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Toast.makeText(getContext(), "No Internet connection! Cannot be translated", Toast.LENGTH_SHORT).show();
                    }

                });
            } else {
                editTextImageSpeak.setImageResource(R.drawable.ic_mic_translation);
                editTextImageVolumeUp.setVisibility(View.GONE);

                textViewTranslation.setVisibility(View.GONE);
                textViewImageVolumeUp.setVisibility(View.GONE);
                textViewImageCopy.setVisibility(View.GONE);
                textViewImageMoreOptions.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public boolean checkInternetConnection()
    {
        boolean isConnected;
        //check internet connection
        ConnectivityManager connectivityManager= (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        isConnected=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()== NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()==NetworkInfo.State.CONNECTED;

        return isConnected;
    }

    public void getTranslateService()
    {
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try(InputStream is=getResources().openRawResource(R.raw.credentials))
        {
            //get credentials
            final GoogleCredentials myCredentials=GoogleCredentials.fromStream(is);

            //setting credentials and get translate service
            TranslateOptions translateOptions=TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate=translateOptions.getService();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String translate(String textToTranslate,String sourceLang, String targetLang)
    {
        String translatedText;

        Translation translation=translate.translate(textToTranslate, Translate.TranslateOption.sourceLanguage(sourceLang), Translate.TranslateOption.targetLanguage(targetLang), Translate.TranslateOption.model("base"));
        translatedText=translation.getTranslatedText();

        return translatedText;
    }

    public void languageSelection()
    {
        //click listener for lang 1
        materialTxtViewLang1.setOnClickListener(v->
                //Navigation.findNavController(getView()).navigate(R.id.action_fragmentTranslation_to_fragmentLanguagesList2)
        {
            Intent intent=new Intent(getActivity(),LanguageListActivity.class);
            intent.putExtra("value","Lang1");
            startActivity(intent);
        });

        //click listener for lang 2
        materialTxtViewLang2.setOnClickListener( v->{
            Intent intent=new Intent(getActivity(),LanguageListActivity.class);
            intent.putExtra("value","Lang2");
            startActivity(intent);
        });
    }
}
