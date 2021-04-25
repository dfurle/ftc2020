package me.walkerknapp.tabbultimategoal;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

@Autonomous(name="RTP Autonomous", group="Primary")
public class AutonomousRTPOpMode extends OpMode {

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

        hardware.rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    @Override
    public void start() {
        super.start();

        this.stopped = false;

        driveService = executorService.submit(new Runnable() {
            @Override
            public void run() {
                hardware.pushServo.setPosition(0.0);
                hardware.leftMotor.setTargetPosition(hardware.leftMotor.getCurrentPosition() + 10214);
                hardware.rightMotor.setTargetPosition(hardware.leftMotor.getCurrentPosition() + 10214);
                hardware.hDriveMotor.setPower(0.3);

                while ((hardware.leftMotor.isBusy() || hardware.rightMotor.isBusy()) && !stopped) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (stopped) {
                    hardware.zeroAll();
                    return;
                }

                hardware.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                hardware.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                hardware.hDriveMotor.setPower(0);
                hardware.flywheelMotor.setPower(1.0);

                for (int i = 0; i < 3; i++) {
                    hardware.pushServo.setPosition(0.0);

                    if (unsafeWait(1000)) {
                        hardware.zeroAll();
                        return;
                    }

                    hardware.pushServo.setPosition(1.0);

                    if (unsafeWait(1000)) {
                        hardware.zeroAll();
                        return;
                    }
                }

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
            this.executorService = Executors.newSingleThreadScheduledExecutor();
        }
    }
}
