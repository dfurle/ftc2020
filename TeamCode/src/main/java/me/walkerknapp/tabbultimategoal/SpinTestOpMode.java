package me.walkerknapp.tabbultimategoal;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@TeleOp(name="Spin Test OpMode", group = "Primary")
public class SpinTestOpMode extends OpMode {

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private HardwareWobblySquare2 hardware = new HardwareWobblySquare2();

    double prevLeftPower = 0.0f;
    double prevRightPower = 0.0f;
    double prevHDrivePower = 0.0f;

    boolean flywheelActive = false;


    private Future<?> driveService;

    @Override
    public void init() {
        hardware.init(hardwareMap);
    }

    @Override
    public void loop() {
        super.start();

        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (SpinTestOpMode.this.gamepad1.a) {
                    hardware.leftMotor.setPower(-1.0);
                    hardware.rightMotor.setPower(-1.0);
                    //hardware.pickupBottomMotor.setPower(-0.5);
                    //hardware.pickupTopMotor.setPower(-0.5);

                    telemetry.addData("on", true);
                } else {
                    hardware.leftMotor.setPower(0.0);
                    hardware.rightMotor.setPower(0.0);
                    //hardware.pickupBottomMotor.setPower(0.0);
                    //hardware.pickupTopMotor.setPower(0.0);

                    telemetry.addData("on", false);
                }

                telemetry.update();
            }
        }, 0, 20, TimeUnit.MILLISECONDS);
    }
}
