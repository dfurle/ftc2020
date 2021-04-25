package me.walkerknapp.tabbultimategoal;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@TeleOp(name="Servo Test OpMode", group = "Test")
public class ServoTestOpMode extends OpMode {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final HardwareWobblySquare2 hardware = new HardwareWobblySquare2();

    private float servoPosition;

    private Future<?> runFuture;

    @Override
    public void init() {
        hardware.init(hardwareMap);

        ((ScheduledThreadPoolExecutor)executorService).setRemoveOnCancelPolicy(true);

        //executorService.scheduleAtFixedRate(telemetry::update, 0, (long) (telemetry.getMsTransmissionInterval() * 1.5f), TimeUnit.MILLISECONDS);
    }

    @Override
    public void start() {
        runFuture = executorService.scheduleAtFixedRate(() -> {
            if (ServoTestOpMode.this.gamepad1.dpad_up) {
                servoPosition += 0.03f;
            }
            if (ServoTestOpMode.this.gamepad1.dpad_down) {
                servoPosition -= 0.03f;
            }

            hardware.pushServo.setPosition(servoPosition);

            telemetry.addData("servo position", servoPosition);
            telemetry.update();
        },0, 20, TimeUnit.MILLISECONDS);
    }

    @Override
    public void loop() {

    }

    @Override
    public void stop() {
        super.stop();
        runFuture.cancel(true);
    }
}
