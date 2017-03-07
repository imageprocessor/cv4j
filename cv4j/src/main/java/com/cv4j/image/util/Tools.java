package com.cv4j.image.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;

/** This class contains static utility methods. */
 public class Tools {
	/** This array contains the 16 hex digits '0'-'F'. */
	public static final char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	/** Converts a Color to an 7 byte hex string starting with '#'. */
	public static String c2hex(int c) {
		char[] buf7 = new char[7];
		buf7[0] = '#';
		for (int pos=6; pos>=1; pos--) {
			buf7[pos] = hexDigits[c&0xf];
			c >>>= 4;
		}
		return new String(buf7);
	}
		
	/** Converts a float to an 9 byte hex string starting with '#'. */
	public static String f2hex(float f) {
		int i = Float.floatToIntBits(f);
		char[] buf9 = new char[9];
		buf9[0] = '#';
		for (int pos=8; pos>=1; pos--) {
			buf9[pos] = hexDigits[i&0xf];
			i >>>= 4;
		}
		return new String(buf9);
	}
		
	/** Converts an int to a zero-padded hex string of fixed length 'digits'. 
	 *  If the number is too high, it gets truncated, keeping only the lowest 'digits' characters. */
	public static String int2hex(int i, int digits) {
		char[] buf = new char[digits];
		for (int pos=buf.length-1; pos>=0; pos--) {
			buf[pos] = hexDigits[i&0xf];
			i >>>= 4;
		}
		return new String(buf);
	}

	public static double[] getMinMax(double[] a) {
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		double value;
		for (int i=0; i<a.length; i++) {
			value = a[i];
			if (value<min)
				min = value;
			if (value>max)
				max = value;
		}
		double[] minAndMax = new double[2];
		minAndMax[0] = min;
		minAndMax[1] = max;
		return minAndMax;
	}

	public static double[] getMinMax(float[] a) {
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		double value;
		for (int i=0; i<a.length; i++) {
			value = a[i];
			if (value<min)
				min = value;
			if (value>max)
				max = value;
		}
		double[] minAndMax = new double[2];
		minAndMax[0] = min;
		minAndMax[1] = max;
		return minAndMax;
	}
	
	/** Converts the float array 'a' to a double array. */
	public static double[] toDouble(float[] a) {
		int len = a.length;
		double[] d = new double[len];
		for (int i=0; i<len; i++)
			d[i] = a[i];
		return d;
	}
	
	/** Converts the double array 'a' to a float array. */
	public static float[] toFloat(double[] a) {
		int len = a.length;
		float[] f = new float[len];
		for (int i=0; i<len; i++)
			f[i] = (float)a[i];
		return f;
	}
	
	/** Converts carriage returns to line feeds. */
	public static String fixNewLines(String s) {
		if (s==null)
			return null;
		char[] chars = s.toCharArray();
		for (int i=0; i<chars.length; i++)
			{if (chars[i]=='\r') chars[i] = '\n';}
		return new String(chars);
	}

	/**
	* Returns a double containg the value represented by the 
	* specified <code>String</code>.
	*
	* @param      s   the string to be parsed.
	* @param      defaultValue   the value returned if <code>s</code>
	*	does not contain a parsable double
	* @return     The double value represented by the string argument or
	*	<code>defaultValue</code> if the string does not contain a parsable double
	*/
	public static double parseDouble(String s, double defaultValue) {
		if (s==null) return defaultValue;
		try {
			defaultValue = Double.parseDouble(s);
		} catch (NumberFormatException e) {}
		return defaultValue;			
	}

	/**
	* Returns a double containg the value represented by the 
	* specified <code>String</code>.
	*
	* @param      s   the string to be parsed.
	* @return     The double value represented by the string argument or
	*	Double.NaN if the string does not contain a parsable double
	*/
	public static double parseDouble(String s) {
		return parseDouble(s, Double.NaN);
	}
	
	/** Returns the number of decimal places needed to display a 
		number, or -2 if exponential notation should be used. */
	public static int getDecimalPlaces(double n) {
		if ((int)n==n || Double.isNaN(n))
			return 0;
		String s = ""+n;
		if (s.contains("E"))
			return -2;
		while (s.endsWith("0"))
			s = s.substring(0,s.length()-1);
		int index = s.indexOf(".");
		if (index==-1) return 0;
		int digits = s.length() - index - 1;
		if (digits>4) digits=4;
		return digits;
	}
	
	/** Returns the number of decimal places needed to display two numbers,
		or -2 if exponential notation should be used. */
	public static int getDecimalPlaces(double n1, double n2) {
		if ((int)n1==n1 && (int)n2==n2)
			return 0;
		int digits = getDecimalPlaces(n1);
		int digits2 = getDecimalPlaces(n2);
		if (digits==0)
			return digits2;
		if (digits2==0)
			return digits;
		if (digits<0 || digits2<0)
			return digits;
		if (digits2>digits)
			digits = digits2;
		return digits;
	}
	
	/** Splits a string into substrings using the default delimiter set, 
	which is " \t\n\r" (space, tab, newline and carriage-return). */
	public static String[] split(String str) {
		return split(str, " \t\n\r");
	}

	/** Splits a string into substring using the characters
	contained in the second argument as the delimiter set. */
	public static String[] split(String str, String delim) {
		if (delim.equals("\n"))
			return splitLines(str);
		StringTokenizer t = new StringTokenizer(str, delim);
		int tokens = t.countTokens();
		String[] strings;
		if (tokens>0) {
			strings = new String[tokens];
			for(int i=0; i<tokens; i++) 
				strings[i] = t.nextToken();
		} else
			strings = new String[0];
		return strings;
	}
	
	static String[] splitLines(String str) {
		Vector v = new Vector();
		try {
			BufferedReader br  = new BufferedReader(new StringReader(str));
			String line;
			while (true) {
				line = br.readLine();
				if (line == null) break;
				v.addElement(line);
			}
			br.close();
		} catch(Exception e) { }
		String[] lines = new String[v.size()];
		v.copyInto((String[])lines);
		return lines;
	}
	
	/** Returns a sorted list of indices of the specified double array.
		Modified from: http://stackoverflow.com/questions/951848 by N.Vischer.
	*/
	public static int[] rank(double[] values) {
		int n = values.length;
		final Integer[] indexes = new Integer[n];
		final Double[] data = new Double[n];
		for (int i=0; i<n; i++) {
			indexes[i] = new Integer(i);
			data[i] = new Double(values[i]);
		}
		Arrays.sort(indexes, new Comparator<Integer>() {
			public int compare(final Integer o1, final Integer o2) {
				return data[o1].compareTo(data[o2]);
			}
		});
		int[] indexes2 = new int[n];
		for (int i=0; i<n; i++)
			indexes2[i] = indexes[i].intValue();
		return indexes2;
	}

	/** Returns a sorted list of indices of the specified String array. */
	public static int[] rank(final String[] data) {
		int n = data.length;
		final Integer[] indexes = new Integer[n];
		for (int i=0; i<n; i++)
			indexes[i] = new Integer(i);
		Arrays.sort(indexes, new Comparator<Integer>() {
			public int compare(final Integer o1, final Integer o2) {
				return data[o1].compareToIgnoreCase(data[o2]);
			}
		});
		int[] indexes2 = new int[n];
		for (int i=0; i<n; i++)
			indexes2[i] = indexes[i].intValue();
		return indexes2;
	}

	public static int clamp(int value) {
		return value > 255 ? 255 :(value < 0 ? 0 : value);
	}
}
