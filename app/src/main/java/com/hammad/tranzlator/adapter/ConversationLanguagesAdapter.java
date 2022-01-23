package com.hammad.tranzlator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.tranzlator.R;
import com.hammad.tranzlator.model.ConversationLanguageModel;

import java.util.ArrayList;
import java.util.List;

public class ConversationLanguagesAdapter extends RecyclerView.Adapter<ConversationLanguagesAdapter.MyViewHolder> implements Filterable {

    List<ConversationLanguageModel> supportedLanguagesList;
    Context context;

    //for filtering/searching items in list
    List<ConversationLanguageModel> supportedLanguagesListAll;

    ConversationLanguagesAdapter.OnConversationLanguageSelectionListener mOnConversationLanguageSelectionListener;

    boolean btnOne, btnTwo;

    public ConversationLanguagesAdapter(Context context, ConversationLanguagesAdapter.OnConversationLanguageSelectionListener onConversationLanguageSelectionListener, boolean btnOne, boolean btnTwo)
    {
        this.context=context;
        this.mOnConversationLanguageSelectionListener=onConversationLanguageSelectionListener;
        this.btnOne=btnOne;
        this.btnTwo=btnTwo;

        //initializing supported languages list
        supportedLanguagesList=new ArrayList<>();

        supportedLanguagesList.add(new ConversationLanguageModel("Albanian","sq"));
        supportedLanguagesList.add(new ConversationLanguageModel("Arabic","ar"));

        supportedLanguagesList.add(new ConversationLanguageModel("Bangla (Bangladesh)","bn-BD"));
        supportedLanguagesList.add(new ConversationLanguageModel("Bangla (India)","bn-IN"));
        supportedLanguagesList.add(new ConversationLanguageModel("Bosnian","bs"));
        supportedLanguagesList.add(new ConversationLanguageModel("Bulgarian (Bulgaria)","bg-BG"));

        supportedLanguagesList.add(new ConversationLanguageModel("Catalan","ca"));
        supportedLanguagesList.add(new ConversationLanguageModel("Chinese (China)","zh-CN"));
        supportedLanguagesList.add(new ConversationLanguageModel("Chinese (Taiwan)","zh-TW"));
        supportedLanguagesList.add(new ConversationLanguageModel("Croatian","hr"));
        supportedLanguagesList.add(new ConversationLanguageModel("Czech (Czech Republic)","cs-CZ"));

        supportedLanguagesList.add(new ConversationLanguageModel("Danish (Denmark)","da-DK"));
        supportedLanguagesList.add(new ConversationLanguageModel("Dutch (Belgium)","nl-BE"));
        supportedLanguagesList.add(new ConversationLanguageModel("Dutch (Netherlands)","nl-NL"));

        supportedLanguagesList.add(new ConversationLanguageModel("English (Australia)","en-AU"));
        supportedLanguagesList.add(new ConversationLanguageModel("English (United Kingdom)","en-GB"));
        supportedLanguagesList.add(new ConversationLanguageModel("English (India)","en-IN"));
        supportedLanguagesList.add(new ConversationLanguageModel("English (Nigeria)","en-NG"));
        supportedLanguagesList.add(new ConversationLanguageModel("English (United States)","en-US"));
        supportedLanguagesList.add(new ConversationLanguageModel("Estonian (Estonia)","et-EE"));

        supportedLanguagesList.add(new ConversationLanguageModel("Finnish (Finland)","fi-FI"));
        supportedLanguagesList.add(new ConversationLanguageModel("French (Canada)","fr-CA"));
        supportedLanguagesList.add(new ConversationLanguageModel("French (France)","fr-FR"));

        supportedLanguagesList.add(new ConversationLanguageModel("German (Germany)","de-DE"));
        supportedLanguagesList.add(new ConversationLanguageModel("Greek (Greece)","el-GR"));
        supportedLanguagesList.add(new ConversationLanguageModel("Gujarati (India)","gu-IN"));

        supportedLanguagesList.add(new ConversationLanguageModel("Hindi","hi-IN"));
        supportedLanguagesList.add(new ConversationLanguageModel("Hungarian (Hungary)","hu-HU"));

        supportedLanguagesList.add(new ConversationLanguageModel("Indonesian (Indonesia)","id-ID"));
        supportedLanguagesList.add(new ConversationLanguageModel("Italian (Italy)","it-IT"));

        supportedLanguagesList.add(new ConversationLanguageModel("Japanese","ja-JP"));
        supportedLanguagesList.add(new ConversationLanguageModel("Javanese (Indonesia)","jv-ID"));

        supportedLanguagesList.add(new ConversationLanguageModel("Kannada (India)","kn-IN"));
        supportedLanguagesList.add(new ConversationLanguageModel("Khmer (Cambodia)","km-KH"));
        supportedLanguagesList.add(new ConversationLanguageModel("Korean","ko-KR"));
        supportedLanguagesList.add(new ConversationLanguageModel("Kurdish","ku"));

        supportedLanguagesList.add(new ConversationLanguageModel("Latvian","lv-LV"));

        supportedLanguagesList.add(new ConversationLanguageModel("Malay (Malaysia)","ms-MY"));
        supportedLanguagesList.add(new ConversationLanguageModel("Malayalam (India)","ml-IN"));
        supportedLanguagesList.add(new ConversationLanguageModel("Marathi","mr-IN"));

        supportedLanguagesList.add(new ConversationLanguageModel("Polish (Poland)","pl-PL"));
        supportedLanguagesList.add(new ConversationLanguageModel("Portuguese (Brazil)","pt-BR"));
        supportedLanguagesList.add(new ConversationLanguageModel("Portuguese (Portugal)","pt-PT"));
        supportedLanguagesList.add(new ConversationLanguageModel("Punjabi (India)","pa-IN"));

        supportedLanguagesList.add(new ConversationLanguageModel("Romanian (Romania)","ro-RO"));
        supportedLanguagesList.add(new ConversationLanguageModel("Russian (Russia)","ru-RU"));

        supportedLanguagesList.add(new ConversationLanguageModel("Serbian (Serbia)","sr"));
        supportedLanguagesList.add(new ConversationLanguageModel("Sinhala (Sri Lanka)","si-LK"));
        supportedLanguagesList.add(new ConversationLanguageModel("Slovak (Slovakia)","sk-SK"));
        supportedLanguagesList.add(new ConversationLanguageModel("Spanish (Spain)","es-ES"));
        supportedLanguagesList.add(new ConversationLanguageModel("Spanish (US)","es-US"));
        supportedLanguagesList.add(new ConversationLanguageModel("Sundanese (Indonesia)","su-ID"));
        supportedLanguagesList.add(new ConversationLanguageModel("Swahili","sw"));
        supportedLanguagesList.add(new ConversationLanguageModel("Swedish (Sweden)","sv-SE"));

        supportedLanguagesList.add(new ConversationLanguageModel("Tamil (India)","ta-IN"));
        supportedLanguagesList.add(new ConversationLanguageModel("Telugu (India)","te-IN"));
        supportedLanguagesList.add(new ConversationLanguageModel("Thai (Thailand)","th-TH"));
        supportedLanguagesList.add(new ConversationLanguageModel("Turkish (Turkey)","tr-TR"));

        supportedLanguagesList.add(new ConversationLanguageModel("Ukrainian","uk-UA"));
        supportedLanguagesList.add(new ConversationLanguageModel("Urdu (Pakistan)","ur-PK"));

        supportedLanguagesList.add(new ConversationLanguageModel("Vietnamese (Vietnam)","vi-VN"));

        supportedLanguagesList.add(new ConversationLanguageModel("Welsh","cy"));

        //for filtering
        supportedLanguagesListAll=new ArrayList<>(supportedLanguagesList);

    }

    @NonNull
    @Override
    public ConversationLanguagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_language_list, parent, false);
        return new ConversationLanguagesAdapter.MyViewHolder(view, mOnConversationLanguageSelectionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationLanguagesAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(supportedLanguagesList.get(position).getConLanguageName());
    }

    @Override
    public int getItemCount() {
        return supportedLanguagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ConversationLanguagesAdapter.OnConversationLanguageSelectionListener onConversationLanguageSelectionListener;

        public MyViewHolder(@NonNull View itemView, ConversationLanguagesAdapter.OnConversationLanguageSelectionListener onConversationLanguageSelectionListener) {
            super(itemView);

            textView = itemView.findViewById(R.id.textview_language_list);
            this.onConversationLanguageSelectionListener = onConversationLanguageSelectionListener;

            textView.setOnClickListener(v -> {
                onConversationLanguageSelectionListener.onConversationLanguageSelection(supportedLanguagesList.get(getAdapterPosition()).getConLanguageName(), supportedLanguagesList.get(getAdapterPosition()).getConLanguageCode()
                        , btnOne, btnTwo);
            });

        }
    }

    public interface OnConversationLanguageSelectionListener {
        void onConversationLanguageSelection(String lang, String langCode, boolean btnOnePressed, boolean btnTwoPressed);
    }

    //for searching/filtering items in recyclerview
    @Override
    public Filter getFilter() {
        return languageFilter;
    }

    private Filter languageFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ConversationLanguageModel> filteredLanguages = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredLanguages.addAll(supportedLanguagesListAll);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ConversationLanguageModel item : supportedLanguagesListAll) {
                    if (item.getConLanguageName().toLowerCase().contains(filterPattern)) {
                        filteredLanguages.add(item);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredLanguages;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            supportedLanguagesList.clear();
            supportedLanguagesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
