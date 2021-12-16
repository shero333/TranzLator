package com.hammad.tranzlator;

import android.os.Bundle;
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

import com.google.android.material.textfield.TextInputEditText;

public class FragmentTranslation extends Fragment implements PopupMenu.OnMenuItemClickListener {

    ImageView editTextImageVolumeUp, editTextImageSpeak;
    ImageView textViewImageVolumeUp, textViewImageCopy, textViewImageMoreOptions;
    TextView textViewTranslation;
    TextInputEditText inputEditText;
    ImageView imageViewSwapLang;
    Animation animation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);

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
                    textViewTranslation.setVisibility(View.VISIBLE);
                    textViewTranslation.setText(s.toString());

                    textViewImageVolumeUp.setVisibility(View.VISIBLE);
                    textViewImageCopy.setVisibility(View.VISIBLE);
                    textViewImageMoreOptions.setVisibility(View.VISIBLE);
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
}
