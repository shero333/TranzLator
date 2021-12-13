package com.hammad.tranzlator;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentTranslation extends Fragment
{
    ImageView imageViewMore;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_translation,container,false);

        imageViewMore=view.findViewById(R.id.textview_imageview_more);

        registerForContextMenu(imageViewMore);

        return view;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater= getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.textview_translation_menu,menu);

        switch (v.getId())
        {
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
    }
}
