package org.firstinspires.ftc.teamcode.robot.controllers;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

//gets state of controllers and makes the teleop code look nicer
// has multiple different states
public class ControllerState {
    public Gamepad gamepad;

    private Analog left_stick_x;
    private Analog left_stick_y;
    private Analog right_stick_x;
    private Analog right_stick_y;
    private Analog left_trigger;
    private Analog right_trigger;
    private Button dpad_up;
    private Button dpad_down;
    private Button dpad_left;
    private Button dpad_right;
    private Button guide;
    private Button x;
    private Button y;
    private Button a;
    private Button b;
    private Button left_bumper;
    private Button right_bumper;

    private Map<String, Button> buttonMap = new HashMap<>();
    private Map<ButtonEvent, EventHandler> buttonEventListeners = new HashMap<>();
    private Map<String, Analog> analogMap = new HashMap<>();
    private Map<AnalogEvent, EventHandler> analogEventListeners = new HashMap<>();

    public ControllerState(Gamepad g) {
        // Map gamepad buttons, sticks, and triggers to Button and Analog
        gamepad = g;

        left_stick_x = new Analog();
        left_stick_y = new Analog();
        right_stick_x = new Analog();
        right_stick_y = new Analog();
        left_trigger = new Analog();
        right_trigger = new Analog();

        dpad_up = new Button();
        dpad_down = new Button();
        dpad_left = new Button();
        dpad_right = new Button();

        guide = new Button();

        x = new Button();
        y = new Button();
        a = new Button();
        b = new Button();
        left_bumper = new Button();
        right_bumper = new Button();


        analogMap.put("left_stick_x", left_stick_x);
        analogMap.put("left_stick_y", left_stick_y);
        analogMap.put("right_stick_x", right_stick_x);
        analogMap.put("right_stick_y", right_stick_y);
        analogMap.put("left_trigger", left_trigger);
        analogMap.put("right_trigger", right_trigger);

        buttonMap.put("dpad_up", dpad_up);
        buttonMap.put("dpad_down", dpad_down);
        buttonMap.put("dpad_left", dpad_left);
        buttonMap.put("dpad_right", dpad_right);

        buttonMap.put("guide", guide);

        buttonMap.put("x", x);
        buttonMap.put("y", y);
        buttonMap.put("a", a);
        buttonMap.put("b", b);
        buttonMap.put("left_bumper", left_bumper);
        buttonMap.put("right_bumper", right_bumper);
    }
    // Allow accessing button and analog values with string name outside class
    public boolean getButtonValue(String buttonName) { return buttonMap.get(buttonName).getValue(); }
    public double getAnalogValue(String analogName) {
        return analogMap.get(analogName).getValue();
    }

    // Updates all buttons and analogs
    public void updateControllerState() {
        left_stick_x.update(gamepad.left_stick_x);
        left_stick_y.update(gamepad.left_stick_y);
        right_stick_x.update(gamepad.right_stick_x);
        right_stick_y.update(gamepad.right_stick_y);
        left_trigger.update(gamepad.left_trigger);
        right_trigger.update(gamepad.right_trigger);

        dpad_up.update(gamepad.dpad_up);
        dpad_down.update(gamepad.dpad_down);
        dpad_left.update(gamepad.dpad_left);
        dpad_right.update(gamepad.dpad_right);

        guide.update(gamepad.guide);

        x.update(gamepad.x);
        y.update(gamepad.y);
        a.update(gamepad.a);
        b.update(gamepad.b);
        left_bumper.update(gamepad.left_bumper);
        right_bumper.update(gamepad.right_bumper);
    }

    // Add event listeners in teleop
    public void addEventListener(String buttonName, ButtonState state, EventHandler handler) {
        buttonEventListeners.put(new ButtonEvent(buttonMap.get(buttonName), state), handler);
    }
    public void addEventListener(String analogName, AnalogCheck condition, double bound, EventHandler handler) {
        analogEventListeners.put(new AnalogEvent(analogMap.get(analogName), condition, bound), handler);
    }

    // Checks events, runs execute()
    public void handleEvents() throws InterruptedException {
        for (Entry<ButtonEvent, EventHandler> entry: buttonEventListeners.entrySet()) {
            if (entry.getKey().checkEvent()) {
                entry.getValue().execute();
            }
        }
        for (Entry<AnalogEvent, EventHandler> entry: analogEventListeners.entrySet()) {
            if (entry.getKey().checkEvent()) {
                entry.getValue().execute();
            }
        }
    }
}

// Model of a controller Button
class Button {
    private boolean value;
    private ButtonState state;

    public Button() {
        value = false;
        state = ButtonState.OFF;
    }

    public boolean getValue() {
        return value;
    }
    public ButtonState getState() {
        return state;
    }

    // Implementing state machine for Button
    // OFF -> PRESSED -> HELD -> OFF
    public void update(boolean newValue) {
        if (state == ButtonState.OFF) {
            if (newValue) {
                value = true;
                state = ButtonState.PRESSED;
            }
            // else stay false and OFF
        }
        else if (state == ButtonState.PRESSED) {
            if (newValue) {
                state = ButtonState.HELD;
            }
            else {
                value = false;
                state = ButtonState.OFF;
            }
        }
        else if (state == ButtonState.HELD) {
            if (!newValue) {
                value = false;
                state = ButtonState.OFF;
            }
            // else stay true and HELD
        }
    }
}

// Holds Button & ButtonState event check
class ButtonEvent {
    private Button button;
    private ButtonState state;
    public ButtonEvent(Button b, ButtonState s) {
        button = b;
        state = s;
    }
    public boolean checkEvent() {
        return button.getState() == state;
    }
}

// Model of controller sticks and triggers
class Analog {
    private double value;

    public Analog() {
        value = 0.0;
    }
    public double getValue() {
        return value;
    }
    public void update(double newValue) {
        value = newValue;
    }
}

// Holds Analog and an [in]equality condition
class AnalogEvent {
    private Analog analog;
    private AnalogCheck condition;
    private double bound;

    public AnalogEvent(Analog a, AnalogCheck c, double b) {
        analog = a;
        condition = c;
        bound = b;
    }
    public boolean checkEvent() {
        if (condition == AnalogCheck.GREATER_THAN) {
            return analog.getValue() > bound;
        }
        else if (condition == AnalogCheck.GREATER_THAN_EQUALS) {
            return analog.getValue() >= bound;
        }
        else if (condition == AnalogCheck.EQUALS) {
            return analog.getValue() == bound;
        }
        else if (condition == AnalogCheck.LESS_THAN_EQUALS) {
            return analog.getValue() <= bound;
        }
        else if (condition == AnalogCheck.LESS_THAN) {
            return analog.getValue() < bound;
        }
        // To be implemented ?
        else if (condition == AnalogCheck.READ) {
            return true;
        }
        else {
            return false;
        }
    }
}