package com.hammad.tranzlator;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.MyViewHolder> implements Filterable{

    List<String> supportedLanguages;
    List<String> languagesCodes;
    Context context;

    //for filtering/searching items in list
    List<String> supportedLanguagesAll;

    OnLanguageSelectionListener mOnLanguageSelectionListener;

    boolean btnOne,btnTwo;

    public LanguagesAdapter(Context context,OnLanguageSelectionListener onLanguageSelectionListener,boolean btnOne,boolean btnTwo)
    {
        this.context=context;
        this.mOnLanguageSelectionListener=onLanguageSelectionListener;
        this.btnOne=btnOne;
        this.btnTwo=btnTwo;
        /**
         * we converted the supported languages list to linked list because on casting string array to list,
         * the methods of list gives an exception of *UnsupportedOperationException*
         * the reason is that string array has a fixed size so we can't add or delete items from it
         * fix is to convert the list into linked list
         **/

        //converting the declared string arrays to list
        supportedLanguages=new LinkedList<String>(Arrays.asList(context.getResources().getStringArray(R.array.supportedLanguages)));
        languagesCodes=Arrays.asList(context.getResources().getStringArray(R.array.languagesCodes));

        //for filtering
        supportedLanguagesAll=new ArrayList<>(supportedLanguages);

    }

    @NonNull
    @Override
    public LanguagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.layout_language_list,parent,false);
        return new MyViewHolder(view,mOnLanguageSelectionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguagesAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(supportedLanguages.get(position));
    }

    @Override
    public int getItemCount() {
        return supportedLanguages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        OnLanguageSelectionListener onLanguageSelectionListener;

        public MyViewHolder(@NonNull View itemView,OnLanguageSelectionListener onLanguageSelectionListener) {
            super(itemView);

            textView=itemView.findViewById(R.id.textview_language_list);
            this.onLanguageSelectionListener=onLanguageSelectionListener;

            textView.setOnClickListener(v ->{
                //Toast.makeText(context, "Code: "+languagesCodes.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                onLanguageSelectionListener.onLanguageSelection(supportedLanguages.get(getAdapterPosition()).toString(),languagesCodes.get(getAdapterPosition()).toString()
                ,btnOne,btnTwo);
            });

        }
    }

    //for searching/filtering items in recyclerview
    @Override
    public Filter getFilter() {
        return languageFilter;
    }

    private Filter languageFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredLanguages=new ArrayList<>();

            if(constraint==null || constraint.length()==0)
            {
                filteredLanguages.addAll(supportedLanguagesAll);
            }
            else
            {
                String filterPattern=constraint.toString().toLowerCase().trim();

                for(String item : supportedLanguagesAll)
                {
                    if (item.toLowerCase().contains(filterPattern)) {
                        filteredLanguages.add(item);
                    }
                }
            }

            FilterResults filterResults=new FilterResults();
            filterResults.values=filteredLanguages;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            supportedLanguages.clear();
            supportedLanguages.addAll((List<String>) results.values);
            notifyDataSetChanged();
        }
    };

    public interface OnLanguageSelectionListener{
        void onLanguageSelection(String lang,String langCode,boolean btnOnePressed,boolean btnTwoPressed);
    }

}
