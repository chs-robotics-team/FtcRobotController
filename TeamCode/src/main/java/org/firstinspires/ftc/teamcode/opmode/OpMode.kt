package org.firstinspires.ftc.teamcode.opmode

import com.arcrobotics.ftclib.drivebase.MecanumDrive
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.arcrobotics.ftclib.gamepad.ToggleButtonReader
import com.arcrobotics.ftclib.hardware.SimpleServo
import com.arcrobotics.ftclib.hardware.motors.Motor
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.eventloop.opmode.OpMode

private const val ARM_SPEED = 0.5
private const val MAX_ARM_POSITION = 180.0
private const val DRIVE_SPEED = 0.4
private const val SLIDE_SPEED = 0.075
private const val MAX_SLIDE_POS = 180.0
private const val CLAW_OPEN_POS = 0.0
private const val CLAW_CLOSE_POS = 180.0
private const val CLAW_WRIST_UP_POS = 0.0
private const val CLAW_WRIST_DOWN_POS = 180.0

@TeleOp(name = "OpMode")
@Suppress("unused")
class OpMode : OpMode() {
    private lateinit var leftSlideMotor: Motor
    private lateinit var rightSlideMotor: Motor
    private lateinit var clawArm: Motor
    private lateinit var clawWrist: SimpleServo
    private lateinit var clawServo: SimpleServo
    private lateinit var driveTrain: MecanumDrive
    private lateinit var gamepad: GamepadEx
    private lateinit var clawOpen: ToggleButtonReader
    private lateinit var wristBack: ToggleButtonReader

    override fun init() {
        leftSlideMotor = Motor(hardwareMap, "lSlide")
        rightSlideMotor = Motor(hardwareMap, "rSlide")
        clawArm = Motor(hardwareMap, "clawMotor")
        clawWrist = SimpleServo(hardwareMap, "clawWrist", CLAW_WRIST_UP_POS, CLAW_WRIST_DOWN_POS)
        clawServo = SimpleServo(hardwareMap, "clawServo", CLAW_OPEN_POS, CLAW_CLOSE_POS)

        driveTrain = MecanumDrive(
            Motor(hardwareMap, "flMotor"),
            Motor(hardwareMap, "frMotor"),
            Motor(hardwareMap, "blMotor"),
            Motor(hardwareMap, "brMotor")
        )

        gamepad = GamepadEx(gamepad1)
        clawOpen = ToggleButtonReader(gamepad, GamepadKeys.Button.A)
        // TODO: change this to use the rotation of the arm to determine if the wrist is back
        wristBack = ToggleButtonReader(gamepad, GamepadKeys.Button.B)

        telemetry.addData("[BOT]", "Initialized Motors")
    }

    override fun start() {
        telemetry.addData("[BOT]", "Started")
    }

    override fun loop() {
        driveTrain.driveRobotCentric(
            gamepad.leftX * DRIVE_SPEED,
            gamepad.leftY * DRIVE_SPEED,
            gamepad.rightX * DRIVE_SPEED,
            false
        )

        val avgSlidePos = (leftSlideMotor.currentPosition + rightSlideMotor.currentPosition) / 2.0

        val slidePower = if (avgSlidePos < MAX_SLIDE_POS) when {
            gamepad.isDown(GamepadKeys.Button.DPAD_UP) -> SLIDE_SPEED
            gamepad.isDown(GamepadKeys.Button.DPAD_DOWN) -> -SLIDE_SPEED
            else -> 0.0
        } else 0.0

        val armPower = if (clawArm.currentPosition < MAX_ARM_POSITION) when {
            gamepad.isDown(GamepadKeys.Button.LEFT_BUMPER) -> ARM_SPEED
            gamepad.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.4 -> -ARM_SPEED
            else -> 0.0
        } else 0.0

        // TODO: change this to use the rotation of the arm to determine the wrist position
        val wristPosition = if (wristBack.state) CLAW_WRIST_DOWN_POS else CLAW_WRIST_UP_POS
        val clawPosition = if (clawOpen.state) CLAW_OPEN_POS else CLAW_CLOSE_POS

        leftSlideMotor.set(slidePower)
        rightSlideMotor.set(slidePower)
        clawArm.set(armPower)
        clawServo.position = clawPosition
        clawWrist.position = wristPosition
    }
}