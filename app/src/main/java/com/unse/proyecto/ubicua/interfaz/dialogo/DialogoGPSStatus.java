package com.unse.proyecto.ubicua.interfaz.dialogo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.unse.proyecto.ubicua.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogoGPSStatus extends DialogFragment {

    View view;
    Button btnAceptar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_gps, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getDialog().getWindow() != null)
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loadViews();

        loadData();

        loadListener();

        return view;
    }

    private void loadListener() {
        btnAceptar.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void loadData() {
    }

    private void loadViews() {
        btnAceptar = view.findViewById(R.id.btnSI);

    }

}
