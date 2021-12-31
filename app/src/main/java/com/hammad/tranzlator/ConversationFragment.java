package com.hammad.tranzlator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ConversationFragment extends Fragment {
    ImageView imageView;
    Animation animation;
    ImageView imageViewLang1, imageViewLang2;
    RecyclerView recyclerViewConversation;
    ConversationAdapter conversationAdapter;

    ArrayList<String> testArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        //initializing image views
        imageView = view.findViewById(R.id.img_btn_swapping);

        imageViewLang1 = view.findViewById(R.id.imageview_speak_lang_1);
        imageViewLang2 = view.findViewById(R.id.imageview_speak_lang_2);

        //initializing the animation for image view click
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.img_button_animation);

        imageView.setOnClickListener(v ->
                imageView.startAnimation(animation));

        //initializing conversation recyclerview
        recyclerViewConversation = view.findViewById(R.id.recyclerview_conversation);

        //setting the layout for conversation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewConversation.setLayoutManager(linearLayoutManager);


        testArray = new ArrayList<>();
        testArray.add("Item_1");
        testArray.add("Item_2");

        //setting adapter to recyclerview
        conversationAdapter = new ConversationAdapter(getActivity(), testArray);
        recyclerViewConversation.setAdapter(conversationAdapter);

        // Language 1 click listener
        imageViewLang1.setOnClickListener(v -> {
            testArray.add("Item_1");
            adapterPosition();

        });

        // Language 2 click listener
        imageViewLang2.setOnClickListener(v -> {
            testArray.add("Item_2");
            adapterPosition();

        });

        return view;
    }

    /**
     * this function is used to set the recyclerview position to the new scrolled position
     * the goal is to set view on recyclerview as a chat app
     */
    public void adapterPosition()
    {
        int newPosition = testArray.size() - 1;
        conversationAdapter.notifyItemChanged(newPosition);
        recyclerViewConversation.scrollToPosition(newPosition);
    }
}
