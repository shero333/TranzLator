package com.hammad.tranzlator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class DictionaryFragment extends Fragment
{
    TextInputEditText textInputEditText;
    TextInputLayout textInputLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_dictionary,container,false);

        textInputEditText=view.findViewById(R.id.dictionary_edit_text);
        textInputLayout=view.findViewById(R.id.dictionary_edit_text_layout);

        return view;
    }
}
