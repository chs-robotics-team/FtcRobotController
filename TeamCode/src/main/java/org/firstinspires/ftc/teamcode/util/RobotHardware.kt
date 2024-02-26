package org.firstinspires.ftc.teamcode.util

import com.arcrobotics.ftclib.drivebase.MecanumDrive
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.arcrobotics.ftclib.gamepad.ToggleButtonReader
import com.arcrobotics.ftclib.hardware.SimpleServo
import com.arcrobotics.ftclib.hardware.motors.Motor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.opmode.logger

fun IntRange.shift(n: Int) = first + n..last + n

fun ToggleButtonReader.check() = this.readValue().let { this.state }

object Constants {
    object Arm {
        const val UP_SPEED = 0.6
        const val DOWN_SPEED = 0.4

        // The max position of the arm so it doesn't hit the back acrylic plate
        const val MAX_DISPLACEMENT = 400

        // When to stop trying to counteract gravity (should be roughly near the top of the turn)
        val GRAVITY_THRESHOLD = -180..-100
//        val WRIST_THRESHOLD = -500..-250
    }

    object DriveTrain {
        const val LOW_SPEED = 0.4
        const val HIGH_SPEED = 0.8
    }

    object Claw {
        const val OPEN_POS = 360.0
        const val CLOSE_POS = 80.0

        // Position when arm is placing pixel on the board
        const val WRIST_UP_POS = 180.0

        // Position when arm is on the ground
        const val WRIST_DOWN_POS = 30.0
    }

    object Slide {
        const val SPEED = 0.8
    }

    object Drone {
        const val LAUNCHED_POS = 180.0
        const val RETRACTED_POS = 30.0
    }
}

data class Slide(val hardware: RobotHardware) {
    init {
        // So positive numbers move the slide up instead of down
        hardware.leftSlide.inverted = true
        hardware.leftSlide.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE)
    }

    fun move() {
        val slidePower = when {
            hardware.gamepad.getButton(GamepadKeys.Button.LEFT_BUMPER) -> Constants.Slide.SPEED
            hardware.gamepad.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.5 -> -Constants.Slide.SPEED
            else -> 0.0
        }

        logger.debug("Slide Power: $slidePower")
        hardware.leftSlide.set(slidePower)
    }
}

class ClawArm(val hardware: RobotHardware) {
    private val initialEncoderVal = hardware.armMotor.currentPosition

    init {
        hardware.armMotor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE)
        hardware.wristServo.position = Constants.Claw.WRIST_DOWN_POS
        hardware.wristServo.inverted = true
        hardware.droneServo.inverted = true
    }

    private fun gravityThreshold(initial: Int) = Constants.Arm.GRAVITY_THRESHOLD.shift(initial)
//    private fun wristThreshold(initial: Int) = Constants.Arm.WRIST_THRESHOLD.shift(initial)

    fun move() {
        val encoderVal = hardware.armMotor.currentPosition
        val displacement = initialEncoderVal - encoderVal

        val gravityRange = gravityThreshold(initialEncoderVal)
//        val wristRange = wristThreshold(initialEncoderVal)

        val gravityAdjustment = if (gravityRange.contains(encoderVal)) -0.25 else 0.0

        val armSpeed = when {
            displacement <= 100 && !hardware.gamepad.getButton(GamepadKeys.Button.RIGHT_BUMPER) -> 0.0
            hardware.gamepad.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.5 -> Constants.Arm.DOWN_SPEED
            displacement >= Constants.Arm.MAX_DISPLACEMENT -> 0.25
            hardware.gamepad.getButton(GamepadKeys.Button.RIGHT_BUMPER) -> -Constants.Arm.UP_SPEED
            else -> gravityAdjustment
        }

//        val wristAngle = when {
//            wristThreshold(initialEncoderVal).contains(hardware.armMotor.currentPosition) -> Constants.Claw.WRIST_UP_POS
//            else -> Constants.Claw.WRIST_DOWN_POS
//        }

        logger.debug("Arm Position: ${hardware.armMotor.currentPosition} | $displacement")
//        logger.debug("Wrist Threshold: $wristRange | ${wristRange.contains(hardware.armMotor.currentPosition)}")

        val clawAngle =
            if (hardware.toggleClaw.check()) Constants.Claw.OPEN_POS else Constants.Claw.CLOSE_POS

        val wristAngle =
            if (hardware.toggleWrist.check()) Constants.Claw.WRIST_UP_POS else Constants.Claw.WRIST_DOWN_POS

        val droneAngle =
            if (hardware.gamepad.isDown(GamepadKeys.Button.Y)) Constants.Drone.LAUNCHED_POS else Constants.Drone.RETRACTED_POS

        hardware.clawServo.turnToAngle(clawAngle)
        hardware.wristServo.turnToAngle(wristAngle)
        hardware.droneServo.turnToAngle(droneAngle)

        logger.debug("Angle: $wristAngle")
        logger.debug("Encoder Val: $encoderVal")
        logger.debug("Initial: $initialEncoderVal")
        logger.debug("Wrist Position: ${hardware.wristServo.angle to wristAngle}")
        logger.debug("Claw Position: ${hardware.clawServo.angle to clawAngle}")
        logger.debug("Drone Position: ${hardware.droneServo.angle to droneAngle}")

        hardware.armMotor.set(armSpeed)
    }
}

class DriveTrain(private val hardware: RobotHardware) : MecanumDrive(
    hardware.frontLeftMotor,
    hardware.frontRightMotor,
    hardware.backLeftMotor,
    hardware.backRightMotor,
) {
    fun drive(speed: Double? = null) {
        val speedModifier = speed
            ?: if (hardware.toggleFast.check()) Constants.DriveTrain.HIGH_SPEED else Constants.DriveTrain.LOW_SPEED

        val driveSpeed = if (speedModifier == Constants.DriveTrain.HIGH_SPEED) "High" else "Low"
        logger.debug("Drive Speed: $driveSpeed")

        driveRobotCentric(
            -hardware.gamepad.leftX * speedModifier,
            -hardware.gamepad.leftY * speedModifier,
            -hardware.gamepad.rightX * speedModifier,
        )
    }
}

class RobotHardware(val hardwareMap: HardwareMap, val gamepad: GamepadEx) {
    val frontLeftMotor = Motor(hardwareMap, "flMotor", Motor.GoBILDA.RPM_435)
    val frontRightMotor = Motor(hardwareMap, "frMotor", Motor.GoBILDA.RPM_435)
    val backLeftMotor = Motor(hardwareMap, "blMotor", Motor.GoBILDA.RPM_435)
    val backRightMotor = Motor(hardwareMap, "brMotor", Motor.GoBILDA.RPM_435)
    val driveTrain = DriveTrain(this)

    val armMotor = Motor(hardwareMap, "armMotor", Motor.GoBILDA.RPM_435)
    val clawServo = SimpleServo(hardwareMap, "clawServo", 0.0, 360.0)
    val wristServo = SimpleServo(hardwareMap, "pivotServo", 0.0, 420.0)
    val droneServo = SimpleServo(hardwareMap, "droneServo", 0.0, 360.0)
    val clawArm = ClawArm(this)

    val toggleClaw = ToggleButtonReader(gamepad, GamepadKeys.Button.A)
    val toggleWrist = ToggleButtonReader(gamepad, GamepadKeys.Button.B)
    val toggleFast = ToggleButtonReader(gamepad, GamepadKeys.Button.RIGHT_STICK_BUTTON)

    val leftSlide = Motor(hardwareMap, "lSlide", Motor.GoBILDA.RPM_435)
    val slide = Slide(this)
}