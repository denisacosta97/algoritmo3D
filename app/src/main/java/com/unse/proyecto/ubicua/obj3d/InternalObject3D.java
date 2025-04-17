package com.unse.proyecto.ubicua.obj3d;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.google.ar.sceneform.rendering.ModelRenderable;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;

import androidx.annotation.RequiresApi;

public class InternalObject3D extends Modelo3D {

    private Uri mUri;

    public InternalObject3D(Context context, Uri url) {
        super(context, "");
        this.mUri = url;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void build(YesNoDialogListener list) {
        ModelRenderable.builder()
                .setSource(context, mUri)
                .build().thenAccept(
                modelRenderable -> model = modelRenderable
        )
                .exceptionally(
                        throwable -> {
                            model = null;
                            return null;
                        }
                );
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }
}
