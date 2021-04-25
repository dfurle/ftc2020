package me.walkerknapp.tabbultimategoal;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class HardwareWobblySquare2 {
    public Servo pushServo;
    public Servo armUpServo;
    public Servo armGrabServo;

    public DcMotor leftMotor;
    public DcMotor rightMotor;
    public DcMotor hDriveMotor;
    public DcMotor flywheelMotor;
    public DcMotor pickupTopMotor;
    public DcMotor pickupBottomMotor;

    public BNO055IMU imu;

    public void init(HardwareMap hw) {
        pushServo = hw.servo.get("push");
        armUpServo = hw.servo.get("armUp");
        armGrabServo = hw.servo.get("armGrab");

        leftMotor = hw.dcMotor.get("left");
        rightMotor = hw.dcMotor.get("right");
        hDriveMotor = hw.dcMotor.get("hdrive");
        pickupTopMotor = hw.dcMotor.get("pickupTop");
        pickupBottomMotor = hw.dcMotor.get("pickupBottom");
        flywheelMotor = hw.dcMotor.get("flywheel");

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        //rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        pickupTopMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        pickupTopMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        pickupBottomMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        pickupBottomMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheelMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheelMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
       // parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";

        imu = hw.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    public void zeroAll() {
        //this.pushServo.setPosition(0.35);
        //this.armGrabServo.setPosition(1.0);
        //this.armUpServo.setPosition(0.71);

        this.leftMotor.setPower(0);
        this.rightMotor.setPower(0);
        this.hDriveMotor.setPower(0);
        this.pickupTopMotor.setPower(0);
        this.pickupBottomMotor.setPower(0);
        this.flywheelMotor.setPower(0);
    }


}
