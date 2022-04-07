package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Autonomous
public class RED_DUCK_STORAGE extends LinearOpMode {

    private FtcDashboard dashboard;
    public static Pose2d startPR = new Pose2d(-39.1, -64, Math.toRadians(180));
    public static Pose2d DuckRed = new Pose2d(-57.2, -53.7, Math.toRadians(130));
    public static Pose2d StoragePark = new Pose2d(-58.7, -34.7, Math.toRadians((90)));
    public static Pose2d MoveRight1 = new Pose2d(-55.0, -53.7, Math.toRadians(130));


    @Override
    public void runOpMode() throws InterruptedException {
        Hardware Oscar = new Hardware(hardwareMap, telemetry);

        Oscar.init(hardwareMap);

        Oscar.drive.setPoseEstimate(startPR);


        TrajectorySequence autoTrajectory1 = Oscar.drive.trajectorySequenceBuilder(startPR)
                .forward(5)
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

        waitForStart();

        Oscar.drive.followTrajectorySequence(autoTrajectory1);


        Oscar.grabber.carousellOn();

        Thread.sleep(5000);

        Oscar.drive.followTrajectorySequence(autoTrajectory2);
    }
}
