package com.example.bmi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bmi.EditFoodActivity;
import com.example.bmi.R;
import com.example.bmi.model.OldStatues;
import com.example.bmi.model.ViewFood;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterViewFood extends RecyclerView.Adapter<AdapterViewFood.Holder> {
    ArrayList<ViewFood> adapterIteamChaletLists;
    OnItemClickListener onItemClickListener;
    Context context;

    ViewGroup gg;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public AdapterViewFood(ArrayList<ViewFood> adapterIteamChaletLists, Context context) {
        this.adapterIteamChaletLists = adapterIteamChaletLists;
        this.context = context;

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_food, null, false);
        Holder holder = new Holder(v);
         gg = parent;

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ViewFood viewFood=adapterIteamChaletLists.get(position);

        holder.txt_name_food_list.setText(viewFood.getName());
        holder.txt_category_food_list.setText(viewFood.getCategory());
        holder.txt_calory_food_list.setText(viewFood.getCalory());

        Picasso.get().load(viewFood.getImage()).into(holder.image_food_list);

        holder.btn_edit_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditFoodActivity.class);
                intent.putExtra("getCategory",viewFood.getCategory());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return adapterIteamChaletLists.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView txt_name_food_list, txt_category_food_list,txt_calory_food_list;
        ImageView image_food_list;
        Button btn_edit_food;

        public Holder(@NonNull View itemView) {

            super(itemView);
            image_food_list = itemView.findViewById(R.id.image_food_list);
            txt_name_food_list = itemView.findViewById(R.id.txt_name_food_list);
            txt_category_food_list = itemView.findViewById(R.id.txt_category_food_list);
            txt_calory_food_list = itemView.findViewById(R.id.txt_calory_food_list);
            btn_edit_food = itemView.findViewById(R.id.btn_edit_food);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });


        }
    }

}
