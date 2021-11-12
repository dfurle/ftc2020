package me.walkerknapp.tabbultimategoal.hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class WobblySquared {
    public DcMotor frontLeftMotor;
    public DcMotor frontRightMotor;
    public DcMotor backLeftMotor;
    public DcMotor backRightMotor;

    public Servo pushServo;
    public Servo armUpServo;
    public Servo armGrabServo;

    public DcMotor hDriveMotor;
    public DcMotor flywheelMotor;
    public DcMotor pickupTopMotor;
    public DcMotor pickupBottomMotor;

    public BNO055IMU imu;

    public void init(HardwareMap hw) {
        this.frontLeftMotor = hw.dcMotor.get("frontLeft");
        this.frontRightMotor = hw.dcMotor.get("frontRight");
        this.backLeftMotor = hw.dcMotor.get("backLeft");
        this.backRightMotor = hw.dcMotor.get("backRight");

        pushServo = hw.servo.get("push");
        armUpServo = hw.servo.get("armUp");
        armGrabServo = hw.servo.get("armGrab");

        pickupTopMotor = hw.dcMotor.get("pickupTop");
        pickupBottomMotor = hw.dcMotor.get("pickupBottom");
        flywheelMotor = hw.dcMotor.get("flywheel");
    }
}
