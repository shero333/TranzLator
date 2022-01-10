package com.hammad.tranzlator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TranslationHistoryAdapter extends RecyclerView.Adapter<TranslationHistoryAdapter.MyViewHolder> {

    Context context;
    List<TranslatedDataEntity> dataList;
    TranslationRoomDB database;

    public TranslationHistoryAdapter(Context context, List<TranslatedDataEntity> dataEntityList) {
        this.context = context;
        dataList=dataEntityList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TranslationHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.layout_translation_history,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TranslationHistoryAdapter.MyViewHolder holder, int position) {

        TranslatedDataEntity list=dataList.get(position);
        database=TranslationRoomDB.getInstance(context);


        holder.textViewLang1.setText(list.getSourceLang());
        holder.textViewLang2.setText(list.getTargetLang());
        holder.textViewEnteredText.setText(list.getSourceText());
        holder.textViewTranslatedText.setText(list.getTranslatedText());

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewLang1,textViewLang2,textViewEnteredText,textViewTranslatedText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewLang1=itemView.findViewById(R.id.lang1);
            textViewLang2=itemView.findViewById(R.id.lang2);
            textViewEnteredText=itemView.findViewById(R.id.text_entered);
            textViewTranslatedText=itemView.findViewById(R.id.text_translated);

            //click listener for history recyclerview
            itemView.setOnClickListener(v ->
                    Toast.makeText(context, "Item clicked: "+getAdapterPosition(), Toast.LENGTH_SHORT).show());

        }
    }
}
