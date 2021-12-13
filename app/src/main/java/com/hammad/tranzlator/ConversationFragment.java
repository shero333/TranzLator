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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragement_conversation,container,false);

        imageView=view.findViewById(R.id.img_btn_swapping);

        //initializing the animation for image view click
        animation=AnimationUtils.loadAnimation(getActivity(),R.anim.img_button_animation);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.startAnimation(animation);
            }
        });

        return view;
    }
}
