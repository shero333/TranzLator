package com.hammad.tranzlator.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hammad.tranzlator.DictionaryModel;
import com.hammad.tranzlator.R;
import com.hammad.tranzlator.VolleySingleton;
import com.hammad.tranzlator.adapter.DictionaryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryFragment extends Fragment {
    TextInputEditText textInputEditText;
    TextInputLayout textInputLayout;

    TextView textViewWord, textViewPhonetic;
    ImageView imageViewSpeech;
    RecyclerView recyclerView;
    DictionaryAdapter adapter;
    List<DictionaryModel> dictionaryModelList = new ArrayList<>();

     ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);

        //function to initialize view
        initializeViews(view);

        textInputEditText.addTextChangedListener(textWatcher);

        //initializing the progress dialog
        //initializeProgressDialog();

        return view;
    }

    private void initializeViews(View view) {
        //initializing  material edit text
        textInputEditText = view.findViewById(R.id.dictionary_edit_text);
        textInputLayout = view.findViewById(R.id.dictionary_edit_text_layout);

        //initializing rest of view
        textViewWord = view.findViewById(R.id.txt_word);
        textViewPhonetic = view.findViewById(R.id.txt_phonetic);
        imageViewSpeech = view.findViewById(R.id.img_speech);

        //recyclerview
        recyclerView = view.findViewById(R.id.recyclerview_dictionary);
    }

    private void setupRecyclerview() {
        //setup linear layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //setting the adapter
        adapter = new DictionaryAdapter(requireContext(), dictionaryModelList);
        recyclerView.setAdapter(adapter);
    }

    public TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (s.toString().trim().length() > 0) {

                //when search IME is clicked on keyboard it will search the word meaning
                textInputEditText.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEND) {

                        //this if condition checks whether the string contains any space between words
                        if (checkWhiteSpace(textInputEditText.getText().toString().trim())) {
                            //custom toast function
                            showToast("Please enter single word", 0);
                        } else if (!(checkWhiteSpace(textInputEditText.getText().toString().trim()))) {
                            //hides soft input keyboard
                            hideSoftInputKeyboard(v);

                            //checking internet connection
                            if (checkInternetConnection()) {
                                //calling the initialize progress dialog to show progress dialog while fetching data from API
                                initializeProgressDialog();

                                //calling this function where meaning is searched in API
                                getMeaning(textInputEditText.getText().toString().trim());
                            } else {
                                Toast.makeText(requireContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                    return true;
                });
            } else {
                //setting to visibility to "Invisible"
                textViewWord.setVisibility(View.GONE);
                textViewPhonetic.setVisibility(View.GONE);
                imageViewSpeech.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);

                //clear the DictionaryModel list so that no previous data in list can be used
                dictionaryModelList.clear();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //searching the word in dictionary api
    private void getMeaning(String word) {
        String URL = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;

        //as the base response from API is json array, so first we will create JSON Array as response type from API
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, response -> {
            try {
                //first/main object to get from API
                JSONObject mainJsonObject = response.getJSONObject(0);

                //setting the word to textview
                textViewWord.setText(mainJsonObject.getString("word"));

                //JSON Array to get phonetic array from API which contains the phonetic  and audio
                // (For example, the word 'Hello' phonetic is 'həˈləʊ')
                JSONArray phoneticJsonArray = mainJsonObject.getJSONArray("phonetics");
                JSONObject phoneticJsonObject = phoneticJsonArray.getJSONObject(0);

                //setting the phonetic text to textview
                textViewPhonetic.setText(phoneticJsonObject.getString("text"));

                imageViewSpeech.setOnClickListener(v ->
                {
                    try {
                        //setting the phonetic audio to media play
                        playPhoneticAudio(phoneticJsonObject.getString("audio"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

                //getting the 'meanings' JSON Array
                JSONArray meaningsJsonArray = mainJsonObject.getJSONArray("meanings");


                if (meaningsJsonArray.length() >= 1) {
                    for (int i = 0; i < meaningsJsonArray.length(); i++) {
                        JSONObject partOfSpeechJsonObject = meaningsJsonArray.getJSONObject(i);
                        String partOfSpeech = partOfSpeechJsonObject.getString("partOfSpeech");

                        JSONArray definitionsJsonArray = partOfSpeechJsonObject.getJSONArray("definitions");

                        JSONObject definitionsJsonObject = definitionsJsonArray.getJSONObject(0);

                        String definition = definitionsJsonObject.getString("definition").trim();
                        String example = definitionsJsonObject.getString("example").trim();

                        dictionaryModelList.add(new DictionaryModel(partOfSpeech, definition, example));
                    }
                }

                //setting the recyclerview
                setupRecyclerview();

                //setting the views visibility to true
                setViewsVisibility();

                progressDialog.hide();

            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.hide();
            }

        }, error -> {
            showToast("Sorry! we couldn't find definition(s) for the word", 1);
            progressDialog.hide();
        });

        //instantiating the VolleySingleton Class
        VolleySingleton volleySingleton = new VolleySingleton();
        volleySingleton.getInstance(requireContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void playPhoneticAudio(String audioUri) {
        if (audioUri != null) {
            MediaPlayer mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource("https:" + audioUri);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setViewsVisibility() {
        textViewWord.setVisibility(View.VISIBLE);
        textViewPhonetic.setVisibility(View.VISIBLE);
        imageViewSpeech.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private boolean checkWhiteSpace(String word) {
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(word);
        return matcher.find();
    }

    private void showToast(String message, int value) {
        Toast toast = Toast.makeText(requireContext(), message, value);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private void hideSoftInputKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void initializeProgressDialog()
    {
        progressDialog=new ProgressDialog(requireContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.layout_progress_dialog);
    }

    private boolean checkInternetConnection() {
        boolean isConnected;
        //check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        isConnected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return isConnected;
    }
}
