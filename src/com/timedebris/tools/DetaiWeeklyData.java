package com.timedebris.tools;

public class DetaiWeeklyData {
	public int type;
	public String name;
	public long intervalOrCount;

	public DetaiWeeklyData(int type, String name, String date,
			long intervalOrCount) {
		this.type = type;
		this.name = name;
		this.intervalOrCount = intervalOrCount;
	}
}