package com.unse.proyecto.ubicua.obj3d;

import android.content.Context;

import com.google.ar.sceneform.rendering.ModelRenderable;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;

public abstract class Modelo3D {

    ModelRenderable model;
    String url;
    Context context;
    YesNoDialogListener mListener;

    public Modelo3D(Context context, String url) {
        this.url = url;
        this.context = context;
    }

    public abstract void build(YesNoDialogListener list);

    public ModelRenderable getModel() {
        return model;
    }
}
