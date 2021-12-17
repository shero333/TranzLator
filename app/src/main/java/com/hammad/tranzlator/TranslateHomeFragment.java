package com.hammad.tranzlator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TranslateHomeFragment extends Fragment {
    TextView textViewTranslTransferFragment;
    ImageView imageViewSwapLang;
    Animation animation;
    RecyclerView recyclerViewHistory;
    TranslationHistoryAdapter translationHistoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragement_translate_home, container, false);

        textViewTranslTransferFragment = view.findViewById(R.id.textview_enter_text);

        imageViewSwapLang=view.findViewById(R.id.img_btn_swapping);

        //initializing the animation for image view click
        animation= AnimationUtils.loadAnimation(getActivity(),R.anim.img_button_animation);

        imageViewSwapLang.setOnClickListener(v ->imageViewSwapLang.startAnimation(animation));

        textViewTranslTransferFragment.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_bottom_nav_translation_to_fragmentTranslation);

        });

        //initializing history recyclerview
        recyclerViewHistory=view.findViewById(R.id.recyclerview_history);

        //setting the layout for recyclerview
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerViewHistory.setLayoutManager(linearLayoutManager);

        //setting the adapter to the recyclerview
        translationHistoryAdapter=new TranslationHistoryAdapter(getActivity());
        recyclerViewHistory.setAdapter(translationHistoryAdapter);

        return view;
    }
}
