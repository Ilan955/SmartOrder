package com.example.smartord.Dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartord.R;
import com.google.firebase.auth.FirebaseAuth;

public class DialogFragment extends androidx.fragment.app.DialogFragment {
    String action;
    int position;

    public OnInputListener onInputListener;
    TextView title, text;
    Button cancel, submit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        action = getArguments().getString("Type");
        if (action == "Delete") {
            position = getArguments().getInt("position");
        } else {
            position = -1;
        }

    }

    public interface OnInputListener {
        void sendInput(String input, int position);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {

            onInputListener = (OnInputListener) getActivity();
        } catch (ClassCastException e) {
            // TODO: logger
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_frag, container, false);
        title = view.findViewById(R.id.dialogTitle);
        text = view.findViewById(R.id.dialogText);
        submit = view.findViewById(R.id.okBtn);
        cancel = view.findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if (action == "Delete") {
            title.setText("Delete reservation");
            text.setText("Are you sure you want to delete the reservation?");
            submit.setText("Delete");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onInputListener.sendInput("YES", position);
                    dismiss();
                }
            });
        } else if (action == "Exit") {
            title.setText("Exit Application");
            text.setText("Are you sure you want to Exit the application?");
            submit.setText("Exit");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onInputListener.sendInput("YES", position);
                    dismiss();

                }
            });
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInputListener.sendInput("NO", position);
                dismiss();
            }
        });

        return view;
    }
}
