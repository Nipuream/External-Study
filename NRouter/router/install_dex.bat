adb root
adb remount
adb push dex/sdk.dex /system/framework/sdk.dex
adb push dex/data.dex /system/framework/data.dex
adb push dex/business.dex /system/framework/business.dex
pause