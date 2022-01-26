package com.hammad.tranzlator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.tranzlator.R;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.MyViewHolder> {

    Context context;

    public DictionaryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.layout_dictionary,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textViewIncrement.setText(""+(position+1));
        holder.textViewDefinition.setText("Some definition");
        holder.textViewPartOfSpeech.setText("verb");
        holder.textViewExample.setText("example");
        holder.textViewSynonyms.setText("syn 1\nsyn 2\nsyn 3");
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewIncrement,textViewDefinition,textViewPartOfSpeech,textViewExample,textViewSynonyms;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewIncrement=itemView.findViewById(R.id.text_increment);
            textViewDefinition=itemView.findViewById(R.id.text_definition);
            textViewPartOfSpeech=itemView.findViewById(R.id.text_part_of_speech);
            textViewExample=itemView.findViewById(R.id.text_example);
            textViewSynonyms=itemView.findViewById(R.id.text_synonyms);
        }
    }
}
