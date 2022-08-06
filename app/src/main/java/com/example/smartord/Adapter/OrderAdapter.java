package com.example.smartord.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartord.Classes.Order;
import com.example.smartord.Dialog.DialogFragment;
import com.example.smartord.R;

public class OrderAdapter extends ListAdapter<Order, OrderAdapter.OrderViewHolder>  {
    OrderClickInterface orderClickInterface;
    FragmentManager fm;

    // adapter constructor -> set inter
    public OrderAdapter(@NonNull DiffUtil.ItemCallback<Order> diffCallback, OrderClickInterface orderClickInterface,FragmentManager fm) {
        super(diffCallback);
        this.orderClickInterface = orderClickInterface;
            this.fm = fm;
        }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView dateTextView, attendedTextView,hour;
        ImageButton deleteBtn;


        // connect variables to xml, set on click method
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateOfReservation);
            attendedTextView = itemView.findViewById(R.id.attendedNumbers);
            hour = itemView.findViewById(R.id.timeOfReservation);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // clicking on bin btn to erase the specific item
                    DialogFragment dialogFragment =new DialogFragment();
                    Bundle args = new Bundle();
                    args.putInt("position",getAdapterPosition());
                    args.putString("Type","Delete");
                    dialogFragment.setArguments(args);
                    dialogFragment.show(fm, "Custom");

                }
            });

        }

        // bind values
        public void bind(Order order){
            String dateUnparsed = order.getOrderDate();
            String dateParsed = dateUnparsed.split("T")[0];
            String timeParsed = dateUnparsed.split("T")[1]+":00";
            dateTextView.setText(dateParsed);
            hour.setText(timeParsed);
            attendedTextView.setText(order.getAttendsNumber());
        }


    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    // define interface
    public interface OrderClickInterface{
        public void onDelete(int position);
    }
}
