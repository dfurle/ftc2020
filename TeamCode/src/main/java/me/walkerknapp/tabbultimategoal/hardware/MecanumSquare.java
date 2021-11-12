package me.walkerknapp.tabbultimategoal.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MecanumSquare {
    public DcMotor frontLeftMotor;
    public DcMotor frontRightMotor;
    public DcMotor backLeftMotor;
    public DcMotor backRightMotor;

    public void init(HardwareMap hw) {
        this.frontLeftMotor = hw.dcMotor.get("frontLeft");
        this.frontRightMotor = hw.dcMotor.get("frontRight");
        this.backLeftMotor = hw.dcMotor.get("backLeft");
        this.backRightMotor = hw.dcMotor.get("backRight");
    }
}
