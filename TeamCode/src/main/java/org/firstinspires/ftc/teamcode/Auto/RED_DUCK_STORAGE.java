package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.robot.CV.BarcodePositionDetector;
import org.firstinspires.ftc.teamcode.robot.DEPOSIT_LINEAR;

@Autonomous
public class RED_DUCK_STORAGE extends LinearOpMode {

    private FtcDashboard dashboard;
    public static Pose2d startPR = new Pose2d(-39.1, -64, Math.toRadians(180));
    public static Pose2d DuckRed = new Pose2d(-35.8, -51.7, Math.toRadians(130));
    public static Pose2d StoragePark = new Pose2d(-58.7, -34.7, Math.toRadians((90)));
    public static Pose2d MoveRight1 = new Pose2d(-55.0, -53.7, Math.toRadians(130));

    BarcodePositionDetector.BarcodePosition position;

    @Override
    public void runOpMode() throws InterruptedException {

        Hardware Oscar = new Hardware(hardwareMap, telemetry);
        DEPOSIT_LINEAR deposit_linear = new DEPOSIT_LINEAR(Oscar, telemetry, gamepad1, gamepad2);
        Oscar.init(hardwareMap);

        Oscar.drive.setPoseEstimate(startPR);


        TrajectorySequence autoTrajectory1 = Oscar.drive.trajectorySequenceBuilder(startPR)
                .splineToLinearHeading(DuckRed, 140)
                .build();

        TrajectorySequence autoTrajectory2 = Oscar.drive.trajectorySequenceBuilder(startPR)
                .splineToLinearHeading(MoveRight1, 140)
                .splineToLinearHeading(StoragePark, 90)
                .build();

        //TrajectorySequence autoTrajectory2 = Oscar.drive.trajectorySequenceBuilder(autoTrajectory1.end())
        //       .back(70)
        //     .strafeLeft(2)
        //     .back(20)
        //    .build();
        Oscar.cvUtil.init();

        int counterTop = 0;
        int counterBottom = 0;
        int counterMid = 0;
        Oscar.slides.slidesHome();


        BarcodePositionDetector.BarcodePosition barcodePosition;

        while (!isStopRequested() && !opModeIsActive()) {
            barcodePosition = Oscar.cvUtil.getBarcodePosition();
            telemetry.addData("Barcode position", barcodePosition);
            if(barcodePosition == BarcodePositionDetector.BarcodePosition.LEFT){
                counterBottom++;
            }
            else if( barcodePosition == BarcodePositionDetector.BarcodePosition.MIDDLE){
                counterMid++;
            }
            else if( barcodePosition == BarcodePositionDetector.BarcodePosition.RIGHT){
                counterTop++;
            }

            telemetry.update();
        }

        if (counterBottom > counterMid && counterBottom > counterTop){
            position = BarcodePositionDetector.BarcodePosition.LEFT;
        }
        else if(counterMid > counterTop && counterMid > counterBottom){
            position = BarcodePositionDetector.BarcodePosition.MIDDLE;
        }
        else if (counterTop > counterBottom && counterTop > counterMid){
            position = BarcodePositionDetector.BarcodePosition.RIGHT;
        }
        else {
            position = BarcodePositionDetector.BarcodePosition.RIGHT;
        }

        waitForStart();

        Oscar.drive.followTrajectorySequence(autoTrajectory1);

        if(position == BarcodePositionDetector.BarcodePosition.LEFT){
            deposit_linear.DROP_DA_THING_ON_TOP();
        }
        if(position == BarcodePositionDetector.BarcodePosition.MIDDLE){
            deposit_linear.DROP_DA_THING_ON_MIDDLE();
        }
        if(position == BarcodePositionDetector.BarcodePosition.RIGHT){
            deposit_linear.DROP_DA_THING_ON_TOP();
        }


        Oscar.grabber.carousellOn();

        Thread.sleep(5000);

        Oscar.drive.followTrajectorySequence(autoTrajectory2);
    }
}
