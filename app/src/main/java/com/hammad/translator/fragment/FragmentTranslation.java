package com.hammad.translator.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.hammad.translator.BuildConfig;
import com.hammad.translator.R;
import com.hammad.translator.entities.TranslatedDataEntity;
import com.hammad.translator.activity.TranslationFullScreen;
import com.hammad.translator.TranslationRoomDB;
import com.hammad.translator.activity.TranslationLanguageList;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

public class FragmentTranslation extends Fragment implements PopupMenu.OnMenuItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int REQUEST_CODE_AUDIO = 101;
    public static final int REQUEST_CODE_SPEECH_INPUT = 102;

    ImageView editTextImageSpeak;
    ImageView textViewImageVolumeUp, textViewImageMoreOptions;
    TextView textViewTranslation;
    TextInputEditText inputEditText;
    ImageView imageViewSwapLang;
    Animation animation;
    ImageView imageViewCopyContent;

    //initializing the cloud translation
    Translate translate;

    //material textviews for selecting languages
    MaterialTextView materialTxtViewLang1, materialTxtViewLang2;

    //shared preference
    SharedPreferences mPreference;
    SharedPreferences.Editor mEditor;

    //string variables for storing source, target languages and codes
    String sourceLang, targetLang, sourceLangCode, targetLangCode;

    //integer variable for handling the onSharedPreferenceChanged() listener condition
    int sharedPrefChecker = 0;

    //this variable is used for conditioning the update preferences function
    int prefDecrement = 0;

    //TTS for translated text
    TextToSpeech mTTS;

    TextToSpeech textToSpeech;

    //progress bar
    ProgressBar progressBar;

    //banner AdView
    private AdView mAdView;

    //frame layout which holds the adaptive banner ad
    FrameLayout bannerFrameLayout;

    private AdRequest adRequest;

    //adaptive banner ad unit id
    String adUnitId=""/*getString(R.string.banner_ad_id)*//*"ca-app-pub-3940256099942544/6300978111"*/;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize AdMob
        MobileAds.initialize(requireContext(), initializationStatus -> {});

        adRequest= new AdRequest.Builder().build();

        //cheking whether app is running in release/debug version
        if(BuildConfig.DEBUG)
        {
            adUnitId="ca-app-pub-3940256099942544/6300978111";
            Log.i("FRAG_TRANS_AD_ID", "if called: "+adUnitId);
        }
        else{
            adUnitId=getString(R.string.banner_ad_id);
            Log.i("FRAG_TRANS_AD_ID", "else called: "+adUnitId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);

        //initialize preference
        mPreference = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEditor = mPreference.edit();

        //initializing material textview which are used to select languages from & to translate
        materialTxtViewLang1 = view.findViewById(R.id.lang_selector_1);
        materialTxtViewLang2 = view.findViewById(R.id.lang_selector_2);

        //initializing swap language image view
        imageViewSwapLang = view.findViewById(R.id.img_btn_swapping);

        //loading animation
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.img_button_animation);

        //input text initialization
        inputEditText = view.findViewById(R.id.edittext_input_layout_translation);
        //setting the ime type to action done with multiline edit text
        inputEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        inputEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        //image view related to edit text initialization
        editTextImageSpeak = view.findViewById(R.id.edittext_imageview_speak_translation);

        //text view translated initialization
        textViewTranslation = view.findViewById(R.id.textview_translated);

        //image views related to text view translated initialization
        textViewImageVolumeUp = view.findViewById(R.id.textview_imageview_volume_up);
        textViewImageMoreOptions = view.findViewById(R.id.textview_imageview_more);
        imageViewCopyContent = view.findViewById(R.id.textview_imageview_copy_content);

        //initialize progressbar
        progressBar=view.findViewById(R.id.progress_circular);
        progressBar.bringToFront();

        //instantiating banner frame layout
        bannerFrameLayout=view.findViewById(R.id.translate_banner);
        //initializing adview
        mAdView=new AdView(requireContext());
        mAdView.setAdUnitId(adUnitId);
        //setting the adview to frame layout
        bannerFrameLayout.addView(mAdView);

        //setting the ad size for adaptive banner ad
        AdSize adSize = getAdSize();
        mAdView.setAdSize(adSize);

        //loading ad into add view
        mAdView.loadAd(adRequest);
        //calling the ad listener here
        adListener();

        //getting the shared preferences values
        checkSharedPreferences();

        //this method handles the click listeners for translation language selection
        languageSelection();

        //click listener swap languages
        imageViewSwapLang.setOnClickListener(v -> {

            imageViewSwapLang.startAnimation(animation);
            reserveTranslationLanguages();
        });

        //popup menu for clicking on more option in translated text view
        textViewImageMoreOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            popupMenu.inflate(R.menu.textview_translation_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        });

        //text change listener for text translation
        inputEditText.addTextChangedListener(textWatcher);

        //click listener for copy content
        imageViewCopyContent.setOnClickListener(v ->
                copyContent());

        //gets data from bundles if any
        Bundle bundle = getArguments();

        /*
            This condition checks which data to get from bundle based on the interaction
            the outer IF condition checks whether there is any data preset in bundle.
            If data exists, then execute the function based on condition which is true
        */
        if(bundle != null) {
            if (bundle.getBoolean("micIsPressed") == true) {
                isMicButtonPressed();
            }
            else if (bundle.getString("srcLang").length() > 0) {
                checkBundleData(bundle);
            }
        }

        editTextImageSpeak.setOnClickListener(v -> {
            if (inputEditText.getText().toString().trim().length() == 0) {
                checkAudioPermission();
            }
        });

        return view;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.textview_translation_share:
                shareToApps();
                break;

            case R.id.textview_translation_fullscreen:
                Intent intent = new Intent(getActivity(), TranslationFullScreen.class);
                intent.putExtra("sourceText", inputEditText.getText().toString());
                intent.putExtra("translatedText", textViewTranslation.getText().toString());
                startActivity(intent);
                break;

            case R.id.textview_translation_reverse_translation:
                reverseTranslation();
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

                editTextImageSpeak.setOnClickListener(v -> {

                    //hiding the soft input keyboard
                    hideSoftInputKeyboard(v);

                    if (s.toString().trim().length() == 0) {
                        checkAudioPermission();
                    } else if (checkInternetConnection()) {
                        //setting the translation service
                        getTranslateService();

                        //condition to check source & target languages
                        if (sourceLangCode.equals(targetLangCode)) {
                            Toast.makeText(requireContext(), "Please select different source & target Translation languages", Toast.LENGTH_SHORT).show();
                        } else {
                            TranslationAsyncTask asyncTask=new TranslationAsyncTask(FragmentTranslation.this);
                            asyncTask.execute(s.toString(), sourceLangCode, targetLangCode);

                            //function for checking the TTS (text to speech) of language
                            textToSpeechInitialization();

                            textViewImageVolumeUp.setOnClickListener(view -> speech());
                        }

                    } else {
                        Toast.makeText(getContext(), "No Internet connection! Cannot be translated", Toast.LENGTH_SHORT).show();
                    }

                });
            } else {
                editTextImageSpeak.setImageResource(R.drawable.ic_mic_translation);

                textViewTranslation.setVisibility(View.GONE);
                textViewImageVolumeUp.setVisibility(View.GONE);
                imageViewCopyContent.setVisibility(View.GONE);
                textViewImageMoreOptions.setVisibility(View.GONE);

                //setting the progress bar visiblity to gone
                progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public boolean checkInternetConnection() {
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

        Translation translation = translate.translate(textToTranslate, Translate.TranslateOption.sourceLanguage(sourceLang), Translate.TranslateOption.targetLanguage(targetLang), Translate.TranslateOption.model("base"));
        translatedText = translation.getTranslatedText();

        return translatedText;
    }

    public void languageSelection() {
        //click listener for lang 1
        materialTxtViewLang1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TranslationLanguageList.class);
            intent.putExtra("value", "Lang1");
            startActivity(intent);
        });

        //click listener for lang 2
        materialTxtViewLang2.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TranslationLanguageList.class);
            intent.putExtra("value", "Lang2");
            startActivity(intent);
        });
    }

    public void checkSharedPreferences() {
        sourceLang = mPreference.getString(getString(R.string.lang_one), "English");
        sourceLangCode = mPreference.getString(getString(R.string.lang_one_code), "en");
        targetLang = mPreference.getString(getString(R.string.lang_two), "Urdu");
        targetLangCode = mPreference.getString(getString(R.string.lang_two_code), "ur");

        materialTxtViewLang1.setText(sourceLang);

        materialTxtViewLang2.setText(targetLang);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (sharedPrefChecker >= 1) {
            updateSharedPreferences();
        } else {
            checkSharedPreferences();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        TranslationLanguageList.registerPreference(requireContext(), this);
    }

    @Override
    public void onDestroy() {
        //destroy adview for banner ad with activity onDestroy()
        if(mAdView != null)
        {
            mAdView.destroy();
        }
        TranslationLanguageList.unregisterPreference(getActivity(), this);
        super.onDestroy();
    }

    /* Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /* Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    public void copyContent() {
        if (textViewTranslation.getText().toString().trim().length() != 0) {
            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("translated Text", textViewTranslation.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getContext(), "Copied!", Toast.LENGTH_SHORT).show();
        }

    }

    public void shareToApps() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, textViewTranslation.getText().toString().trim());
        startActivity(intent);
    }

    public void reverseTranslation() {
        //getting the data from edittext & textview
        String translatingText = inputEditText.getText().toString();
        String translatedText = textViewTranslation.getText().toString();

        //reversing the values of source language & code with target language & code
        String tempLang, tempLangCode;

        tempLang = sourceLang;
        tempLangCode = sourceLangCode;

        sourceLang = targetLang;
        sourceLangCode = targetLangCode;

        targetLang = tempLang;
        targetLangCode = tempLangCode;

        //incrementing the sharedPrefChecker value here so that we can handle the onSharedPreferenceChanged() condition
        sharedPrefChecker++;

        //this line of code will trigger the onSharedPreferenceChanged() interface
        mEditor.putString(getString(R.string.lang_one), sourceLang).apply();

        //for animating the swap languages button
        imageViewSwapLang.startAnimation(animation);

        //setting the changed source & target translation languages
        materialTxtViewLang1.setText(sourceLang);
        materialTxtViewLang2.setText(targetLang);

        //setting the reversed text
        inputEditText.setText(translatedText);
        textViewTranslation.setText(translatingText);

        /*
            This condition will assign '0' value to sharedPrefChecker variable.
            This is because we have 4 preference values to update. When preference change is called, (4 times called in our case)
            It will assign again '0' value to preference so that if we only want to change one language from language list we can do it
         */

        if (prefDecrement <= 4) {
            sharedPrefChecker = 0;
            prefDecrement = 0;
        }

    }

    public void reserveTranslationLanguages() {
        //reversing the values of source language & code with target language & code
        String tempLang, tempLangCode;
        tempLang = sourceLang;
        tempLangCode = sourceLangCode;

        sourceLang = targetLang;
        sourceLangCode = targetLangCode;

        targetLang = tempLang;
        targetLangCode = tempLangCode;

        //incrementing the sharedPrefChecker value here so that we can handle the onSharedPreferenceChanged() condition
        sharedPrefChecker++;

        //this line of code will trigger the onSharedPreferenceChanged() interface
        mEditor.putString(getString(R.string.lang_one), sourceLang).apply();

        //setting the changed source & target translation languages
        materialTxtViewLang1.setText(sourceLang);
        materialTxtViewLang2.setText(targetLang);

        /*
            This condition will assign '0' value to sharedPrefChecker variable.
            This is because we have 4 preference values to update. When preference change is called, (4 times called in our case)
            It will assign again '0' value to preference so that if we only want to change one language from language list we can do it
         */

        if (prefDecrement <= 4) {
            sharedPrefChecker = 0;
            prefDecrement = 0;
        }
    }

    //this function updates shared preferences values
    private void updateSharedPreferences() {
        //updates the prefDecrement variable value so that we can reset its values in reserveTranslationLanguages() & reverseTranslation() functions
        prefDecrement++;

        mEditor.putString(getString(R.string.lang_one_code), sourceLangCode).apply();
        mEditor.putString(getString(R.string.lang_two), targetLang).apply();
        mEditor.putString(getString(R.string.lang_two_code), targetLangCode).apply();

    }

    public void saveToDatabase() {
        if (inputEditText.getText().toString().trim().length() != 0 && textViewTranslation.getText().toString().trim().length() != 0) {
            TranslationRoomDB database = TranslationRoomDB.getInstance(getContext());
            TranslatedDataEntity dataEntity = new TranslatedDataEntity();

            dataEntity.setSourceLang(sourceLang);
            dataEntity.setSourceLangCode(sourceLangCode);
            dataEntity.setSourceText(inputEditText.getText().toString().trim());

            dataEntity.setTargetLang(targetLang);
            dataEntity.setTargetLangCode(targetLangCode);
            dataEntity.setTranslatedText(textViewTranslation.getText().toString().trim());

            database.translationHistoryDao().addTranslatedData(dataEntity);

        }
    }

    //this function return the item data selected from Translation History Adapter
    public void getTranslatedDatabaseData() {
        sourceLang = getArguments().getString("srcLang");
        sourceLangCode = getArguments().getString("srcLangCode");
        targetLang = getArguments().getString("trgtLang");
        targetLangCode = getArguments().getString("trgtLangCode");

        materialTxtViewLang1.setText(sourceLang);
        materialTxtViewLang2.setText(targetLang);
        inputEditText.setText(getArguments().getString("srcText"));
        textViewTranslation.setText(getArguments().getString("transText"));

        inputEditText.requestFocus();

        //setting the visibility of textview where translated text is set
        textViewTranslation.setVisibility(View.VISIBLE);
        textViewImageVolumeUp.setVisibility(View.VISIBLE);
        imageViewCopyContent.setVisibility(View.VISIBLE);
        textViewImageMoreOptions.setVisibility(View.VISIBLE);
    }

    public void checkBundleData(Bundle bundle) {
        if (bundle != null) {
            getTranslatedDatabaseData();
        }
    }

    public void textToSpeechInitialization() {
        mTTS = new TextToSpeech(getActivity(), status -> {

            if (status == TextToSpeech.SUCCESS) {
                //string which will save locale code for TTS (if TTS is available for particular language
                String localeCode = "";

                //string which contain code of language for checking TTS
                String languageCode = mPreference.getString(getString(R.string.lang_two_code), "ur");

                for (Locale text : mTTS.getAvailableLanguages()) {
                    if (text.toLanguageTag().contains(languageCode)) {
                        localeCode = text.toLanguageTag();
                        break;
                    }
                }

                if (localeCode.isEmpty()) {
                    textViewImageVolumeUp.setVisibility(View.INVISIBLE);
                } else if (!(localeCode.isEmpty())) {
                    textViewImageVolumeUp.setVisibility(View.VISIBLE);
                }

                Locale locale = new Locale(localeCode);
                mTTS.setLanguage(locale);

                //setting the speed rate of TTS. Normal is 1.0f. Slower < 1.0f ; Faster > 1.0f
                mTTS.setSpeechRate(0.7f);

            } else if (status == TextToSpeech.ERROR) {
                Log.d("TTS Initialization TRA", "Error in TTS translated text Initialization");
            }
        });
    }

    public void speech() {
        mTTS.speak(textViewTranslation.getText().toString().trim(), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_AUDIO);
        } else {
            speechToText();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_AUDIO && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            speechToText();
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

    public void speechToText() {

        String[] localeCode = {""};
        String langCode = mPreference.getString(getString(R.string.lang_one_code), "en");

        textToSpeech = new TextToSpeech(getActivity(), v -> {

            if (v == TextToSpeech.SUCCESS) {
                for (Locale text : textToSpeech.getAvailableLanguages()) {
                    if (text.toLanguageTag().contains(langCode)) {
                        localeCode[0] = text.toLanguageTag();
                    }
                }

                if (localeCode[0].trim().length() == 0) {
                    Toast.makeText(requireContext(), "Speech not available for " + mPreference.getString(getString(R.string.lang_one), ""), Toast.LENGTH_LONG).show();
                } else if (localeCode[0].trim().length() != 0) {

                    //setting the locale language code
                    Locale locale = new Locale(localeCode[0]);

                    Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeCode[0]);
                    speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, mPreference.getString(getString(R.string.lang_one), "English"));

                    startActivityForResult(speechIntent, REQUEST_CODE_SPEECH_INPUT);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            //getting the data
            ArrayList<String> resultedData = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            inputEditText.setText(resultedData.get(0));
        }
    }

    public void isMicButtonPressed() {
        Bundle speechBundle = getArguments();

        if (speechBundle != null) {
            speechToText();
        }

    }

    private static class TranslationAsyncTask extends AsyncTask<String,Void,String> {

        private WeakReference<FragmentTranslation> translationWeakReference;

        public TranslationAsyncTask(FragmentTranslation translation) {
            this.translationWeakReference = new WeakReference<>(translation);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //weak reference to the context of this function
            FragmentTranslation fragment=translationWeakReference.get();
            if(fragment==null || fragment.getActivity().isFinishing())
            {
                return;
            }
            fragment.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            FragmentTranslation fragment=translationWeakReference.get();
            if(fragment==null || fragment.getActivity().isFinishing()) {

            }
            return fragment.translate(strings[0],strings[1],strings[2]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //weak reference to the context of this function
            FragmentTranslation fragment=translationWeakReference.get();
            if(fragment==null || fragment.getActivity().isFinishing())
            {
                return;
            }

            //setting the progress bar visibility to gone
            fragment.progressBar.setVisibility(View.GONE);

            //setting the resturned text to textview
            fragment.textViewTranslation.setText(s);

            //setting the visibility of textview where translated text is set
            fragment.textViewTranslation.setVisibility(View.VISIBLE);

            fragment.imageViewCopyContent.setVisibility(View.VISIBLE);
            fragment.textViewImageMoreOptions.setVisibility(View.VISIBLE);

            //saving data to database here because data is saved to DB when both iput edit tex and text view translation have length greater than zero
            fragment.saveToDatabase();
        }
    }

    private void hideSoftInputKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void adListener()
    {
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireContext(), adWidth);
    }

}
