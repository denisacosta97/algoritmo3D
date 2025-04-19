package com.unse.proyecto.ubicua.interfaz.dialogo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;

public class DialogoBienvenida extends DialogFragment {

    View view;
    Button mButton;
    PreferenciasManager preferenciasManager;

    public DialogoBienvenida(PreferenciasManager preferenciasManager) {
        this.preferenciasManager = preferenciasManager;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_bienvenida, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getDialog().getWindow() != null)
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loadViews();

        loadListener();

        return view;
    }

    private void loadViews() {
        mButton = view.findViewById(R.id.btnSI);
    }

    private void loadListener() {
        mButton.setOnClickListener(v -> {
            this.dismiss();
            preferenciasManager.setFirstTimeLaunch(Boolean.FALSE);
        });
    }


}