package me.walkerknapp.tabbultimategoal;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import me.walkerknapp.tabbultimategoal.hardware.MecanumSquare;
import me.walkerknapp.tabbultimategoal.hardware.WobblySquared;

@TeleOp
public class NewMecanumDriveOpMode extends LinearOpMode {

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

    @Override
    public void runOpMode() throws InterruptedException {
        WobblySquared robot = new WobblySquared();
        WobblySquared hardware = robot;
        robot.init(hardwareMap);

        robot.frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, this is reversed!
            double x = -gamepad1.right_stick_x * 1.1; // Counteract imperfect strafing
            double rx = -gamepad1.left_stick_x;

            double frontLeftPower = y + x + rx;
            double backLeftPower = y - x + rx;
            double frontRightPower = y - x - rx;
            double backRightPower = y + x - rx;

            double flywheelPower;
            double pickupTopPower = 0;
            double pickupBottomPower = 0;

            // Put powers in the range of -1 to 1 only if they aren't already
            if (Math.abs(frontLeftPower) > 1 || Math.abs(backLeftPower) > 1 ||
                    Math.abs(frontRightPower) > 1 || Math.abs(backRightPower) > 1) {

                // Find the largest power
                double max;
                max = Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower));
                max = Math.max(Math.abs(frontRightPower), max);
                max = Math.max(Math.abs(backRightPower), max);

                frontLeftPower /= max;
                backLeftPower /= max;
                frontRightPower /= max;
                backRightPower /= max;
            }

            robot.frontLeftMotor.setPower(frontLeftPower);
            robot.backLeftMotor.setPower(backLeftPower);
            robot.frontRightMotor.setPower(frontRightPower);
            robot.backRightMotor.setPower(backRightPower);
            
            // Not drive 10head

            if (this.gamepad1.a) {
                if (!aPress) {
                    aPress = true;
                    if (!flywheelActive) {
                        flywheelActive = true;
                        flywheelReverse = false;

                        hardware.pickupTopMotor.setPower(1.0);
                        hardware.pickupBottomMotor.setPower(1.0);

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

                /*if (this.gamepad1.b) {
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

            if (this.gamepad1.right_bumper) {
                flywheelPower = Math.max(prevFlywheelPower - 0.05, -0.60);
            } else {
                flywheelPower = 0;
            }

            if (this.gamepad1.left_bumper) {
                hardware.pushServo.setPosition(0.0);
            } else {
                hardware.pushServo.setPosition(0.35);
            }

            if (this.gamepad1.dpad_up && !dPadUpDownPressed) {
                if (armStep < 4) {
                    armStep++;
                }
                dPadUpDownPressed = true;
                //hardware.armUpServo.setPosition(Math.max(0.0, hardware.armUpServo.getPosition() - 0.01));
            } else if (this.gamepad1.dpad_down && !dPadUpDownPressed) {
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

            if (this.gamepad1.x) {
                hardware.armGrabServo.setPosition(Math.max(0.4, hardware.armGrabServo.getPosition() - 0.01));
            } else {
                hardware.armGrabServo.setPosition(Math.min(1.0, hardware.armGrabServo.getPosition() + 0.01));
            }

            telemetry.addData("armUpServo", hardware.armUpServo.getPosition());
            telemetry.addData("armGrabServo", hardware.armGrabServo.getPosition());
            telemetry.addData("pushServo", hardware.pushServo.getPosition());

            telemetry.addData("pickupTopPower", pickupTopPower);
            telemetry.addData("pickupBottomPower", pickupBottomPower);
            telemetry.addData("flywheelPower", flywheelPower);

            telemetry.update();

            if(prevFlywheelPower != flywheelPower) {
                hardware.flywheelMotor.setPower(flywheelPower);
                prevFlywheelPower = flywheelPower;
            }
        }
    }
}
