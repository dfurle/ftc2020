package me.walkerknapp.tabbultimategoal;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@TeleOp(name="Driving OpMode", group = "Primary")
public class DriveRobotOpMode extends OpMode {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final HardwareWobblySquare2 hardware = new HardwareWobblySquare2();

    double prevLeftPower = 0.0f;
    double prevRightPower = 0.0f;
    double prevHDrivePower = 0.0f;
    double prevFlywheelPower = 0.0f;
    double prevPickupTopPower = 0.0f;
    double prevPickupBottomPower = 0.0f;

    int armStep = 0;

    boolean flywheelActive = false;
    boolean flywheelReverse = false;

    boolean aPress = false;
    boolean bPress = false;

    boolean dPadUpDownPressed = false;

    boolean stopped = false;


    private Future<?> driveService;

    @Override
    public void init() {
        hardware.init(hardwareMap);

        ((ScheduledThreadPoolExecutor)executorService).setRemoveOnCancelPolicy(true);

        //executorService.scheduleAtFixedRate(telemetry::update, 0, (long) (telemetry.getMsTransmissionInterval() * 1.5f), TimeUnit.MILLISECONDS);
    }

    @Override
    public void start() {
        super.start();

        this.stopped = false;

        driveService = executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (DriveRobotOpMode.this.stopped) {
                    hardware.zeroAll();
                    return;
                }

                // Setup a variable for each drive wheel to save power level for telemetry
                double leftPower;
                double rightPower;
                double hdrivePower;
                double flywheelPower;
                double pickupTopPower = 0;
                double pickupBottomPower = 0;

                double drive = -DriveRobotOpMode.this.gamepad1.right_stick_y;
                double turn = -DriveRobotOpMode.this.gamepad1.left_stick_x;
                //double hdrive = DriveRobotOpMode.this.gamepad1.right_stick_x;
                //absMax(this.gamepad1.left_stick_y, this.gamepad2.left_stick_y);
                //double turn = -absMax(this.gamepad1.right_stick_x, this.gamepad2.right_stick_x);
                leftPower = Range.clip(-(drive + turn), -1.0, 1.0);
                rightPower = Range.clip(-(drive - turn), -1.0, 1.0);
                //hdrivePower = Range.clip(hdrive, -1.0, 1.0);

                if (DriveRobotOpMode.this.gamepad1.dpad_left) {
                    hdrivePower = 1.0;
                } else if (DriveRobotOpMode.this.gamepad1.dpad_right) {
                    hdrivePower = -1.0;
                } else {
                    hdrivePower = 0.0;
                }

                if (DriveRobotOpMode.this.gamepad1.a) {
                    if (!aPress) {
                        aPress = true;
                        if (!flywheelActive) {
                            flywheelActive = true;
                            flywheelReverse = false;

                            hardware.pickupTopMotor.setPower(1.0);
                            hardware.pickupBottomMotor.setPower(-1.0);

                            //pickupTopPower = 0.75;
                            //pickupBottomPower = 0.75;
                        } else {
                            flywheelActive = false;
                            flywheelReverse = false;

                            hardware.pickupTopMotor.setPower(0);
                            hardware.pickupBottomMotor.setPower(0);

                            //pickupTopPower = 0;
                            //pickupBottomPower = 0;
                        }
                    }
                } else {
                    aPress = false;
                }

                /*if (DriveRobotOpMode.this.gamepad1.b) {
                    if(!bPress) {
                        bPress = true;
                        if (!flywheelReverse) {
                            flywheelReverse = true;
                            flywheelActive = false;

                            pickupTopPower = -0.75;
                            pickupBottomPower = -0.75;
                        } else {
                            flywheelReverse = false;
                            flywheelActive = false;

                            pickupTopPower = 0;
                            pickupBottomPower = 0;
                        }
                    }
                } else {
                    bPress = false;
                }*/

                if (DriveRobotOpMode.this.gamepad1.right_bumper) {
                    flywheelPower = Math.max(prevFlywheelPower - 0.05, -1.0);
                } else {
                    flywheelPower = 0;
                }

                if (DriveRobotOpMode.this.gamepad1.left_bumper) {
                    hardware.pushServo.setPosition(0.0);
                } else {
                    hardware.pushServo.setPosition(0.35);
                }

                if (DriveRobotOpMode.this.gamepad1.dpad_up && !dPadUpDownPressed) {
                    if (armStep < 4) {
                        armStep++;
                    }
                    dPadUpDownPressed = true;
                    //hardware.armUpServo.setPosition(Math.max(0.0, hardware.armUpServo.getPosition() - 0.01));
                } else if (DriveRobotOpMode.this.gamepad1.dpad_down && !dPadUpDownPressed) {
                    if (armStep > 0) {
                        armStep --;
                    }
                    dPadUpDownPressed = true;
                    //hardware.armUpServo.setPosition(Math.min(1.0, hardware.armUpServo.getPosition() + 0.01));
                } else {
                    dPadUpDownPressed = false;
                }

                switch (armStep) {
                    case 0:
                        hardware.armUpServo.setPosition(0.72);
                        break;
                    case 1:
                        hardware.armUpServo.setPosition(0.4);
                        break;
                    case 3:
                        hardware.armUpServo.setPosition(0.2);
                        break;
                    case 4:
                        hardware.armUpServo.setPosition(0);
                        break;
                }

                if (DriveRobotOpMode.this.gamepad1.x) {
                    hardware.armGrabServo.setPosition(Math.max(0.4, hardware.armGrabServo.getPosition() - 0.01));
                } else {
                    hardware.armGrabServo.setPosition(Math.min(1.0, hardware.armGrabServo.getPosition() + 0.01));
                }

                telemetry.addData("armUpServo", hardware.armUpServo.getPosition());
                telemetry.addData("armGrabServo", hardware.armGrabServo.getPosition());
                telemetry.addData("pushServo", hardware.pushServo.getPosition());

                telemetry.addData("leftpower", leftPower * leftPower);
                telemetry.addData("rightpower", rightPower * rightPower);
                telemetry.addData("hrdivepower", hdrivePower * hdrivePower);
                telemetry.addData("pickupTopPower", pickupTopPower);
                telemetry.addData("pickupBottomPower", pickupBottomPower);
                telemetry.addData("flywheelPower", flywheelPower);

                telemetry.update();

                if(prevLeftPower != leftPower) {
                    hardware.leftMotor.setPower(leftPower);
                    prevLeftPower = leftPower;
                }
                if(prevRightPower != rightPower) {
                    hardware.rightMotor.setPower(rightPower);
                    prevRightPower = rightPower;
                }
                if(prevHDrivePower != hdrivePower) {
                    hardware.hDriveMotor.setPower(hdrivePower);
                    prevHDrivePower = hdrivePower;
                }
                /*if(prevPickupTopPower != pickupTopPower) {
                    hardware.pickupTopMotor.setPower(pickupTopPower);
                    prevPickupTopPower = pickupTopPower;
                }
                if(prevPickupBottomPower != pickupBottomPower) {
                    hardware.pickupBottomMotor.setPower(pickupBottomPower);
                    prevPickupBottomPower = pickupBottomPower;
                }*/
                if(prevFlywheelPower != flywheelPower) {
                    hardware.flywheelMotor.setPower(flywheelPower);
                    prevFlywheelPower = flywheelPower;
                }
            }
        }, 0, 20, TimeUnit.MILLISECONDS);
    }

    private float absMax(float n1, float n2) {
        if(Math.abs(n1) > Math.abs(n2)) {
            return n1;
        }
        return n2;
    }

    @Override
    public void loop() {

    }

    @Override
    public void stop() {
        super.stop();

        this.stopped = true;

        this.hardware.zeroAll();

        /*if (this.driveService != null) {
                this.driveService.cancel(true);
                this.executorService.shutdownNow();
                this.executorService = Executors.newSingleThreadScheduledExecutor();
        }*/

        if (this.driveService != null) {
            this.driveService.cancel(true);
        }
    }
}
