package org.firstinspires.ftc.teamcode.robot;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class sensors {
    DistanceSensor frSensor;
    DistanceSensor bkSensor;
    View relativeLayout;
    private boolean detected = false;

    public sensors( HardwareMap ahwMap){
        frSensor = ahwMap.get(DistanceSensor.class, "fr_sensor_distance");
        bkSensor = ahwMap.get(DistanceSensor.class,"bk_sensor_distance");
        // Get a reference to the RelativeLayout so we can later change the background
        // color of the Robot Controller app to match the hue detected by the RGB sensor.
        int relativeLayoutId = ahwMap.appContext.getResources().getIdentifier("RelativeLayout", "id", ahwMap.appContext.getPackageName());
        relativeLayout = ((Activity) ahwMap.appContext).findViewById(relativeLayoutId);
        relativeLayout.setBackgroundColor(Color.BLACK);

    }

    public void freightDetected (){

        // if values ~ to range then set detected to true
        // boolean then allows the fsm to run and set it to false once it is no longer detected
        // have one for each side or have both for 1 if statement using ors
        // use normalized powers to get the average values over a set time
        //




    }
}
