
package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range

@Autonomous(name = "TestAuto")
class TestAuto : OpMode() {
    private val runtime = ElapsedTime()
    private lateinit var leftDrive: DcMotor
    private lateinit var rightDrive: DcMotor

    override fun init() {
        leftDrive = hardwareMap.get(DcMotor::class.java, "left_drive")
        rightDrive = hardwareMap.get(DcMotor::class.java, "right_drive")

        leftDrive.direction = DcMotorSimple.Direction.REVERSE
        rightDrive.direction = DcMotorSimple.Direction.FORWARD

        telemetry.addData("[BOT]", "Initialized")
        telemetry.update()

        leftDrive.power = 1.0
        rightDrive.power = 1.0
    }

    override fun start() {
        runtime.reset()
    }

    override fun loop() {
        // Setup a variable for each drive wheel to save power level for telemetry

        // Setup a variable for each drive wheel to save power level for telemetry
        val leftPower: Double
        val rightPower: Double

        // Choose to drive using either Tank Mode, or POV Mode
        // Comment out the method that's not used.  The default below is POV.

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.

        // Choose to drive using either Tank Mode, or POV Mode
        // Comment out the method that's not used.  The default below is POV.

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.
        val drive = -gamepad1.left_stick_y.toDouble()
        val turn = gamepad1.right_stick_x.toDouble()
        leftPower = Range.clip(drive + turn, -1.0, 1.0)
        rightPower = Range.clip(drive - turn, -1.0, 1.0)

        // Send calculated power to wheels
        leftDrive.power = leftPower
        rightDrive.power = rightPower

        // Show the elapsed game time and wheel power.

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: $runtime")
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower)
    }

    override fun stop() {
        telemetry.addData("[BOT]", "Stopped")
    }
}