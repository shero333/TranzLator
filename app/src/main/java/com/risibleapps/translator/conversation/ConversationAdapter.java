package com.risibleapps.translator.conversation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.risibleapps.translator.R;
import com.risibleapps.translator.conversation.db.ConversationDataEntity;
import com.risibleapps.translator.room.TranslationRoomDB;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    Context context;

    List<ConversationDataEntity> list;

    OnSpeakerPressedListener mOnSpeakerPressedListener;

    TranslationRoomDB database;


    public ConversationAdapter(Context context, OnSpeakerPressedListener onSpeakerPressedListener, List<ConversationDataEntity> arrayList1) {
        this.context = context;
        mOnSpeakerPressedListener=onSpeakerPressedListener;
        list =arrayList1;
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

        ConversationDataEntity conversationDataEntity= list.get(position);
        database=TranslationRoomDB.getInstance(context);

        if(list.get(position).getConCode()==1)
        {
            holder.cardView1.setVisibility(View.VISIBLE);

            holder.textView1Card1.setText(list.get(position).getConSourceText());
            holder.textView2Card1.setText(list.get(position).getConTranslatedText());

            //setting the cardview 2 visibility to gone
            holder.cardView2.setVisibility(View.GONE);
        }
        else if(list.get(position).getConCode()==2)
        {
            holder.cardView2.setVisibility(View.VISIBLE);

            holder.textView1Card2.setText(list.get(position).getConSourceText());
            holder.textView2Card2.setText(list.get(position).getConTranslatedText());

            //setting the cardview 1 visibility to gone
            holder.cardView1.setVisibility(View.GONE);
        }

        //click listeners for image buttons which will convert translated text to speech
        holder.imageViewCard1.setOnClickListener(v ->
        mOnSpeakerPressedListener.onSpeakerPressed(list.get(position).getTTSCode(), list.get(position).getConTranslatedText()));

        holder.imageViewCard2.setOnClickListener(v ->
                mOnSpeakerPressedListener.onSpeakerPressed(list.get(position).getTTSCode(), list.get(position).getConTranslatedText()));

    }

    @Override
    public int getItemCount() {
        return list.size();
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

    public interface OnSpeakerPressedListener
    {
        void onSpeakerPressed(String code,String textToSpeech);
    }


}
