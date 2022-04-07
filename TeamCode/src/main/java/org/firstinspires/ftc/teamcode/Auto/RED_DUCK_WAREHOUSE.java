package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


import org.firstinspires.ftc.teamcode.robot.CV.BarcodePositionDetector;
import org.firstinspires.ftc.teamcode.robot.DEPOSIT_LINEAR;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import dashboard.RobotConstants;

    @Config
    @Autonomous(group = "drive")
    public class RED_DUCK_WAREHOUSE extends LinearOpMode {
        private FtcDashboard dashboard;
        public static Pose2d startPR = new Pose2d(-39.1, -64, Math.toRadians(180));
        public static Pose2d DuckRed = new Pose2d(-60.3,-56.5, Math.toRadians(110));
        public static Pose2d returnToPos = new Pose2d(-39.1, -69, Math.toRadians(180));
        public static Pose2d driveToWarehouse = new Pose2d(44.8,-69,Math.toRadians(180));
        public static Pose2d RedCyclePreload = new Pose2d(-37.2, -56.5,Math.toRadians(150));
        //Hardware Oscar = new Hardware(hardwareMap, telemetry);
        //test 2

        BarcodePositionDetector.BarcodePosition position;
        @Override
        public void runOpMode() throws InterruptedException {
            Hardware Oscar = new Hardware(hardwareMap, telemetry);
            DEPOSIT_LINEAR deposit_linear = new DEPOSIT_LINEAR(Oscar, telemetry, gamepad1, gamepad2);


            Oscar.init(hardwareMap);

            Oscar.drive.setPoseEstimate(startPR);

            TrajectorySequence autoTrajectory0 = Oscar.drive.trajectorySequenceBuilder(startPR)
                    .splineToLinearHeading(RedCyclePreload, Math.toRadians(150))
                    .build();


            TrajectorySequence autoTrajectory1 = Oscar.drive.trajectorySequenceBuilder(autoTrajectory0.end())
                    .forward(5)
                    .splineToLinearHeading(DuckRed, 140)
                    .build();
            TrajectorySequence autoTrajectory2 = Oscar.drive.trajectorySequenceBuilder(autoTrajectory1.end())
                    .lineToLinearHeading(returnToPos)
                    .build();


            TrajectorySequence rerunToWarehouse = Oscar.drive.trajectorySequenceBuilder(autoTrajectory2.end())
                    .lineToLinearHeading(driveToWarehouse)
                    .build();

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
            if(counterMid > counterTop && counterMid > counterBottom){
                position = BarcodePositionDetector.BarcodePosition.MIDDLE;

            }
            if (counterTop > counterBottom && counterTop > counterMid){
                position = BarcodePositionDetector.BarcodePosition.RIGHT;
            }

            waitForStart();
            Oscar.drive.followTrajectorySequence(autoTrajectory0);
            Thread.sleep(1000);

            if(position == BarcodePositionDetector.BarcodePosition.LEFT){
                deposit_linear.DROP_DA_THING_ON_TOP();
            }
            if(position == BarcodePositionDetector.BarcodePosition.MIDDLE){
                deposit_linear.DROP_DA_THING_ON_MIDDLE();
            }
            if(position == BarcodePositionDetector.BarcodePosition.RIGHT){
                deposit_linear.DROP_DA_THING_ON_TOP();
            }


            Oscar.drive.followTrajectorySequence(autoTrajectory1);

            Oscar.grabber.carousellOn();

            Thread.sleep(5000);

            Oscar.drive.followTrajectorySequence(autoTrajectory2);
            Oscar.drive.followTrajectorySequence(rerunToWarehouse);


        }
    }

