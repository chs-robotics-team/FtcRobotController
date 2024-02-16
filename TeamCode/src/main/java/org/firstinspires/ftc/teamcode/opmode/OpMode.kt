package org.firstinspires.ftc.teamcode.opmode

import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.eventloop.opmode.OpMode
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
        logger = TelemetryLogger(telemetry)

        logger.info("Initialized OpMode")
    }

    override fun start() {
        hardware = RobotHardware(hardwareMap, gamepad)
        logger.info("Started OpMode")
    }

    override fun loop() {
        hardware.driveTrain.drive()
        hardware.clawArm.move()
        hardware.slide.move()
        gamepad.readButtons()
    }
}