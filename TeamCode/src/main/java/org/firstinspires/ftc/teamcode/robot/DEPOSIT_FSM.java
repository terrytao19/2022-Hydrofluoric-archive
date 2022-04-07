package org.firstinspires.ftc.teamcode.robot;

import android.system.Os;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DEPOSIT_FSM {
    enum DEPOSIT_STATE {
        INIT,
        STATE_0,
        STATE_1,
        STATE_2,
        STATE_3,
        STATE_4,
        STATE_5,
        STATE_6,
        STATE_7,
        STATE_8
    }
    enum MID_DEPOSIT_STATE {
        INIT,
        STATE_0,
        STATE_1,
        STATE_2,
        STATE_3,
        STATE_4,
        STATE_5,
        STATE_6,
        STATE_7,
        STATE_8
    }
    enum BOTTOM_DEPOSIT_STATE {
        INIT,
        STATE_0,
        STATE_1,
        STATE_2,
        STATE_3,
        STATE_4,
        STATE_5,
        STATE_6,
        STATE_7,
        STATE_8
    }
    enum SHARED_DEPOSIT_STATE {
        INIT,
        STATE_0,
        STATE_1,
        STATE_2,
        STATE_3
    }
    private DEPOSIT_STATE deposit_state = DEPOSIT_STATE.INIT;
    private MID_DEPOSIT_STATE mid_deposit_state = MID_DEPOSIT_STATE.INIT;
    private BOTTOM_DEPOSIT_STATE bottom_deposit_state = BOTTOM_DEPOSIT_STATE.INIT;
    private SHARED_DEPOSIT_STATE shared_deposit_state = SHARED_DEPOSIT_STATE.INIT;
    private final Hardware Oscar;
    private final Gamepad gamepad1;
    private final Gamepad gamepad2;
    private boolean topBusy = false;
    private boolean midBusy = false;
    private boolean bottomBusy = false;
    private boolean sharedBusy = false;
    private boolean midDeposited = false;
    private boolean bottomDeposited = false;
    private boolean topDeposited = false;
    private boolean sharedDeposited = false;

    public boolean startDeposittop = false;
    public boolean startDepositmid = false;
    public boolean startDepositbot = false;
    public boolean startDepositShared = false;

    public boolean DROP_THE_THING_NOW = false;

    public boolean THE_THING_CAN_BE_DROPPED_NOW = false;

    private final Telemetry telemetry;

    public boolean isAnyDeposited() {
        return(midDeposited || bottomDeposited || topDeposited || sharedDeposited);
    }
    public boolean isAnyBusy() {return(midBusy || bottomBusy || topBusy || sharedBusy);}

    public DEPOSIT_FSM(Hardware hardware, Telemetry telemetry, Gamepad c1, Gamepad c2) {
        this.Oscar = hardware;
        this.telemetry = telemetry;
        gamepad1 = c1;
        gamepad2 = c2;
    }
    public DEPOSIT_FSM(Hardware hardware, Telemetry telemetry){
        this.Oscar = hardware;
        this.telemetry = telemetry;

        gamepad1 = null;
        gamepad2 = null;
    }

    private final ElapsedTime time = new ElapsedTime();

    public void reset() {
        topBusy = false;
        topDeposited = false;
        deposit_state = DEPOSIT_STATE.INIT;
        time.reset();
    }

    public void doDepositTopAsync() {
        telemetry.addData("DEPOSIT STATE: ", deposit_state);
        switch(deposit_state) {
            case INIT:
                if((gamepad2.triangle || gamepad1.triangle || startDeposittop) && !midBusy && !bottomBusy && !sharedBusy) {
                    startDeposittop = false;
                    deposit_state = DEPOSIT_STATE.STATE_0;
                    topBusy = true;
                    topDeposited = false;
                    time.reset();
                }
                else {
                    topBusy = false;
                    topDeposited = false;
                }
                break;
            case STATE_0:
                if(time.milliseconds() > 150) {
                    deposit_state = DEPOSIT_STATE.STATE_1;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesTop();
                    Oscar.grabber.closeGrab();
                    Oscar.grabber.goStart();
                    Oscar.elbow.moveStart();
                }
                break;
            case STATE_1:
                if(Oscar.slides.getMotorPosition() > 300) {
                    Oscar.slides.slidesTop();
                    Oscar.elbow.moveTop();
                    Oscar.grabber.goTop();
                    deposit_state = DEPOSIT_STATE.STATE_2;
                    time.reset();
                    Oscar.slides.RESET_ADJUSTABLE_TOP_TICKS();
                }
                else {
                    Oscar.slides.slidesTop();
                }
                break;
            case STATE_2:
                if(gamepad1.triangle || gamepad2.triangle || DROP_THE_THING_NOW) {
                    DROP_THE_THING_NOW = false;
                    THE_THING_CAN_BE_DROPPED_NOW = false;
                    deposit_state = DEPOSIT_STATE.STATE_3;
                    time.reset();
                }
                else {
                    if(gamepad2.right_bumper && time.milliseconds() > 15){
                        Oscar.elbow.moveRelative(-.005);
                        time.reset();
                    }
                    if(gamepad2.left_bumper && time.milliseconds() > 15){
                        Oscar.elbow.moveRelative(.005);
                        time.reset();
                    }
                }
                if(time.milliseconds() > 550) {
                    THE_THING_CAN_BE_DROPPED_NOW = true;
                }
                break;
            case STATE_3:
                if(time.milliseconds() > 300) {
                    deposit_state = DEPOSIT_STATE.STATE_4;
                    topDeposited = true;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesHold();
                    Oscar.grabber.openGrab();
                    Oscar.grabber.openGrabExtra();
                }
                break;
            case STATE_4:
                if(time.milliseconds() > 150) {
                    deposit_state = DEPOSIT_STATE.STATE_5;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesHold();
                    Oscar.elbow.goToGrabPos();
                    Oscar.grabber.closeGrabExtra();
                }
                break;
            case STATE_5:
                if(Oscar.slides.getMotorPosition() < 380) {
                    deposit_state = DEPOSIT_STATE.STATE_6;
                    Oscar.grabber.goStart();
                    time.reset();
                }
                else {
                    Oscar.slides.slidesGrab();
                }
                break;
            case STATE_6:
                if(time.milliseconds() > 500) {
                    reset();
                    deposit_state = DEPOSIT_STATE.INIT;
                }
                else {
                    Oscar.slides.slidesGrab();
                }
                break;
            default:
                deposit_state = DEPOSIT_STATE.INIT;
                break;
        }
    }
    public void doDepositMiddleAsync() {
        telemetry.addData("DEPOSIT STATE (MIDDLE): ", mid_deposit_state);
        switch(mid_deposit_state) {
            case INIT:
                if((gamepad2.square || startDepositmid || gamepad1.circle) && !topBusy && !bottomBusy) {
                    startDepositmid = false;
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_0;
                    midBusy = true;
                    midDeposited = false;
                    time.reset();
                }
                else {
                    midBusy = false;
                    midDeposited = false;
                }
                break;
            case STATE_0:
                if(time.milliseconds() > 150) {
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_1;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesOutABit();
                    Oscar.grabber.closeGrab();
                    Oscar.grabber.goStart();
                    Oscar.elbow.moveStart();
                }
                break;
            case STATE_1:
                if(time.milliseconds() > 700) {
                    Oscar.slides.slidesMid();
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_2;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesOutABit();
                    Oscar.elbow.moveMid();
                    Oscar.grabber.goMiddle();
                }
                break;
            case STATE_2:
                if(gamepad2.square || DROP_THE_THING_NOW || gamepad1.circle) {
                    DROP_THE_THING_NOW = false;
                    THE_THING_CAN_BE_DROPPED_NOW = false;
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_3;
                    time.reset();
                }
                else {
                    if(gamepad2.right_bumper && time.milliseconds() > 15){
                        Oscar.elbow.moveRelative(-.005);
                        time.reset();
                    }
                    if(gamepad2.left_bumper && time.milliseconds() > 15){
                        Oscar.elbow.moveRelative(.005);
                        time.reset();
                    }
                }
                if(time.milliseconds() > 800) {
                    THE_THING_CAN_BE_DROPPED_NOW = true;
                }
                break;
            case STATE_3:
                if(time.milliseconds() > 200) {
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_4;
                    midDeposited = true;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesHold();
                    Oscar.grabber.openGrab();
                    Oscar.grabber.openGrabExtra();
                }
                break;
            case STATE_4:
                if(time.milliseconds() > 580) {
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_5;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesHold();
                    Oscar.elbow.goToGrabPos();
                    Oscar.grabber.closeGrabExtra();
                    Oscar.slides.slidesOutABit();
                }
                break;
            case STATE_5:
                if(Oscar.slides.getMotorPosition() < 250) {
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_6;
                    Oscar.grabber.goStart();
                    Oscar.grabber.moveByAngle(-90,"start");
                    time.reset();
                }
                else {
                    Oscar.slides.slidesOutABit();
                }
                break;
            case STATE_6:
                if(time.milliseconds() > 750) {
                    reset();
                    Oscar.grabber.moveByAngle(90,"start");
                    mid_deposit_state = MID_DEPOSIT_STATE.INIT;
                }
                else if(time.milliseconds() < 650) {
                    Oscar.slides.slidesOutABit();
                }
                else {
                    Oscar.slides.slidesGrab();
                }
                break;
            default:
                mid_deposit_state = MID_DEPOSIT_STATE.INIT;
                break;
        }
    }
    public void doDepositBottomAsync() {
        telemetry.addData("DEPOSIT STATE (BOTTOM): ", bottom_deposit_state);
        switch(bottom_deposit_state) {
            case INIT:
                if((gamepad2.cross || startDepositbot) && !topBusy && !midBusy) {
                    startDepositbot = false;
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_0;
                    bottomBusy = true;
                    bottomDeposited = false;
                    time.reset();
                }
                else {
                    bottomBusy = false;
                    bottomDeposited = false;
                }
                break;
            case STATE_0:
                if(time.milliseconds() > 150) {
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_1;
                    time.reset();
                }
                else {
                    //very interesting name( SLides out a bit)
                    Oscar.slides.slidesOutABit();
                    Oscar.grabber.closeGrab();
                    Oscar.grabber.goStart();
                    Oscar.elbow.moveStart();
                }
                break;
            case STATE_1:
                if(time.milliseconds() > 800) {
                    Oscar.slides.slidesBottom();
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_2;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesOutABit();
                    Oscar.elbow.moveBottom();
                    Oscar.grabber.goBottom();
                }
                break;
            case STATE_2:
                if(gamepad2.cross || DROP_THE_THING_NOW) {
                    DROP_THE_THING_NOW = false;
                    THE_THING_CAN_BE_DROPPED_NOW = false;
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_3;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesBottom();
                }
                if(time.milliseconds() > 800) {
                    THE_THING_CAN_BE_DROPPED_NOW = true;
                }
                break;
            case STATE_3:
                if(time.milliseconds() > 230) {
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_4;
                    bottomDeposited = true;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesHold();
                    Oscar.grabber.openGrab();
                    Oscar.grabber.openGrabExtra();
                }
                break;
            case STATE_4:
                if(time.milliseconds() > 300) {
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_5;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesOutABit();
                }
                break;
            case STATE_5:
                if(time.milliseconds() > 800) {
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_6;
                    Oscar.grabber.goStart();
                    time.reset();
                }
                else {
                    if(time.milliseconds() > 500) {
                        Oscar.grabber.goStart();
                    }
                    Oscar.grabber.closeGrabExtra();
                    Oscar.elbow.goToGrabPos();
                    Oscar.slides.slidesOutABit();
                }
                break;
            case STATE_6:
                if(time.milliseconds() > 750) {
                    reset();
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.INIT;
                }
                else if(time.milliseconds() < 400) {
                    Oscar.slides.slidesOutABit();
                }
                else {
                    Oscar.slides.slidesGrab();
                }
                break;
            default:
                bottom_deposit_state = BOTTOM_DEPOSIT_STATE.INIT;
                break;
        }
    }
    public void doDepositSharedAsync() {
        telemetry.addData("Shared State: ", shared_deposit_state);
        switch (shared_deposit_state) {
            case INIT:
                if(gamepad1.dpad_right || gamepad2.dpad_right) {
                    sharedBusy = true;
                    sharedDeposited = false;
                    time.reset();
                    shared_deposit_state = SHARED_DEPOSIT_STATE.STATE_0;
                }
                else {
                    sharedBusy = false;
                    sharedDeposited = false;
                }
                break;
            case STATE_0:
                if(time.milliseconds() > 500) {
                    shared_deposit_state = SHARED_DEPOSIT_STATE.STATE_1;
                    time.reset();
                }
                else {
                    Oscar.elbow.moveStart();
                    Oscar.slides.slidesShared();
                }
                break;
            case STATE_1:
                if(gamepad1.dpad_right || gamepad2.dpad_right) {
                    shared_deposit_state = SHARED_DEPOSIT_STATE.STATE_2;
                    Oscar.slides.RESET_ADJUSTABLE_SHARED_TICKS();
                    time.reset();
                }
                else {
                    if(gamepad2.right_bumper && time.milliseconds() > 5){
                        Oscar.slides.CHANGE_ADJUSTABLE_SHARED_TICKS(3);
                        Oscar.slides.GO_TO_ADJUSTABLE_SHARED_POSITION();
                        time.reset();
                    }
                    if(gamepad2.left_bumper && time.milliseconds() > 5){
                        Oscar.slides.CHANGE_ADJUSTABLE_SHARED_TICKS(-3);
                        Oscar.slides.GO_TO_ADJUSTABLE_SHARED_POSITION();
                        time.reset();
                    }
                    else {
                        Oscar.slides.GO_TO_ADJUSTABLE_SHARED_POSITION();
                    }
                }
                break;
            case STATE_2:
                if(time.milliseconds() > 500) {
                    shared_deposit_state = SHARED_DEPOSIT_STATE.STATE_3;
                    Oscar.grabber.g1.setPosition(.015);
                    Oscar.grabber.moveByAngle(-90, "start");
                    time.reset();
                }
                else {
                    Oscar.slides.slidesHold();
                    Oscar.grabber.g1.setPosition(.5);
                }
                break;
            case STATE_3:
                if(time.milliseconds() > 500) {
                    shared_deposit_state = SHARED_DEPOSIT_STATE.INIT;
                    Oscar.grabber.moveByAngle(90,"start");
                    time.reset();
                    Oscar.elbow.goToGrabPos();
                }
                else {
                    Oscar.slides.slidesGrab();
                }
                break;
            default:
                shared_deposit_state = SHARED_DEPOSIT_STATE.INIT;
                break;
        }
    }
}
