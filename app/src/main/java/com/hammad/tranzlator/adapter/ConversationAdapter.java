package com.hammad.tranzlator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.tranzlator.ConversationDataModel;
import com.hammad.tranzlator.R;

import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    Context context;

    ArrayList<ConversationDataModel> arrayList=new ArrayList<>();

    ConversationDataModel conversationDataModel=new ConversationDataModel();
    OnItemChangeListener mOnItemChangeListener;

    public ConversationAdapter(Context context,OnItemChangeListener onItemChangeListener) {
        this.context = context;
        this.mOnItemChangeListener=onItemChangeListener;
    }

    @NonNull
    @Override
    public ConversationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.layout_conversation,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.MyViewHolder holder, int position) {

        if(arrayList.get(position).getButtonPressedID()==1)
        {
            holder.cardView1.setVisibility(View.VISIBLE);

            holder.textView1Card1.setText(arrayList.get(position).getSpeechToText());
            holder.textView2Card1.setText(arrayList.get(position).getTranslatedText());

            //setting the cardview 2 visibility to gone
            holder.cardView2.setVisibility(View.GONE);
        }
        else if(arrayList.get(position).getButtonPressedID()==2)
        {
            holder.cardView2.setVisibility(View.VISIBLE);

            holder.textView1Card2.setText(arrayList.get(position).getSpeechToText());
            holder.textView2Card2.setText(arrayList.get(position).getTranslatedText());

            //setting the cardview 1 visibility to gone
            holder.cardView1.setVisibility(View.GONE);
        }

        //click listeners for image buttons which will convert translated text to speech
        holder.imageViewCard1.setOnClickListener(v ->
                Toast.makeText(context, arrayList.get(position).getCode(), Toast.LENGTH_SHORT).show());

        holder.imageViewCard2.setOnClickListener(v ->
                Toast.makeText(context, arrayList.get(position).getCode(), Toast.LENGTH_SHORT).show());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView1, cardView2;
        TextView textView1Card1,textView2Card1,textView1Card2,textView2Card2;
        ImageView imageViewCard1,imageViewCard2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView1=itemView.findViewById(R.id.cardview_1);
            cardView2=itemView.findViewById(R.id.cardview_2);

            textView1Card1=itemView.findViewById(R.id.text_1_cardview_1);
            textView2Card1=itemView.findViewById(R.id.text_2_cardview_1);

            textView1Card2=itemView.findViewById(R.id.text_1_cardview_2);
            textView2Card2=itemView.findViewById(R.id.text_2_cardview_2);

            imageViewCard1=itemView.findViewById(R.id.img_cardview_1);
            imageViewCard2=itemView.findViewById(R.id.img_cardview_2);
        }
    }

    public void addNewItem(int id,String speechToText, String translatedText, String code)
    {
        conversationDataModel.setButtonPressedID(id);
        conversationDataModel.setSpeechToText(speechToText);
        conversationDataModel.setTranslatedText(translatedText);
        conversationDataModel.setCode(code);

        arrayList.add(conversationDataModel);
        int newPosition = arrayList.size() - 1;
        notifyItemChanged(newPosition);
    }

    public interface OnItemChangeListener{
        void onItemChanged(int position);
    }

}
