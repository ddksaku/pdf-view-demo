package com.artifex.mupdfdemo;

// Version of MuPDFAlert without enums to simplify JNI
public class MuPDFAlertInternal {
    public final String message;
    public final int iconType;
    public final int buttonGroupType;
    public final String title;
    public int buttonPressed;


    MuPDFAlertInternal(MuPDFAlert alert) {
        message = alert.message;
        iconType = alert.iconType.ordinal();
        buttonGroupType = alert.buttonGroupType.ordinal();
        title = alert.message;
        buttonPressed = alert.buttonPressed.ordinal();
    }


    MuPDFAlert toAlert() {
        return new MuPDFAlert(message, MuPDFAlert.IconType.values()[iconType], MuPDFAlert.ButtonGroupType.values()[buttonGroupType], title, MuPDFAlert.ButtonPressed.values()[buttonPressed]);
    }
}