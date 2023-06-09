package com.risibleapps.translator.translate.translation;

import static android.app.Activity.RESULT_OK;
import static com.risibleapps.translator.mainActivity.HomeScreen.isHomeTransFragment;

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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.risibleapps.translator.R;
import com.risibleapps.translator.Util.Commons;
import com.risibleapps.translator.Util.Constants;
import com.risibleapps.translator.ads.AdHelperClass;
import com.risibleapps.translator.room.TranslationRoomDB;
import com.risibleapps.translator.translate.translateHome.db.TranslatedDataEntity;
import com.risibleapps.translator.translate.translationLanguages.TranslationLanguageList;
import com.risibleapps.translator.volleyLibrary.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FragmentTranslation extends Fragment implements PopupMenu.OnMenuItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    ImageView editTextImageSpeak;
    ImageView textViewImageVolumeUp, textViewImageMoreOptions;
    TextView textViewTranslation;
    TextInputEditText inputEditText;
    ImageView imageViewSwapLang;
    Animation animation;
    ImageView imageViewCopyContent;

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

    //variable for controlling the ad on translation
    int translateAdCount = 0;

    //interstitial ad instance
    private InterstitialAd mInterstitialAd;

    //native ad (of banner size)
    private UnifiedNativeAd nativeAd;

    // tts for bundle data from Home Translation Fragment(Translation History)
    private TextToSpeech mTTSBundle;

    //shimmer layout
    private ShimmerFrameLayout shimmerFrameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);

        //initializing views
        initializeViews(view);

        //loading intersitial ad
        mInterstitialAd = AdHelperClass.loadInterstitialAd(requireContext());

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
        imageViewCopyContent.setOnClickListener(v -> AdHelperClass.showInterstitialAd(requireActivity(), new AdHelperClass.AdCloseListener() {
            @Override
            public void onAdClosed() {

                copyContent();

                //loading the ad again
                if (mInterstitialAd == null) {
                    mInterstitialAd = AdHelperClass.loadInterstitialAd(requireContext());
                }
            }
        }));

        //gets data from bundles if any
        Bundle bundle = getArguments();

        /*
            This condition checks which data to get from bundle based on the interaction
            the outer IF condition checks whether there is any data preset in bundle.
            If data exists, then execute the function based on condition which is true
        */
        if (bundle != null) {
            if (bundle.getBoolean("micIsPressed") == true) {
                isMicButtonPressed();
            } else if (bundle.getString("srcLang").length() > 0) {
                checkBundleData(bundle);
            }
        }

        editTextImageSpeak.setOnClickListener(v -> {
            if (inputEditText.getText().toString().trim().length() == 0) {
                checkAudioPermission();
            }
        });

        //decrementing the value of 'isHomeTransFragment' to 0 so that the exit dialog appears only in TranslateHomeFragment.
        isHomeTransFragment = 0;

        return view;
    }

    private void initializeViews(View view) {
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
        progressBar = view.findViewById(R.id.progress_circular);
        progressBar.bringToFront();

        //native ad shimmer layout
        shimmerFrameLayout=view.findViewById(R.id.shimmer_trans_frag);

        //initializing native ad
        nativeAd = AdHelperClass.refreshNativeAd(requireActivity(), 3, null);
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

    public boolean checkInternetConnection() {
        boolean isConnected;
        //check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        isConnected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return isConnected;
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

                        //condition to check source & target languages
                        if (sourceLangCode.equals(targetLangCode)) {
                            Toast.makeText(requireContext(), "Please select different source & target Translation languages", Toast.LENGTH_SHORT).show();
                        } else {
                            //incrementing the value
                            translateAdCount++;

                            //saving the incremeted value in preference
                            mEditor.putInt(getString(R.string.translation_ad_count), translateAdCount);

                            //ad will be displayed after every two translation clicks
                            if (translateAdCount == 0 || translateAdCount == 3) {
                                //displaying ad before translation
                                AdHelperClass.showInterstitialAd(requireActivity(), new AdHelperClass.AdCloseListener() {
                                    @Override
                                    public void onAdClosed() {

                                        //showing the progress bar
                                        progressBar.setVisibility(View.VISIBLE);

                                        //function for calling the text translation
                                        transText(s);

                                        //loading the ad again
                                        if (mInterstitialAd == null) {
                                            mInterstitialAd = AdHelperClass.loadInterstitialAd(requireContext());
                                        }
                                    }
                                });
                            } else {

                                //showing the progress bar
                                progressBar.setVisibility(View.VISIBLE);

                                //function for calling the text translation
                                transText(s);

                                //setting the value to 1, when preference count reaches 3.
                                if (translateAdCount > 3) {
                                    mEditor.putInt(getString(R.string.translation_ad_count), 1).apply();

                                    //setting the current count to 1 as well
                                    translateAdCount = 1;
                                }
                            }
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
        public void afterTextChanged(Editable s) {}
    };

    private void transText(CharSequence s) {
        //translating the text
        translate(s.toString(),sourceLangCode,targetLangCode);

        //function for checking the TTS (text to speech) of language
        textToSpeechInitialization();

        textViewImageVolumeUp.setOnClickListener(view -> speech());
    }

    public void translate(String textToTranslate, String sourceLang, String targetLang) {

        String URL = "https://www.googleapis.com/language/translate/v2?key=" + getString(R.string.api_key) + "&source=" + sourceLang + "&target=" + targetLang + "&q=" + textToTranslate;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject object1 = response.getJSONObject("data");

                    JSONArray array = object1.getJSONArray("translations");

                    //setting the returned text to textview
                    textViewTranslation.setText(array.getJSONObject(0).getString("translatedText"));

                    //setting the progress bar visibility to gone
                    progressBar.setVisibility(View.GONE);

                    //setting the visibility of textview where translated text is set
                    textViewTranslation.setVisibility(View.VISIBLE);

                    imageViewCopyContent.setVisibility(View.VISIBLE);
                    textViewImageMoreOptions.setVisibility(View.VISIBLE);

                    //saving data to database here because data is saved to DB when both input edit tex and text view translation have length greater than zero
                    saveToDatabase();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getMessage();

                Toast.makeText(getContext(), "Error! Cannot translate.", Toast.LENGTH_SHORT).show();

                //if there's error, show the error toast and set visisbility of rest of views to GONE
                progressBar.setVisibility(View.GONE);

                //setting the visibility of textview where translated text is set
                textViewTranslation.setVisibility(View.GONE);

                imageViewCopyContent.setVisibility(View.GONE);
                textViewImageMoreOptions.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<>();
                header.put("X-Android-Package", getContext().getPackageName());
                header.put("X-Android-Cert", Commons.getSignature(getContext().getPackageManager(),getContext().getPackageName()));

                return header;
            }
        };

        //instantiating the VolleySingleton Class
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void languageSelection() {

        //click listener for lang 1
        materialTxtViewLang1.setOnClickListener(v -> {
            AdHelperClass.showInterstitialAd(requireActivity(), new AdHelperClass.AdCloseListener() {
                @Override
                public void onAdClosed() {

                    Intent intent = new Intent(getActivity(), TranslationLanguageList.class);
                    intent.putExtra("value", "Lang1");
                    startActivity(intent);

                    //load the ad after showing
                    if (mInterstitialAd == null) {
                        mInterstitialAd = AdHelperClass.loadInterstitialAd(requireContext());
                    }

                }
            });
        });

        //click listener for lang 2
        materialTxtViewLang2.setOnClickListener(v -> {
            AdHelperClass.showInterstitialAd(requireActivity(), new AdHelperClass.AdCloseListener() {
                @Override
                public void onAdClosed() {

                    Intent intent = new Intent(getActivity(), TranslationLanguageList.class);
                    intent.putExtra("value", "Lang2");
                    startActivity(intent);

                    //loading the ad again
                    if (mInterstitialAd == null) {
                        mInterstitialAd = AdHelperClass.loadInterstitialAd(requireContext());
                    }
                }
            });
        });
    }

    public void checkSharedPreferences() {
        sourceLang = mPreference.getString(getString(R.string.lang_one), "English");
        sourceLangCode = mPreference.getString(getString(R.string.lang_one_code), "en");
        targetLang = mPreference.getString(getString(R.string.lang_two), "Urdu");
        targetLangCode = mPreference.getString(getString(R.string.lang_two_code), "ur");

        //getting the translation ad count
        translateAdCount = mPreference.getInt(getString(R.string.translation_ad_count), -1);

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

        //text to speech click listener
        textViewImageVolumeUp.setOnClickListener(v -> {

            //initializing text to speech
            mTTSBundle = new TextToSpeech(getActivity(), status -> {

                if (status == TextToSpeech.SUCCESS) {

                    String localeCode = "";

                    for (Locale text : mTTSBundle.getAvailableLanguages()) {
                        if (text.toLanguageTag().contains(targetLangCode)) {
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
                    mTTSBundle.setLanguage(locale);

                    //setting the speed rate of TTS. Normal is 1.0f. Slower < 1.0f ; Faster > 1.0f
                    mTTSBundle.setSpeechRate(0.7f);

                    mTTSBundle.speak(textViewTranslation.getText().toString().trim(), TextToSpeech.QUEUE_FLUSH, null, null);

                }

            });

        });
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
                Log.d("TTS Initialization TRA", "Error in TTS translated text Initialization" + status);
            }
        });
    }

    public void speech() {
        mTTS.speak(textViewTranslation.getText().toString().trim(), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, Constants.REQUEST_CODE_AUDIO);
        } else {
            speechToText();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_CODE_AUDIO && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

                    startActivityForResult(speechIntent, Constants.REQUEST_CODE_SPEECH_INPUT);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
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

    private void hideSoftInputKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        TranslationLanguageList.registerPreference(requireContext(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        mInterstitialAd = null;

        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }

    @Override
    public void onStop() {
        mInterstitialAd = null;
        super.onStop();
    }

    @Override
    public void onDestroy() {

        mInterstitialAd = null;

        TranslationLanguageList.unregisterPreference(getActivity(), this);

        //destroying the native ad instance
        if (nativeAd != null) {
            nativeAd.destroy();
        }

        super.onDestroy();
    }

}
