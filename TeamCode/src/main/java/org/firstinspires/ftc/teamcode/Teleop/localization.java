package org.firstinspires.ftc.teamcode.Teleop;

import com.acmerobotics.roadrunner.drive.MecanumDrive;
import com.acmerobotics.roadrunner.localization.Localizer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

class AutoLocalizationMap extends LinearOpMode {
    SampleMecanumDrive Oscar;
    final String MAP_NAME = "Auto Localization Test";

    @Override
    public void runOpMode() throws InterruptedException{
        Oscar = new SampleMecanumDrive(hardwareMap);

        telemetry.addLine("Make sure the robot is in the center of the field. Press start to begin mapping.");
        telemetry.update();

        waitForStart();

        Oscar.update();

        Localizer localizer = Oscar.getLocalizer();

        Oscar.setLocalizer(new MecanumDrive.MecanumLocalizer(Oscar));
        // move around to make sure

    }

}
