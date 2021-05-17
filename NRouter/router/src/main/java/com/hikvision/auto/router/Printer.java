package com.hikvision.auto.router;

public class Printer {

    public static void setDebugger(boolean debugger) {
        HikRouter.compatibleSdk().logger().setDebugger(debugger);
    }

    public static void debug(String tag, String msg) {
        HikRouter.compatibleSdk().logger().i(tag, msg);
    }

    public static void info(String tag, String msg) {
        HikRouter.compatibleSdk().logger().i(tag, msg);
    }

    public static void error(String tag, String msg) {
        HikRouter.compatibleSdk().logger().e(tag, msg);
    }

}
