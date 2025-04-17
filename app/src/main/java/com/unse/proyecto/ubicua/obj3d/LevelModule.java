package com.unse.proyecto.ubicua.obj3d;

import android.content.Context;

import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class LevelModule {

    private static final String XP_COUNT = "xp_count";
    public static final String XP_FOR_LEVEL = "xp_for_level";
    private static final String LEVEL_COUNT = "level_count";
    PreferenciasManager mPreferenciasManager;

    public LevelModule(Context context) {
        mPreferenciasManager = new PreferenciasManager(context);
    }

    public int getXPLevel(int xp, int level) {
        ArrayList<Integer> levels = getXPLevel();
        return level < levels.size() ? levels.get(level - 1) : level;
        /*for (Integer s : levels) {
            if (xp <= s) {
                return s;
            }
        }
        return 0;*/
    }

    public int getLevel() {
        return mPreferenciasManager.getValueInt(LEVEL_COUNT) == 0 ? 1 : mPreferenciasManager.getValueInt(LEVEL_COUNT);
        /*int xp = getXP();
        Set<String> levels = getXPLevel();
        int i = 0;
        for (String s : levels) {
            i++;
            if (xp < Integer.parseInt(s)) {
                break;
            }
        }
        return i;*/
    }

    public void addLevel(int xp) {
        int size = mPreferenciasManager.getValueSet(XP_FOR_LEVEL).size();
        int level = getLevel();
        int xpLevel = getXPLevel().get(level - 1);
        int resto = xpLevel - xp;
        if (level < size) {
            level++;
        }
        mPreferenciasManager.setValue(LEVEL_COUNT, level);
        mPreferenciasManager.setValue(XP_COUNT, resto);

    }

    public int getXP() {
        return mPreferenciasManager.getValueInt(XP_COUNT);
    }

    private ArrayList<Integer> getXPLevel() {
        Set<String> list = mPreferenciasManager.getValueSet(XP_FOR_LEVEL);
        ArrayList<String> levels = new ArrayList<>(list);
        ArrayList<Integer> inte = new ArrayList<>();
        for (String s : levels) {
            inte.add(Integer.parseInt(s));
        }
        Collections.sort(inte);
        return inte;
    }

    public void addXP(Objeto3D obj) {
        int xp = getXP();
        mPreferenciasManager.setValue(XP_COUNT, xp + obj.getXp());
        checkLevel(xp);
    }

    private void checkLevel(int xpNew) {
        int xp = getXP();
        int lastLevel = getLevel();
        ArrayList<Integer> levels = getXPLevel();
        if (xp <= levels.get(lastLevel - 1)) {
            return;
        } else {
            addLevel(xpNew);
        }
    }


}
