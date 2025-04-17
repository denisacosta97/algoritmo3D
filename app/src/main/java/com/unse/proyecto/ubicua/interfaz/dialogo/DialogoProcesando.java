package com.unse.proyecto.ubicua.interfaz.dialogo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;
import com.unse.proyecto.ubicua.obj3d.LevelModule;
import com.unse.proyecto.ubicua.principal.database.Objeto3DRepo;
import com.unse.proyecto.ubicua.principal.database.PistaRepo;
import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.modelo.Pista;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

public class DialogoProcesando extends DialogFragment {

    View view;
    Context mContext;
    TextView txtTitulo;
    ProgressBar mProgressBar;
    YesNoDialogListener mYesNoDialogListener;
    String textoTitulo;

    public DialogoProcesando(Context context, YesNoDialogListener yesNoDialogListener, String title) {
        mContext = context;
        mYesNoDialogListener = yesNoDialogListener;
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

    private void startTimer() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
                    mYesNoDialogListener.yes();
                    dismiss();
                }
                , 20000);
    }

    private void loadData() {
        txtTitulo.setText(textoTitulo);
        mProgressBar.setVisibility(View.VISIBLE);
        startTimer();
    }

    private void loadViews() {
        txtTitulo = view.findViewById(R.id.txtTitulo);
        mProgressBar = view.findViewById(R.id.progres);

    }
}
