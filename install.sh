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