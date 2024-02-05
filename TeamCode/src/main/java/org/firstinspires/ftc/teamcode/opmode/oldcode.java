//package org.firstinspires.ftc.teamcode.opmode;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//import org.firstinspires.ftc.robotcore.external.JavaUtil;
//@TeleOp(name = "TedOpMode (OnBotJava)")
//public class TedOpMode extends LinearOpMode {
//private DcMotor flMotor;
//private DcMotor blMotor;
//private DcMotor frMotor;
//private DcMotor brMotor;
//private DcMotor lSlide;
//private DcMotor rSlide;
//private Servo lServo;
//private Servo rServo;
///**
//* This function is executed when this Op Mode is selected from the Driver Station.
//*/
//@Override
//public void runOpMode() {
//double s;
//double slidespeed;
//float y;
//float x;
//double rx;
//double denominator;
//double lOpenClaw = .35;
//double lClosedClaw = .5;
//double rOpenClaw = .2;
//double rClosedClaw = .5;
//long speedToggleInputTime = 0;
//long slideSpeedToggleInputTime = 0;
//int avgSlidePos = 1000;
//double flPower;
//double blPower;
//double frPower;
//double brPower;
//double powerSum;
//
//flMotor = hardwareMap.get(DcMotor.class, "flMotor");
//blMotor = hardwareMap.get(DcMotor.class, "blMotor");
//frMotor = hardwareMap.get(DcMotor.class, "frMotor");
//brMotor = hardwareMap.get(DcMotor.class, "brMotor");
//
//lSlide = hardwareMap.get(DcMotor.class, "leftSlide");
//rSlide = hardwareMap.get(DcMotor.class, "rightSlide");
//lServo = hardwareMap.get(Servo.class, "leftServo");
//rServo = hardwareMap.get(Servo.class, "rightServo");
//waitForStart();
//if (opModeIsActive()) {
//// Reverse the right side motors
//// Reverse left motors if you are using NeveRests
//flMotor.setDirection(DcMotorSimple.Direction.REVERSE);
//blMotor.setDirection(DcMotorSimple.Direction.REVERSE);
//
//rSlide.setDirection(DcMotorSimple.Direction.REVERSE);
//lServo.setPosition(lOpenClaw);
//rServo.setPosition(rOpenClaw);
//
//s = .4;
//slidespeed = 1;
//while (opModeIsActive()) {
//avgSlidePos = (rSlide.getCurrentPosition() + lSlide.getCurrentPosition()) / 2;
//// Remember, this is reversed!
//y = -gamepad1.left_stick_y;
//x = gamepad1.left_stick_x;
//// Counteract imperfect strafing
//rx = gamepad1.right_stick_x * 1.1;
//// Denominator is the largest motor power
//// (absolute value) or 1.
//// This ensures all the powers maintain
//// the same ratio, but only when at least one is
//// out of the range [-1, 1].
//denominator =
//
//JavaUtil.maxOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(y),
//Math.abs(x), Math.abs(rx))), 1));
//
//// Make sure your ID's match your configuration
//flPower = ((y + x + rx) / denominator) * s;
//blPower = (((y - x) + rx) / denominator) * s;
//frPower = (((y - x) - rx) / denominator) * s;
//brPower = (((y + x) - rx) / denominator) * s;
//powerSum = flPower + blPower + frPower + brPower;
//// if (avgSlidePos > 2000 && (powerSum/4) > 0) {
//// flPower *= .5;
//// blPower *= .5;
//// frPower *= .5;
//// brPower *= .5;
//// }
//flMotor.setPower(flPower);
//blMotor.setPower(blPower);
//
//frMotor.setPower(frPower);
//brMotor.setPower(brPower);
//if (gamepad1.right_bumper) {
//if ((System.currentTimeMillis() - speedToggleInputTime) > 750) {
//if (s == 1) {
//s = 0.4;
//} else {
//s = 1;
//}
//speedToggleInputTime = System.currentTimeMillis();
//}
//}
//if (gamepad1.left_bumper) {
//if ((System.currentTimeMillis() - slideSpeedToggleInputTime) > 750) {
//if (slidespeed == 1) {
//slidespeed = 0.75;
//} else {
//slidespeed = 1;
//}
//slideSpeedToggleInputTime = System.currentTimeMillis();
//}
//}
//if (gamepad1.left_trigger > .4) { // open both sides of claw
//rServo.setPosition(rOpenClaw);
//lServo.setPosition(lOpenClaw);
//}
//if (gamepad1.right_trigger > .4) { // close both sides of claw
//rServo.setPosition(rClosedClaw);
//lServo.setPosition(lClosedClaw);
//}
//if (gamepad1.a && gamepad1.b) {
//lSlide.setPower(0.075);
//rSlide.setPower(0.075);
//} else if (gamepad1.a && avgSlidePos < 4100){
//lSlide.setPower(slidespeed);
//rSlide.setPower(slidespeed);
//} else if (gamepad1.b && avgSlidePos > 30) {
//lSlide.setPower(-slidespeed);
//rSlide.setPower(-slidespeed);
//} else {
//lSlide.setPower(0.075);
//rSlide.setPower(0.075);
//}
//telemetry.addData("left slide encoder", lSlide.getCurrentPosition());
//telemetry.addData("right slide encoder", rSlide.getCurrentPosition());
//telemetry.addData("average slide encoder", avgSlidePos);
//telemetry.addData("Move Speed", (s == 1 ? "fast" : "slow"));
//telemetry.addData("Slide Speed", slidespeed);
//// telemetry.addData("fl", String.format("%.2f", flPower));
//// telemetry.addData("bl", String.format("%.2f", blPower));
//// telemetry.addData("fr", String.format("%.2f", frPower));
//// telemetry.addData("br", String.format("%.2f", brPower));
//telemetry.update();
//}
//}
//}
//
//}