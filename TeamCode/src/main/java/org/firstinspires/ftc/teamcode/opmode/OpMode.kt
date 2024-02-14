package org.firstinspires.ftc.teamcode.opmode

import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.util.Constants
import org.firstinspires.ftc.teamcode.util.RobotHardware
import org.firstinspires.ftc.teamcode.util.TelemetryLogger

lateinit var logger: TelemetryLogger

@TeleOp(name = "OpMode")
@Suppress("unused")
class OpMode : OpMode() {
    private lateinit var gamepad: GamepadEx
    private lateinit var hardware: RobotHardware

    override fun init() {
        gamepad = GamepadEx(gamepad1)
        hardware = RobotHardware(hardwareMap, gamepad)
        logger = TelemetryLogger(telemetry)

        logger.info("Initialized OpMode")
    }

    override fun start() {
        telemetry.addData("[BOT]", "Started OpMode")
    }

    override fun loop() {
        hardware.toggleA.readValue()
        logger.debug("A: ${hardware.toggleA.state}")
        hardware.toggleB.readValue()
        logger.debug("B: ${hardware.toggleB.state}")

        hardware.mecanumDrive.driveRobotCentric(
            -gamepad.leftX * Constants.DriveTrain.DRIVE_SPEED,
            -gamepad.leftY * Constants.DriveTrain.DRIVE_SPEED,
            -gamepad.rightX * Constants.DriveTrain.DRIVE_SPEED,
        )

        hardware.clawArm.move()
        hardware.slide.move()
        gamepad.readButtons()
    }
}