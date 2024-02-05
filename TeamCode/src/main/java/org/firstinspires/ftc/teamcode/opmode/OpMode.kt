package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import kotlin.math.abs

private const val DRIVE_SPEED = 0.4

@TeleOp(name = "OpMode")
@Suppress("unused")
class OpMode : OpMode() {
    private lateinit var frontLeft: DcMotor
    private lateinit var frontRight: DcMotor
    private lateinit var backLeft: DcMotor
    private lateinit var backRight: DcMotor
    private lateinit var leftSlide: DcMotor
    private lateinit var rightSlide: DcMotor
    private lateinit var clawArm: DcMotor
    private lateinit var claw: Servo

    override fun init() {
        frontLeft = hardwareMap.dcMotor.get("flMotor")
        frontRight = hardwareMap.dcMotor.get("frMotor")
        backLeft = hardwareMap.dcMotor.get("blMotor")
        backRight = hardwareMap.dcMotor.get("brMotor")
        leftSlide = hardwareMap.dcMotor.get("lSlide")
        rightSlide = hardwareMap.dcMotor.get("rSlide")
        clawArm = hardwareMap.dcMotor.get("clawArm")
        claw = hardwareMap.servo.get("claw")

        telemetry.addData("[BOT]", "Initialized Motors")
    }

    override fun start() {
        telemetry.addData("[BOT]", "Started")
    }

    override fun loop() {
        val leftY = -gamepad1.left_stick_y.toDouble()
        val leftX = gamepad1.left_stick_x.toDouble()
        val rightX = gamepad1.right_stick_x.toDouble()

        val denominator = listOf(abs(leftY), abs(leftX), abs(rightX), abs(rightX)).maxOrNull() ?: 1.0
        val frontLeftPower = (leftY + leftX + rightX) / denominator * DRIVE_SPEED
        val frontRightPower = (leftY - leftX - rightX) / denominator * DRIVE_SPEED
        val backLeftPower = (leftY - leftX + rightX) / denominator * DRIVE_SPEED
        val backRightPower = (leftY + leftX - rightX) / denominator * DRIVE_SPEED

        frontLeft.power = frontLeftPower
        frontRight.power = frontRightPower
        backLeft.power = backLeftPower
        backRight.power = backRightPower
    }
}