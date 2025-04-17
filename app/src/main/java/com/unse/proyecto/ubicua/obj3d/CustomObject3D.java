package com.unse.proyecto.ubicua.obj3d;

import android.content.Context;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;

import com.google.ar.sceneform.rendering.ViewRenderable;
import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;
import com.unse.proyecto.ubicua.principal.util.Utils;

import androidx.annotation.RequiresApi;

public class CustomObject3D extends Modelo3D {


    ViewRenderable model;
    int view;
    String texto;

    public CustomObject3D(Context context, String url, int view, String text) {
        super(context, url);
        this.view = view;
        this.texto = text;
    }

    public ViewRenderable getModelView() {
        return model;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void build(YesNoDialogListener list) {
        ViewRenderable.builder()
                .setView(context, view)
                .build()
                .thenAccept(viewRenderable -> {
                    model = viewRenderable;
                    TextView btn = model.getView().findViewById(R.id.txtDescripcion);
                    Button button = model.getView().findViewById(R.id.btnSI);
                    button.setOnClickListener(v -> {
                        Utils.showToast(context, "prueba");
                    });
                    btn.setText(texto);
                    list.yes();
                })
                .exceptionally(throwable -> {
                    model = null;
                    list.no();
                    return null;
                });
    }
}
