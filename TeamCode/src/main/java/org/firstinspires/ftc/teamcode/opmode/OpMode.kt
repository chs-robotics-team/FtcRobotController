package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "OpMode")
@Suppress("unused")
class OpMode : OpMode() {
    override fun init() {
        telemetry.addData("[BOT]", "Initialized")
    }

    override fun loop() {

    }
}