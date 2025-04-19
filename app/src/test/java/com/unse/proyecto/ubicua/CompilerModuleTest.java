package com.unse.proyecto.ubicua;

import static com.unse.compilador.lenguaje.model.MovementType.*;
import static org.junit.Assert.assertEquals;

import com.unse.compilador.lenguaje.model.Movement;
import com.unse.compilador.lenguaje.model.MovementType;
import com.unse.proyecto.ubicua.compilador.CompilerModule;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CompilerModuleTest {

    @Test
    public void movementToString() {
        List<Movement> movementList = new ArrayList<>();
        movementList.add(build(ADVANCE, null, null));
        movementList.add(build(ADVANCE, null, null));
        movementList.add(build(LEFT, null, null));
        movementList.add(build(IF, BACK, null));
        movementList.add(build(REPEAT, RIGHT, 1));

        List<String> movements = CompilerModule.movementsToString(movementList);

        List<String> movementsString = new ArrayList<>();
        movementsString.add("ADVANCE");
        movementsString.add("ADVANCE");
        movementsString.add("LEFT");
        movementsString.add("IF BACK");
        movementsString.add("REPEAT 1 RIGHT");
        assertEquals(movementsString, movements);
    }

    @Test
    public void stringToMovement() {
        List<String> movementsString = new ArrayList<>();
        movementsString.add("ADVANCE");
        movementsString.add("ADVANCE");
        movementsString.add("LEFT");
        movementsString.add("IF BACK");
        movementsString.add("REPEAT 1 RIGHT");
        List<Movement> movements = CompilerModule.stringToMovements(movementsString);

        List<Movement> movementList = new ArrayList<>();
        movementList.add(build(ADVANCE, null, null));
        movementList.add(build(ADVANCE, null, null));
        movementList.add(build(LEFT, null, null));
        movementList.add(build(IF, BACK, null));
        movementList.add(build(REPEAT, RIGHT, 1));

        assertEquals(movementList, movements);
    }

    private Movement build(MovementType type, MovementType movement, Integer value){
        return Movement.builder()
                .value(value)
                .movement(movement)
                .type(type)
                .build();
    }
}
