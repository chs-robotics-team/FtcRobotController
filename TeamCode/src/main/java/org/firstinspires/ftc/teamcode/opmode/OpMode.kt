package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import kotlin.math.abs

private const val DRIVE_SPEED = 0.4
private const val SLIDE_SPEED = 0.075

@TeleOp(name = "OpMode")
@Suppress("unused")
class OpMode : OpMode() {
    private lateinit var frontLeftMotor: DcMotor
    private lateinit var frontRightMotor: DcMotor
    private lateinit var backLeftMotor: DcMotor
    private lateinit var backRightMotor: DcMotor
    private lateinit var leftSlideMotor: DcMotor
    private lateinit var rightSlideMotor: DcMotor
    private lateinit var clawMotor: DcMotor
    private lateinit var clawServo: Servo

    override fun init() {
        frontLeftMotor = hardwareMap.dcMotor.get("flMotor")
        frontRightMotor = hardwareMap.dcMotor.get("frMotor")
        backLeftMotor = hardwareMap.dcMotor.get("blMotor")
        backRightMotor = hardwareMap.dcMotor.get("brMotor")
        leftSlideMotor = hardwareMap.dcMotor.get("lSlide")
        rightSlideMotor = hardwareMap.dcMotor.get("rSlide")
        clawMotor = hardwareMap.dcMotor.get("clawArm")
        clawServo = hardwareMap.servo.get("claw")

        telemetry.addData("[BOT]", "Initialized Motors")
    }

    override fun start() {
        telemetry.addData("[BOT]", "Started")
    }

    override fun loop() {
        // https://gm0.org/en/latest/docs/software/tutorials/mecanum-drive.html
        val leftY = -gamepad1.left_stick_y.toDouble()
        val leftX = gamepad1.left_stick_x.toDouble()
        val rightX = gamepad1.right_stick_x.toDouble()
        val (dpadUp, dpadDown) = gamepad1.dpad_up to gamepad1.dpad_down

        // Largest motor power; ensures all powers maintain same ratio when one is outside of [-1, 1]
        val denominator = (abs(leftY) + abs(leftX) + abs(rightX)).coerceAtLeast(1.0)

        val frontLeftPower = (leftY + leftX + rightX) / denominator * DRIVE_SPEED
        val frontRightPower = (leftY - leftX - rightX) / denominator * DRIVE_SPEED
        val backLeftPower = (leftY - leftX + rightX) / denominator * DRIVE_SPEED
        val backRightPower = (leftY + leftX - rightX) / denominator * DRIVE_SPEED

        frontLeftMotor.power = frontLeftPower
        frontRightMotor.power = frontRightPower
        backLeftMotor.power = backLeftPower
        backRightMotor.power = backRightPower

        val slidePower = when {
            dpadUp -> SLIDE_SPEED
            dpadDown -> -SLIDE_SPEED
            else -> 0.0
        }

        leftSlideMotor.power = slidePower
        rightSlideMotor.power = slidePower
    }
}