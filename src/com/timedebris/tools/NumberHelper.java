package com.timedebris.tools;

public class NumberHelper {

	public static String getAccurateAverage(double average, int bitAfterPoint) {
		java.text.DecimalFormat df = null;
		if (bitAfterPoint == 1)
			df = new java.text.DecimalFormat("#.#");
		else if (bitAfterPoint == 2)
			df = new java.text.DecimalFormat("#.##");
		return df.format(average);
	}
}
