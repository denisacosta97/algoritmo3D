package com.unse.proyecto.ubicua.compilador;

import com.google.android.gms.maps.model.LatLng;
import com.unse.compilador.lenguaje.Geo3DCompiler;
import com.unse.compilador.lenguaje.model.Execution;
import com.unse.compilador.lenguaje.model.Movement;
import com.unse.compilador.lenguaje.model.MovementType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompilerModule {

    private CompilerInput input;
    private Execution output;

    public void build(List<LatLng> history, Integer level) {
        input = new CompilerInput(history, level);
    }

    public List<Movement> translate() {
        try {
            output = Geo3DCompiler.compile(input.toString());
            if (output.getStatus()) {
                return output.getResult();
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    public static List<Movement> stringToMovements(List<String> values) {
        List<Movement> movements = new ArrayList<>();
        for (String value : values) {
            String[] parts = value.split(" ");

            if (parts.length == 1) {
                // Caso: ADVANCE, BACK, LEFT, RIGHT → solo contiene el movimiento
                MovementType type = MovementType.valueOf(parts[0]);
                movements.add(Movement.builder().type(type).build());
            } else if (parts.length == 2) {
                // Caso: IF FORWARD
                MovementType type = MovementType.valueOf(parts[0]);
                MovementType movement = MovementType.valueOf(parts[1]);
                movements.add(Movement.builder().type(type).movement(movement).build());
            } else if (parts.length == 3) {
                // Caso: REPEAT 3 FORWARD
                MovementType type = MovementType.valueOf(parts[0]);
                int repeatValue = Integer.parseInt(parts[1]);
                MovementType movement = MovementType.valueOf(parts[2]);
                movements.add(Movement.builder().type(type).movement(movement).value(repeatValue).build());
            }
        }
        return movements;
    }

    public static List<String> movementsToString(List<Movement> movements) {
        List<String> values = new ArrayList<>();
        for (Movement movement : movements) {
            if (movement.getType() == MovementType.ADVANCE ||
                    movement.getType() == MovementType.BACK ||
                    movement.getType() == MovementType.LEFT ||
                    movement.getType() == MovementType.RIGHT) {
                values.add(movement.getType().name());
            } else if (movement.getType() == MovementType.REPEAT) {
                values.add(String.format("%s %s %s",
                        movement.getType().name(),
                        movement.getValue(),
                        movement.getMovement().name()));
            } else if (movement.getType() == MovementType.IF) {
                values.add(String.format("%s %s",
                        movement.getType().name(),
                        movement.getMovement().name()));
            }

        }
        return values;
    }
}
