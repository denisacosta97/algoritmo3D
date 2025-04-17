package com.unse.proyecto.ubicua.interfaz.dialogo;

import android.content.Context;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.adapter.FlechasAdapter;
import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.modelo.Sentencia;

import java.util.ArrayList;

public class DialogoBienvenida extends DialogFragment {

    View view;
    Button mButton;
    Context mContext;

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
        mButton.setOnClickListener(v -> this.dismiss());
    }


}