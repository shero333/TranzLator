package com.hammad.tranzlator.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.hammad.tranzlator.ConversationLanguageList;
import com.hammad.tranzlator.R;
import com.hammad.tranzlator.adapter.ConversationAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class ConversationFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int SPEECH_INPUT_REQUEST_CODE_1 = 1;
    private static final int SPEECH_INPUT_REQUEST_CODE_2 = 2;
    private static final int AUDIO_REQUEST_CODE = 100;

    ImageView imageView;
    Animation animation;
    ImageView imageViewLang1, imageViewLang2;
    RecyclerView recyclerViewConversation;
    ConversationAdapter conversationAdapter;

    //material textviews for selecting languages
    private MaterialTextView materialLang1, materialLang2;

    SharedPreferences mPreference;
    SharedPreferences.Editor mEditor;

    //string variables for storing source & target languages and codes
    String srcLang, trgtLang, srcLangCode, trgtLangCode;

    //integer variable for handling the condition in onSharedPreferenceChanged() listener
    int sharedPrefChangedChecker = 0;

    //this variable is used for conditioning the update preferences function
    int prefDecrement = 0;

    //boolean for handling the audio permission check conditions
    boolean micLeftPressed = false, micRightPressed = false;

    //this integer variable is used to handle condition in TTS module
    int iTTS =0;

    //initializing the cloud translation
    Translate translate;

    //TTS for translated text
    TextToSpeech mTTS;

    ArrayList<String> testArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        //initialize preference
        mPreference = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEditor = mPreference.edit();

        //initializing image views
        imageView = view.findViewById(R.id.img_btn_swapping);

        imageViewLang1 = view.findViewById(R.id.imageview_speak_lang_1);
        imageViewLang2 = view.findViewById(R.id.imageview_speak_lang_2);

        //initializing material textview which are used to select languages from & to translate
        materialLang1 = view.findViewById(R.id.lang_selector_1);
        materialLang2 = view.findViewById(R.id.lang_selector_2);

        //initializing the animation for image view click
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.img_button_animation);

        //initializing conversation recyclerview
        recyclerViewConversation = view.findViewById(R.id.recyclerview_conversation);

        //setting the layout for conversation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewConversation.setLayoutManager(linearLayoutManager);


        testArray = new ArrayList<>();
        testArray.add("Item_1");
        testArray.add("Item_2");

        //setting adapter to recyclerview
        conversationAdapter = new ConversationAdapter(getActivity(), testArray);
        recyclerViewConversation.setAdapter(conversationAdapter);

        // Language 1 click listener
        imageViewLang1.setOnClickListener(v -> {

            //this boolean handles the condition of audio permission check on which data to pass to Speech to Text
            micLeftPressed = true;

            //for lang 1 its value is '1'
            iTTS =1;

            //check internet connection
            if (checkInternetConnection()) {
                checkAudioPermission();
                /*testArray.add("Item_1");
                adapterPosition();*/
            } else {
                Toast.makeText(requireContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }


        });

        // Language 2 click listener
        imageViewLang2.setOnClickListener(v -> {

            //this boolean handles the condition of audio permission check on which data to pass to Speech to Text
            micRightPressed = true;

            //for lang 1 its value is '2'
            iTTS =2;

            //check internet connection
            if (checkInternetConnection()) {
                checkAudioPermission();
                /*testArray.add("Item_2");
                adapterPosition();*/
            }
            else {
                Toast.makeText(requireContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        });


        //function for selecting source and target languages
        languageSelectionHome();

        //swap languages function
        swapLanguages();

        //function for displaying the source & target languages from list
        checkSharePreference();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ConversationLanguageList.registerConversationPreference(getActivity(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConversationLanguageList.unregisterConversationPreference(getActivity(), this);
        mTTS.shutdown();
    }

    private void languageSelectionHome() {
        //click listener for lang 1
        materialLang1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ConversationLanguageList.class);
            intent.putExtra("value", "Lang1");
            startActivity(intent);
        });

        //click listener for lang 2
        materialLang2.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ConversationLanguageList.class);
            intent.putExtra("value", "Lang2");
            startActivity(intent);
        });
    }

    /*
        this function is used to set the recyclerview position to the new scrolled position
        the goal is to set view on recyclerview as a chat app
     */
    private void adapterPosition() {
        int newPosition = testArray.size() - 1;
        conversationAdapter.notifyItemChanged(newPosition);
        recyclerViewConversation.scrollToPosition(newPosition);
    }

    private void swapLanguages() {
        imageView.setOnClickListener(v -> {
            imageView.startAnimation(animation);
            reserveTranslationLanguages();
        });
    }

    private void reserveTranslationLanguages() {
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
        mEditor.putString(getString(R.string.conversation_lang_one), srcLang).apply();

        //setting the changed source & target translation languages
        materialLang1.setText(srcLang);
        materialLang2.setText(trgtLang);

        //condition to reset the prefDecrement & sharedPrefChangedChecker values so that the updatePreference function can execute properly
        //to see more details on this condition, go to FragmentTranslation class, and then go to reserveTranslationLanguages() function
        if (prefDecrement <= 4) {
            sharedPrefChangedChecker = 0;
            prefDecrement = 0;
        }
    }

    private void checkSharePreference() {
        srcLang = mPreference.getString(getString(R.string.conversation_lang_one), "English (United Kingdom)");
        srcLangCode = mPreference.getString(getString(R.string.conversation_lang_one_code), "en-GB");
        trgtLang = mPreference.getString(getString(R.string.conversation_lang_two), "Urdu (Pakistan)");
        trgtLangCode = mPreference.getString(getString(R.string.conversation_lang_two_code), "ur-PK");

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

    private void updateSharedPreferences() {
        //updates the prefDecrement variable value so that we can reset its values in reserveTranslationLanguages()
        prefDecrement++;

        //mEditor.putString(getString(R.string.lang_one), srcLang).apply();
        mEditor.putString(getString(R.string.conversation_lang_one_code), srcLangCode).apply();
        mEditor.putString(getString(R.string.conversation_lang_two), trgtLang).apply();
        mEditor.putString(getString(R.string.conversation_lang_two_code), trgtLangCode).apply();
    }

    public void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
        } else {
            //handling the clicks of which speech to text button was clicked

            if (micLeftPressed) {
                //setting the to false again here to handles the click listener for respective speech to text button
                micLeftPressed = false;
                speechToText(srcLang, srcLangCode, SPEECH_INPUT_REQUEST_CODE_1);
            } else if (micRightPressed) {
                //setting the to false again here to handles the click listener for respective speech to text button
                micRightPressed = false;
                speechToText(trgtLang, trgtLangCode, SPEECH_INPUT_REQUEST_CODE_2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AUDIO_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //handling the clicks of which speech to text button was clicked

            if (micLeftPressed) {
                //setting the to false again here to handles the click listener for respective speech to text button
                micLeftPressed = false;
                speechToText(srcLang, srcLangCode, SPEECH_INPUT_REQUEST_CODE_1);
            } else if (micRightPressed) {
                //setting the to false again here to handles the click listener for respective speech to text button
                micRightPressed = false;
                speechToText(trgtLang, trgtLangCode, SPEECH_INPUT_REQUEST_CODE_2);
            }
        }

        //this condition handles the flow when user selects "Deny & Never Ask again"
        else if (!shouldShowRequestPermissionRationale(permissions[0])) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Audio permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void speechToText(String languageName, String languageCode, int REQUEST_CODE) {

        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, languageName);

        startActivityForResult(speechIntent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_INPUT_REQUEST_CODE_1 && resultCode == RESULT_OK && data != null) {
            //getting the data
            ArrayList<String> resultedData = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(requireContext(), "Data 1: " + resultedData.get(0), Toast.LENGTH_LONG).show();
            String test = resultedData.get(0);
            translateText(test, srcLangCode, trgtLangCode);

            //inputEditText.setText(resultedData.get(0));
        } else if (requestCode == SPEECH_INPUT_REQUEST_CODE_2 && resultCode == RESULT_OK && data != null) {
            //getting the data
            ArrayList<String> resultedData = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(requireContext(), "Data 2: " + resultedData.get(0), Toast.LENGTH_LONG).show();
            String test1 = resultedData.get(0);
            translateText(test1, trgtLangCode, srcLangCode);
            //inputEditText.setText(resultedData.get(0));
        }
    }

    private void translateText(String text, String sourceCode, String targetCode) {
        if (checkInternetConnection()) {

            //setting the translation service
            getTranslateService();

            //condition to check source & target languages
            if (srcLangCode.equals(trgtLangCode)) {
                Toast.makeText(requireContext(), "Please select different source & target Translation languages", Toast.LENGTH_SHORT).show();
            } else {
                //translating the text
                String translation = translate(text, sourceCode, targetCode);
                Toast.makeText(requireContext(), translation, Toast.LENGTH_LONG).show();

                //this condition checks text to speech
                switch (iTTS)
                {
                    case 1:
                        Toast.makeText(requireContext(), "case 1", Toast.LENGTH_SHORT).show();
                        iTTS =0;
                        textToSpeechInitialization(trgtLangCode);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                speech(translation);
                            }
                        },5000);
                        break;

                    case 2:
                        Toast.makeText(requireContext(), "case 2", Toast.LENGTH_SHORT).show();
                        iTTS=0;
                        textToSpeechInitialization(srcLangCode);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                speech(translation);
                            }
                        },5000);
                        break;
                }
            }
        } else {
            Toast.makeText(requireContext(), "No Internet connection! Cannot be translated", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkInternetConnection() {
        boolean isConnected;
        //check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        isConnected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return isConnected;
    }

    public void getTranslateService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.credentials)) {
            //get credentials
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //setting credentials and get translate service
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String translate(String textToTranslate, String sourceLang, String targetLang) {
        String translatedText;

        Translation translation = translate.translate(textToTranslate, Translate.TranslateOption.sourceLanguage(trimLanguageCode(sourceLang)), Translate.TranslateOption.targetLanguage(trimLanguageCode(targetLang)), Translate.TranslateOption.model("base"));
        translatedText = translation.getTranslatedText();

        return translatedText;
    }

    /*
        this function trim the language code to two characters to translate it from Google Translation API
        Example: the Locale code for urdu is 'ur-PK' & for translation to Google API is 'ur'
    */
    private String trimLanguageCode(String code) {
        String stringCode;

        StringBuilder sb = new StringBuilder(code);
        if (sb.length() >= 3) {
            stringCode = sb.substring(0, 2);
        } else {
            stringCode = sb.toString();
        }

        return stringCode;
    }

    private void textToSpeechInitialization(String code) {
        mTTS = new TextToSpeech(getActivity(), status -> {

            if (status == TextToSpeech.SUCCESS) {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show();
                Locale locale = new Locale(code);
                mTTS.setLanguage(locale);

                //setting the speed rate of TTS. Normal is 1.0f. Slower < 1.0f ; Faster > 1.0f
                mTTS.setSpeechRate(0.7f);

            } else if (status == TextToSpeech.ERROR) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.d("TTS Initialization TRA", "Error in TTS translated text Initialization");
            }
        });
    }

    private void speech(String data) {
        Toast.makeText(requireContext(), "Speech", Toast.LENGTH_SHORT).show();
        mTTS.speak(data.trim(), TextToSpeech.QUEUE_FLUSH, null, null);
    }

}