package com.risibleapps.translator.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.risibleapps.translator.R;
import com.risibleapps.translator.entities.TranslatedDataEntity;
import com.risibleapps.translator.TranslationRoomDB;

import java.util.List;

public class TranslationHistoryAdapter extends RecyclerView.Adapter<TranslationHistoryAdapter.MyViewHolder> {

    Context context;
    List<TranslatedDataEntity> translatedDataEntityList;
    TranslationRoomDB database;

    public TranslationHistoryAdapter(Context context, List<TranslatedDataEntity> dataEntityList) {
        this.context = context;
        translatedDataEntityList = dataEntityList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TranslationHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_translation_history, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TranslationHistoryAdapter.MyViewHolder holder, int position) {

        TranslatedDataEntity dataList = translatedDataEntityList.get(position);
        database = TranslationRoomDB.getInstance(context);

        holder.textViewLang1.setText(dataList.getSourceLang());
        holder.textViewLang2.setText(dataList.getTargetLang());
        holder.textViewEnteredText.setText(dataList.getSourceText());
        holder.textViewTranslatedText.setText(dataList.getTranslatedText());
    }

    @Override
    public int getItemCount() {
        return translatedDataEntityList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLang1, textViewLang2, textViewEnteredText, textViewTranslatedText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewLang1 = itemView.findViewById(R.id.lang1);
            textViewLang2 = itemView.findViewById(R.id.lang2);
            textViewEnteredText = itemView.findViewById(R.id.text_entered);
            textViewTranslatedText = itemView.findViewById(R.id.text_translated);

            //click listener for history recyclerview
            itemView.setOnClickListener(v -> {

                TranslatedDataEntity dataEntityItem=translatedDataEntityList.get(getAdapterPosition());

                //bundle for passing data to other fragment
                Bundle bundle=new Bundle();
                bundle.putString("srcLang",dataEntityItem.getSourceLang());
                bundle.putString("srcLangCode",dataEntityItem.getSourceLangCode());
                bundle.putString("srcText",dataEntityItem.getSourceText());

                bundle.putString("trgtLang",dataEntityItem.getTargetLang());
                bundle.putString("trgtLangCode",dataEntityItem.getTargetLangCode());
                bundle.putString("transText",dataEntityItem.getTranslatedText());

                Navigation.findNavController(itemView).navigate(R.id.action_bottom_nav_translation_to_fragmentTranslation,bundle);

            });
        }
    }
}