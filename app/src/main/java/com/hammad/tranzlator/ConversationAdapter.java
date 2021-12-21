package com.hammad.tranzlator;

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

import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> testArray;

    public ConversationAdapter(Context context,ArrayList<String> array) {
        this.context = context;
        testArray=array;
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

        if(testArray.get(position).equals("Item_1"))
        {
            holder.cardView1.setVisibility(View.VISIBLE);

            holder.textView1Card1.setText("Item 1 spoken text");
            holder.textView2Card1.setText("Item 1 translated text");

            //setting the cardview 2 visibility to gone
            holder.cardView2.setVisibility(View.GONE);
        }
        else if(testArray.get(position).equals("Item_2"))
        {
            holder.cardView2.setVisibility(View.VISIBLE);

            holder.textView1Card2.setText("Item 2 spoken text");
            holder.textView2Card2.setText("Item 2 translated text");

            //setting the cardview 1 visibility to gone
            holder.cardView1.setVisibility(View.GONE);
        }

        //click listeners for image buttons which will convert translated text to speech
        holder.imageViewCard1.setOnClickListener(v ->
                Toast.makeText(context, "Img 1", Toast.LENGTH_SHORT).show());

        holder.imageViewCard2.setOnClickListener(v ->
                Toast.makeText(context, "Img 2", Toast.LENGTH_SHORT).show());

    }

    @Override
    public int getItemCount() {
        return testArray.size();
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
}
