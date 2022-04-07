package com.example.meepmeep;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
import com.noahbres.meepmeep.roadrunner.trajectorysequence.TrajectorySequence;

import java.util.Vector;

public class MeepMeepTesting {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");

        MeepMeep meepMeep = new MeepMeep(800);

        // Declare our first bot
        Pose2d splineCV = new Pose2d(4.1,-55.2,Math.toRadians(210));
        Pose2d splineTest = new Pose2d(4.1,-55.2, Math.toRadians(210));
        Pose2d RevertTest = new Pose2d(4.1,-63.2, Math.toRadians(180));
        Pose2d DuckRedCycle = new Pose2d(-57.2,53.7, Math.toRadians(30));
        Pose2d StoragePark = new Pose2d(-58.7, 34.7, Math.toRadians((90)));
        Pose2d MoveRight1 = new Pose2d(-55.0, -53.7, Math.toRadians(130));
        Pose2d startPR = new Pose2d(-39.1, 64, Math.toRadians(0));
        Pose2d returnToPos = new Pose2d(-39.1, 64, Math.toRadians(0));


        Pose2d RedDuckPreload = new Pose2d(-37.2, 55,Math.toRadians(30));

        Vector2d WarehouseSpline = new Vector2d(48,-55.8);

        Vector2d vectorTest = new Vector2d(37,-64);
        Vector2d returnVector = new Vector2d(37,-64);
        RoadRunnerBotEntity myFirstBot = new DefaultBotBuilder(meepMeep)
                // We set this bot to be blue
                .setColorScheme(new ColorSchemeBlueDark())
                .setConstraints(140.63964888286645, 52.48291908330528, Math.toRadians(180), Math.toRadians(180), 12.6)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(19, -64, Math.toRadians(180)))
                                .back(18)
                                .splineToConstantHeading(WarehouseSpline,Math.toRadians(180))
                                .splineToConstantHeading(returnVector,Math.toRadians(180))
                                .forward(18)

                                .build()
                );



//        // Declare out second bot
        RoadRunnerBotEntity mySecondBot = new DefaultBotBuilder(meepMeep)
                // We set this bot to be red
                .setColorScheme(new ColorSchemeRedDark())
                .setConstraints(140.63964888286645, 52.48291908330528, Math.toRadians(180), Math.toRadians(180), 12.6)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(48,62,Math.toRadians(0)))
                                .lineToConstantHeading(new Vector2d(48, 63))
                                .splineToConstantHeading(new Vector2d(32, 65), Math.toRadians(0))
                                .lineToLinearHeading(new Pose2d(-12, 65, Math.toRadians(0)))
                                .lineToLinearHeading(new Pose2d(32, 65,Math.toRadians(0)))
                                .splineToConstantHeading(new Vector2d(48,62), Math.toRadians(0))
                                .build()
                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_FREIGHTFRENZY_ADI_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)

                // Add both of our declared bot entities
                //.addEntity(myFirstBot)
                .addEntity(mySecondBot)
                .start();
    }


}