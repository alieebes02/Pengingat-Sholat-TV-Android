package com.pengingatshalat.tv;

import java.util.Calendar;

public class PrayerTimes {

    public static final String[] KEYS = {"subuh", "dzuhur", "ashar", "maghrib", "isya"};

    private static double dtr(double d){ return d * Math.PI / 180.0; }
    private static double rtd(double r){ return r * 180.0 / Math.PI; }
    private static double fixAngle(double a){ a = a - 360 * Math.floor(a / 360); return a < 0 ? a + 360 : a; }
    private static double fixHour(double h){ h = h - 24 * Math.floor(h / 24); return h < 0 ? h + 24 : h; }

    private static double julian(int y, int m, int d){
        if (m <= 2) { y -= 1; m += 12; }
        int A = (int) Math.floor(y / 100.0);
        int B = 2 - A + (int) Math.floor(A / 4.0);
        return Math.floor(365.25 * (y + 4716)) + Math.floor(30.6001 * (m + 1)) + d + B - 1524.5;
    }

    private static double[] sunPosition(double jd){
        double D = jd - 2451545.0;
        double g = fixAngle(357.529 + 0.98560028 * D);
        double q = fixAngle(280.459 + 0.98564736 * D);
        double L = fixAngle(q + 1.915 * Math.sin(dtr(g)) + 0.020 * Math.sin(dtr(2 * g)));
        double e = 23.439 - 0.00000036 * D;
        double dec = rtd(Math.asin(Math.sin(dtr(e)) * Math.sin(dtr(L))));
        double RA = rtd(Math.atan2(Math.cos(dtr(e)) * Math.sin(dtr(L)), Math.cos(dtr(L)))) / 15.0;
        RA = fixHour(RA);
        double eqt = q / 15.0 - RA;
        return new double[]{dec, eqt};
    }

    private static Double sunAngleTime(double jd, double angle, double lat, double lng, double tz, boolean ccw){
        double[] se = sunPosition(jd + 0.5);
        double dec = se[0], eqt = se[1];
        double noon = 12 - lng / 15.0 - eqt + tz;
        double num = -Math.sin(dtr(angle)) - Math.sin(dtr(lat)) * Math.sin(dtr(dec));
        double den = Math.cos(dtr(lat)) * Math.cos(dtr(dec));
        double ratio = num / den;
        if (Math.abs(ratio) > 1) return null;
        double t = rtd(Math.acos(ratio)) / 15.0;
        return noon + (ccw ? -t : t);
    }

    private static double asrTime(double jd, double lat, double lng, double tz){
        double[] se = sunPosition(jd + 0.5);
        double dec = se[0], eqt = se[1];
        double noon = 12 - lng / 15.0 - eqt + tz;
        double a = rtd(Math.atan(1.0 / (1 + Math.tan(dtr(Math.abs(lat - dec))))));
        double num = Math.sin(dtr(a)) - Math.sin(dtr(lat)) * Math.sin(dtr(dec));
        double den = Math.cos(dtr(lat)) * Math.cos(dtr(dec));
        double t = rtd(Math.acos(num / den)) / 15.0;
        return noon + t;
    }

    public static long[] hitung(int year, int month, int day, double lat, double lng, double tzHours, String metode){
        double fajrAngle = 20, ishaAngle = 18;
        if ("muhammadiyah".equals(metode)) { fajrAngle = 18; ishaAngle = 18; }
        else if ("mwl".equals(metode)) { fajrAngle = 18; ishaAngle = 17; }
        else if ("isna".equals(metode)) { fajrAngle = 15; ishaAngle = 15; }

        double jd = julian(year, month, day);
        Double fajr = sunAngleTime(jd, fajrAngle, lat, lng, tzHours, true);
        double[] se = sunPosition(jd + 0.5);
        double dzuhur = 12 - lng / 15.0 - se[1] + tzHours + (2.0 / 60);
        double ashar = asrTime(jd, lat, lng, tzHours);
        Double maghrib = sunAngleTime(jd, 0.833, lat, lng, tzHours, false);
        Double isya = sunAngleTime(jd, ishaAngle, lat, lng, tzHours, false);

        double[] jamJam = { fajr == null ? 5 : fajr, dzuhur, ashar, maghrib == null ? 18 : maghrib, isya == null ? 19 : isya };
        long[] hasil = new long[5];
        for (int i = 0; i < 5; i++) {
            double h = fixHour(jamJam[i]);
            int jam = (int) Math.floor(h);
            int menit = (int) Math.round((h - jam) * 60);
            if (menit == 60) { menit = 0; jam = (jam + 1) % 24; }
            Calendar c = Calendar.getInstance();
            c.set(year, month - 1, day, jam, menit, 0);
            c.set(Calendar.MILLISECOND, 0);
            hasil[i] = c.getTimeInMillis();
        }
        return hasil;
    }
}
