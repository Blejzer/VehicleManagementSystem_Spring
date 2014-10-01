package ba.fit.vms.util;

import java.util.Calendar;
import java.util.LinkedHashMap;

public class DateTimeGenerator {
	
	private LinkedHashMap<Integer, String> months;
	private LinkedHashMap<Integer, Integer> years;
	
	public DateTimeGenerator(){
		this.months = new LinkedHashMap<Integer, String>();
		this.months.put(1, "Januar");
		this.months.put(2, "Februar");
		this.months.put(3, "Mart");
		this.months.put(4, "April");
		this.months.put(5, "Maj");
		this.months.put(6, "Juni");
		this.months.put(7, "Juli");
		this.months.put(8, "Avgust");
		this.months.put(9, "Septembar");
		this.months.put(10, "Oktobar");
		this.months.put(11, "Novembar");
		this.months.put(12, "Decembar");
		
		int year = Calendar.getInstance().get(Calendar.YEAR);
		this.years = new LinkedHashMap<Integer, Integer>();
		for (int i = year; i > year-5; i--) {
			this.years.put(i, i);
		}
	}
	public LinkedHashMap<Integer, String> getMonths() {
		return months;
	}
	public LinkedHashMap<Integer, Integer> getYears() {
		return years;
	}
	public Integer getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH)+1;
	}
	public Integer getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

}

