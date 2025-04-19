package com.unse.proyecto.ubicua.obj3d;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;

import androidx.annotation.RequiresApi;

public class DownloadedObject3D extends Modelo3D {

    public DownloadedObject3D(Context context, String url) {
        super(context, url);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void build(YesNoDialogListener list) {
        ModelRenderable.builder()
                .setSource(context, RenderableSource.builder().setSource(
                        context,
                        Uri.parse(url),
                        RenderableSource.SourceType.GLB)
                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                        .build())
                .setRegistryId(url)
                .build()
                .thenAccept(renderable -> {
                    model = renderable;
                    list.yes();
                })
                .exceptionally(
                        throwable -> {
                            model = null;
                            list.no();
                            return null;
                        });
    }
}
