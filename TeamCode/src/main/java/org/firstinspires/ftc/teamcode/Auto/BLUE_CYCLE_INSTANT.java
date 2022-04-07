package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.robot.CAPPER;
import org.firstinspires.ftc.teamcode.robot.CV.BarcodePositionDetector;
import org.firstinspires.ftc.teamcode.robot.DEPOSIT_FSM;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.INTAKE_FSM;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Config
@Autonomous(group = "advanced")
public class BLUE_CYCLE_INSTANT extends LinearOpMode {
    Hardware Oscar;

    double AMOUNT_ITERATE_Y = 1.2;

    double ADJUSTABLE_INTAKE_X = 48;
    double AMOUNT_INCREASE_INTAKE_X = 1.8;

    double ADJUSTABLE_INTAKE_Y = 62;
    double AMOUNT_INCREASE_INTAKE_Y = -3;

    double WALL_PUSH = 65;

    //Milliseconds
    double STUCK_INTAKE_TIMEOUT = 2000;

    Pose2d startPose = new Pose2d(12, 63, Math.toRadians(0));
    Pose2d depositPose = new Pose2d(-14, WALL_PUSH, Math.toRadians(0));
    Vector2d depositVector = new Vector2d(depositPose.getX(), depositPose.getY());
    Pose2d bottomDepositPose = new Pose2d(-1.5, 67.5, Math.toRadians(0));
    Pose2d warehousePose = new Pose2d(38, WALL_PUSH, Math.toRadians(0));
    Pose2d intakePose = new Pose2d(ADJUSTABLE_INTAKE_X, ADJUSTABLE_INTAKE_Y, Math.toRadians(0));
    Vector2d intakeVector = new Vector2d(ADJUSTABLE_INTAKE_X, ADJUSTABLE_INTAKE_Y);
    Vector2d warehouseVector = new Vector2d(38, WALL_PUSH);

    Trajectory START_TO_DEPOSIT;
    TrajectorySequence DEPOSIT_TO_WAREHOUSE;
    TrajectorySequence WAREHOUSE_TO_DEPOSIT;
    Trajectory START_TO_DEPOSIT_BOTTOM;
    Trajectory DEPOSIT_BOTTOM_TO_WAREHOUSE;

    private void iterateIntakeX() {
        ADJUSTABLE_INTAKE_X += AMOUNT_INCREASE_INTAKE_X;
        ADJUSTABLE_INTAKE_Y += AMOUNT_INCREASE_INTAKE_Y;
        intakeVector = new Vector2d(ADJUSTABLE_INTAKE_X, ADJUSTABLE_INTAKE_Y);
        intakePose = new Pose2d(ADJUSTABLE_INTAKE_X, ADJUSTABLE_INTAKE_Y, Math.toRadians(0));
        DEPOSIT_TO_WAREHOUSE = Oscar.drive.trajectorySequenceBuilder(depositPose)
//                .setReversed(true)
                .lineToLinearHeading(warehousePose)
                .splineToConstantHeading(intakeVector, Math.toRadians(0))
                .build();
        WAREHOUSE_TO_DEPOSIT = Oscar.drive.trajectorySequenceBuilder(DEPOSIT_TO_WAREHOUSE.end())
//                .setReversed(true)
                .splineToConstantHeading(warehouseVector, Math.toRadians(0))
                .lineToLinearHeading(depositPose)
                .build();
    }

    enum STATE {
        INIT,
        BACKWARD,
        INTAKE,
        RESTART_INTAKE,
        FORWARD,
        IDLE
    }

    BarcodePositionDetector.BarcodePosition position;

    @Override
    public void runOpMode() throws InterruptedException {


        Oscar = new Hardware(hardwareMap, telemetry);
        DEPOSIT_FSM deposit_fsm = new DEPOSIT_FSM(Oscar, telemetry, gamepad1, gamepad2);
        INTAKE_FSM intake_fsm = new INTAKE_FSM(Oscar, telemetry, gamepad1, gamepad2);
        CAPPER capper = new CAPPER(hardwareMap, telemetry, gamepad2);

        START_TO_DEPOSIT = Oscar.drive.trajectoryBuilder(startPose)
                .lineToLinearHeading(new Pose2d(-8.2, 64, Math.toRadians(0)))
                .build();
        START_TO_DEPOSIT_BOTTOM = Oscar.drive.trajectoryBuilder(startPose)
                .lineToLinearHeading(bottomDepositPose)
                .build();
        DEPOSIT_BOTTOM_TO_WAREHOUSE = Oscar.drive.trajectoryBuilder(bottomDepositPose)
                .lineToLinearHeading(warehousePose)
                .splineToConstantHeading(intakeVector, Math.toRadians(0))
                .build();
        DEPOSIT_TO_WAREHOUSE = Oscar.drive.trajectorySequenceBuilder(START_TO_DEPOSIT.end())
//                .setReversed(true)
                .lineToLinearHeading(warehousePose)
                .splineToConstantHeading(intakeVector, Math.toRadians(0))
                .build();
        WAREHOUSE_TO_DEPOSIT = Oscar.drive.trajectorySequenceBuilder(DEPOSIT_TO_WAREHOUSE.end())
//                .setReversed(true)
                .splineToConstantHeading(warehouseVector, Math.toRadians(0))
                .lineToLinearHeading(depositPose)
                .build();

        Oscar.drive.setPoseEstimate(startPose);

        Oscar.elbow.goToGrabPos();
        Oscar.grabber.goStart();
        Oscar.grabber.openGrab();
        Oscar.slides.slidesOutABit();
        Thread.sleep(500);
        Oscar.slides.slidesHome();


        STATE state = STATE.INIT;

        ElapsedTime time = new ElapsedTime();
        ElapsedTime RUNTIME = new ElapsedTime();
        ElapsedTime forceFlipTime = new ElapsedTime();

        boolean ENSURE_ONE_DEPOSIT = false;

        boolean FORCE_FLIP_TIMEOUT = false;

        double STOP_CYCLING_TIMEOUT = 24;

        Oscar.flippers.moveDown("front");
        Oscar.flippers.moveUp("back");

        int counterTop = 0;
        int counterBottom = 0;
        int counterMid = 0;

        Oscar.cvUtil.init();

        BarcodePositionDetector.BarcodePosition barcodePosition;

        while (!isStopRequested() && !opModeIsActive()) {
            barcodePosition = Oscar.cvUtil.getBarcodePosition();
            telemetry.addData("Barcode position", barcodePosition);
            telemetry.update();
        }

        waitForStart();

        time.reset();
        while (time.milliseconds() < 200) {
            barcodePosition = Oscar.cvUtil.getBarcodePosition();
            if(barcodePosition == BarcodePositionDetector.BarcodePosition.LEFT){
                counterBottom++;
            }
            else if( barcodePosition == BarcodePositionDetector.BarcodePosition.MIDDLE){
                counterMid++;
            }
            else if( barcodePosition == BarcodePositionDetector.BarcodePosition.RIGHT){
                counterTop++;
            }

            if (counterBottom > counterMid && counterBottom > counterTop){
                position = BarcodePositionDetector.BarcodePosition.RIGHT;
            }
            if(counterMid > counterTop && counterMid > counterBottom){
                position = BarcodePositionDetector.BarcodePosition.MIDDLE;

            }
            if (counterTop > counterBottom && counterTop > counterMid){
                position = BarcodePositionDetector.BarcodePosition.RIGHT;
            }

            telemetry.addData("Barcode position", position);
            telemetry.update();
        }

        Oscar.drive.followTrajectoryAsync(START_TO_DEPOSIT);
        time.reset();
        RUNTIME.reset();

        while(isStarted() && !isStopRequested()) {
            telemetry.addData("STATE: ", state);
            telemetry.addData("DISTNACE: ", ((DistanceSensor) Oscar.colorFront).getDistance(DistanceUnit.CM));
            telemetry.update();
            switch (state) {
                case INIT:
                    if(position == BarcodePositionDetector.BarcodePosition.LEFT) {
                        if(!ENSURE_ONE_DEPOSIT && time.milliseconds() > 500) {
                            deposit_fsm.startDepositbot = true;
                            ENSURE_ONE_DEPOSIT = true;
                        }
                        else if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                            deposit_fsm.DROP_THE_THING_NOW = true;
                            ENSURE_ONE_DEPOSIT = false;
                            Oscar.drive.followTrajectoryAsync(DEPOSIT_BOTTOM_TO_WAREHOUSE);
                            state = STATE.BACKWARD;
                            time.reset();
                        }
                    }
                    else if(position == BarcodePositionDetector.BarcodePosition.MIDDLE) {
                        if(!ENSURE_ONE_DEPOSIT && time.milliseconds() > 500) {
                            deposit_fsm.startDepositmid = true;
                            ENSURE_ONE_DEPOSIT = true;
                        }
                        else if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                            deposit_fsm.DROP_THE_THING_NOW = true;
                            ENSURE_ONE_DEPOSIT = false;
                            Oscar.drive.followTrajectorySequenceAsync(DEPOSIT_TO_WAREHOUSE);
                            state = STATE.BACKWARD;
                            time.reset();
                        }
                    }
                    else {
                        if(!ENSURE_ONE_DEPOSIT && time.milliseconds() > 500) {
                            deposit_fsm.startDeposittop = true;
                            ENSURE_ONE_DEPOSIT = true;
                        }
                        else if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                            deposit_fsm.DROP_THE_THING_NOW = true;
                            ENSURE_ONE_DEPOSIT = false;
                            Oscar.drive.followTrajectorySequenceAsync(DEPOSIT_TO_WAREHOUSE);
                            state = STATE.BACKWARD;
                            time.reset();
                        }
                    }
                    break;
                case BACKWARD:
                    if(((DistanceSensor) Oscar.colorFront).getDistance(DistanceUnit.CM) < 1.5 || !Oscar.drive.isBusy()) {
                        state = STATE.INTAKE;
                        time.reset();
                    }
                    else {
                        Oscar.intake.frontIn();
                    }
                    break;
                case INTAKE:
                    Oscar.drive.setWeightedDrivePower(new Pose2d(.4,0,0));
                    if(((DistanceSensor) Oscar.colorFront).getDistance(DistanceUnit.CM) < 1.5) {
                        intake_fsm.SET_EXEC_FRONT_FLIP(true);
                        Oscar.drive.setWeightedDrivePower(new Pose2d(0,0,0));
                        Oscar.drive.setPoseEstimate(new Pose2d(Oscar.drive.getPoseEstimate().getX(), Oscar.drive.getPoseEstimate().getY() - AMOUNT_ITERATE_Y, Oscar.drive.getPoseEstimate().getHeading()));
                        if(RUNTIME.seconds() < STOP_CYCLING_TIMEOUT) {
                                Oscar.drive.followTrajectorySequenceAsync(WAREHOUSE_TO_DEPOSIT);
                                state = STATE.FORWARD;
                                time.reset();
                        }
                        else state = STATE.IDLE;
                    }
                    else if(time.milliseconds() > STUCK_INTAKE_TIMEOUT) {
                        state = STATE.RESTART_INTAKE;
                        time.reset();
                    }
                    break;
                case RESTART_INTAKE:
                    if(Oscar.drive.getPoseEstimate().getX() < 52) {
                        state = STATE.INTAKE;
                        Oscar.intake.frontIn();
                        time.reset();
                    }
                    else{
                        Oscar.intake.backOut();
                        Oscar.drive.setWeightedDrivePower(new Pose2d(-.4,0,0));
                    }
                    break;
                case FORWARD:
                    if(Oscar.drive.getPoseEstimate().getX() < 24) {
                        intake_fsm.forceDownFront();
                        Oscar.intake.frontOut();
                        if(!ENSURE_ONE_DEPOSIT) {
                            forceFlipTime.reset();
                            ENSURE_ONE_DEPOSIT = true;
                            FORCE_FLIP_TIMEOUT = false;
                        }
                        if(forceFlipTime.milliseconds() > 50 && !FORCE_FLIP_TIMEOUT) {
                            deposit_fsm.startDeposittop = true;
                            FORCE_FLIP_TIMEOUT = true;
                        }
                    }
                    if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                        iterateIntakeX();
                        deposit_fsm.DROP_THE_THING_NOW = true;
                        ENSURE_ONE_DEPOSIT = false;
                        Oscar.drive.followTrajectorySequenceAsync(DEPOSIT_TO_WAREHOUSE);
                        state = STATE.BACKWARD;
                        time.reset();
                    }
                    break;
                case IDLE:
                    Oscar.intake.off();
                    break;
            }
            deposit_fsm.doDepositTopAsync();
            deposit_fsm.doDepositMiddleAsync();
            deposit_fsm.doDepositBottomAsync();
            intake_fsm.handleEvents(deposit_fsm.isAnyBusy(), true, false);
            Oscar.drive.update();
        }
    }
}
