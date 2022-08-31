package com.risibleapps.translator.conversation;

import static android.app.Activity.RESULT_OK;
import static com.risibleapps.translator.mainActivity.HomeScreen.isHomeTransFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.textview.MaterialTextView;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.risibleapps.translator.R;
import com.risibleapps.translator.ads.AdHelperClass;
import com.risibleapps.translator.conversation.conversationLanguages.ConversationLanguageList;
import com.risibleapps.translator.conversation.db.ConversationDataEntity;
import com.risibleapps.translator.room.TranslationRoomDB;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConversationFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, ConversationAdapter.OnSpeakerPressedListener {

    private static final int SPEECH_INPUT_REQUEST_CODE_1 = 1;
    private static final int SPEECH_INPUT_REQUEST_CODE_2 = 2;
    private static final int AUDIO_REQUEST_CODE = 100;

    ImageView imageViewSwapLanguages;
    Animation animation;
    ImageView imageViewMic1, imageViewMic2;
    RecyclerView recyclerViewConversation;
    ConversationAdapter conversationAdapter;

    //material textviews for selecting languages
    private MaterialTextView materialLangSelectionOne, materialLangSelectionTwo;

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

    //initializing the cloud translation
    Translate translate;

    //TTS for translated text
    TextToSpeech mTTS;

    //Conversation Data Entity list
    List<ConversationDataEntity> conversationDataEntityList = new ArrayList<>();

    //room database
    TranslationRoomDB database;

    String strSpeechToText = "", strTranslatedText = "";

    //interstitial ad instance
    private InterstitialAd mInterstitialAd;

    //native ad
    private UnifiedNativeAd nativeAd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        //initialize preference
        mPreference = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEditor = mPreference.edit();

        //initializing views
        initialViews(view);

        //loading the interstitial ad
        mInterstitialAd = AdHelperClass.loadInterstitialAd(requireContext());

        //setting the recyclerview
        setupRecyclerview();

        //Language 1 click listener
        imageViewMic1.setOnClickListener(v -> languageOneClickListener());

        //Language 2 click listener
        imageViewMic2.setOnClickListener(v -> languageTwoClickListener());

        //function for selecting source and target languages
        conversationLanguagesSelection();

        //swap languages function
        swapLanguages();

        //function for displaying the source & target languages from list
        checkSharePreference();

        //native ad loading
        nativeAd = AdHelperClass.refreshNativeAd(requireActivity(),5, null);

        //decrementing the value of 'isHomeTransFragment' to 0 so that the exit dialog appears only in TranslateHomeFragment.
        isHomeTransFragment = 0;

        return view;
    }

    private void initialViews(View view){

        //initializing image views
        imageViewSwapLanguages = view.findViewById(R.id.img_btn_swapping);

        imageViewMic1 = view.findViewById(R.id.imageview_speak_lang_1);
        imageViewMic2 = view.findViewById(R.id.imageview_speak_lang_2);

        //initializing material textview which are used to select languages from & to translate
        materialLangSelectionOne = view.findViewById(R.id.lang_selector_1);
        materialLangSelectionTwo = view.findViewById(R.id.lang_selector_2);

        //initializing the animation for image view click
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.img_button_animation);

        //initializing room database instance
        database = TranslationRoomDB.getInstance(requireContext());

        //initializing conversation recyclerview
        recyclerViewConversation = view.findViewById(R.id.recyclerview_conversation);
    }

    private void setupRecyclerview() {
        //setting the layout for conversation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewConversation.setLayoutManager(linearLayoutManager);

        //getting all the data from database
        conversationDataEntityList = database.conversationDao().getAllConversationData();

        //scroll recyclerview to last entered item position
        int newPosition = conversationDataEntityList.size() - 1;
        recyclerViewConversation.scrollToPosition(newPosition);

        //setting adapter to recyclerview
        conversationAdapter = new ConversationAdapter(getActivity(), this, conversationDataEntityList);
        recyclerViewConversation.setAdapter(conversationAdapter);
    }

    private void languageOneClickListener() {
        //this boolean handles the condition of audio permission check on which data to pass to Speech to Text
        micLeftPressed = true;

        //check internet connection
        if (checkInternetConnection()) {
            checkAudioPermission();
        } else {
            Toast.makeText(requireContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void languageTwoClickListener() {
        //this boolean handles the condition of audio permission check on which data to pass to Speech to Text
        micRightPressed = true;

        //check internet connection
        if (checkInternetConnection()) {
            checkAudioPermission();
        } else {
            Toast.makeText(requireContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void adapterPosition() {
        int newPosition = conversationDataEntityList.size() - 1;
        conversationAdapter.notifyItemInserted(newPosition);
        recyclerViewConversation.scrollToPosition(newPosition);
    }

    private void conversationLanguagesSelection() {
        //click listener for lang 1
        materialLangSelectionOne.setOnClickListener(v -> AdHelperClass.showInterstitialAd(requireActivity(), () -> {

            Intent intent = new Intent(getActivity(), ConversationLanguageList.class);
            intent.putExtra("value", "Lang1");
            startActivity(intent);

            //loading the ad again after showing
            if(mInterstitialAd == null){
                mInterstitialAd = AdHelperClass.loadInterstitialAd(requireContext());
            }
        }));

        //click listener for lang 2
        materialLangSelectionTwo.setOnClickListener(v -> AdHelperClass.showInterstitialAd(requireActivity(), () -> {

            Intent intent = new Intent(getActivity(), ConversationLanguageList.class);
            intent.putExtra("value", "Lang2");
            startActivity(intent);

            //loading ad after showing
            if(mInterstitialAd == null){
                mInterstitialAd = AdHelperClass.loadInterstitialAd(requireContext());
            }
        }));
    }

    private void swapLanguages() {
        imageViewSwapLanguages.setOnClickListener(v -> {
            imageViewSwapLanguages.startAnimation(animation);
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
        materialLangSelectionOne.setText(srcLang);
        materialLangSelectionTwo.setText(trgtLang);

        /*
            condition to reset the prefDecrement & sharedPrefChangedChecker values so that the updatePreference function can execute properly
            to see more details on this condition, go to FragmentTranslation class, and then go to reserveTranslationLanguages() function
        */
        if (prefDecrement <= 4) {
            sharedPrefChangedChecker = 0;
            prefDecrement = 0;
        }
    }

    private void checkSharePreference() {
        srcLang = mPreference.getString(getString(R.string.conversation_lang_one), "English (United States)");
        srcLangCode = mPreference.getString(getString(R.string.conversation_lang_one_code), "en-US");
        trgtLang = mPreference.getString(getString(R.string.conversation_lang_two), "Urdu (Pakistan)");
        trgtLangCode = mPreference.getString(getString(R.string.conversation_lang_two_code), "ur-PK");

        materialLangSelectionOne.setText(srcLang);

        materialLangSelectionTwo.setText(trgtLang);
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

                //checking if source & target languages are same (English(US) & English(UK) are same)
                if (trimLanguageCode(srcLangCode).equals(trimLanguageCode(trgtLangCode))) {
                    Toast.makeText(requireContext(), "Please select different source & target Translation languages", Toast.LENGTH_SHORT).show();
                } else {

                    //function calling
                    speechToText(srcLang, srcLangCode, SPEECH_INPUT_REQUEST_CODE_1);
                }

            } else if (micRightPressed) {
                //setting the to false again here to handles the click listener for respective speech to text button
                micRightPressed = false;

                //checking if source & target languages are same (English(US) & English(UK) are same)
                if (trimLanguageCode(srcLangCode).equals(trimLanguageCode(trgtLangCode))) {
                    Toast.makeText(requireContext(), "Please select different source & target Translation languages", Toast.LENGTH_SHORT).show();
                } else {
                    //function calling
                    speechToText(trgtLang, trgtLangCode, SPEECH_INPUT_REQUEST_CODE_2);
                }

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

            //saves the speech to text data in strSpeechToText
            strSpeechToText = resultedData.get(0);

            //translate function called here
            translateText(strSpeechToText, srcLangCode, trgtLangCode, 1);
        } else if (requestCode == SPEECH_INPUT_REQUEST_CODE_2 && resultCode == RESULT_OK && data != null) {
            //getting the data
            ArrayList<String> resultedData = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            //saves the speech to text data in strSpeechToText
            strSpeechToText = resultedData.get(0);

            //translate function called here
            translateText(strSpeechToText, trgtLangCode, srcLangCode, 2);
        }
    }

    private void translateText(String text, String sourceCode, String targetCode, int requestCode) {

        if (checkInternetConnection()) {
            //setting the translation service
            getTranslateService();

            //condition to check source & target languages
            if (srcLangCode.equals(trgtLangCode)) {
                Toast.makeText(requireContext(), "Please select different source & target Translation languages", Toast.LENGTH_SHORT).show();
            } else {

                //translating the text
                strTranslatedText = translate(text, sourceCode, targetCode);

                //this condition checks which mic was pressed and displays that particular view
                if (requestCode == 1) {
                    //this sets the left side view of Conversation adapter view
                    conversationDataEntityList.add(new ConversationDataEntity(1, strSpeechToText, strTranslatedText, trgtLangCode));
                    adapterPosition();

                    //saving data to database
                    saveToDatabase(1, strSpeechToText, strTranslatedText, trgtLangCode);
                } else if (requestCode == 2) {
                    //this sets the left side view of Conversation adapter view
                    conversationDataEntityList.add(new ConversationDataEntity(2, strSpeechToText, strTranslatedText, srcLangCode));
                    adapterPosition();

                    //saving data to database
                    saveToDatabase(2, strSpeechToText, strTranslatedText, srcLangCode);
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

        try (InputStream is = getResources().openRawResource(R.raw.credentials_1)) {
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

    private void textToSpeechInitialization(String code, String textToSpeech) {
        mTTS = new TextToSpeech(getActivity(), status -> {

            if (status == TextToSpeech.SUCCESS) {
                Locale locale = new Locale(code);
                mTTS.setLanguage(locale);

                //setting the speed rate of TTS. Normal is 1.0f. Slower < 1.0f ; Faster > 1.0f
                mTTS.setSpeechRate(0.7f);

                //calling the speech function
                speech(textToSpeech);

            } else if (status == TextToSpeech.ERROR) {
                Log.d("TTS Initialization TRA", "Error in TTS translated text Initialization");
            }
        });
    }

    private void speech(String data) {
        mTTS.speak(data.trim(), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onSpeakerPressed(String code, String textToSpeech) {
        textToSpeechInitialization(code, textToSpeech);
    }

    private void saveToDatabase(int conversationBtnPressed, String speechToText, String translatedText, String textToSpeechCode) {
        if (speechToText.trim().length() != 0 && translatedText.trim().length() != 0) {
            TranslationRoomDB translationRoomDB = TranslationRoomDB.getInstance(requireContext());
            ConversationDataEntity conversationDataEntity = new ConversationDataEntity();

            conversationDataEntity.setConCode(conversationBtnPressed);
            conversationDataEntity.setConSourceText(speechToText);
            conversationDataEntity.setConTranslatedText(translatedText);
            conversationDataEntity.setTTSCode(textToSpeechCode);

            translationRoomDB.conversationDao().addConversationData(conversationDataEntity);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ConversationLanguageList.registerConversationPreference(getActivity(), this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mInterstitialAd = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        mInterstitialAd = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConversationLanguageList.unregisterConversationPreference(getActivity(), this);
        if (mTTS != null) {
            mTTS.shutdown();
        }

        //setting the interstitial ad object to null
        mInterstitialAd = null;

        //destroying the native ad object
        if(nativeAd != null){
            nativeAd.destroy();
        }
    }

}