package com.quiz.mathematics;


import java.util.ArrayList;

public class Constants {
    public static int BORDER_WIDTH_DP = 5;
    public static ArrayList<String> FORMAT_IMAGE = new ImageTypeList();
    public static String PKG_APP = BuildConfig.APPLICATION_ID;
    public static boolean SHOW_ADS = true;
    public static String INSTA = "com.instagram.android";
    public static String MESSEGER = "com.facebook.orca";
    public static String TWITTER = "com.twitter.android";
    public static String WHATSAPP = "com.whatsapp";
    public static String YOUTU = "com.google.android.youtube";
    public static String ZALO = "com.zing.zalo";
    public static String FACE = "com.facebook.katana";
    public static String GMAIL = "com.google.android.gm";
    static class ImageTypeList extends ArrayList<String> {
        ImageTypeList() {
            add(".PNG");
            add(".JPEG");
            add(".jpg");
            add(".png");
            add(".jpeg");
            add(".JPG");
        }
    }
}
