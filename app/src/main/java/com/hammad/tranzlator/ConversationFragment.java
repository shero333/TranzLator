package com.hammad.tranzlator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ConversationFragment extends Fragment
{
    ImageView imageView;
    Animation animation;
    ImageView imageViewLang1,imageViewLang2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragement_conversation,container,false);

        //initializing image views
        imageView=view.findViewById(R.id.img_btn_swapping);

        imageViewLang1=view.findViewById(R.id.imageview_speak_lang_1);
        imageViewLang2=view.findViewById(R.id.imageview_speak_lang_2);

        //initializing the animation for image view click
        animation=AnimationUtils.loadAnimation(getActivity(),R.anim.img_button_animation);

        imageView.setOnClickListener(v ->
                imageView.startAnimation(animation));

        // Language 1 click listener
        imageViewLang1.setOnClickListener(v->
                Toast.makeText(getActivity(), "Lang 1", Toast.LENGTH_SHORT).show());

        // Language 2 click listener
        imageViewLang2.setOnClickListener(v->
                Toast.makeText(getActivity(), "Lang 2", Toast.LENGTH_SHORT).show());

        return view;
    }
}
