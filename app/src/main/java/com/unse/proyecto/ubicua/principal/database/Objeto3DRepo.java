package com.unse.proyecto.ubicua.principal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.util.ArrayList;


public class Objeto3DRepo {

    static String createTable() {
        return String.format("create table %s(%s %s %s,%s %s %s,%s %s %s,%s %s %s,%s %s %s," +
                        "%s %s %s,%s %s %s,%s %s %s,%s %s %s, primary key(%s, %s))",
                Objeto3D.TABLA,
                Objeto3D.KEY_ID, Utils.INT_TYPE, Utils.NULL_TYPE,
                Objeto3D.KEY_NOMBRE, Utils.STRING_TYPE, Utils.NULL_TYPE,
                Objeto3D.KEY_URL, Utils.STRING_TYPE, Utils.NULL_TYPE,
                Objeto3D.KEY_CATEGORIA, Utils.INT_TYPE, Utils.NULL_TYPE,
                Objeto3D.KEY_XP, Utils.INT_TYPE, Utils.NULL_TYPE,
                Objeto3D.KEY_PROB, Utils.FLOAT_TYPE, Utils.NULL_TYPE,
                Objeto3D.KEY_LAT, Utils.STRING_TYPE, Utils.NULL_TYPE,
                Objeto3D.KEY_LOG, Utils.STRING_TYPE, Utils.NULL_TYPE,
                Objeto3D.KEY_URL_IMG, Utils.STRING_TYPE, Utils.NULL_TYPE,
                Objeto3D.KEY_ID, Objeto3D.KEY_CATEGORIA);
    }

    private Objeto3D mLinea;


    public Objeto3DRepo(Context context) {
        Utils.initBD(context);
        mLinea = new Objeto3D();
    }

    private ContentValues loadValues(Objeto3D objeto, int tipo) {
        ContentValues values = new ContentValues();
        if (tipo == 1)
            values.put(Objeto3D.KEY_ID, objeto.getId());
        values.put(Objeto3D.KEY_NOMBRE, objeto.getNombre());
        values.put(Objeto3D.KEY_URL, objeto.getUrl());
        values.put(Objeto3D.KEY_URL_IMG, objeto.getUrlImg());
        values.put(Objeto3D.KEY_CATEGORIA, objeto.getCategoria());
        values.put(Objeto3D.KEY_XP, objeto.getXp());
        values.put(Objeto3D.KEY_PROB, objeto.getProbabilidad());
        values.put(Objeto3D.KEY_LAT, objeto.getLat());
        values.put(Objeto3D.KEY_LOG, objeto.getLog());

        return values;
    }

    private Objeto3D loadDataFromCursor(Cursor cursor, int tipo) {
        mLinea = new Objeto3D();
        mLinea.setId(cursor.getInt(0));
        mLinea.setNombre(cursor.getString(1));
        mLinea.setUrl(cursor.getString(2));
        mLinea.setUrlImg(cursor.getString(8));
        mLinea.setCategoria(cursor.getInt(3));
        mLinea.setXp(cursor.getInt(4));
        mLinea.setProbabilidad(cursor.getDouble(5));
        mLinea.setLat(Double.valueOf(cursor.getString(6)));
        mLinea.setLog(Double.valueOf(cursor.getString(7)));
        return mLinea;
    }

    public int insert(Objeto3D objeto) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        int x = (int) db.insert(Objeto3D.TABLA, null, loadValues(objeto, 1));
        DBManager.getInstance().closeDatabase();
        return (int) x;
    }

    public void update(Objeto3D objeto) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String id = String.valueOf(objeto.getId());
        String selection = Objeto3D.KEY_ID + " = " + id;
        db.update(Objeto3D.TABLA, loadValues(objeto, 1), selection, null);
        DBManager.getInstance().closeDatabase();
    }

    public Objeto3D get(int id) {
        mLinea = new Objeto3D();
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from " + Objeto3D.TABLA + " where " +
                Objeto3D.KEY_ID + " = " + id, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            mLinea = loadDataFromCursor(cursor, 1);
            cursor.close();
        }
        DBManager.getInstance().closeDatabase();
        return mLinea;
    }

    public Objeto3D get(String id) {
        mLinea = new Objeto3D();
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from " + Objeto3D.TABLA + " where " +
                Objeto3D.KEY_NOMBRE + " = '" + id + "'", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            mLinea = loadDataFromCursor(cursor, 1);
            cursor.close();
        }
        DBManager.getInstance().closeDatabase();
        return mLinea;
    }

    public ArrayList<Objeto3D> getAll() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        ArrayList<Objeto3D> list = new ArrayList<Objeto3D>();
        String query = String.format("select * from %s", Objeto3D.TABLA);
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

    public ArrayList<Objeto3D> getAllByLevel(int level) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        ArrayList<Objeto3D> list = new ArrayList<Objeto3D>();
        String query = String.format("select * from %s where %s = %s", Objeto3D.TABLA, Objeto3D.KEY_CATEGORIA, level);
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
        db.delete(Objeto3D.TABLA, null, null);
        DBManager.getInstance().closeDatabase();
    }
}
