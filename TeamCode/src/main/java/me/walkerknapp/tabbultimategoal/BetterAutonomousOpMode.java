package me.walkerknapp.tabbultimategoal;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name="Better Primary Autonomous", group="Primary")
public class BetterAutonomousOpMode extends AutoWithIMU {

    public boolean unsafeWait(long time) {
        long targetTime = System.currentTimeMillis() + time;

        while (targetTime > System.currentTimeMillis()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return !opModeIsActive();
    }

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();

        hardware.armUpServo.setPosition(0.0);
        unsafeWait(400);
        hardware.flywheelMotor.setPower(-0.85);

        gyroDrive(0.5, 90, 0);

        hardware.armGrabServo.setPosition(0.4);
        unsafeWait(1000);
        hardware.armUpServo.setPosition(0);
        unsafeWait(2000);

        hardware.rightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        hardware.leftMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        gyroDrive(0.5, 5, 0);

        hardware.rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        hardware.leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        hardware.leftMotor.setPower(0);
        hardware.rightMotor.setPower(0);
        hardware.hDriveMotor.setPower(0);

        hardware.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hardware.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        /*hardware.hDriveMotor.setPower(0.3);

        unsafeWait(400);

        hardware.hDriveMotor.setPower(0);*/

        unsafeWait(400);


        for (int i = 0; i < 5; i++) {
            hardware.pushServo.setPosition(0);

            if (unsafeWait(1000)) {
                hardware.zeroAll();
                return;
            }

            hardware.pushServo.setPosition(0.35);

            if (unsafeWait(1000)) {
                hardware.zeroAll();
                return;
            }
        }

        hardware.zeroAll();
    }

    @Override
    public void internalPostInitLoop() {
        super.internalPostInitLoop();

        hardware.armUpServo.setPosition(0.0);
    }
}
