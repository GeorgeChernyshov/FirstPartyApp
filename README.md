# FirstPartyApp

A minimal Android application built with Kotlin and Jetpack Compose for exploring system-app installation and behavior. Requires Android 12L (API 32) or newer.

## What is a first-party application?

According to the official Android wiki:
- first-party app is an app created using AOSP System APIs and is installed as a privileged or device manufacturer app. A **system app** is included on a system image partition; a **privileged app** is a system app installed under a `priv-app` directory and eligible for allowlisted privileged permissions.
- third-party app is an app developer that solely uses Android's public SDK and is installed via regular means (like Google Play or using adb).

## Installation

First, build the development APK:

```powershell
.\gradlew.bat assembleDebug
```

The APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

### Emulator

1. Create an Android Virtual Device using an **AOSP system image** at API 32 or newer that supports `adb root`. Google Play images do not support root. Stop the AVD before starting it with a writable system image:

Launch it with `-writable-system` to prepare for modifying the system image. `-no-snapshot` disables Quick Boot snapshots for a predictable startup; it is optional.

   ```powershell
   emulator -list-avds
   emulator -avd YOUR_AVD_NAME -writable-system -no-snapshot
   ```

Keep this terminal open and run the remaining commands in another terminal after Android finishes booting.

If `emulator` is not on `PATH`, use PowerShell's call operator and the SDK executable path:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -avd YOUR_AVD_NAME -writable-system -no-snapshot
```

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
      adb wait-for-device
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

3. Use the repository's [permission allowlist](permissions.xml). Its entries must match permissions requested in the preinstalled APK. Run the following commands one line at a time from the project root.

4. Copy the APK into the privileged app directory and install the allowlist on the same partition. Complete both before rebooting: missing privileged permission allowlist entries can prevent Android from booting when enforcement is enabled.

   ```powershell
   adb shell mkdir -p /system/priv-app/FirstPartyApp
   adb push app/build/outputs/apk/debug/app-debug.apk /system/priv-app/FirstPartyApp/FirstPartyApp.apk
   adb push permissions.xml /system/etc/permissions/permissions-firstpartyapp.xml
   adb shell chown root:root /system/priv-app/FirstPartyApp /system/priv-app/FirstPartyApp/FirstPartyApp.apk /system/etc/permissions/permissions-firstpartyapp.xml
   adb shell chmod 755 /system/priv-app/FirstPartyApp
   adb shell chmod 644 /system/priv-app/FirstPartyApp/FirstPartyApp.apk /system/etc/permissions/permissions-firstpartyapp.xml
   adb shell restorecon -R /system/priv-app/FirstPartyApp
   adb shell restorecon /system/etc/permissions/permissions-firstpartyapp.xml
   adb reboot
   adb wait-for-device
   ```

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

1. Integrate the APK into the device's Android build as a privileged prebuilt application, include its module in the product configuration, and install it under `/system/priv-app/FirstPartyApp/`. Include the allowlist above under `/system/etc/permissions/`. If using another partition such as `/product`, place both files on that partition. Use a stable release signing key for distribution; the debug APK above is for development.

2. Build and sign the device images through the device's supported firmware process, then flash them using its documented procedure. Back up device data first: bootloader unlocking generally erases user data, and incompatible firmware can prevent booting. Exact build and flashing commands depend on the device and ROM.

3. Boot the device and run the verification commands from the emulator section. Future app updates must use a signing certificate compatible with the preinstalled APK.

### Reinstalling after code changes

Once the app is installed as a system app, build and install subsequent versions as updates:

```powershell
.\gradlew.bat assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am force-stop com.example.firstpartyapp
adb shell am start -n com.example.firstpartyapp/.MainActivity
```

Use the same signing key as the preinstalled APK. The `-r` option preserves app data. Android stores the update under `/data/app` and retains updated-system-app status while the system copy remains installed. No remount or reboot is needed. Android Studio's Run button can also build, install, and launch the update.
An installed `/data/app` update takes precedence over the preinstalled copy. If new UI is missing after replacing only the system APK, install the latest APK with `adb install -r` instead of deleting files from `/data/app`.

**Adding a new privileged permission requires reprovisioning:** add its manifest declaration, rebuild, replace the APK under `/system/priv-app/FirstPartyApp/`, update the allowlist, and reboot using the installation procedure above. Then update the active `/data/app` APK as well. An APK update alone cannot acquire a privileged permission absent from the preinstalled APK. See [AOSP permission guidance](https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/permission/Permissions.md).

For multiple connected devices, select the target using `ANDROID_SERIAL` as above or use `adb -s SERIAL` for every command. If using the optional development grant for `WRITE_SECURE_SETTINGS`, reapply it after uninstalling and reinstalling.
