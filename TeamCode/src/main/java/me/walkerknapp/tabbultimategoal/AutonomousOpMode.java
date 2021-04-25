package me.walkerknapp.tabbultimategoal;

import android.graphics.drawable.GradientDrawable;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Autonomous(name="Primary Autonomous", group="Primary")
public class AutonomousOpMode extends OpMode {

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final HardwareWobblySquare2 hardware = new HardwareWobblySquare2();

    private boolean stopped = false;

    private Future<?> driveService;

    public boolean unsafeWait(long time) {
        long targetTime = System.currentTimeMillis() + time;

        while (targetTime > System.currentTimeMillis() && !stopped) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return stopped;
    }

    @Override
    public void init() {
        hardware.init(hardwareMap);

        hardware.armUpServo.setPosition(0.0);
        //unsafeWait(400);
        //hardware.flywheelMotor.setPower(-0.85);
    }

    @Override
    public void start() {
        super.start();

        this.stopped = false;

        driveService = executorService.submit(new Runnable() {
            @Override
            public void run() {

                hardware.armUpServo.setPosition(0.0);
                //unsafeWait(400);
                //hardware.flywheelMotor.setPower(0.00);

                hardware.pushServo.setPosition(0.35);
                hardware.leftMotor.setPower(0.53);
                hardware.rightMotor.setPower(0.5);
                //hardware.hDriveMotor.setPower(0.3);

                if (unsafeWait(4500)) {
                    hardware.zeroAll();
                    return;
                }

                hardware.leftMotor.setPower(0);
                hardware.rightMotor.setPower(0);
                hardware.hDriveMotor.setPower(0);
                hardware.flywheelMotor.setPower(-0.85);

                hardware.armGrabServo.setPosition(0.4);
                unsafeWait(1000);
                hardware.armUpServo.setPosition(0);
                unsafeWait(2000);

                hardware.rightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
                hardware.leftMotor.setDirection(DcMotorSimple.Direction.FORWARD);

                hardware.leftMotor.setPower(0.4);
                hardware.rightMotor.setPower(0.4);
                //hardware.hDriveMotor.setPower(0.3);

                if (unsafeWait(2000)) {
                    hardware.zeroAll();
                    return;
                }

                hardware.leftMotor.setPower(0);
                hardware.rightMotor.setPower(0);

                hardware.rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
                hardware.leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

                for (int i = 0; i < 5; i++) {
                    hardware.pushServo.setPosition(0.35);

                    if (unsafeWait(1000)) {
                        hardware.zeroAll();
                        return;
                    }

                    hardware.pushServo.setPosition(0);

                    if (unsafeWait(1000)) {
                        hardware.zeroAll();
                        return;
                    }
                }

                hardware.leftMotor.setPower(0.5);
                hardware.rightMotor.setPower(0.5);
                //hardware.hDriveMotor.setPower(0.3);

                if (unsafeWait(2000)) {
                    hardware.zeroAll();
                    return;
                }

                hardware.leftMotor.setPower(0);
                hardware.rightMotor.setPower(0);

                hardware.zeroAll();
            }
        });
    }

    @Override
    public void loop() {
    }

    @Override
    public void stop() {
        super.stop();

        this.stopped = true;

        this.hardware.zeroAll();

        if (this.driveService != null) {
            this.driveService.cancel(true);
            this.executorService.shutdownNow();
            this.executorService = Executors.newScheduledThreadPool(1);
            ((ScheduledThreadPoolExecutor)this.executorService).setRemoveOnCancelPolicy(true);
        }
    }
}
