package org.evd.game.runtime;

import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 
 *
 * 时间、日期处理相关
 * 本工具所有方法都是基于默认时区
 */
public class TimeUtils {
	/** 地区id，默认服务器时区（后续可以通过配置表配置） */
	public static final ZoneId ZONE_ID = ZoneId.systemDefault();

	/** 秒 */
	public static final long SEC = 1000L;
	/** 分 */
	public static final long MIN = 60 * SEC;
	/** 小时 */
	public static final long HOUR = 60 * MIN;
	/** 天 */
	public static final long DAY = 24 * HOUR;
	/** 周 */
	public static final long WEEK = 7 * DAY;
	//以下是不准确的时间，只能用于估算
	/** 年 */
	public static final long YEAR = 365 * DAY;
	/** 十年(最大持续时间，可以当永久时间用，游戏运营不了十年的) */
	public static final long TEN_YEAR = 10 * YEAR;

	/** 通用日期时间格式 yyyy-MM-dd HH:mm:ss */
	public static final String COMMON_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /** 通用日期时间格式 yyyy-MM-dd HH:mm */
    public static final String COMMON_DATE_TIME_PATTERN_MIN = "yyyy-MM-dd HH:mm";
	/** 通用日期格式 yyyy-MM-dd */
	public static final String COMMON_DATE_PATTERN = "yyyy-MM-dd";
	/** 通用时间格式 HH:mm:ss */
	public static final String COMMON_TIME_PATTERN = "HH:mm:ss";

	//******************************************* 解析部分 ********************************************//
	/**
	 * 从日期时间字符串解析时间戳
	 * 日期时间字符串必须完整包含日期、时间
	 * 否则使用对应的 getMilliOfTime getMilliOfDate
	 * @param dateTimeStr 日期时间字符串，完整包含日期、时间
	 * @return 失败时返回0
	 */
	public static long parseToTimestamp(String dateTimeStr, String pattern) {
		LocalDateTime dateTime = DateTimeUtils.parseDateTime(dateTimeStr, pattern);
		if (dateTime == null) {
			return 0L;
		}
		return DateTimeUtils.getTimestampOfDateTime(dateTime);
	}

	/**
	 * 从日期时间字符串（格式：yyyy-MM-dd HH:mm:ss）解析时间戳
	 * @param dateTimeStr 日期时间字符串，必须完整包含日期、时间
	 * @return 失败时返回0
	 */
	public static long parseToTimestamp(String dateTimeStr) {
		return parseToTimestamp(dateTimeStr, COMMON_DATE_TIME_PATTERN);
	}

	//******************************************* 格式化部分 ********************************************//
	/**
	 * 格式化时间戳
	 * @param timestamp 时间戳
	 * @param pattern 格式
	 * @return 格式化失败时返回null
	 */
	public static String formatTimestamp(long timestamp, String pattern) {
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfTimestamp(timestamp);
		try {
			return dateTime.format(DateTimeFormatter.ofPattern(pattern));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 格式化时间戳（格式：yyyy-MM-dd HH:mm:ss）
	 * @param timestamp 时间戳
	 * @return 失败时返回null
	 */
	public static String formatTimestamp(long timestamp) {
		return formatTimestamp(timestamp, COMMON_DATE_TIME_PATTERN);
	}

	//******************************************* 工具方法部分 ********************************************//
	/**
	 * 获取两个时间之间的自然天数(time2 > time1)
	 * 不考虑时间，所以非绝对天数
	 * @param timestamp1 时间一
	 * @param timestamp2 时间二
	 * @return 相隔自然天数
	 */
	public static int getDaysBetween(long timestamp1, long timestamp2) {
		long startTimeOfDay1 = getZeroOfDay(timestamp1);
		long startTimeOfDay2 = getZeroOfDay(timestamp2);
		return (int) ((startTimeOfDay2 - startTimeOfDay1) / DAY);
	}

	/**
	 * 获取两个时间之间的绝对天数(24小时算一天)
	 * @param timestamp1 时间一
	 * @param timestamp2 时间二
	 * @return 相隔的绝对天数
	 */
	public static int getDaysBetweenAbsolute(long timestamp1, long timestamp2) {
		return (int) Math.floorDiv(timestamp2 - timestamp1, DAY);
	}

	/**
	 * 是否包含指定星期[timestamp1, timestamp2]
	 * @param timestamp1 时间一
	 * @param timestamp2 时间二
	 * @param week 星期
	 * @return boolean
	 */
	public static boolean isWeekdayBetween(long timestamp1, long timestamp2, DayOfWeek week) {
		// 时间差
		long time = timestamp2 - timestamp1;
		if (time < 0) {
			return false;
		}
		// 大于一周
		if (time >= WEEK) {
			return true;
		}
		DayOfWeek week1 = DateTimeUtils.getDateOfTimestamp(timestamp1).getDayOfWeek();
		DayOfWeek week2 = DateTimeUtils.getDateOfTimestamp(timestamp2).getDayOfWeek();

		return week1.getValue() <= week.getValue() && week.getValue() <= week2.getValue();
	}

	/**
	 * 指定时间戳是否在指定时间区间范围内(startDateTime, endDateTime)
	 * @param timestamp 时间戳
	 * @param startDateTime 开始日期时间
	 * @param endDateTime 结束日期时间
	 * @return boolean
	 */
	public static boolean isBetween(long timestamp, String startDateTime, String endDateTime) {
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfTimestamp(timestamp);
		LocalDateTime end = DateTimeUtils.parseDateTime(endDateTime, COMMON_DATE_TIME_PATTERN);
		LocalDateTime start = DateTimeUtils.parseDateTime(startDateTime, COMMON_DATE_TIME_PATTERN);
		if (start == null || end == null) {
			return false;
		}
		return start.isBefore(dateTime) && end.isAfter(dateTime);
	}

	/**
	 * 指定时间戳是否在指定时间区间范围内(startTimestamp, endTimestamp)
	 * @param timestamp 时间戳
	 * @param startTimestamp 开始时间戳
	 * @param endTimestamp 结束时间戳
	 * @return boolean
	 */
	public static boolean isBetween(long timestamp, long startTimestamp, long endTimestamp) {
		return timestamp > startTimestamp && timestamp < endTimestamp;
	}

	/**
	 * 是否在一天中的某个时间段内(startTime，endTime)
	 * @param timestamp 时间戳
	 * @param startTime 开始时间字符串，格式：HH:mm:ss
	 * @param endTime 结束时间字符串，格式：HH:mm:ss
	 * @return boolean
	 */
	public static boolean isBetweenOfTime(long timestamp, String startTime, String endTime) {
		startTime = formatTimeHour(startTime);
		endTime = formatTimeHour(endTime);
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfTimestamp(timestamp);
		LocalDate date = DateTimeUtils.getDateOfTimestamp(timestamp);
		LocalTime start = DateTimeUtils.parseTime(startTime, COMMON_TIME_PATTERN);
		LocalTime end = DateTimeUtils.parseTime(endTime, COMMON_TIME_PATTERN);
		if (start == null || end == null) {
			return false;
		}
		return start.atDate(date).isBefore(dateTime) && end.atDate(date).isAfter(dateTime);
	}

	/**
	 * 是否在一天中的某个时间段内(startTime，endTime)
	 * @param timestamp 时间戳
	 * @param startTime 开始时间字符串，格式：HH:mm:ss
	 * @param endTime 结束时间字符串，格式：HH:mm:ss
	 * @param offSetDays 偏移天数
	 * @return boolean
	 */
	public static boolean isBetweenOfTimeWithOffSet(long timestamp, String startTime, String endTime, long offSetDays) {
		startTime = formatTimeHour(startTime);
		endTime = formatTimeHour(endTime);
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfTimestamp(timestamp);
		LocalDate date = DateTimeUtils.getDateOfTimestamp(timestamp);
		LocalTime start = DateTimeUtils.parseTime(startTime, COMMON_TIME_PATTERN);
		LocalTime end = DateTimeUtils.parseTime(endTime, COMMON_TIME_PATTERN);
		if (start == null || end == null) {
			return false;
		}
		LocalDateTime startDate = start.atDate(date);
		LocalDateTime endDate = end.atDate(date);
		if (offSetDays > 0) {
			endDate = endDate.plusDays(offSetDays);
		}
		return startDate.isBefore(dateTime) && endDate.isAfter(dateTime);
	}
	
	private static String formatTimeHour(String time) {
		return time.indexOf(":") != 1 ? time : "0"+time;
	}

	/**
	 * 是否是同一天
	 * @param timestamp1 时间戳
	 * @param timestamp2 时间戳
	 * @return boolean
	 */
	public static boolean isSameDay(long timestamp1, long timestamp2) {
		LocalDate date1 = DateTimeUtils.getDateOfTimestamp(timestamp1);
		LocalDate date2 = DateTimeUtils.getDateOfTimestamp(timestamp2);
		return DateTimeUtils.isSameDay(date1, date2);
	}

	/**
	 * 是否是同一天
	 * @param timestamp1 时间戳
	 * @param timestamp2 时间戳
	 * @param delayTime delay时间
	 * @return boolean
	 */
	public static boolean isSameDay(long timestamp1, long timestamp2, long delayTime) {
		LocalDate date1 = DateTimeUtils.getDateOfTimestamp(timestamp1 - delayTime);
		LocalDate date2 = DateTimeUtils.getDateOfTimestamp(timestamp2 - delayTime);
		return DateTimeUtils.isSameDay(date1, date2);
	}

	/**
	 * 是否是同一周
	 * @param timestamp1 时间戳
	 * @param timestamp2 时间戳
	 * @return boolean
	 */
	public static boolean isSameWeek(long timestamp1, long timestamp2) {
		LocalDate date1 = DateTimeUtils.getDateOfTimestamp(timestamp1);
		LocalDate date2 = DateTimeUtils.getDateOfTimestamp(timestamp2);
		// 都将日期拉到周一再进行比较
		LocalDate monday1 = DateTimeUtils.getMondayOfWeek(date1);
		LocalDate monday2 = DateTimeUtils.getMondayOfWeek(date2);
		return DateTimeUtils.isSameDay(monday1, monday2);
	}

	/**
	 * 是否同是星期几
	 * @param timestamp1 时间戳
	 * @param timestamp2 时间戳
	 * @return boolean
	 */
	public static boolean isSameWeekDay(long timestamp1, long timestamp2) {
		LocalDate date1 = DateTimeUtils.getDateOfTimestamp(timestamp1);
		LocalDate date2 = DateTimeUtils.getDateOfTimestamp(timestamp2);
		return date1.getDayOfWeek().getValue() == date2.getDayOfWeek().getValue();
	}

	/**
	 * 是否是指定的星期数
	 * @param timestamp 时间戳
	 * @param week 指定星期数
	 * @return boolean
	 */
	public static boolean isDayOfWeek(long timestamp, DayOfWeek week) {
		LocalDate date = DateTimeUtils.getDateOfTimestamp(timestamp);
		return date.getDayOfWeek().getValue() == week.getValue();
	}

	/**
	 * 是否是同一个月
	 * @param timestamp1 时间戳
	 * @param timestamp2 时间戳
	 * @return boolean
	 */
	public static boolean isSameMonth(long timestamp1, long timestamp2) {
		LocalDate date1 = DateTimeUtils.getDateOfTimestamp(timestamp1);
		LocalDate date2 = DateTimeUtils.getDateOfTimestamp(timestamp2);
		// 将日期拉到1日再进行比较
		LocalDate firstDay1 = DateTimeUtils.getFirstDayOfMonth(date1);
		LocalDate firstDay2 = DateTimeUtils.getFirstDayOfMonth(date2);
		return DateTimeUtils.isSameDay(firstDay1, firstDay2);
	}

	/**
	 * 获取指定时间戳的当日零点
	 * @param timestamp 时间戳
	 * @return 时间戳
	 */
	public static long getZeroOfDay(long timestamp) {
		LocalDate date = DateTimeUtils.getDateOfTimestamp(timestamp);
		return DateTimeUtils.getTimestampOfDateTime(DateTimeUtils.getZeroOfDay(date));
	}

	/**
	 * 获取指定时间戳的下一天零点
	 * @param timestamp 时间戳
	 * @return 时间戳
	 */
	public static long getNextZeroOfDay(long timestamp) {
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfTimestamp(timestamp);
		LocalDate nextDay = DateTimeUtils.getNextDayOfDateTime(dateTime, 1).toLocalDate();
		return DateTimeUtils.getTimestampOfDateTime(nextDay.atStartOfDay());
	}

	/**
	 * 获取指定时间戳的下n天
	 * @param timestamp 时间戳
	 * @return 时间戳
	 */
	public static long getNextDayOfTimestamp(long timestamp, int days) {
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfTimestamp(timestamp);
		return DateTimeUtils.getTimestampOfDateTime(DateTimeUtils.getNextDayOfDateTime(dateTime, days));
	}

	/**
	 * 获取今天某个时间的时间戳
	 * @param timeStr 时间字符串
	 * @param pattern 格式
	 * @return 时间戳，失败返回0
	 */
	public static long getTimestampOfTodayTime(String timeStr, String pattern) {
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfTodayTime(timeStr, pattern);
		if (dateTime == null) {
			return 0L;
		}
		return DateTimeUtils.getTimestampOfDateTime(dateTime);
	}

	/**
	 * 无日期时间毫秒数
	 * @param timeStr 时间字符串
	 * @param pattern 格式
	 * @return 时间毫秒数
	 */
	public static long getMilliOfTime(String timeStr, String pattern) {
		LocalTime time = DateTimeUtils.parseTime(timeStr, pattern);
		if (time == null) {
			return 0L;
		}
		// 1970-01-01
		LocalDate date = LocalDateTime.ofInstant(Instant.EPOCH, ZONE_ID).toLocalDate();
		return DateTimeUtils.getTimestampOfDateTime(time.atDate(date));
	}

	/**
	 * 获取日期的毫秒数
	 * @param dateStr 日期字符串
	 * @param pattern 格式
	 * @return 时间毫秒数
	 */
	public static long getMilliOfDate(String dateStr, String pattern) {
		LocalDate date = DateTimeUtils.parseDate(dateStr, pattern);
		if (date == null) {
			return 0L;
		}
		return DateTimeUtils.getTimestampOfDateTime(date.atStartOfDay());
	}

	/**
	 * 获取时间戳指定日期的日期时间
	 * @param timestamp 时间戳
	 * @param timeStr 时间字符串
	 * @param pattern 格式
	 * @return 时间戳，失败返回0
	 */
	public static long getTimestampOfPointTime(long timestamp, String timeStr, String pattern) {
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfDayTime(timestamp, timeStr, pattern);
		if (dateTime == null) {
			return 0L;
		}
		return DateTimeUtils.getTimestampOfDateTime(dateTime);
	}

	/**
	 * 获取时间戳指定日期的日期时间
	 * @param timestamp 时间戳
	 * @param hour 小时
	 * @param minute 分支
	 * @param second 秒
	 * @return 时间戳
	 */
	public static long getTimestampOfPointTime(long timestamp, int hour, int minute, int second) {
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfDayTime(timestamp, hour, minute, second);
		return DateTimeUtils.getTimestampOfDateTime(dateTime);
	}

	/**
	 * 获取指定时间戳的周一零点
	 * @param timestamp 时间戳
	 * @return LocalDate
	 */
	public static long getMondayZeroOfWeek(long timestamp) {
		LocalDate date = DateTimeUtils.getDateOfTimestamp(timestamp);
		LocalDate monday = DateTimeUtils.getMondayOfWeek(date);
		return DateTimeUtils.getTimestampOfDateTime(monday.atStartOfDay());
	}

	/**
	 * 获取指定时间戳的周一的指定时间
	 * @param timestamp 时间戳
	 * @return 时间戳
	 */
	public static long getMondayTimeOfWeek(long timestamp) {
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfTimestamp(timestamp);
		LocalDateTime monday = DateTimeUtils.getMondayTimeOfWeek(dateTime);
		return DateTimeUtils.getTimestampOfDateTime(monday);
	}

	/**
	 * 获取指定时间戳的当月1号零点
	 * @param timestamp 时间戳
	 * @return LocalDate
	 */
	public static long getFirstDayZeroOfMonth(long timestamp) {
		LocalDate date = DateTimeUtils.getDateOfTimestamp(timestamp);
		LocalDate firstDay = DateTimeUtils.getFirstDayOfMonth(date);
		return DateTimeUtils.getTimestampOfDateTime(firstDay.atStartOfDay());
	}

	/**
	 * 获取指定时间戳的当月1号的指定时间
	 * @param timestamp 时间戳
	 * @return 时间戳
	 */
	public static long getFirstDayTimeOfMonth(long timestamp) {
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfTimestamp(timestamp);
		LocalDateTime firstDay = DateTimeUtils.getFirstDayTimeOfMonth(dateTime);
		return DateTimeUtils.getTimestampOfDateTime(firstDay);
	}

	public static void main(String[] args) {
		long time = getLastFirstDayTimeOfMonth(1585598400000L);
		System.out.println(formatTimestamp(time));
	}

	public static long getLastFirstDayTimeOfMonth(long timestamp) {
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfTimestamp(timestamp);
		LocalDateTime lastMonthFirstDay = dateTime.minusMonths(1).withDayOfMonth(1);
		return DateTimeUtils.getTimestampOfDateTime(lastMonthFirstDay);
	}

	/**
	 * 获取指定时间戳的当年1月1号的指定时间
	 * @param timestamp 时间戳
	 * @return 时间戳
	 */
	public static long getFirstDayTimeOfYear(long timestamp) {
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfTimestamp(timestamp);
		LocalDateTime firstDay = dateTime.withMonth(1).withDayOfMonth(1);
		return DateTimeUtils.getTimestampOfDateTime(firstDay.toLocalDate().atStartOfDay());
	}

	/**
	 * 获取指定时间戳的去年1月1号的指定时间
	 * @param timestamp 时间戳
	 * @return 时间戳
	 */
	public static long getLastFirstDayTimeOfYear(long timestamp) {
		LocalDateTime dateTime = DateTimeUtils.getDateTimeOfTimestamp(timestamp);
		LocalDateTime lastYearFistDay = dateTime.minusYears(1).withDayOfYear(1);
		return DateTimeUtils.getTimestampOfDateTime(lastYearFistDay);
	}


	/**
	 * 获取是星期几，周一到周日分别是 1 -> 7
	 * @param timestamp 时间戳
	 * @return DayOfWeek
	 */
	public static int getWeekDayOfTime(long timestamp) {
		LocalDate date = DateTimeUtils.getDateOfTimestamp(timestamp);
		return date.getDayOfWeek().getValue();
	}

	/**
	 * 根据参数获取quartz的执行时间格式
	 * @param day 注意:!!!!!!quartz cron中 周日至周六的数字是  1-7
	 * @param hour
	 * @param min
	 * @param second
	 * @return 如: 每周1,3,5 12:30 执行schedule 0 30 12 ? * 2,4,6 *
	 */
	public static String getQuartzCron(int[] day, int hour, int min, int second) {

		//0 30 12 ? * 2,4,6 *
		StringBuilder cron = new StringBuilder();

		if (second !=-1) {
			cron.append(second + " ");
		}else{
			cron.append("* ");
		}

		if (min !=-1) {
			cron.append(min + " ");
		}else{
			cron.append("* ");
		}

		if (hour != -1) {
			cron.append(hour +" ");
		}else{
			cron.append("* ");
		}
		cron.append("? ");
		cron.append("* ");

		if (day.length > 0 && day[0]!=-1) {
			for (int i=0; i< day.length; i++) {
				if (i > 0) {
					cron.append(",");
				}
				cron.append(day[i]);
			}
			cron.append(" ");
		}else{
			cron.append("* ");
		}
		cron.append("* ");
		return cron.toString();
	}

	/**
	 * 根据cron表达式计算出最近1次的执行具体时间
	 *
	 * @param cron 时间表达式
	 * @param day 需要计算的时间区间（可以为负数）
	 *
	 * @return
	 */
	public static Date lastFromCron(String cron, int day) {
		if (day == 0) {
			return null;
		}

		try {
			CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
			// 这里写要准备猜测的cron表达式
			cronTriggerImpl.setCronExpression(cron);

			Calendar calendar = Calendar.getInstance();
			Date now = calendar.getTime();

			// 设置统计区间
			calendar.add(Calendar.DAY_OF_MONTH, day);
			List<Date> dates = TriggerUtils.computeFireTimesBetween(
					cronTriggerImpl, null, now, calendar.getTime());
			if (dates == null || dates.size() <= 0) {
				return null;
			}

			if (day < 0) {
				return dates.get(dates.size() - 1);
			}else{
				return dates.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从指定时间开始已经执行了几次
	 *
	 * @param cron
	 * @param start
	 * @return
	 */
	public static int countFromCron(String cron, String start) {
		try {
			CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
			// 这里写要准备猜测的cron表达式
			cronTriggerImpl.setCronExpression(cron);

			Calendar calendar = Calendar.getInstance();
			Date now = calendar.getTime();

			calendar.setTimeInMillis(parseToTimestamp(start));
			List<Date> dates = TriggerUtils.computeFireTimesBetween(
					cronTriggerImpl, null, calendar.getTime(), now);
			if (dates == null || dates.size() <= 0) {
				return 0;
			}

			return dates.size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 把倒计时时间毫秒变成str，如300000转成5分0秒，最大支持天
	 * @param countDowmTime
	 * @return
	 */
	public static String parseCountDownStr(long countDowmTime){
		StringBuilder sb = new StringBuilder();
		long day = countDowmTime / TimeUtils.DAY;
		countDowmTime = countDowmTime % TimeUtils.DAY;
		if(day > 0){
			sb.append(day);
			sb.append("天");
		}

		long hour = countDowmTime / TimeUtils.HOUR;
		countDowmTime = countDowmTime % TimeUtils.HOUR;
		if(hour > 0){
			sb.append(hour);
			sb.append("小时");
		}

		long min = countDowmTime / TimeUtils.MIN;
		countDowmTime = countDowmTime % TimeUtils.MIN;
		if(min > 0){
			if(day > 0 && hour == 0){
				sb.append(hour);
				sb.append("小时");
			}
			sb.append(min);
			sb.append("分钟");
		}

		long sec = countDowmTime / TimeUtils.SEC;
		if(sec > 0){
			if((day > 0 || hour > 0) && min == 0){
				sb.append(min);
				sb.append("分钟");
			}
			sb.append(sec);
			sb.append("秒");
		}

		return sb.toString();
	}


	/**
	 * 日期时间相关工具
	 */
	private static class DateTimeUtils {

		//******************************************* 基本方法部分 ********************************************//
		/**
		 * 由时间戳获取dateTime
		 * @param timestamp 时间戳
		 * @return LocalDateTime
		 */
		private static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
			return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeUtils.ZONE_ID);
		}

		/**
		 * 由时间戳获取日期
		 * @param timestamp 时间戳
		 * @return LocalDate
		 */
		private static LocalDate getDateOfTimestamp(long timestamp) {
			return getDateTimeOfTimestamp(timestamp).toLocalDate();
		}

		/**
		 * 由时间戳获取时间
		 * @param timestamp 时间戳
		 * @return LocalTime
		 */
		private static LocalTime getTimeOfTimestamp(long timestamp) {
			return getDateTimeOfTimestamp(timestamp).toLocalTime();
		}

		/**
		 * 由日期时间获取时间戳
		 * @param dateTime 日期时间
		 * @return 时间戳
		 */
		private static long getTimestampOfDateTime(LocalDateTime dateTime) {
			return dateTime.atZone(TimeUtils.ZONE_ID).toInstant().toEpochMilli();
		}

		//****************************************** 字符串解析部分 ********************************************//
		/**
		 * 解析日期时间
		 * @param dateTimeStr 日期时间字符串
		 * @param pattern 格式
		 * @return 解析失败返回null
		 */
		private static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
			try {
				return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		/**
		 * 解析时间，不带日期
		 * @param timeStr 时间字符串
		 * @param pattern 格式
		 * @return 失败返回null
		 */
		private static LocalTime parseTime(String timeStr, String pattern) {
			try {
				return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern(pattern));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		/**
		 * 解析日期，不带时间
		 * @param dateStr 日期字符串
		 * @param pattern 格式
		 * @return 失败返回null
		 */
		private static LocalDate parseDate(String dateStr, String pattern) {
			try {
				return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		//************************************ 工具方法 ****************************************//S

		/**
		 * 是否是同一天
		 * @param date1 日期一
		 * @param date2 日期一
		 * @return boolean
		 */
		private static boolean isSameDay(LocalDate date1, LocalDate date2) {
			return date1.equals(date2);
		}

		/**
		 * 获取指定日期的当日零点
		 * @param date 日期
		 * @return LocalDateTime
		 */
		private static LocalDateTime getZeroOfDay(LocalDate date) {
			return date.atStartOfDay();
		}

		/**
		 * 获取指定日期时间的下n天
		 * @param dateTime 日期时间
		 * @param days 天数
		 * @return LocalDateTime
		 */
		private static LocalDateTime getNextDayOfDateTime(LocalDateTime dateTime, int days) {
			return dateTime.plusDays(days);
		}

		/**
		 * 获取今天某个时间的日期时间
		 * @param timeStr 时间字符串
		 * @param pattern 格式
		 * @return 失败返回null
		 */
		private static LocalDateTime getDateTimeOfTodayTime(String timeStr, String pattern) {
			LocalTime time = parseTime(timeStr, pattern);
			if (time == null) {
				return null;
			}
			return time.atDate(LocalDate.now(TimeUtils.ZONE_ID));
		}

		/**
		 * 获取时间戳指定日期的日期时间
		 * @param timestamp 时间戳
		 * @param timeStr 时间字符串
		 * @param pattern 格式
		 * @return 失败返回null
		 */
		private static LocalDateTime getDateTimeOfDayTime(long timestamp, String timeStr, String pattern) {
			LocalTime time = parseTime(timeStr, pattern);
			if (time == null) {
				return null;
			}
			LocalDate date = getDateOfTimestamp(timestamp);
			return date.atTime(time);
		}

		/**
		 * 获取时间戳指定日期的日期时间
		 * @param timestamp 时间戳
		 * @param hour 小时
		 * @param minute 分支
		 * @param second 秒
		 * @return LocalDateTime
		 */
		private static LocalDateTime getDateTimeOfDayTime(long timestamp, int hour, int minute, int second) {
			LocalDate date = getDateOfTimestamp(timestamp);
			return date.atTime(hour, minute, second);
		}

		/**
		 * 获取指定日期的周一
		 * @param date 日期
		 * @return LocalDate
		 */
		private static LocalDate getMondayOfWeek(LocalDate date) {
			return date.minusDays(date.getDayOfWeek().getValue() - 1);
		}

		/**
		 * 获取指定日期的当月1号
		 * @param date 日期
		 * @return LocalDate
		 */
		private static LocalDate getFirstDayOfMonth(LocalDate date) {
			return date.withDayOfMonth(1);
		}

		/**
		 * 获取指定日期时间的周一的指定时间
		 * @param dateTime 日期时间
		 * @return LocalDateTime
		 */
		private static LocalDateTime getMondayTimeOfWeek(LocalDateTime dateTime) {
			return dateTime.minusDays(dateTime.getDayOfWeek().getValue() - 1);
		}

		/**
		 * 获取指定日期时间的当月1号的指定时间
		 * @param dateTime 日期时间
		 * @return LocalDateTime
		 */
		private static LocalDateTime getFirstDayTimeOfMonth(LocalDateTime dateTime) {
			return dateTime.withDayOfMonth(1);
		}

		/**
		 * 获取指定日期时间的当月1号的指定时间
		 * @param dateTime 日期时间
		 * @return LocalDateTime
		 */
		private static LocalDateTime getLastFirstDayTimeOfMonth(LocalDateTime dateTime) {
			dateTime = dateTime.minusMonths(1);
			return dateTime.withDayOfMonth(1);
		}
	}
}

