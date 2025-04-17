package com.unse.proyecto.ubicua.obj3d;

import android.content.Context;

import com.unse.proyecto.ubicua.principal.database.Objeto3DRepo;
import com.unse.proyecto.ubicua.principal.database.ObjetoCapturaRepo;
import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.modelo.ObjetoCaptura;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class O3DModule {

    Objeto3DRepo mObjeto3DRepo;
    ObjetoCapturaRepo mObjetoCapturaRepo;
    LevelModule mLevelModule;

    public O3DModule(Context context) {
        mObjeto3DRepo = new Objeto3DRepo(context);
        mObjetoCapturaRepo = new ObjetoCapturaRepo(context);
        mLevelModule = new LevelModule(context);
    }

    public Objeto3D get(String nombre) {
        return mObjeto3DRepo.get(nombre);
    }

    public Objeto3D getObjects() {
        int nivel = mLevelModule.getLevel();
        ArrayList<Objeto3D> objetos = search(nivel);
        ArrayList<ObjetoCaptura> capturados = mObjetoCapturaRepo.getAllByCat(nivel);
        Random random = new Random();
        boolean valid = false;
        int numero = 0;
        do {
            if(objetos.size() > 0){
                valid = search(capturados, objetos.get(numero));
                if (!valid)
                    numero = random.nextInt(objetos.size());
            }
        } while (!valid);
        return objetos.get(numero);
    }

    private boolean search(ArrayList<ObjetoCaptura> capturados, Objeto3D pos) {
        for (ObjetoCaptura obj : capturados) {
            if (obj.getId() == pos.getId()) {
                return false;
            }
        }
        return true;

    }

    public ArrayList<Objeto3D> search(int nivel) {
        if (mObjeto3DRepo != null) {
            return mObjeto3DRepo.getAllByLevel(nivel);
        }
        return null;
    }

    public void discover(Objeto3D objeto3D) {
        String fecha = Utils.getFechaName(new Date(System.currentTimeMillis()));
        ObjetoCaptura objetoCaptura = new ObjetoCaptura(objeto3D.getId(), objeto3D.getCategoria(),
                fecha);
        mObjetoCapturaRepo.insert(objetoCaptura);
        mLevelModule.addXP(objeto3D);
    }
}
