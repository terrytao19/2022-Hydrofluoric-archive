package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class CAPPER {
    public Servo UPDOWN;
    public Servo LR;
    public CRServo INOUT;

    private final Gamepad gamepad2;
    private final Telemetry telemetry;


    private double k = 1.1;

    private static final double YAW_MAX = .0010;
    private static final double PITCH_MAX = .0010;
    private static final double SURGE_MAX = 1;


    //starting positions
    private final double INITPOSUPDOW = .5;
    private final double INITPOSLR = .5;

    //current position
    private double CURUPDOWN = .5;
    private double CURLR = .5;
    //meep


    private final double INITINOUT = .5;

    private ElapsedTime timePitch = new ElapsedTime();

    private ElapsedTime timeYaw = new ElapsedTime();
    private ElapsedTime timeSurge = new ElapsedTime();

    public CAPPER(HardwareMap ahwMap, Telemetry telemetry, Gamepad c1) {
        UPDOWN = ahwMap.get(Servo.class, "Pitch");
        LR = ahwMap.get(Servo.class, "Yaw");
        INOUT = ahwMap.get(CRServo.class, "Surge");
        this.telemetry = telemetry;
        gamepad2 = c1;

        UPDOWN.setPosition(INITPOSUPDOW);
        LR.setPosition(INITPOSLR);
        INOUT.setPower(0);
    }


    private void LRupdatePositions() {
        LR.setPosition(CURLR);

    }

    private void UPDOWNupdatePositions() {
        UPDOWN.setPosition(CURUPDOWN);

    }

    public void LRmoveRelative(double deltaTicks) {
        CURLR += deltaTicks;

        LRupdatePositions();
    }

    public void UPDOWNmoveRelative(double deltaTicks) {
        CURUPDOWN += deltaTicks;

        UPDOWNupdatePositions();
    }

    private void pitchAsync() {
        double pitchVal = gamepad2.right_stick_y;
        if (timePitch.milliseconds() > 2 ) {
            double pitchMovement = (pitchVal < -.2 || pitchVal > .2) ? (pitchVal * PITCH_MAX) : 0;
            telemetry.addData("Pitch Value ", pitchMovement);
            if (pitchMovement > 1 ){
                pitchMovement = 1;
            }
            else if (pitchMovement < -1){
                pitchMovement = -1;
            }


            UPDOWNmoveRelative(pitchMovement);
            timePitch.reset();
        }
    }

    private void YawAsync() {
        if (timeYaw.milliseconds() > 2) {
            double yawVal = -gamepad2.right_stick_x;
            double movement = (yawVal < -.2 || yawVal > .2) ? (yawVal * YAW_MAX) : 0;
            telemetry.addData("Pitch Movement", movement);
            if (movement > 1){
                movement = 1;
            }
            else if( movement < -1){
                movement = -1;
            }

            LRmoveRelative(movement);
            timeYaw.reset();
        }

    }

    private void SurgeAsync() {

        double val = gamepad2.left_stick_y;
        double speed;

        if (val < -.1){
            speed = -1;
        }
        else if (val > .1){
            speed = 1;
        }
        else {
            speed = 0;
        }
        telemetry.addData("In/out speed", speed);

        INOUT.setPower(speed);

    }

    public void CapperHandleEvents(){
        SurgeAsync();

        YawAsync();

        pitchAsync();
    }


}
