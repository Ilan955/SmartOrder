package com.example.smartord.Classes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class Order {
    public String orderDate;
    public String attendsNumber;


    public static DiffUtil.ItemCallback<Order> itemCallback = new DiffUtil.ItemCallback<Order>() {
        @Override
        public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.orderDate.equals(newItem.orderDate);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.equals(newItem);
        }
    };

    public String getOrderDate() { return orderDate; }

    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getAttendsNumber() { return attendsNumber; }

    public void setAttendsNumber(String attendsNumber) { this.attendsNumber = attendsNumber; }

}
