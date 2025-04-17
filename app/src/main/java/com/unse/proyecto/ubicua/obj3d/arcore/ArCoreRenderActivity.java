package com.unse.proyecto.ubicua.obj3d.arcore;

import static com.unse.proyecto.ubicua.network.di.AprendizajeUbicuoService.BASE_URL;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3Evaluator;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoGeneral;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;
import com.unse.proyecto.ubicua.obj3d.CustomObject3D;
import com.unse.proyecto.ubicua.obj3d.DownloadedObject3D;
import com.unse.proyecto.ubicua.obj3d.Modelo3D;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ArCoreRenderActivity extends AppCompatActivity implements View.OnClickListener {

    AnchorNode mAnchorNode, mAnchorNode2, nodeInicio, nodeFin;
    Modelo3D modelo, modelo2;
    ArFragment mArSceneView;
    String objectModelName;
    CardView cardCargando, cardMensaje, cardClose, cardReAnchor;
    ArrayList<TransformableNode> modelos;
    boolean placed = false, foundMode = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcore_render);

        if (getIntent().getStringExtra(Utils.OBJ_INFO) != null) {
            objectModelName = getIntent().getStringExtra(Utils.OBJ_INFO);
        }
        if (getIntent().getBooleanExtra(Utils.FOUND_MODE, false)) {
            foundMode = true;
        }
        if (objectModelName != null) {
            loadViews();

            loadData();

            loadListener();
        } else {
            Utils.showToast(getApplicationContext(), getString(R.string.objNoLoad));
            finish();
        }


    }

    private void loadListener() {
        cardReAnchor.setOnClickListener(this);
        cardClose.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadData() {
        modelos = new ArrayList<>();
        setupModel();
        arConfig();
    }

    private void arConfig() {
        mArSceneView.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                Anchor anchor = hitResult.createAnchor();
                mAnchorNode = new AnchorNode(anchor);
                mAnchorNode.setParent(mArSceneView.getArSceneView().getScene());

                if (nodeInicio == null) {
                    nodeInicio = new AnchorNode(anchor);
                    nodeInicio.setParent(mArSceneView.getArSceneView().getScene());
                    createModel(nodeInicio, 1);
                } else {
                    nodeFin = new AnchorNode(anchor);
                    nodeFin.setParent(mArSceneView.getArSceneView().getScene());
                    createModel(nodeFin, 2);
                    //animate();
                }

            }
        });
        mArSceneView.getArSceneView().getScene().addOnUpdateListener(new Scene.OnUpdateListener() {
            @Override
            public void onUpdate(FrameTime frameTime) {
                if (placed)
                    return;
                Frame frame = mArSceneView.getArSceneView().getArFrame();
                Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);
                for (Plane plane : planes) {
                    if (plane.getTrackingState() == TrackingState.TRACKING && !placed) {
                        Anchor anchor = plane.createAnchor(plane.getCenterPose());
                        mAnchorNode = new AnchorNode(anchor);
                        mAnchorNode.setParent(mArSceneView.getArSceneView().getScene());
                        createModel(mAnchorNode, 0);
                        createModel(mAnchorNode2, 0);
                        placed = true;
                    }
                }

            }
        });
        mArSceneView.getArSceneView().getScene().addOnPeekTouchListener(new Scene.OnPeekTouchListener() {
            @Override
            public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Node node = hitTestResult.getNode();
                    if (node != null) {
                        String nombre = node.getName();
                        Utils.showLog(nombre);
                    }
                }
            }
        });
        mArSceneView.getPlaneDiscoveryController().hide();
        ViewGroup container = findViewById(R.id.sceneform_hand_layout);
        container.removeAllViews();
        View phoneImage = getLayoutInflater().inflate(R.layout.item_move_phone, container, true);
        mArSceneView.getPlaneDiscoveryController().setInstructionView(phoneImage);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void animate() {
        ObjectAnimator objectAnimator = new ObjectAnimator();
        objectAnimator.setAutoCancel(true);
        objectAnimator.setTarget(modelos.get(0));
        objectAnimator.setObjectValues(modelos.get(0).getWorldPosition(), modelos.get(1).getWorldPosition());
        objectAnimator.setPropertyName("worldPosition");
        objectAnimator.setEvaluator(new Vector3Evaluator());
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(1500);
        objectAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupModel() {
        cardCargando.setVisibility(View.VISIBLE);
        modelo = new DownloadedObject3D(getApplicationContext(), BASE_URL+"objects/model/"+ objectModelName);
        modelo.build(new YesNoDialogListener() {
            @Override
            public void yes() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cardCargando.setVisibility(View.GONE);
                cardMensaje.setVisibility(View.VISIBLE);
            }

            @Override
            public void no() {
                errorRenderable(true);
            }
        });
    }

    private void errorRenderable(boolean change) {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTitulo(change ? "¡ERROR DE DESCARGA!" : "¡ERROR DE CARGA!")
                .setTipo(change ? DialogoGeneral.TIPO_LIBRE : DialogoGeneral.TIPO_ACEPTAR)
                .setTextButtonSi("REINTENTAR")
                .setTextButtonNo("CANCELAR")
                .setIcono(R.drawable.ic_error)
                .setDescripcion("HUBO UN ERROR AL INTENTAR CARGAR EL OBJETO" + (change ? ", POR FAVOR REINTENTE" : ""))
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }

                    @Override
                    public void no() {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                });
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.setCancelable(false);
        dialogoGeneral.show(getSupportFragmentManager(), "dialogo_error");
    }

    private void createModel(AnchorNode anchorNode, Integer pos) {
        cardMensaje.setVisibility(View.GONE);
        TransformableNode placa = new TransformableNode(mArSceneView.getTransformationSystem());
        placa.setName(objectModelName);
        placa.getScaleController().setMinScale(0.4999f);
        if (pos != 1)
            placa.getScaleController().setMaxScale(0.5000f);
        placa.setParent(anchorNode);
        if (getModelo() != null) {
            placa.setRenderable(pos == 1 ? modelo2.getModel() : getModelo());
            placa.select();
            modelos.add(placa);
        } else {
            errorRenderable(false);
        }
    }

    private Renderable getModelo() {
        if (modelo instanceof CustomObject3D) {
            return ((CustomObject3D) modelo).getModelView();
        } else
            return modelo.getModel();
    }

    private void loadViews() {
        cardCargando = findViewById(R.id.cardLoad);
        cardClose = findViewById(R.id.cardClose);
        cardMensaje = findViewById(R.id.cardMovePhone);
        cardReAnchor = findViewById(R.id.cardAnchor);
        loadArcoreFragment();

    }

    private void loadArcoreFragment() {
        // mFragment = new ArFragment();
        //getSupportFragmentManager().beginTransaction().add(R.id.arView, mFragment, "fragm").commit();
        mArSceneView = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arView);
        //mArSceneView = (ArFragment) mFragment;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mArSceneView != null) {
            mArSceneView.getArSceneView().pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mArSceneView != null) {
            mArSceneView.getArSceneView().destroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardClose:
                //customView();
                founded();
                break;
            case R.id.cardAnchor:
                //restart();
                setResult(Activity.RESULT_OK);
                finish();
                break;
        }
    }

    private void customView() {
        modelo = new CustomObject3D(getApplicationContext(), "",
                R.layout.dialogo_pistas, "Camina hacia la Escuela de Innovación Educativa");
        modelo.build(new YesNoDialogListener() {
            @Override
            public void yes() {
                createModel(mAnchorNode, null);
            }

            @Override
            public void no() {

            }
        });
    }

    private void restart() {
        if (mArSceneView != null) {
            for (TransformableNode node : modelos) {
                Utils.showLog(String.format("Position %s %s %s Global %s %s %s- Rotate %s %s %s %s", node.getLocalPosition().x,
                        node.getLocalPosition().y, node.getLocalPosition().z,
                        node.getWorldPosition().x, node.getWorldPosition().y, node.getWorldPosition().z,
                        node.getLocalRotation().x, node.getLocalRotation().y, node.getLocalRotation().z,
                        node.getLocalRotation().w));
            }
        } else {
            Utils.showToast(getApplicationContext(), getString(R.string.errorReAnchor));
        }
    }

    private void close() {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTitulo(foundMode ? "¡OBJETO AÚN NO REGISTRADO!" : "")
                .setTipo(DialogoGeneral.TIPO_SI_NO)
                .setIcono(R.drawable.ic_advertencia)
                .setDescripcion(foundMode ? "¿ESTÁ SEGURO QUE DESEA CERRAR EL MODO RA?" +
                        "\nSI CIERRA, EL OBJETO NO SERÁ REGISTRADO" :
                        "¿ESTÁ SEGURO QUE DESEA CERRAR EL MODO RA?")
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }

                    @Override
                    public void no() {

                    }
                });
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.setCancelable(false);
        dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
    }

    private void founded() {
        DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                .setTitulo("¡OBJETO ENCONTRADO!")
                .setTipo(DialogoGeneral.TIPO_ACEPTAR)
                .setIcono(R.drawable.ic_check)
                .setDescripcion("AHORA ES MOMENTO DE COMPRENDER LO REALIZADO")
                .setListener(new YesNoDialogListener() {
                    @Override
                    public void yes() {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }

                    @Override
                    public void no() {

                    }
                });
        DialogoGeneral dialogoGeneral = builder.build();
        dialogoGeneral.setCancelable(false);
        dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
    }
}
