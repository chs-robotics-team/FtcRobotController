# CHS Robotics Club Code

## Pairing Guide

1. Open your `~/.zshrc` file and add the following to add the Android Developer Tools to your path:

```sh
PATH="~/Library/Android/sdk/tools:~/Library/Android/sdk/platform-tools:$PATH"
```

2. Connect to the robot internet network (`22449-RC`)

> **Note**
> You can find the password in the Driver Station App. Go to program & manage in the top right.

3. Run the `adb connect 192.168.43.1:5555 && adb tcpip 5555` command. You should see the robot in your Device Manager on Android Studio!

After connecting, you can run code with the green run button on the top next to `TeamCode`.