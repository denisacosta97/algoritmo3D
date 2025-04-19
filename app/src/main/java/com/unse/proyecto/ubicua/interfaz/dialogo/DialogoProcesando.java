package com.unse.proyecto.ubicua.interfaz.dialogo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unse.proyecto.ubicua.R;

public class DialogoProcesando extends DialogFragment {

    View view;
    Context mContext;
    TextView txtTitulo;
    ProgressBar mProgressBar;
    String textoTitulo;

    public DialogoProcesando(Context context, String title) {
        mContext = context;
        textoTitulo = title;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_carga, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getDialog().getWindow() != null)
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loadViews();

        loadData();

        return view;
    }

    private void loadData() {
        txtTitulo.setText(textoTitulo);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void loadViews() {
        txtTitulo = view.findViewById(R.id.txtTitulo);
        mProgressBar = view.findViewById(R.id.progres);

    }
}
