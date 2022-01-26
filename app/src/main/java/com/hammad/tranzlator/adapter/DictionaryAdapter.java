package com.hammad.tranzlator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.tranzlator.DictionaryModel;
import com.hammad.tranzlator.R;

import java.util.List;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.MyViewHolder> {

    Context context;
    List<DictionaryModel> modelList;

    public DictionaryAdapter(Context context,List<DictionaryModel> modelList1) {
        this.context = context;
        this.modelList=modelList1;
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

        if(modelList.size() > 1)
        {
            holder.textViewIncrement.setText(""+(position+1));
            holder.textViewDefinition.setText(modelList.get(position).getDefinition().trim());
            holder.textViewPartOfSpeech.setText(modelList.get(position).getPartOfSpeech().trim());
            holder.textViewExample.setText(modelList.get(position).getExample().trim());
        }
        else
        {
            holder.textViewDefinition.setText(modelList.get(position).getDefinition().trim());
            holder.textViewPartOfSpeech.setText(modelList.get(position).getPartOfSpeech().trim());
            holder.textViewExample.setText(modelList.get(position).getExample().trim());
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewIncrement,textViewDefinition,textViewPartOfSpeech,textViewExample;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewIncrement=itemView.findViewById(R.id.text_increment);
            textViewDefinition=itemView.findViewById(R.id.text_definition);
            textViewPartOfSpeech=itemView.findViewById(R.id.text_part_of_speech);
            textViewExample=itemView.findViewById(R.id.text_example);
        }
    }
}
