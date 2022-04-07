package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.Encoder;

public class Intake {
    private DcMotorEx intakeFront;
    private DcMotorEx intakeBack;

//    private Encoder encoderFront;
//    private Encoder encoderBack;

    private boolean intakeMode;// True = on, false = off
    private boolean intakeDirection;//true = in, false = off


    //intake powers are kinda self explanatory
    public static final double INTAKE_POWER = 1;
    public static final double INTAKE_POWER_SLOW = 1;


    public void doStopVerticalAsync() {

    }

    public Intake(HardwareMap ahwMap){
        intakeBack = ahwMap.get(DcMotorEx.class, "intakeFront");
        intakeFront = ahwMap.get(DcMotorEx.class, "intakeBack");

//        encoderFront = new Encoder(ahwMap.get(DcMotorEx.class, "encoderFront"));
//        encoderBack = new Encoder(ahwMap.get(DcMotorEx.class, "encoderBack"));

        intakeFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        intakeBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // intake motors go in opposite directions because they face opposite directions
        intakeFront.setDirection(DcMotor.Direction.FORWARD);
        intakeBack.setDirection(DcMotor.Direction.REVERSE);

        setIntakeMode(false);
        setIntakeDirection(true);


    }
    //rabbit hole to get cool looking intake(whoosh) too much time on my hands
    public boolean getIntakeMode(){return intakeMode;}
    public void setIntakeMode(boolean mode){
        intakeMode = mode;

        if (intakeMode) {
            if(intakeDirection){
                intakeFront.setPower(INTAKE_POWER);
                intakeBack.setPower(INTAKE_POWER);
            }
            else{
                intakeFront.setPower(-INTAKE_POWER);
                intakeBack.setPower(-INTAKE_POWER);


            }
        }
        else{
            intakeFront.setPower(0);
            intakeBack.setPower(0);
        }
    }
    public boolean IntakeDirection(){return intakeDirection;}
    public void setIntakeDirection(boolean direction){
        intakeDirection = direction;
        // intake power only updates when setIntakeMode is Called
        if(getIntakeMode()){
            setIntakeMode((getIntakeMode()));
        }
    }

    public void forward(){
        intakeFront.setPower(INTAKE_POWER);
        intakeBack.setPower(INTAKE_POWER);
    }
    public void reverse(){
        intakeFront.setPower(-INTAKE_POWER);
        intakeBack.setPower(-INTAKE_POWER);
    }
    public void off(){
        intakeFront.setPower(0);
        intakeBack.setPower(0);
    }
    public void backIn() {
        intakeFront.setPower(INTAKE_POWER);
    }
    public void frontIn() {
        intakeBack.setPower(INTAKE_POWER);
    }
    public void backOut() {
        intakeFront.setPower(-INTAKE_POWER);
    }
    public void frontOut() {
        intakeBack.setPower(-INTAKE_POWER);
    }
}
