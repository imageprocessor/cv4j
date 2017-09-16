/*
 * Copyright (c) 2017-present, CV4J Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cv4j.image.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.cv4j.core.datamodel.Rect;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class contains static utility methods.
 */
public class Tools {
    /**
     * The value of pi as a float.
     */
    public final static float PI = (float) Math.PI;

    /**
     * The value of half pi as a float.
     */
    public final static float HALF_PI = (float) Math.PI / 2.0f;

    /**
     * The value of quarter pi as a float.
     */
    public final static float QUARTER_PI = (float) Math.PI / 4.0f;

    /**
     * The value of two pi as a float.
     */
    public final static float TWO_PI = (float) Math.PI * 2.0f;

    /**
     * This array contains the 16 hex digits '0'-'F'.
     */
    public static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static int clamp(int c) {
        return c > 255 ? 255 : ((c < 0) ? 0 : c);
    }

    public static int clamp(float c) {
        float p = (c > 255 ? 255 : ((c < 0) ? 0 : c));
        return (int) p;
    }

    public static float[] calcMeansAndDev(float[] data) {
        float sum = 0;
        for(int i=0; i<data.length; i++) {
            sum += data[i];
        }
        float means = sum / data.length;
        double powSum = 0.0;
        for (int d=0; d<data.length; d++) {
            powSum += Math.pow((data[d] - means), 2);
        }

        float sdValue = (float)Math.sqrt(powSum / (data.length - 1.0d));
        return new float[] { means, sdValue };
    }

    public static float[] calcMeansAndDev(int[] data) {
        float sum = 0;
        for(int i=0; i<data.length; i++) {
            sum += data[i];
        }
        float means = sum / data.length;
        double powSum = 0.0;
        for (int d=0; d<data.length; d++) {
            powSum += Math.pow((data[d] - means), 2);
        }

        float sdValue = (float)Math.sqrt(powSum / (data.length - 1.0d));
        return new float[] { means, sdValue };
    }

    public static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }

    /**
     * Bilinear interpolation of ARGB values.
     *
     * @param x   the X interpolation parameter 0..1
     * @param y   the y interpolation parameter 0..1
     * @return the interpolated value
     */
    public static int bilinearInterpolate(float x, float y, int nw, int ne, int sw, int se) {
        float m0, m1;
        int a0 = (nw >> 24) & 0xff;
        int r0 = (nw >> 16) & 0xff;
        int g0 = (nw >> 8) & 0xff;
        int b0 = nw & 0xff;

        int a1 = (ne >> 24) & 0xff;
        int r1 = (ne >> 16) & 0xff;
        int g1 = (ne >> 8) & 0xff;
        int b1 = ne & 0xff;

        int a2 = (sw >> 24) & 0xff;
        int r2 = (sw >> 16) & 0xff;
        int g2 = (sw >> 8) & 0xff;
        int b2 = sw & 0xff;

        int a3 = (se >> 24) & 0xff;
        int r3 = (se >> 16) & 0xff;
        int g3 = (se >> 8) & 0xff;
        int b3 = se & 0xff;

        float cx = 1.0f - x;
        float cy = 1.0f - y;

        m0 = cx * a0 + x * a1;
        m1 = cx * a2 + x * a3;
        int a = (int) (cy * m0 + y * m1);

        m0 = cx * r0 + x * r1;
        m1 = cx * r2 + x * r3;
        int r = (int) (cy * m0 + y * m1);

        m0 = cx * g0 + x * g1;
        m1 = cx * g2 + x * g3;
        int g = (int) (cy * m0 + y * m1);

        m0 = cx * b0 + x * b1;
        m1 = cx * b2 + x * b3;
        int b = (int) (cy * m0 + y * m1);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Converts a Color to an 7 byte hex string starting with '#'.
     */
    public static String c2hex(int c) {
        char[] buf7 = new char[7];
        buf7[0] = '#';
        for (int pos = 6; pos >= 1; pos--) {
            buf7[pos] = hexDigits[c & 0xf];
            c >>>= 4;
        }
        return new String(buf7);
    }

    /**
     * Converts a float to an 9 byte hex string starting with '#'.
     */
    public static String f2hex(float f) {
        int i = Float.floatToIntBits(f);
        char[] buf9 = new char[9];
        buf9[0] = '#';
        for (int pos = 8; pos >= 1; pos--) {
            buf9[pos] = hexDigits[i & 0xf];
            i >>>= 4;
        }
        return new String(buf9);
    }

    /**
     * Converts an int to a zero-padded hex string of fixed length 'digits'.
     * If the number is too high, it gets truncated, keeping only the lowest 'digits' characters.
     */
    public static String int2hex(int i, int digits) {
        char[] buf = new char[digits];
        for (int pos = buf.length - 1; pos >= 0; pos--) {
            buf[pos] = hexDigits[i & 0xf];
            i >>>= 4;
        }
        return new String(buf);
    }

    public static void rgb2hsv(byte[][] rgb, byte[][] hsv) {
        byte[] R = rgb[0];
        byte[] G = rgb[1];
        byte[] B = rgb[2];
        int len = R.length;
        for(int i=0; i<len; i++) {
            int r = R[i]&0xff;
            int g = G[i]&0xff;
            int b = B[i]&0xff;
            int[] result = rgb2hsv(r, g, b);
            hsv[0][i] = (byte)result[0];
            hsv[1][i] = (byte)result[1];
            hsv[2][i] = (byte)result[2];
        }
    }

    public static int[] rgb2hsv(int r, int g, int b)
    {
        double delta, min;
        double h = 0, s, v;

        min = Math.min(Math.min(r, g), b);
        v = Math.max(Math.max(r, g), b);
        delta = v - min;

        if (v == 0.0)
            s = 0;
        else
            s = delta / v;

        if (s == 0)
            h = 0.0;

        else
        {
            if (r == v)
                h = (g - b) / delta;
            else if (g == v)
                h = 2 + (b - r) / delta;
            else if (b == v)
                h = 4 + (r - g) / delta;

            h *= 60;

            if (h < 0.0)
                h = h + 360;
        }

        return new int[]{(int)(h/2.0), (int)(s*255), (int)(v / 255)*255};
    }

    public static double[] getMinMax(final double[] a) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        double value;
        for (double anA : a) {
            value = anA;
            if (value < min)
                min = value;
            if (value > max)
                max = value;
        }
        double[] minAndMax = new double[2];
        minAndMax[0] = min;
        minAndMax[1] = max;
        return minAndMax;
    }

    public static double[] getMinMax(final float[] a) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        double value;
        for (float anA : a) {
            value = anA;
            if (value < min)
                min = value;
            if (value > max)
                max = value;
        }
        double[] minAndMax = new double[2];
        minAndMax[0] = min;
        minAndMax[1] = max;
        return minAndMax;
    }

    /**
     * Converts the float array 'a' to a double array.
     */
    public static double[] toDouble(float[] a) {
        int len = a.length;
        double[] d = new double[len];
        for (int i = 0; i < len; i++)
            d[i] = a[i];
        return d;
    }

    /**
     * Converts the double array 'a' to a float array.
     */
    public static float[] toFloat(double[] a) {
        int len = a.length;
        float[] f = new float[len];
        for (int i = 0; i < len; i++)
            f[i] = (float) a[i];
        return f;
    }

    /**
     * Converts carriage returns to line feeds.
     */
    public static String fixNewLines(String s) {
        if (s == null)
            return null;
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '\r') chars[i] = '\n';
        }
        return new String(chars);
    }

    /**
     * Returns a double containg the value represented by the
     * specified <code>String</code>.
     *
     * @param s            the string to be parsed.
     * @param defaultValue the value returned if <code>s</code>
     *                     does not contain a parsable double
     * @return The double value represented by the string argument or
     * <code>defaultValue</code> if the string does not contain a parsable double
     */
    public static double parseDouble(String s, double defaultValue) {
        if (s == null) return defaultValue;

        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
        }
        return defaultValue;
    }

    /**
     * Returns a double containg the value represented by the
     * specified <code>String</code>.
     *
     * @param s the string to be parsed.
     * @return The double value represented by the string argument or
     * Double.NaN if the string does not contain a parsable double
     */
    public static double parseDouble(String s) {
        return parseDouble(s, Double.NaN);
    }

    /**
     * Returns the number of decimal places needed to display a
     * number, or -2 if exponential notation should be used.
     */
    public static int getDecimalPlaces(double n) {
        if ((int) n == n || Double.isNaN(n))
            return 0;
        String s = "" + n;
        if (s.contains("E"))
            return -2;
        while (s.endsWith("0"))
            s = s.substring(0, s.length() - 1);
        int index = s.indexOf(".");
        if (index == -1) return 0;
        int digits = s.length() - index - 1;
        if (digits > 4) digits = 4;
        return digits;
    }

    /**
     * Returns the number of decimal places needed to display two numbers,
     * or -2 if exponential notation should be used.
     */
    public static int getDecimalPlaces(double n1, double n2) {
        if ((int) n1 == n1 && (int) n2 == n2)
            return 0;
        int digits = getDecimalPlaces(n1);
        int digits2 = getDecimalPlaces(n2);
        if (digits == 0)
            return digits2;
        if (digits2 == 0)
            return digits;
        if (digits < 0 || digits2 < 0)
            return digits;
        if (digits2 > digits)
            digits = digits2;
        return digits;
    }

    /**
     * Splits a string into substrings using the default delimiter set,
     * which is " \t\n\r" (space, tab, newline and carriage-return).
     */
    public static String[] split(String str) {
        return split(str, " \t\n\r");
    }

    /**
     * Splits a string into substring using the characters
     * contained in the second argument as the delimiter set.
     */
    public static String[] split(String str, String delim) {
        if (delim.equals("\n"))
            return splitLines(str);
        StringTokenizer t = new StringTokenizer(str, delim);
        int tokens = t.countTokens();
        String[] strings;
        if (tokens > 0) {
            strings = new String[tokens];
            for (int i = 0; i < tokens; i++)
                strings[i] = t.nextToken();
        } else
            strings = new String[0];
        return strings;
    }

    static String[] splitLines(String str) {
        Vector v = new Vector();
        try {
            BufferedReader br = new BufferedReader(new StringReader(str));
            String line;
            while (true) {
                line = br.readLine();
                if (line == null) break;
                v.addElement(line);
            }
            br.close();
        } catch (Exception e) {
        }
        String[] lines = new String[v.size()];
        v.copyInto((String[]) lines);
        return lines;
    }

    /**
     * Returns a sorted list of indices of the specified double array.
     * Modified from: http://stackoverflow.com/questions/951848 by N.Vischer.
     */
    public static int[] rank(double[] values) {
        int n = values.length;
        final Integer[] indexes = new Integer[n];
        final Double[] data = new Double[n];
        for (int i = 0; i < n; i++) {
            indexes[i] = new Integer(i);
            data[i] = new Double(values[i]);
        }
        Arrays.sort(indexes, new Comparator<Integer>() {
            public int compare(final Integer o1, final Integer o2) {
                return data[o1].compareTo(data[o2]);
            }
        });
        int[] indexes2 = new int[n];
        for (int i = 0; i < n; i++)
            indexes2[i] = indexes[i].intValue();
        return indexes2;
    }

    /**
     * Returns a sorted list of indices of the specified String array.
     */
    public static int[] rank(final String[] data) {
        int n = data.length;
        final Integer[] indexes = new Integer[n];
        for (int i = 0; i < n; i++)
            indexes[i] = new Integer(i);
        Arrays.sort(indexes, new Comparator<Integer>() {
            public int compare(final Integer o1, final Integer o2) {
                return data[o1].compareToIgnoreCase(data[o2]);
            }
        });
        int[] indexes2 = new int[n];
        for (int i = 0; i < n; i++)
            indexes2[i] = indexes[i].intValue();
        return indexes2;
    }

    public static void drawRects(@NonNull Bitmap bitmap, @NonNull List<Rect> rectangles) {

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);

        for (Rect rect : rectangles) {
            canvas.drawRect(rect.x, rect.y, rect.br().x, rect.br().y, paint);
        }
    }

    public static int[] rgbToYcrCb(int tr, int tg, int tb) {
        double sum = tr + tg + tb;
        double r = ((double) tr) / sum;
        double g = ((double) tg) / sum;
        double b = ((double) tb) / sum;
        double y = 65.481 * r + 128.553 * g + 24.966 * b + 16.0d;
        double Cr = -37.7745 * r - 74.1592 * g + 111.9337 * b + 128.0d;
        double Cb = 111.9581 * r - 93.7509 * g - 18.2072 * b + 128.0d;
        return new int[]{(int) y, (int) Cr, (int) Cb};
    }
}
