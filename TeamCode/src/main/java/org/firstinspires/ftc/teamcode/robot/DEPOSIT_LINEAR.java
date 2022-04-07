package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

//TODO: delete this class and make everything async
public class DEPOSIT_LINEAR {
    private DEPOSIT_FSM deposit_fsm;
    public DEPOSIT_LINEAR(Hardware hardware, Telemetry telemetry, Gamepad g1, Gamepad g2) {
        deposit_fsm = new DEPOSIT_FSM(hardware, telemetry, g1, g2);
    }
    public void DROP_DA_THING_ON_TOP() {
        deposit_fsm.startDeposittop = true;
        deposit_fsm.doDepositTopAsync();
        while(deposit_fsm.isAnyBusy()) {
            deposit_fsm.doDepositTopAsync();
            if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                deposit_fsm.DROP_THE_THING_NOW = true;
            }
        }
    }
    public void DROP_DA_THING_ON_MIDDLE() {
        deposit_fsm.startDepositmid = true;
        deposit_fsm.doDepositMiddleAsync();
        while(deposit_fsm.isAnyBusy()) {
            deposit_fsm.doDepositMiddleAsync();
            if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                deposit_fsm.DROP_THE_THING_NOW = true;
            }
        }
    }
    public void DROP_DA_THING_ON_BOTTOM() {
        deposit_fsm.startDepositbot = true;
        deposit_fsm.doDepositBottomAsync();
        while(deposit_fsm.isAnyBusy()) {
            deposit_fsm.doDepositBottomAsync();
            if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                deposit_fsm.DROP_THE_THING_NOW = true;
            }
        }
    }
}
