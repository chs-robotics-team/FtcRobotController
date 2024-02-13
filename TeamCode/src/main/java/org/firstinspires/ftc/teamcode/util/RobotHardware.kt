package org.firstinspires.ftc.teamcode.util

import com.arcrobotics.ftclib.drivebase.MecanumDrive
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.arcrobotics.ftclib.gamepad.ToggleButtonReader
import com.arcrobotics.ftclib.hardware.SimpleServo
import com.arcrobotics.ftclib.hardware.motors.Motor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs

object Constants {
    object Arm {
        const val SPEED = 0.05
        const val UP_POS = 90
        const val TICK_INCREMENT = 5

        // The max position of the arm so it doesn't hit the back acrylic plate
        // TODO: implement this
        const val MAX_POSITION = -420
    }

    object DriveTrain {
        const val DRIVE_SPEED = 0.4
    }

    object Claw {
        const val OPEN_POS = 0.0
        const val CLOSE_POS = 90.0

        // Position when arm is placing pixel on the board
        const val WRIST_UP_POS = 90.0

        // Position when arm is on the ground
        const val WRIST_DOWN_POS = 0.0
    }

//    object Slide {
//        const val SPEED = 0.075
//        const val MAX_POSITION = 180.0
//        const val MIN_POSITION = 0.0
//    }
}

// TODO: finish this when we get slide motor connectors
//data class Slide(val hardware: RobotHardware) {
//    private val (left, right) = hardware.leftSlide to hardware.rightSlide
//    private val motors = listOf(left, right)
//
//    init {
//        motors.forEach { it.setRunMode(Motor.RunMode.RawPower) }
//    }
//
//    fun move() {
//        val (dpadUp, dpadDown) = gamepad.getButton(GamepadKeys.Button.DPAD_UP) to hardware.gamepad.getButton(
//            GamepadKeys.Button.DPAD_DOWN
//        )
//
//        val avgSlidePos = (left.currentPosition + right.currentPosition) / 2.0
//
//        val power = when {
//            dpadUp && avgSlidePos < Constants.Slide.MAX_POSITION -> Constants.Slide.SPEED
//            dpadDown && avgSlidePos > Constants.Slide.MIN_POSITION -> -Constants.Slide.SPEED
//            else -> 0.0
//        }
//
//        motors.forEach { it.set(power) }
//    }
//}

class ClawArm(val hardware: RobotHardware) {
    init {
        hardware.armMotor.setRunMode(Motor.RunMode.PositionControl)
        hardware.armMotor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE)

        hardware.clawServo.position = Constants.Claw.OPEN_POS
        hardware.wristServo.position = Constants.Claw.WRIST_DOWN_POS
    }

    fun move(telemetry: Telemetry) {
//        hardware.gamepad.readButtons()
        val angleModifier = when {
            hardware.gamepad.getButton(GamepadKeys.Button.LEFT_BUMPER) -> Constants.Arm.TICK_INCREMENT
            hardware.gamepad.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.5 -> -Constants.Arm.TICK_INCREMENT
            // This should hold the arm in place when triggers/bumpers are released
            else -> return hardware.armMotor.set(0.0)
        }

        val encoderVal = hardware.armMotor.encoder.position
        val angle = encoderVal + angleModifier

        // TODO: Wrist position based on encoder of arm (if arm is up, wrist is up)
        val wristPosition = when {
            hardware.armMotor.currentPosition < Constants.Arm.UP_POS -> Constants.Claw.WRIST_DOWN_POS
            else -> Constants.Claw.WRIST_UP_POS
        }

        telemetry.addData("Encoder Val", encoderVal)
        telemetry.addData("New Angle", angle)

        hardware.armMotor.setTargetPosition(angle)
        hardware.armMotor.set(Constants.Arm.SPEED)
        hardware.wristServo.position = wristPosition
    }
}

// Alternative for FTCLib MecanumDrive if we want to use our own
data class DriveTrain(
    val frontLeft: Motor,
    val frontRight: Motor,
    val backLeft: Motor,
    val backRight: Motor,
    val gamepad: GamepadEx,
    val speed: Double = Constants.DriveTrain.DRIVE_SPEED,
) {
    private val motors = listOf(frontLeft, frontRight, backLeft, backRight)

    init {
        motors.forEach { it.setRunMode(Motor.RunMode.RawPower) }
    }

    fun drive() {
        val (leftX, rightX, leftY) = Triple(-gamepad.leftX, -gamepad.leftY, -gamepad.rightX)

        // Maximum value of the joystick inputs; keeps motors ∈ [-1, 1]
        val denominator = (abs(leftX) + abs(leftY) + abs(rightX)).coerceAtLeast(1.0)

        frontLeft.set((leftY + leftX + rightX) / denominator * speed)
        frontRight.set((leftY - leftX - rightX) / denominator * speed)
        backLeft.set((leftY - leftX + rightX) / denominator * speed)
        backRight.set((leftY + leftX - rightX) / denominator * speed)
    }
}

class RobotHardware(val hardwareMap: HardwareMap, val gamepad: GamepadEx) {
    private val leftFrontMotor = Motor(hardwareMap, "flMotor", Motor.GoBILDA.RPM_435)
    private val rightFrontMotor = Motor(hardwareMap, "frMotor", Motor.GoBILDA.RPM_435)
    private val leftBackMotor = Motor(hardwareMap, "blMotor", Motor.GoBILDA.RPM_435)
    private val rightBackMotor = Motor(hardwareMap, "brMotor", Motor.GoBILDA.RPM_435)

    val armMotor = Motor(hardwareMap, "armMotor", Motor.GoBILDA.RPM_435)
    val clawServo = SimpleServo(
        hardwareMap, "clawServo", Constants.Claw.WRIST_UP_POS, Constants.Claw.WRIST_DOWN_POS
    )
    val wristServo = SimpleServo(
        hardwareMap, "pivotServo", Constants.Claw.OPEN_POS, Constants.Claw.CLOSE_POS
    )

    val mecanumDrive = MecanumDrive(leftFrontMotor, rightFrontMotor, leftBackMotor, rightBackMotor)
    val driveTrain =
        DriveTrain(leftFrontMotor, rightFrontMotor, leftBackMotor, rightBackMotor, gamepad)
    val clawArm = ClawArm(this)
//    val slide = Slide(this)

    val toggleA = ToggleButtonReader(gamepad, GamepadKeys.Button.A)
    val toggleB = ToggleButtonReader(gamepad, GamepadKeys.Button.B)

    init {
        armMotor.setRunMode(Motor.RunMode.PositionControl)
        armMotor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE)
    }
}