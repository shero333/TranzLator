package com.risibleapps.translator.translate.translationLanguages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.risibleapps.translator.R;

import java.util.ArrayList;
import java.util.List;

public class TranslationLanguagesAdapter extends RecyclerView.Adapter<TranslationLanguagesAdapter.MyViewHolder> implements Filterable{

    List<TranslationLanguageModel> supportedLanguagesList;
    Context context;

    //for filtering/searching items in list
    List<TranslationLanguageModel> supportedLanguagesListAll;

    OnLanguageSelectionListener mOnLanguageSelectionListener;

    boolean btnOne,btnTwo;

    public TranslationLanguagesAdapter(Context context, OnLanguageSelectionListener onLanguageSelectionListener, boolean btnOne, boolean btnTwo)
    {
        this.context=context;
        this.mOnLanguageSelectionListener=onLanguageSelectionListener;
        this.btnOne=btnOne;
        this.btnTwo=btnTwo;

        //initializing & assigning the languages names & codes
        supportedLanguagesList=new ArrayList<>();
        supportedLanguagesList.add(new TranslationLanguageModel("Afrikaans","af"));
        supportedLanguagesList.add(new TranslationLanguageModel("Albanian","sq"));
        supportedLanguagesList.add(new TranslationLanguageModel("Amharic","am"));
        supportedLanguagesList.add(new TranslationLanguageModel("Arabic","ar"));
        supportedLanguagesList.add(new TranslationLanguageModel("Armenian","hy"));
        supportedLanguagesList.add(new TranslationLanguageModel("Azerbaijani","az"));

        supportedLanguagesList.add(new TranslationLanguageModel("Basque","eu"));
        supportedLanguagesList.add(new TranslationLanguageModel("Belarusian","be"));
        supportedLanguagesList.add(new TranslationLanguageModel("Bengali","bn"));
        supportedLanguagesList.add(new TranslationLanguageModel("Bosnian","bs"));
        supportedLanguagesList.add(new TranslationLanguageModel("Bulgarian","bg"));

        supportedLanguagesList.add(new TranslationLanguageModel("Catalan","ca"));
        supportedLanguagesList.add(new TranslationLanguageModel("Cebuano","ceb"));
        supportedLanguagesList.add(new TranslationLanguageModel("Chinese (Simplified)","zh-CN"));
        supportedLanguagesList.add(new TranslationLanguageModel("Chinese (Traditional)","zh-TW"));
        supportedLanguagesList.add(new TranslationLanguageModel("Corsican","co"));
        supportedLanguagesList.add(new TranslationLanguageModel("Croatian","hr"));
        supportedLanguagesList.add(new TranslationLanguageModel("Czech","cs"));

        supportedLanguagesList.add(new TranslationLanguageModel("Danish","da"));
        supportedLanguagesList.add(new TranslationLanguageModel("Dutch","nl"));

        supportedLanguagesList.add(new TranslationLanguageModel("English","en"));
        supportedLanguagesList.add(new TranslationLanguageModel("Esperanto","eo"));
        supportedLanguagesList.add(new TranslationLanguageModel("Estonian","et"));

        supportedLanguagesList.add(new TranslationLanguageModel("Finnish","fi"));
        supportedLanguagesList.add(new TranslationLanguageModel("French","fr"));
        supportedLanguagesList.add(new TranslationLanguageModel("Frisian","fy"));

        supportedLanguagesList.add(new TranslationLanguageModel("Galician","gl"));
        supportedLanguagesList.add(new TranslationLanguageModel("Georgian","ka"));
        supportedLanguagesList.add(new TranslationLanguageModel("German","de"));
        supportedLanguagesList.add(new TranslationLanguageModel("Greek","el"));
        supportedLanguagesList.add(new TranslationLanguageModel("Gujarati","gu"));

        supportedLanguagesList.add(new TranslationLanguageModel("Haitian Creole","ht"));
        supportedLanguagesList.add(new TranslationLanguageModel("Hausa","ha"));
        supportedLanguagesList.add(new TranslationLanguageModel("Hawaiian","haw"));
        supportedLanguagesList.add(new TranslationLanguageModel("Hebrew","he"));
        supportedLanguagesList.add(new TranslationLanguageModel("Hindi","hi"));
        supportedLanguagesList.add(new TranslationLanguageModel("Hmong","hmn"));
        supportedLanguagesList.add(new TranslationLanguageModel("Hungarian","hu"));

        supportedLanguagesList.add(new TranslationLanguageModel("Icelandic","is"));
        supportedLanguagesList.add(new TranslationLanguageModel("Igbo","ig"));
        supportedLanguagesList.add(new TranslationLanguageModel("Indonesian","id"));
        supportedLanguagesList.add(new TranslationLanguageModel("Irish","ga"));
        supportedLanguagesList.add(new TranslationLanguageModel("Italian","it"));

        supportedLanguagesList.add(new TranslationLanguageModel("Japanese","ja"));
        supportedLanguagesList.add(new TranslationLanguageModel("Javanese","jv"));

        supportedLanguagesList.add(new TranslationLanguageModel("Kannada","kn"));
        supportedLanguagesList.add(new TranslationLanguageModel("Kazakh","kk"));
        supportedLanguagesList.add(new TranslationLanguageModel("Khmer","km"));
        supportedLanguagesList.add(new TranslationLanguageModel("Kinyarwanda","rw"));
        supportedLanguagesList.add(new TranslationLanguageModel("Korean","ko"));
        supportedLanguagesList.add(new TranslationLanguageModel("Kurdish","ku"));
        supportedLanguagesList.add(new TranslationLanguageModel("Kyrgyz","ky"));

        supportedLanguagesList.add(new TranslationLanguageModel("Lao","lo"));
        supportedLanguagesList.add(new TranslationLanguageModel("Latvian","lv"));
        supportedLanguagesList.add(new TranslationLanguageModel("Lithuanian","lt"));
        supportedLanguagesList.add(new TranslationLanguageModel("Luxembourgish","lb"));

        supportedLanguagesList.add(new TranslationLanguageModel("Macedonian","mk"));
        supportedLanguagesList.add(new TranslationLanguageModel("Malagasy","mg"));
        supportedLanguagesList.add(new TranslationLanguageModel("Malay","ms"));
        supportedLanguagesList.add(new TranslationLanguageModel("Malayalam","ml"));
        supportedLanguagesList.add(new TranslationLanguageModel("Maltese","mt"));
        supportedLanguagesList.add(new TranslationLanguageModel("Maori","mi"));
        supportedLanguagesList.add(new TranslationLanguageModel("Marathi","mr"));
        supportedLanguagesList.add(new TranslationLanguageModel("Mongolian","mn"));
        supportedLanguagesList.add(new TranslationLanguageModel("Myanmar (Burmese)","my"));

        supportedLanguagesList.add(new TranslationLanguageModel("Nepali","ne"));
        supportedLanguagesList.add(new TranslationLanguageModel("Norwegian","no"));
        supportedLanguagesList.add(new TranslationLanguageModel("Nyanja (Chichewa)","ny"));

        supportedLanguagesList.add(new TranslationLanguageModel("Odia (Oriya)","or"));

        supportedLanguagesList.add(new TranslationLanguageModel("Pashto","ps"));
        supportedLanguagesList.add(new TranslationLanguageModel("Persian","fa"));
        supportedLanguagesList.add(new TranslationLanguageModel("Polish","pl"));
        supportedLanguagesList.add(new TranslationLanguageModel("Portuguese (Portugal, Brazil)","pt"));
        supportedLanguagesList.add(new TranslationLanguageModel("Punjabi","pa"));

        supportedLanguagesList.add(new TranslationLanguageModel("Romanian","ro"));
        supportedLanguagesList.add(new TranslationLanguageModel("Russian","ru"));

        supportedLanguagesList.add(new TranslationLanguageModel("Samoan","sm"));
        supportedLanguagesList.add(new TranslationLanguageModel("Scots Gaelic","gd"));
        supportedLanguagesList.add(new TranslationLanguageModel("Serbian","sr"));
        supportedLanguagesList.add(new TranslationLanguageModel("Sesotho","st"));
        supportedLanguagesList.add(new TranslationLanguageModel("Shona","sn"));
        supportedLanguagesList.add(new TranslationLanguageModel("Sindhi","sd"));
        supportedLanguagesList.add(new TranslationLanguageModel("Sinhala (Sinhalese)","si"));
        supportedLanguagesList.add(new TranslationLanguageModel("Slovak","sk"));
        supportedLanguagesList.add(new TranslationLanguageModel("Slovenian","sl"));
        supportedLanguagesList.add(new TranslationLanguageModel("Somali","so"));
        supportedLanguagesList.add(new TranslationLanguageModel("Spanish","es"));
        supportedLanguagesList.add(new TranslationLanguageModel("Sundanese","su"));
        supportedLanguagesList.add(new TranslationLanguageModel("Swahili","sw"));
        supportedLanguagesList.add(new TranslationLanguageModel("Swedish","sv"));

        supportedLanguagesList.add(new TranslationLanguageModel("Tagalog (Filipino)","tl"));
        supportedLanguagesList.add(new TranslationLanguageModel("Tajik","tg"));
        supportedLanguagesList.add(new TranslationLanguageModel("Tamil","ta"));
        supportedLanguagesList.add(new TranslationLanguageModel("Tatar","tt"));
        supportedLanguagesList.add(new TranslationLanguageModel("Telugu","te"));
        supportedLanguagesList.add(new TranslationLanguageModel("Thai","th"));
        supportedLanguagesList.add(new TranslationLanguageModel("Turkish","tr"));
        supportedLanguagesList.add(new TranslationLanguageModel("Turkmen","tk"));

        supportedLanguagesList.add(new TranslationLanguageModel("Ukrainian","uk"));
        supportedLanguagesList.add(new TranslationLanguageModel("Urdu","ur"));
        supportedLanguagesList.add(new TranslationLanguageModel("Uyghur","ug"));
        supportedLanguagesList.add(new TranslationLanguageModel("Uzbek","uz"));

        supportedLanguagesList.add(new TranslationLanguageModel("Vietnamese","vi"));

        supportedLanguagesList.add(new TranslationLanguageModel("Welsh","cy"));

        supportedLanguagesList.add(new TranslationLanguageModel("Xhosa","xh"));

        supportedLanguagesList.add(new TranslationLanguageModel("Yiddish","yi"));
        supportedLanguagesList.add(new TranslationLanguageModel("Yoruba","yo"));

        supportedLanguagesList.add(new TranslationLanguageModel("Zulu","zu"));


        //for filtering
        supportedLanguagesListAll=new ArrayList<>(supportedLanguagesList);

    }

    @NonNull
    @Override
    public TranslationLanguagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.layout_language_list,parent,false);
        return new MyViewHolder(view,mOnLanguageSelectionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TranslationLanguagesAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(supportedLanguagesList.get(position).getLanguageName());
    }

    @Override
    public int getItemCount() {
        return supportedLanguagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        OnLanguageSelectionListener onLanguageSelectionListener;

        public MyViewHolder(@NonNull View itemView,OnLanguageSelectionListener onLanguageSelectionListener) {
            super(itemView);

            textView=itemView.findViewById(R.id.textview_language_list);
            this.onLanguageSelectionListener=onLanguageSelectionListener;

            textView.setOnClickListener(v ->{
                onLanguageSelectionListener.onLanguageSelection(supportedLanguagesList.get(getAdapterPosition()).getLanguageName(),supportedLanguagesList.get(getAdapterPosition()).getLanguageCode()
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
            List<TranslationLanguageModel> filteredLanguages=new ArrayList<>();

            if(constraint==null || constraint.length()==0)
            {
                filteredLanguages.addAll(supportedLanguagesListAll);
            }
            else
            {
                String filterPattern=constraint.toString().toLowerCase().trim();

                for(TranslationLanguageModel item : supportedLanguagesListAll)
                {
                    if (item.getLanguageName().toLowerCase().contains(filterPattern)) {
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
            supportedLanguagesList.clear();
            supportedLanguagesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public interface OnLanguageSelectionListener{
        void onLanguageSelection(String lang,String langCode,boolean btnOnePressed,boolean btnTwoPressed);
    }

}
