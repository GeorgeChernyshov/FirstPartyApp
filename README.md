# FirstPartyApp

A minimal Android application built with Kotlin and Jetpack Compose for exploring system-app installation and behavior. Requires Android 12L (API 32) or newer.

## What is a first-party application?

According to the official Android wiki:
- first-party app is an app created using AOSP System APIs and is installed as a privileged or device manufacturer app.
- third-party app is an app developer that solely uses Android's public SDK and is installed via regular means (like Google Play or using adb).

## Installation

First, build the development APK:

```powershell
.\gradlew.bat assembleDebug
```

The APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

### Emulator

1. Create an Android Virtual Device using an **AOSP system image** at API 32 or newer that supports `adb root`. Google Play images do not support root. Stop the AVD before starting it with a writable system image:

For best results, launch the emulator via terminal

   ```powershell
   emulator -list-avds
   emulator -avd YOUR_AVD_NAME -writable-system -no-snapshot
   ```

Keep this terminal open and run the remaining commands in another terminal after Android finishes booting.

2. Enable root and remount the system partition:


   ```powershell
   adb root
   adb wait-for-device
   adb remount
   ```

You should see the following output (or similar)

   ```powershell
   Successfully disabled verity
   Remounted /system as RW
   Remounted /vendor as RW
   Remounted /product as RW
   Remounted /system_dlkm as RW
   Remounted /system_ext as RW
   Remount succeeded
   ```

If you see `Now reboot your device for settings to take effect` prompt, run following command:

   ```powershell
      adb reboot
      adb root
      adb wait-for-device
      adb remount
   ```

If remount reports that verity must be disabled, run `adb disable-verify`, then commands from the previous section:
   
```powershell
   adb disable-verity
   adb reboot
   adb wait-for-device
   adb root
   adb wait-for-device
   adb remount
   ```

Follow any additional reboot instruction printed by `adb remount` and retry it before copying the APK. If `adb root` reports that production builds cannot run as root, use a root-capable AOSP image.

You can check if root command worked by calling

   ```powershell
   adb -s emulator-{emulatorId} shell id
   ```

It should display `uid=0(root)` in the beginning of its output

3. Copy the APK into the system app directory, set permissions and SELinux labels, then reboot so Android scans it:

   ```powershell
   adb shell mkdir -p /system/app/FirstPartyApp
   adb push app/build/outputs/apk/debug/app-debug.apk /system/app/FirstPartyApp/FirstPartyApp.apk
   adb shell chown root:root /system/app/FirstPartyApp /system/app/FirstPartyApp/FirstPartyApp.apk
   adb shell chmod 755 /system/app/FirstPartyApp
   adb shell chmod 644 /system/app/FirstPartyApp/FirstPartyApp.apk
   adb shell restorecon -R /system/app/FirstPartyApp
   adb reboot
   adb wait-for-device
   ```

4. After Android finishes booting, verify and launch:
   
    ```powershell
   adb shell pm list packages -s com.example.firstpartyapp
   adb shell pm path com.example.firstpartyapp
   adb shell am start -n com.example.firstpartyapp/.MainActivity
   ```

The first command should list `package:com.example.firstpartyapp`; a fresh system installation should resolve to `/system/app/FirstPartyApp/FirstPartyApp.apk`. A later APK update may live under `/data/app` while retaining updated-system-app status. If an existing user installation has a different signing certificate, uninstall that copy before provisioning the system APK; uninstalling deletes its app data.
From now on, you can just launch the app from emulator home screen

The emulator's `-writable-system` image is temporary and is discarded when the emulator process exits. Repeat provisioning for a new session; for a reproducible, persistent setup, include the APK in a custom emulator system image. See the Android documentation for [writable emulator images](https://developer.android.com/studio/run/emulator-commandline) and [root-capable AVD images](https://developer.android.com/studio/run/managing-avds).

### Physical devices

**A normal, locked retail device cannot install this APK as a system app using `adb install`.** Persistent system-app installation requires the device manufacturer's firmware integration or a compatible custom Android build you can flash to a supported device. Unlocking the bootloader alone does not make `adb root` available.

For development on a device running a compatible **userdebug or eng build**:

1. Enable USB debugging, connect the device, and authorize the computer. Confirm it appears in `adb devices` and supports `adb root`.

2. Run the root/remount commands from the emulator section, including disabling verity and rebooting if required. The emulator startup command is not needed. Modern builds may use OverlayFS for writable system changes.

3. Copy the APK, set ownership, permissions, and SELinux labels, then reboot using the same commands as the emulator section. Run the same verification and launch commands afterward.

These steps require a build that supports system remounting; ordinary retail firmware generally does not. Root obtained through another tool does not guarantee that these commands work. See AOSP's [remount and OverlayFS procedure](https://android.googlesource.com/platform/system/core/+/refs/heads/android16-release/fs_mgr/README.overlayfs.md).

For a **persistent firmware installation**:

1. Integrate the APK into the device's Android build as a prebuilt application, include its module in the product configuration, and arrange installation under a system image app directory such as `/system/app/FirstPartyApp/`. Use a stable release signing key for distribution; the debug APK above is for development.

2. Build and sign the device images through the device's supported firmware process, then flash them using its documented procedure. Back up device data first: bootloader unlocking generally erases user data, and incompatible firmware can prevent booting. Exact build and flashing commands depend on the device and ROM.

3. Boot the device and run the verification commands from the emulator section. Future app updates must use a signing certificate compatible with the preinstalled APK.

The current app requests no privileged permissions, so `/system/app` is sufficient for system-app status. If privileged permissions are added later, firmware integration may require a `priv-app` directory and a permission allowlist on the same partition. Platform-signature permissions have separate signing requirements. See AOSP's [privileged permission allowlist documentation](https://source.android.com/docs/core/permissions/perms-allowlist).