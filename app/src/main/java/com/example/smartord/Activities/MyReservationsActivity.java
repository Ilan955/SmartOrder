package com.example.smartord.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.smartord.Adapter.OrderAdapter;
import com.example.smartord.Classes.Order;
import com.example.smartord.Dialog.DialogFragment;
import com.example.smartord.Modal.OrderViewModal;
import com.example.smartord.R;

import java.util.ArrayList;
import java.util.List;

public class MyReservationsActivity extends AppCompatActivity implements OrderAdapter.OrderClickInterface, DialogFragment.OnInputListener {

    private OrderViewModal orderViewModal;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);


        // set action bar text and back btn option
        actionBar = getSupportActionBar();
        actionBar.setTitle("My Reservations");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recycleView);

        // register the recycle view adapter
        orderAdapter = new OrderAdapter(Order.itemCallback, this, getSupportFragmentManager());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(orderAdapter);

        // get view model instance -> pass list of reservations to recycle view adapter
        orderViewModal = new ViewModelProvider(this).get(OrderViewModal.class);
        orderViewModal.getOrderList().observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                orderAdapter.submitList(orders);
            }
        });
    }

    // implementation of the recycle view interface (OrderClickInterface)
    @Override
    public void onDelete(int position) {
        if (!orderViewModal.deleteOrder(position)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Can't delete reservation that has been passed", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    // implementation of the dialog fragment  (OnInputListener)
    @Override
    public void sendInput(String input, int position) {
        closeOptionsMenu();
        if (input.equals("YES")) {
            orderViewModal.deleteOrder(position);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}