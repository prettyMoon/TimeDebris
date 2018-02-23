package com.timedebris.tools;

public class DetaiDailyData {
	public int type;
	public String name;
	public String dateStart;
	public String dateEnd;
	// if(type==2) interval=0;
	public String interval;

	public DetaiDailyData(int type, String name, String timeStart,
			String timeEnd, long interval) {
		this.type = type;
		this.name = name;
		this.dateStart = timeStart;
		this.dateEnd = timeEnd;
		this.interval = DateTimeTransformer.getintervalStringFromLong(interval);
	}

}