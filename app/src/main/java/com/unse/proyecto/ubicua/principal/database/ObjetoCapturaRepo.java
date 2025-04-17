package com.unse.proyecto.ubicua.principal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.unse.proyecto.ubicua.principal.modelo.ObjetoCaptura;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.util.ArrayList;

public class ObjetoCapturaRepo {

    static String createTable() {
        return String.format("create table %s(%s %s %s,%s %s %s,%s %s %s, primary key(%s, %s))",
                ObjetoCaptura.TABLA,
                ObjetoCaptura.KEY_ID, Utils.INT_TYPE, Utils.NULL_TYPE,
                ObjetoCaptura.KEY_CATEGORIA, Utils.INT_TYPE, Utils.NULL_TYPE,
                ObjetoCaptura.KEY_FECHA, Utils.STRING_TYPE, Utils.NULL_TYPE,
                ObjetoCaptura.KEY_ID, ObjetoCaptura.KEY_CATEGORIA);
    }

    private ObjetoCaptura mLinea;


    public ObjetoCapturaRepo(Context context) {
        Utils.initBD(context);
        mLinea = new ObjetoCaptura();
    }

    private ContentValues loadValues(ObjetoCaptura objeto, int tipo) {
        ContentValues values = new ContentValues();
        if (tipo == 1)
            values.put(ObjetoCaptura.KEY_ID, objeto.getId());
        values.put(ObjetoCaptura.KEY_CATEGORIA, objeto.getCat());
        values.put(ObjetoCaptura.KEY_FECHA, objeto.getFecha());

        return values;
    }

    private ObjetoCaptura loadDataFromCursor(Cursor cursor, int tipo) {
        mLinea = new ObjetoCaptura();
        mLinea.setId(cursor.getInt(0));
        mLinea.setCat(cursor.getInt(1));
        mLinea.setFecha(cursor.getString(2));
        return mLinea;
    }

    public int insert(ObjetoCaptura objeto) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        int x = (int) db.insert(ObjetoCaptura.TABLA, null, loadValues(objeto, 1));
        DBManager.getInstance().closeDatabase();
        return (int) x;
    }

    public void update(ObjetoCaptura objeto) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String id = String.valueOf(objeto.getId());
        String selection = ObjetoCaptura.KEY_ID + " = " + id;
        db.update(ObjetoCaptura.TABLA, loadValues(objeto, 1), selection, null);
        DBManager.getInstance().closeDatabase();
    }

    public ObjetoCaptura get(int id) {
        mLinea = new ObjetoCaptura();
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from " + ObjetoCaptura.TABLA + " where " +
                ObjetoCaptura.KEY_ID + " = " + id, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            mLinea = loadDataFromCursor(cursor, 1);
            cursor.close();
        }
        DBManager.getInstance().closeDatabase();
        return mLinea;
    }


    public ArrayList<ObjetoCaptura> getAll() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        ArrayList<ObjetoCaptura> list = new ArrayList<ObjetoCaptura>();
        String query = String.format("select * from %s", ObjetoCaptura.TABLA);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                mLinea = loadDataFromCursor(cursor, 1);
                list.add(mLinea);
            } while (cursor.moveToNext());
        }
        DBManager.getInstance().closeDatabase();
        cursor.close();

        return list;

    }

    public ArrayList<ObjetoCaptura> getAllByCat(int level) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        ArrayList<ObjetoCaptura> list = new ArrayList<ObjetoCaptura>();
        String query = String.format("select * from %s where %s = %s", ObjetoCaptura.TABLA, ObjetoCaptura.KEY_CATEGORIA, level);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                mLinea = loadDataFromCursor(cursor, 1);
                list.add(mLinea);
            } while (cursor.moveToNext());
        }
        DBManager.getInstance().closeDatabase();
        cursor.close();

        return list;

    }


    public void deleteAll() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        db.delete(ObjetoCaptura.TABLA, null, null);
        DBManager.getInstance().closeDatabase();
    }
}

