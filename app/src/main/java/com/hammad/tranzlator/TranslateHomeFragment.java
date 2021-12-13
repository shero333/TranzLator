package com.hammad.tranzlator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TranslateHomeFragment extends Fragment
{
    TextView textViewTranslTransferFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragement_translate_home,container,false);

        textViewTranslTransferFragment =view.findViewById(R.id.textview_enter_text);
        textViewTranslTransferFragment.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
