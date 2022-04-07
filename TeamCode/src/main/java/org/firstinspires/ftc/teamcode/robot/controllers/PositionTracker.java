package org.firstinspires.ftc.teamcode.robot.controllers;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.Arrays;

public class PositionTracker {

    private final double X_MAX_SPEED = 15;
    private final double Y_MAX_SPEED = 15;
    private final double ANG_MAX_SPEED = 10;

    //tracks current velocities
    private double X_VELOCITY = 0;
    private double Y_VELOCITY = 0;
    private double ANG_VELOCITY = 0;

    //if velocity within threshold, then velocity = 0
    private final double X_VELOCITY_THRESHOLD = 1;
    private final double Y_VELOCITY_THRESHOLD = 1;
    private final double ANG_VELOCITY_THRESHOLD = .1;

    private double DELTA_TIME = 0;

    private Pose2d POSITION;
    private Pose2d PREVIOUS_POSITION;

    private ElapsedTime INTERNAL_TIMER;

    //assumed that initial velocities and accelerations are 0, therefore only taking in initial position.
    public PositionTracker(Pose2d INITIAL_POSITION) {
        POSITION = INITIAL_POSITION;
        PREVIOUS_POSITION = INITIAL_POSITION;
        INTERNAL_TIMER = new ElapsedTime();
    }

    //positive is true, negative is false, 0 is 0
    public void setXVelocity(double analogValue) {
        X_VELOCITY = X_MAX_SPEED * analogValue;
    }

    public void setYVelocity(double analogValue) {
        Y_VELOCITY = Y_MAX_SPEED * analogValue;
    }

    //positive / counter clockwise is true, cw / negative is false, 0 is 0
    public void setAngVelocity(int analogValue) {
        ANG_VELOCITY = ANG_MAX_SPEED * analogValue;
    }

    //calculate new position based on elapsed time
    public void iterate() {
        DELTA_TIME = .08;
        PREVIOUS_POSITION = POSITION;
        POSITION = new Pose2d(
                PREVIOUS_POSITION.getX() + (X_VELOCITY * DELTA_TIME),
                -PREVIOUS_POSITION.getY() + (Y_VELOCITY * DELTA_TIME),
                PREVIOUS_POSITION.getHeading() + (ANG_VELOCITY * DELTA_TIME)
                );
        INTERNAL_TIMER.reset();
    }

    public Pose2d getPose() {
        return(POSITION);
    }
    public Pose2d getPreviousPose() {return(PREVIOUS_POSITION);}
    public ArrayList<Double> getVelocities() {
        ArrayList<Double> velocities = new ArrayList<>(Arrays.asList(X_VELOCITY, Y_VELOCITY, ANG_VELOCITY));
        return(velocities);
    }
}
