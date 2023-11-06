package org.firstinspires.ftc.teamcode

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive

@Autonomous(name = "TestAuto")
@Suppress("unused")
class TestAuto : OpMode() {
    private val drive = SampleMecanumDrive(hardwareMap)

    override fun init() {
        telemetry.addData("[BOT]", "Initialized")
    }

    override fun start() {
        telemetry.addData("[BOT]", "Driving forward...")

        // Test drive forward 10"
        drive.trajectoryBuilder(Pose2d())
            .forward(10.0)
            .build()

        telemetry.addData("[BOT]", "Driving stopped...")
    }

    override fun loop() {}

    override fun stop() {
        telemetry.addData("[BOT]", "Stopped")
    }
}