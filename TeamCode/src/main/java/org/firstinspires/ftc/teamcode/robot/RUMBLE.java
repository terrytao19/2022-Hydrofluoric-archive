package org.firstinspires.ftc.teamcode.robot;

import android.system.Os;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class RUMBLE {
    enum RUMBLE_STATE {
        OFF,
        ON
    }

    RUMBLE_STATE rumble_state = RUMBLE_STATE.OFF;

    private final Gamepad gamepad1;
    private final Gamepad gamepad2;

    private boolean doIntakeRumble = false;

    public RUMBLE(Gamepad g1, Gamepad g2) {
        gamepad1 = g1;
        gamepad2 = g2;
    }

    private final ElapsedTime time = new ElapsedTime();

    public void DO_INTAKE_RUMBLE() {
        doIntakeRumble = true;
    }

    public void HANDLE_RUMBLE_EVENTS() {
        switch(rumble_state) {
            case OFF:
                if(doIntakeRumble) {
                    rumble_state = RUMBLE_STATE.ON;
                }
                break;
            case ON:
                gamepad1.rumble(100);
                rumble_state = RUMBLE_STATE.OFF;
                break;
            default:
                rumble_state = RUMBLE_STATE.OFF;
                break;
        }
    }
}

