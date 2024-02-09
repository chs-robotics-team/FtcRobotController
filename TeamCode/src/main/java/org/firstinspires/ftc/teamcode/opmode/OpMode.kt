package org.firstinspires.ftc.teamcode.opmode

import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.util.Constants
import org.firstinspires.ftc.teamcode.util.RobotHardware

@TeleOp(name = "OpMode")
@Suppress("unused")
class OpMode : OpMode() {
    private lateinit var gamepad: GamepadEx
    private lateinit var hardware: RobotHardware

    override fun init() {
        gamepad = GamepadEx(gamepad1)
        hardware = RobotHardware(hardwareMap, gamepad)

        telemetry.addData("[BOT]", "Initialized Hardware & Gamepad")
    }

    override fun start() {
        telemetry.addData("[BOT]", "Started OpMode")
    }

    override fun loop() {
        hardware.toggleA.readValue()
        telemetry.addData("A", hardware.toggleA.state)
        hardware.toggleB.readValue()
        telemetry.addData("B", hardware.toggleB.state)
        telemetry.addData("Bumper", gamepad.isDown(GamepadKeys.Button.RIGHT_BUMPER))
        telemetry.addData("Encoder", hardware.armMotor.encoder.position)

        hardware.mecanumDrive.driveRobotCentric(
            gamepad.leftX * Constants.DriveTrain.DRIVE_SPEED,
            gamepad.leftY * Constants.DriveTrain.DRIVE_SPEED,
            -gamepad.rightX * Constants.DriveTrain.DRIVE_SPEED,
        )

        hardware.clawArm.move()
//        hardware.slide.move()
        gamepad.readButtons()
    }
}
