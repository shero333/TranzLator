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
import com.hammad.tranzlator.model.LanguageModel;

import java.util.ArrayList;
import java.util.List;

public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.MyViewHolder> implements Filterable{

    List<LanguageModel> supportedLanguagesList;
    //List<String> languagesCodes;
    Context context;

    //for filtering/searching items in list
    List<LanguageModel> supportedLanguagesListAll;

    OnLanguageSelectionListener mOnLanguageSelectionListener;

    boolean btnOne,btnTwo;

    public LanguagesAdapter(Context context,OnLanguageSelectionListener onLanguageSelectionListener,boolean btnOne,boolean btnTwo)
    {
        this.context=context;
        this.mOnLanguageSelectionListener=onLanguageSelectionListener;
        this.btnOne=btnOne;
        this.btnTwo=btnTwo;

        //initializing & assigning the languages names & codes
        supportedLanguagesList=new ArrayList<>();
        supportedLanguagesList.add(new LanguageModel("Afrikaans","af"));
        supportedLanguagesList.add(new LanguageModel("Albanian","sq"));
        supportedLanguagesList.add(new LanguageModel("Amharic","am"));
        supportedLanguagesList.add(new LanguageModel("Arabic","ar"));
        supportedLanguagesList.add(new LanguageModel("Armenian","hy"));
        supportedLanguagesList.add(new LanguageModel("Azerbaijani","az"));

        supportedLanguagesList.add(new LanguageModel("Basque","eu"));
        supportedLanguagesList.add(new LanguageModel("Belarusian","be"));
        supportedLanguagesList.add(new LanguageModel("Bengali","bn"));
        supportedLanguagesList.add(new LanguageModel("Bosnian","bs"));
        supportedLanguagesList.add(new LanguageModel("Bulgarian","bg"));

        supportedLanguagesList.add(new LanguageModel("Catalan","ca"));
        supportedLanguagesList.add(new LanguageModel("Cebuano","ceb"));
        supportedLanguagesList.add(new LanguageModel("Chinese (Simplified)","zh-CN"));
        supportedLanguagesList.add(new LanguageModel("Chinese (Traditional)","zh-TW"));
        supportedLanguagesList.add(new LanguageModel("Corsican","co"));
        supportedLanguagesList.add(new LanguageModel("Croatian","hr"));
        supportedLanguagesList.add(new LanguageModel("Czech","cs"));

        supportedLanguagesList.add(new LanguageModel("Danish","da"));
        supportedLanguagesList.add(new LanguageModel("Dutch","nl"));

        supportedLanguagesList.add(new LanguageModel("English","en"));
        supportedLanguagesList.add(new LanguageModel("Esperanto","eo"));
        supportedLanguagesList.add(new LanguageModel("Estonian","et"));

        supportedLanguagesList.add(new LanguageModel("Finnish","fi"));
        supportedLanguagesList.add(new LanguageModel("French","fr"));
        supportedLanguagesList.add(new LanguageModel("Frisian","fy"));

        supportedLanguagesList.add(new LanguageModel("Galician","gl"));
        supportedLanguagesList.add(new LanguageModel("Georgian","ka"));
        supportedLanguagesList.add(new LanguageModel("German","de"));
        supportedLanguagesList.add(new LanguageModel("Greek","el"));
        supportedLanguagesList.add(new LanguageModel("Gujarati","gu"));

        supportedLanguagesList.add(new LanguageModel("Haitian Creole","ht"));
        supportedLanguagesList.add(new LanguageModel("Hausa","ha"));
        supportedLanguagesList.add(new LanguageModel("Hawaiian","haw"));
        supportedLanguagesList.add(new LanguageModel("Hebrew","he"));
        supportedLanguagesList.add(new LanguageModel("Hindi","hi"));
        supportedLanguagesList.add(new LanguageModel("Hmong","hmn"));
        supportedLanguagesList.add(new LanguageModel("Hungarian","hu"));

        supportedLanguagesList.add(new LanguageModel("Icelandic","is"));
        supportedLanguagesList.add(new LanguageModel("Igbo","ig"));
        supportedLanguagesList.add(new LanguageModel("Indonesian","id"));
        supportedLanguagesList.add(new LanguageModel("Irish","ga"));
        supportedLanguagesList.add(new LanguageModel("Italian","it"));

        supportedLanguagesList.add(new LanguageModel("Japanese","ja"));
        supportedLanguagesList.add(new LanguageModel("Javanese","jv"));

        supportedLanguagesList.add(new LanguageModel("Kannada","kn"));
        supportedLanguagesList.add(new LanguageModel("Kazakh","kk"));
        supportedLanguagesList.add(new LanguageModel("Khmer","km"));
        supportedLanguagesList.add(new LanguageModel("Kinyarwanda","rw"));
        supportedLanguagesList.add(new LanguageModel("Korean","ko"));
        supportedLanguagesList.add(new LanguageModel("Kurdish","ku"));
        supportedLanguagesList.add(new LanguageModel("Kyrgyz","ky"));

        supportedLanguagesList.add(new LanguageModel("Lao","lo"));
        supportedLanguagesList.add(new LanguageModel("Latvian","lv"));
        supportedLanguagesList.add(new LanguageModel("Lithuanian","lt"));
        supportedLanguagesList.add(new LanguageModel("Luxembourgish","lb"));

        supportedLanguagesList.add(new LanguageModel("Macedonian","mk"));
        supportedLanguagesList.add(new LanguageModel("Malagasy","mg"));
        supportedLanguagesList.add(new LanguageModel("Malay","ms"));
        supportedLanguagesList.add(new LanguageModel("Malayalam","ml"));
        supportedLanguagesList.add(new LanguageModel("Maltese","mt"));
        supportedLanguagesList.add(new LanguageModel("Maori","mi"));
        supportedLanguagesList.add(new LanguageModel("Marathi","mr"));
        supportedLanguagesList.add(new LanguageModel("Mongolian","mn"));
        supportedLanguagesList.add(new LanguageModel("Myanmar (Burmese)","my"));

        supportedLanguagesList.add(new LanguageModel("Nepali","ne"));
        supportedLanguagesList.add(new LanguageModel("Norwegian","no"));
        supportedLanguagesList.add(new LanguageModel("Nyanja (Chichewa)","ny"));

        supportedLanguagesList.add(new LanguageModel("Odia (Oriya)","or"));

        supportedLanguagesList.add(new LanguageModel("Pashto","ps"));
        supportedLanguagesList.add(new LanguageModel("Persian","fa"));
        supportedLanguagesList.add(new LanguageModel("Polish","pl"));
        supportedLanguagesList.add(new LanguageModel("Portuguese (Portugal, Brazil)","pt"));
        supportedLanguagesList.add(new LanguageModel("Punjabi","pa"));

        supportedLanguagesList.add(new LanguageModel("Romanian","ro"));
        supportedLanguagesList.add(new LanguageModel("Russian","ru"));

        supportedLanguagesList.add(new LanguageModel("Samoan","sm"));
        supportedLanguagesList.add(new LanguageModel("Scots Gaelic","gd"));
        supportedLanguagesList.add(new LanguageModel("Serbian","sr"));
        supportedLanguagesList.add(new LanguageModel("Sesotho","st"));
        supportedLanguagesList.add(new LanguageModel("Shona","sn"));
        supportedLanguagesList.add(new LanguageModel("Sindhi","sd"));
        supportedLanguagesList.add(new LanguageModel("Sinhala (Sinhalese)","si"));
        supportedLanguagesList.add(new LanguageModel("Slovak","sk"));
        supportedLanguagesList.add(new LanguageModel("Slovenian","sl"));
        supportedLanguagesList.add(new LanguageModel("Somali","so"));
        supportedLanguagesList.add(new LanguageModel("Spanish","es"));
        supportedLanguagesList.add(new LanguageModel("Sundanese","su"));
        supportedLanguagesList.add(new LanguageModel("Swahili","sw"));
        supportedLanguagesList.add(new LanguageModel("Swedish","sv"));

        supportedLanguagesList.add(new LanguageModel("Tagalog (Filipino)","tl"));
        supportedLanguagesList.add(new LanguageModel("Tajik","tg"));
        supportedLanguagesList.add(new LanguageModel("Tamil","ta"));
        supportedLanguagesList.add(new LanguageModel("Tatar","tt"));
        supportedLanguagesList.add(new LanguageModel("Telugu","te"));
        supportedLanguagesList.add(new LanguageModel("Thai","th"));
        supportedLanguagesList.add(new LanguageModel("Turkish","tr"));
        supportedLanguagesList.add(new LanguageModel("Turkmen","tk"));

        supportedLanguagesList.add(new LanguageModel("Ukrainian","uk"));
        supportedLanguagesList.add(new LanguageModel("Urdu","ur"));
        supportedLanguagesList.add(new LanguageModel("Uyghur","ug"));
        supportedLanguagesList.add(new LanguageModel("Uzbek","uz"));

        supportedLanguagesList.add(new LanguageModel("Vietnamese","vi"));

        supportedLanguagesList.add(new LanguageModel("Welsh","cy"));

        supportedLanguagesList.add(new LanguageModel("Xhosa","xh"));

        supportedLanguagesList.add(new LanguageModel("Yiddish","yi"));
        supportedLanguagesList.add(new LanguageModel("Yoruba","yo"));

        supportedLanguagesList.add(new LanguageModel("Zulu","zu"));


        //for filtering
        supportedLanguagesListAll=new ArrayList<>(supportedLanguagesList);

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
                //Toast.makeText(context, "Code: "+languagesCodes.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
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
            List<LanguageModel> filteredLanguages=new ArrayList<>();

            if(constraint==null || constraint.length()==0)
            {
                filteredLanguages.addAll(supportedLanguagesListAll);
            }
            else
            {
                String filterPattern=constraint.toString().toLowerCase().trim();

                for(LanguageModel item : supportedLanguagesListAll)
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
