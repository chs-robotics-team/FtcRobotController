package org.firstinspires.ftc.teamcode.util

import com.arcrobotics.ftclib.drivebase.MecanumDrive
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.arcrobotics.ftclib.gamepad.ToggleButtonReader
import com.arcrobotics.ftclib.hardware.SimpleServo
import com.arcrobotics.ftclib.hardware.motors.Motor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.opmode.logger

object Constants {
    object Arm {
        const val SPEED = 0.15
        const val UP_POS = -200
        const val TICK_INCREMENT = 4

        // The max position of the arm so it doesn't hit the back acrylic plate
        const val MAX_POSITION = -380

        // When to stop trying to counteract gravity (should be roughly near the top of the turn)
        val GRAVITY_THRESHOLD = -180..-100
    }

    object DriveTrain {
        const val DRIVE_SPEED = 0.4
    }

    object Claw {
        const val OPEN_POS = 360.0
        const val CLOSE_POS = 80.0

        // Position when arm is placing pixel on the board
        const val WRIST_UP_POS = 90.0

        // Position when arm is on the ground
        const val WRIST_DOWN_POS = 0.0
    }

    object Slide {
        const val SPEED = 0.4
        const val MAX_POSITION = 10500
    }
}

data class Slide(val hardware: RobotHardware) {
    private val initialEncoderVal = hardware.leftSlide.currentPosition

    init {
        // So positive numbers move the slide up instead of down
        hardware.leftSlide.inverted = true
        hardware.leftSlide.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE)
//        hardware.leftSlide.setRunMode(Motor.RunMode.PositionControl)
    }

    fun move() {
        val slidePos = hardware.leftSlide.currentPosition
        val displacement = slidePos - initialEncoderVal

        val leftPower = when {
            displacement <= Constants.Slide.MAX_POSITION && hardware.gamepad.getButton(GamepadKeys.Button.DPAD_UP) -> Constants.Slide.SPEED
            hardware.gamepad.getButton(GamepadKeys.Button.DPAD_DOWN) -> -Constants.Slide.SPEED
            else -> 0.0
        }

        logger.debug("Power: $leftPower")
        logger.debug("Slide Pos: $slidePos")
        logger.debug("Initial Slide: $initialEncoderVal")
        logger.debug("Displacement: ${slidePos - initialEncoderVal}")

        hardware.leftSlide.set(leftPower)
    }
}

class ClawArm(val hardware: RobotHardware) {
    private val initialEncoderVal = hardware.armMotor.currentPosition

    init {
        hardware.armMotor.setRunMode(Motor.RunMode.PositionControl)
        hardware.armMotor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE)

        hardware.clawServo.position = Constants.Claw.OPEN_POS
        hardware.wristServo.position = Constants.Claw.WRIST_DOWN_POS
    }

    private fun minRotation(initial: Int) = initial + Constants.Arm.MAX_POSITION
    private fun wristThreshold(initial: Int) = initial + Constants.Arm.UP_POS
    private fun gravityThreshold(initial: Int) =
        Constants.Arm.GRAVITY_THRESHOLD.first + initial..Constants.Arm.GRAVITY_THRESHOLD.last + initial

    fun move() {
        val encoderVal = hardware.armMotor.currentPosition

        val adjustGravity = gravityThreshold(initialEncoderVal).contains(encoderVal)
        val gravityAdjustment = if (adjustGravity) -2 else 0

        val angleModifier = when {
            hardware.gamepad.getButton(GamepadKeys.Button.LEFT_BUMPER) -> Constants.Arm.TICK_INCREMENT + gravityAdjustment
            hardware.gamepad.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.5 -> -Constants.Arm.TICK_INCREMENT
            // Counteract gravity, perchance?
            else -> gravityAdjustment
        }

        // coerceAtLeast keeps minimum at MAX_POSITION; stops arm from going too far back
        val targetPosition =
            (encoderVal + angleModifier).coerceAtLeast(minRotation(initialEncoderVal))

        // TODO: Wrist position based on encoder of arm (if arm is up, wrist is up)
        val currentWrist = hardware.wristServo.position
        val wristPosition = when {
            gravityThreshold(initialEncoderVal).first > hardware.armMotor.currentPosition -> 15.0
            else -> Constants.Claw.WRIST_DOWN_POS
        }

        val targetWrist = currentWrist + wristPosition

        hardware.toggleA.readValue()
        val clawAngle =
            if (hardware.toggleA.state) Constants.Claw.OPEN_POS else Constants.Claw.CLOSE_POS
        hardware.clawServo.turnToAngle(clawAngle)

        logger.debug("Encoder Val: $encoderVal")
        logger.debug("Initial: $initialEncoderVal")
        logger.debug("Target Position: $targetPosition")
        logger.debug("Wrist Position: ${hardware.wristServo.angle to targetWrist} | ${hardware.wristServo.angleRange}")
        logger.debug("Claw Position: ${hardware.clawServo.angle to clawAngle}")

        hardware.armMotor.setTargetPosition(targetPosition)
        hardware.armMotor.set(Constants.Arm.SPEED)
        hardware.wristServo.turnToAngle(targetWrist)
    }
}

// Alternative for FTCLib MecanumDrive if we want to use our own
//data class DriveTrain(
//    val frontLeft: Motor,
//    val frontRight: Motor,
//    val backLeft: Motor,
//    val backRight: Motor,
//    val gamepad: GamepadEx,
//    val speed: Double = Constants.DriveTrain.DRIVE_SPEED,
//) {
//    private val motors = listOf(frontLeft, frontRight, backLeft, backRight)
//
//    init {
//        motors.forEach { it.setRunMode(Motor.RunMode.RawPower) }
//    }
//
//    fun drive() {
//        val (leftX, rightX, leftY) = Triple(-gamepad.leftX, -gamepad.leftY, -gamepad.rightX)
//
//        // Maximum value of the joystick inputs; keeps motors ∈ [-1, 1]
//        val denominator = (abs(leftX) + abs(leftY) + abs(rightX)).coerceAtLeast(1.0)
//
//        frontLeft.set((leftY + leftX + rightX) / denominator * speed)
//        frontRight.set((leftY - leftX - rightX) / denominator * speed)
//        backLeft.set((leftY - leftX + rightX) / denominator * speed)
//        backRight.set((leftY + leftX - rightX) / denominator * speed)
//    }
//}

class RobotHardware(val hardwareMap: HardwareMap, val gamepad: GamepadEx) {
    private val frontLeftMotor = Motor(hardwareMap, "flMotor", Motor.GoBILDA.RPM_435)
    private val frontRightMotor = Motor(hardwareMap, "frMotor", Motor.GoBILDA.RPM_435)
    private val backLeftMotor = Motor(hardwareMap, "blMotor", Motor.GoBILDA.RPM_435)
    private val backRightMotor = Motor(hardwareMap, "brMotor", Motor.GoBILDA.RPM_435)

    val armMotor = Motor(hardwareMap, "armMotor", Motor.GoBILDA.RPM_435)
    val clawServo = SimpleServo(hardwareMap, "clawServo", 0.0, 360.0)
    val wristServo = SimpleServo(
        hardwareMap, "pivotServo", Constants.Claw.WRIST_DOWN_POS, Constants.Claw.WRIST_UP_POS
    )

    val mecanumDrive = MecanumDrive(frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor)

    //    val driveTrain = DriveTrain(leftFrontMotor, rightFrontMotor, leftBackMotor, rightBackMotor, gamepad)
    val clawArm = ClawArm(this)
//    val slide = Slide(this)

    val toggleA = ToggleButtonReader(gamepad, GamepadKeys.Button.A)
    val toggleB = ToggleButtonReader(gamepad, GamepadKeys.Button.B)

    val leftSlide = Motor(hardwareMap, "lSlide", Motor.GoBILDA.RPM_435)

    //    val rightSlide = Motor(hardwareMap, "rSlide", Motor.GoBILDA.RPM_435)
    val slide = Slide(this)
}