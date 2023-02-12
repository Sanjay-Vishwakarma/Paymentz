package com.manager;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.vo.merchantmonitoring.DateVO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created by admin on 4/6/2016.
 */
public class DateManager
{
    private static Logger logger=new Logger(DateManager.class.getName());
    private static Calendar getCalendarForNow()
    {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }
    public static void setTimeToBeginningOfDay(Calendar calendar)
    {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
    public static void setTimeToEndofDay(Calendar calendar)
    {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }
    public static void main(String args[])
    {
        DateManager  dateManager=new DateManager();

        List<DateVO> dateVOList = dateManager.getMonthlyLineChartQuarter();
        for (DateVO dateVO : dateVOList)
        {
           /* System.out.println("==================================");
            System.out.println("Start Date:" + dateVO.getStartDate());
            System.out.println("Start End :" + dateVO.getEndDate());
            System.out.println("Start End :" + dateVO.getDateLabel());
            System.out.println("==================================");*/
        }

        //DateVO dateVO=dateManager.getCalendarCurrentWeekDateRange();
        //System.out.println("start date===="+dateVO.getStartDate());
        //System.out.println("end date===="+dateVO.getEndDate());
        //DateManager dateManager = new DateManager();
        //DateVO dateVO =  dateManager.getLastSixMonthDateRange();
        //System.out.println("start date ====="+dateVO.getStartDate());
        //System.out.println("End date ====="+dateVO.getEndDate());
    }

    public static int monthsBetweenDates(Date startDate, Date endDate)
    {

        Calendar start = Calendar.getInstance();
        start.setTime(startDate);

        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        int monthsBetween = 0;
        int dateDiff = end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH);

        if (dateDiff < 0)
        {
            int borrrow = end.getActualMaximum(Calendar.DAY_OF_MONTH);
            dateDiff = (end.get(Calendar.DAY_OF_MONTH) + borrrow) - start.get(Calendar.DAY_OF_MONTH);
            monthsBetween--;

            if (dateDiff > 0)
            {
                monthsBetween++;
            }
        }
        else
        {
            monthsBetween++;
        }
        monthsBetween += end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
        monthsBetween += (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
        return monthsBetween;
    }

    public DateVO getCurrentMonthDateRange()
    {
        Date begining, end;
        String begining1 = "";
        String end1 = "";
        DateVO dateVO=new DateVO();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        {
            Calendar calendar = getCalendarForNow();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);
            begining = calendar.getTime();
        }
        {
            Calendar calendar = getCalendarForNow();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndofDay(calendar);
            end = calendar.getTime();
        }
        begining1 = simpleDateFormat.format(begining);
        end1 = simpleDateFormat.format(end);

        dateVO.setStartDate1(begining);
        dateVO.setEndDate1(end);
        dateVO.setStartDate(begining1);
        dateVO.setEndDate(end1);
        return dateVO;
    }

    public DateVO getPreviousMonthDateRange()
    {
        Date begining, end;
        String begining1 = "";
        String end1 = "";
        DateVO dateVO=new DateVO();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        {
            Calendar calendar = getCalendarForNow();
            calendar.add(Calendar.MONTH, -1);
            calendar.set(Calendar.DATE, 1);
            setTimeToBeginningOfDay(calendar);
            begining = calendar.getTime();
        }
        {
            Calendar calendar = getCalendarForNow();
            calendar.add(Calendar.MONTH, -1);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndofDay(calendar);
            end = calendar.getTime();
        }
        begining1 = simpleDateFormat.format(begining);
        end1 = simpleDateFormat.format(end);

        dateVO.setStartDate1(begining);
        dateVO.setEndDate1(end);
        dateVO.setStartDate(begining1);
        dateVO.setEndDate(end1);
        return dateVO;
    }

    public DateVO getCalendarCurrentWeekDateRange_Old()
    {
        DateVO dateVO = new DateVO();
        Calendar c = GregorianCalendar.getInstance();
        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        // Print dates of the current week starting on Monday
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String startDate = "", endDate = "";

        startDate = df.format(c.getTime());
        c.add(Calendar.DATE, 6);
        endDate = df.format(c.getTime());

        startDate=startDate.split(" ")[0];
        endDate=endDate.split(" ")[0];

        dateVO.setStartDate(startDate+" 00:00:00");
        dateVO.setEndDate(endDate+" 23:59:59");

        return dateVO;
    }

    /*public DateVO getNonCalendarCurrentWeekDateRange()
    {
        Calendar c = GregorianCalendar.getInstance();
        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Print dates of the current week starting on Monday
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String startDate = "",endDate = "";

        startDate = df.format(c.getTime());
        c.add(Calendar.DATE, 6);
        endDate = df.format(c.getTime());

        DateVO dateVO = new DateVO();
        dateVO.setStartDate(startDate);
        dateVO.setEndDate(endDate);

        return dateVO;
    }*/
    public DateVO getCurrentDayDateRange()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO = new DateVO();
        Date begining, end;
        {
            Calendar c = getCalendarForNow();
            setTimeToBeginningOfDay(c);
            begining = c.getTime();
        }
        {
            Calendar c = getCalendarForNow();
            setTimeToEndofDay(c);
            end = c.getTime();
        }
        dateVO.setStartDate(simpleDateFormat.format(begining));
        dateVO.setEndDate(simpleDateFormat.format(end));
        return dateVO;
    }

    public DateVO getPreviousDayDateRange()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO = new DateVO();
        Date begining, end;
        {
            Calendar c = getCalendarForNow();
            c.add(Calendar.DATE, -1);
            setTimeToBeginningOfDay(c);
            begining = c.getTime();
        }
        {
            Calendar c = getCalendarForNow();
            c.add(Calendar.DATE, -1);
            setTimeToEndofDay(c);
            end = c.getTime();
        }
        dateVO.setStartDate(simpleDateFormat.format(begining));
        dateVO.setEndDate(simpleDateFormat.format(end));
        return dateVO;
    }

    public DateVO getLastSixCalendarMonthDateRange()
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO=new DateVO();
        Date beginning,end;
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -6);
            setTimeToBeginningOfDay(cal);
            cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
            beginning=cal.getTime();
        }
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            setTimeToEndofDay(cal);
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            end=cal.getTime();
        }
        dateVO.setStartDate(targetFormat.format(beginning));
        dateVO.setEndDate(targetFormat.format(end));
        return dateVO;
    }

    public DateVO getLastSixCalendarMonthDateRangeForGraph()
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO = new DateVO();
        Date beginning, end;
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -5);
            setTimeToBeginningOfDay(cal);
            cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
            beginning = cal.getTime();
        }
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -0);
            setTimeToEndofDay(cal);
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            end = cal.getTime();
        }
        dateVO.setStartDate(targetFormat.format(beginning));
        dateVO.setEndDate(targetFormat.format(end));
        return dateVO;
    }

    public DateVO getLastSixMonthDateRange()
    {
        Date beginning,end;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO=new DateVO();
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -5);
            setTimeToBeginningOfDay(cal);
            cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
            beginning=cal.getTime();
        }
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -0);
            setTimeToEndofDay(cal);
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            end=cal.getTime();
        }
        dateVO.setStartDate(targetFormat.format(beginning));
        dateVO.setEndDate(targetFormat.format(end));
        return dateVO;
    }

    public DateVO getLastThreeCalendarMonthDateRange()
    {
        Date beginning,end;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO=new DateVO();
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -3);
            setTimeToBeginningOfDay(cal);
            cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
            beginning=cal.getTime();
        }
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            setTimeToEndofDay(cal);
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            end=cal.getTime();
        }
        dateVO.setStartDate(targetFormat.format(beginning));
        dateVO.setEndDate(targetFormat.format(end));
        return dateVO;
    }

    public DateVO getLastTwoCalendarMonthDateRange()
    {
        Date beginning,end;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO=new DateVO();
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -2);
            setTimeToBeginningOfDay(cal);
            cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
            beginning=cal.getTime();
        }
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            setTimeToEndofDay(cal);
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            end=cal.getTime();
        }
        dateVO.setStartDate(targetFormat.format(beginning));
        dateVO.setEndDate(targetFormat.format(end));
        return dateVO;
    }

    public DateVO getLastMonthCalendarMonthDateRange()
    {
        Date beginning,end;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO=new DateVO();
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            setTimeToBeginningOfDay(cal);
            cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
            beginning=cal.getTime();
        }
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            setTimeToEndofDay(cal);
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            end=cal.getTime();
        }
        dateVO.setStartDate(targetFormat.format(beginning));
        dateVO.setEndDate(targetFormat.format(end));
        return dateVO;
    }

    public DateVO getLastOneDayCalendarMonthDateRange()
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO=new DateVO();
        Date beginning,end;
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -1);
            setTimeToBeginningOfDay(cal);
            beginning=cal.getTime();
        }
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -1);
            setTimeToEndofDay(cal);
            end=cal.getTime();
        }
        dateVO.setStartDate(targetFormat.format(beginning));
        dateVO.setEndDate(targetFormat.format(end));
        return dateVO;
    }

    public DateVO getLastTwoDayCalendarMonthDateRange()
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO=new DateVO();
        Date beginning,end;
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -2);
            setTimeToBeginningOfDay(cal);
            beginning=cal.getTime();
        }
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -2);
            setTimeToEndofDay(cal);
            end=cal.getTime();
        }
        dateVO.setStartDate(targetFormat.format(beginning));
        dateVO.setEndDate(targetFormat.format(end));
        return dateVO;
    }

    public DateVO getPreviousDayDateRange(int days)
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO=new DateVO();
        Date beginning,end;
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, (days*-1));
            setTimeToBeginningOfDay(cal);
            beginning=cal.getTime();
        }
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR,(days*-1));
            setTimeToEndofDay(cal);
            end=cal.getTime();
        }
        dateVO.setStartDate(targetFormat.format(beginning));
        dateVO.setEndDate(targetFormat.format(end));
        return dateVO;
    }

    public List<DateVO> getWeeklyLineChartQuarter()
    {
        DateManager dateManager=new DateManager();
        List<DateVO> dateVOs = new LinkedList();

        String weekArray[]={"","Mon","Tue","Wed","Thu","Fri","Sat","Sun"};

        Calendar cal = Calendar.getInstance();
        Date beginning=cal.getTime();

        int dayOfWeek=beginning.getDay();
        if(dayOfWeek==0)
        {
            dayOfWeek=7;
        }
        for(int i=1;i<=dayOfWeek;i++)
        {
            int daysBack=dayOfWeek-i;
            DateVO vo=dateManager.getPreviousDayDateRange(daysBack);
            vo.setDateLabel(weekArray[i]);
            dateVOs.add(vo);
        }
        return dateVOs;
    }

    public DateVO getCalendarCurrentWeekDateRange()
    {
        DateManager dateManager=new DateManager();
        Calendar cal = Calendar.getInstance();
        Date beginning=cal.getTime();
        String startDate="";
        String endDate="";
        int dayOfWeek=beginning.getDay();
        if(dayOfWeek==0){
            dayOfWeek=7;
        }
        for(int i=1;i<=dayOfWeek;i++){
            int daysBack=dayOfWeek-i;
            DateVO vo=dateManager.getPreviousDayDateRange(daysBack);
            if(i==1){
                startDate=vo.getStartDate();
            }
            if(i==dayOfWeek){
                endDate=vo.getEndDate();
            }
        }
        DateVO dateVO=new DateVO();
        dateVO.setStartDate(startDate);
        dateVO.setEndDate(endDate);
        return dateVO;
    }

    public DateVO getPreviousWeekDateRange()
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO = new DateVO();
        Date begining, end;
        {
            Calendar cal = getCalendarForNow();
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + 1);
            setTimeToBeginningOfDay(cal);
            begining = cal.getTime();
        }
        {
            Calendar cal = getCalendarForNow();
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            setTimeToEndofDay(cal);
            end = cal.getTime();
        }
        dateVO.setStartDate(targetFormat.format(begining));
        dateVO.setEndDate(targetFormat.format(end));
        return dateVO;
    }

    public List<DateVO> getWeeksOfMonth()
    {
        List<DateVO> dateVOs = new LinkedList();

        DateVO week1DateVO = new DateVO();
        DateVO week2DateVO = new DateVO();
        DateVO week3DateVO = new DateVO();
        DateVO week4DateVO = new DateVO();

        String weekLabel1 = "Week 1";
        String startDateWeek1 = "2017-01-01 00:00:00";
        String endDateWeek1 = "2017-01-07 23:59:59";

        String weekLabel2 = "Week 2";
        String startDateWeek2 = "2017-01-08 00:00:00";
        String endDateWeek2 = "2017-01-14 23:59:59";

        String weekLabel3 = "Week 3";
        String startDateWeek3 = "2017-01-15 00:00:00";
        String endDateWeek3 = "2017-01-21 23:59:59";

        String weekLabel4 = "Week 4";
        String startDateWeek4 = "2017-01-22 00:00:00";
        String endDateWeek4 = "2017-01-28 23:59:59";

        week1DateVO.setDateLabel(weekLabel1);
        week1DateVO.setStartDate(startDateWeek1);
        week1DateVO.setEndDate(endDateWeek1);

        week2DateVO.setDateLabel(weekLabel2);
        week2DateVO.setStartDate(startDateWeek2);
        week2DateVO.setEndDate(endDateWeek2);

        week3DateVO.setDateLabel(weekLabel3);
        week3DateVO.setStartDate(startDateWeek3);
        week3DateVO.setEndDate(endDateWeek3);

        week4DateVO.setDateLabel(weekLabel4);
        week4DateVO.setStartDate(startDateWeek4);
        week4DateVO.setEndDate(endDateWeek4);

        dateVOs.add(week1DateVO);
        dateVOs.add(week2DateVO);
        dateVOs.add(week3DateVO);
        dateVOs.add(week4DateVO);

        return dateVOs;
    }

    public List<DateVO> getWeeksOfMonthNew_Old()
    {
        List<DateVO> dateVOs = new LinkedList();
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int numberOfDaysInWeek = 7;
        int totalWeek = dayOfMonth/numberOfDaysInWeek;
        int totalWeekMod = dayOfMonth%numberOfDaysInWeek;
        if(totalWeekMod > 0)
        {
            totalWeek = totalWeek+1;
        }
        int i = 0;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while(i < totalWeek)
        {
            DateVO weekDateVO = new DateVO();
            Date beginning,end;
            {
                Calendar calStart = Calendar.getInstance();
                calStart.set(Calendar.WEEK_OF_MONTH, i);
                calStart.add(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                setTimeToBeginningOfDay(calStart);
                beginning = calStart.getTime();
            }
            {
                Calendar calEnd = Calendar.getInstance();
                calEnd.set(Calendar.WEEK_OF_MONTH, i);
                calEnd.add(Calendar.DAY_OF_WEEK,Calendar.SATURDAY+1);
                setTimeToEndofDay(calEnd);
                end = calEnd.getTime();
            }

            i = i + 1;
            String  startDate = targetFormat.format(beginning);
            String  endDate = targetFormat.format(end);
            String weekLabel1 = "Week "+ i;
            weekDateVO.setDateLabel(weekLabel1);
            weekDateVO.setStartDate(startDate);
            weekDateVO.setEndDate(endDate);
            dateVOs.add(weekDateVO);

            /*System.out.println("======== start week-" + i + "=========");
            System.out.println(weekLabel1);
            System.out.println(startDate);
            System.out.println(endDate);
            System.out.println("======== end week-"+i+"=========");*/
        }

        return dateVOs;
    }

    public List<DateVO> getWeeksOfMonthNew()
    {
        List<DateVO> dateVOs = new LinkedList();
        Calendar c = Calendar.getInstance();
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        int numberOfDaysInWeek = 7;
        int totalWeeks = dayOfMonth / numberOfDaysInWeek;
        int totalWeekMod = dayOfMonth % numberOfDaysInWeek;
        if (totalWeekMod > 0)
        {
            totalWeeks = totalWeeks + 1;
        }
        int i = 1;
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (i <= totalWeeks)
        {
            int weekEndDay = i * numberOfDaysInWeek;
            int weekStartDay = weekEndDay - 6;
            DateVO weekDateVO = new DateVO();
            Date beginning, end;
            {
                Calendar calStart = Calendar.getInstance();
                calStart.set(Calendar.DAY_OF_MONTH, weekStartDay);
                setTimeToBeginningOfDay(calStart);
                beginning = calStart.getTime();
            }
            {
                Calendar calEnd = Calendar.getInstance();
                calEnd.set(Calendar.DAY_OF_MONTH, i * numberOfDaysInWeek);
                setTimeToEndofDay(calEnd);
                end = calEnd.getTime();
            }
            String startDate = targetFormat.format(beginning);
            String endDate = targetFormat.format(end);

            DateVO previousMonthDateVO = getPreviousCalendarMonthDateRange(1);
            weekDateVO.setPreviousMonthStartDate(previousMonthDateVO.getStartDate());
            weekDateVO.setPreviousMonthEndDate(previousMonthDateVO.getEndDate());

            String weekLabel1 = "Week-" + i;
            weekDateVO.setDateLabel(weekLabel1);
            weekDateVO.setStartDate(startDate);
            weekDateVO.setEndDate(endDate);
            dateVOs.add(weekDateVO);

            /*System.out.println("======== start week-"+i+"=========");
            System.out.println(weekLabel1);
            System.out.println(startDate);
            System.out.println(endDate);
            System.out.println("======== end week-"+i+"=========");*/

            i = i + 1;
        }
        return dateVOs;
    }

/*
    public List<DateVO> getMonthlyLineChartQuarter()
    {
        List<DateVO> dateVOs = new LinkedList();
        String monthArray[]={"Jan","Feb","Mar","Apr","May","Jun","July","Aug","Sept","Oct","Nov","Dec"};

        int totalMonth=12;
        Calendar cal = Calendar.getInstance();
        Date beginning=cal.getTime();

        int currentMonthNumber=beginning.getMonth();
        int sixMonthCounter=1;
        for(int i=5;i>=0;i--)
        {
            int k=totalMonth-i;
            if(k>=totalMonth)
            {
                k=0;
            }
            DateVO vo=getPreviousCalendarMonthDateRange(i);
            DateVO vo1 =getPreviousCalendarMonthDateRange(i + 1);
            DateVO vo2 =getPreviousThreeCalendarMonthDateRange(i + 3);

            vo.setDateLabel(monthArray[k]);
            vo.setPreviousMonthStartDate(vo1.getStartDate());
            vo.setPreviousMonthEndDate(vo1.getEndDate());
            vo.setLastThreeMonthStartDate(vo2.getStartDate());
            vo.setLastThreeMonthEndDate(vo2.getEndDate());

            dateVOs.add(vo);
            sixMonthCounter=sixMonthCounter+1;
        }
        return dateVOs;
    }
*/
    public List<DateVO> getMonthlyLineChartQuarter()
    {
        List<DateVO> dateVOs = new LinkedList();
        HashMap<String, String> monthNumberNameMap = new HashMap();
        monthNumberNameMap.put("01", "Jan");
        monthNumberNameMap.put("02", "Feb");
        monthNumberNameMap.put("03", "Mar");
        monthNumberNameMap.put("04", "Apr");
        monthNumberNameMap.put("05", "May");
        monthNumberNameMap.put("06", "Jun");
        monthNumberNameMap.put("07", "July");
        monthNumberNameMap.put("08", "Aug");
        monthNumberNameMap.put("09", "Sept");
        monthNumberNameMap.put("10", "Oct");
        monthNumberNameMap.put("11", "Nov");
        monthNumberNameMap.put("12", "Dec");

        int totalMonth=12;
        Calendar cal = Calendar.getInstance();
        Date beginning=cal.getTime();

        int currentMonthNumber=beginning.getMonth();
        int sixMonthCounter=1;
        int k=0;
        for(int i=5;i>=0;i--)
        {
            if(i==5)
            {
                k = (totalMonth - currentMonthNumber) + 1;
            }
            if(k>=totalMonth)
            {
                k=0;
            }
            DateVO vo = getPreviousCalendarMonthDateRange(i);
            DateVO vo1 = getPreviousCalendarMonthDateRange(i + 1);
            DateVO vo2 = getPreviousThreeCalendarMonthDateRange(i + 3);

            String currentDate = vo.getStartDate().split(" ")[0];
            //System.out.println("currentDate:"+currentDate);
            String monthNumber = currentDate.split("-")[1];
            //System.out.println("monthNumber::::"+monthNumber);
            //int labelNumber = Integer.parseInt(monthNumber.substring(1));


            vo.setDateLabel(monthNumberNameMap.get(monthNumber));
            vo.setPreviousMonthStartDate(vo1.getStartDate());
            vo.setPreviousMonthEndDate(vo1.getEndDate());
            vo.setLastThreeMonthStartDate(vo2.getStartDate());
            vo.setLastThreeMonthEndDate(vo2.getEndDate());

            dateVOs.add(vo);
            sixMonthCounter=sixMonthCounter+1;
            k=k+1;
        }
        return dateVOs;
    }

    public DateVO getDateRangeNew(String dateFilter) throws ParseException
    {
        Functions functions= new Functions();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO= null;
        Calendar cal = Calendar.getInstance();

        if (functions.isValueNull(dateFilter) && dateFilter.equalsIgnoreCase("today"))
        {
            dateVO= new DateVO();
            DateManager.setTimeToBeginningOfDay(cal);
            Date start = cal.getTime();
            String startDateToday = simpleDateFormat.format(start);

            DateManager.setTimeToEndofDay(cal);
            Date end= cal.getTime();
            String endDate = simpleDateFormat.format(end);

            dateVO.setDateLabel("today");
            dateVO.setStartDate(startDateToday);
            dateVO.setEndDate(endDate);
        }
        if (functions.isValueNull(dateFilter) && dateFilter.equalsIgnoreCase("Last seven days"))
        {
            dateVO= new DateVO();
            DateManager.setTimeToEndofDay(cal);
            Date end= cal.getTime();
            String enddate= simpleDateFormat.format(end);

            cal.add(Calendar.DATE, -7);
            DateManager.setTimeToBeginningOfDay(cal);
            Date start = cal.getTime();

            String startdate = simpleDateFormat.format(start);
            dateVO.setStartDate(startdate);
            dateVO.setEndDate(enddate);
            dateVO.setDateLabel("Last seven days");
        }
        if (functions.isValueNull(dateFilter) && dateFilter.equalsIgnoreCase("Current month"))
        {
            DateVO vo= getCurrentMonthDateRange();
            dateVO= new DateVO();
            String startdate= vo.getStartDate();

            String lastdate= vo.getEndDate();
            dateVO.setStartDate(startdate);
            dateVO.setEndDate(lastdate);
            dateVO.setDateLabel("Current month");
        }
        if (functions.isValueNull(dateFilter) && dateFilter.equalsIgnoreCase("Last month"))
        {
            dateVO= new DateVO();

            cal.set(Calendar.DATE, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            DateManager.setTimeToEndofDay(cal);
            Date lastDateOfPreviousMonth = cal.getTime();
            String enddate= simpleDateFormat.format(lastDateOfPreviousMonth);

            cal.set(Calendar.DATE, 1);
            DateManager.setTimeToBeginningOfDay(cal);
            Date firstDateOfPreviousMonth = cal.getTime();
            String startdate= simpleDateFormat.format(firstDateOfPreviousMonth);
            dateVO.setStartDate(startdate);
            dateVO.setEndDate(enddate);
            dateVO.setDateLabel("Last month");
        }
        if (functions.isValueNull(dateFilter) && dateFilter.equalsIgnoreCase("Last six months"))
        {
            dateVO= new DateVO();
            DateManager.setTimeToEndofDay(cal);
            Date end= cal.getTime();

            cal.setTime(end);
            cal.add(Calendar.MONTH, -5);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            DateManager.setTimeToBeginningOfDay(cal);
            String startdate= simpleDateFormat.format(cal.getTime());

            String enddate= simpleDateFormat.format(end);

            dateVO.setStartDate(startdate);
            dateVO.setEndDate(enddate);
            dateVO.setDateLabel("Last six months");
        }
        return dateVO;
    }

    public List<DateVO> getDailyLineChartQuarters(int graphDivider)throws ParseException
    {
        Calendar cal = Calendar.getInstance();

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);

        int totalQuarters = hour / graphDivider;

        DateVO dateVO =getCurrentDayDateRange();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = simpleDateFormat.format(simpleDateFormat.parse(dateVO.getStartDate()));
        Calendar c = getCalendarForNow();


        c.add(Calendar.HOUR, -hour);
        c.add(Calendar.MINUTE, -minutes);
        c.add(Calendar.SECOND, -seconds);

        List<DateVO> dateVOs = new LinkedList();

        for (int i = 1; i <= totalQuarters; i++)
        {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");

            DateVO dateVO1 = new DateVO();

            dateVO1.setDateLabel(simpleDateFormat2.format(simpleDateFormat2.parse(Calendar.HOUR_OF_DAY + ":" + c.get(Calendar.MINUTE))));
            String firstDate = todayDate + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

            c.add(Calendar.HOUR, graphDivider);
            String endDate = todayDate + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

            dateVO1.setDateLabel1(simpleDateFormat2.format(simpleDateFormat2.parse(Calendar.HOUR_OF_DAY + ":" + c.get(Calendar.MINUTE))));

            String wellFormattedStartDate = simpleDateFormat1.format(simpleDateFormat1.parse(firstDate));
            String wellFormattedEndDate = simpleDateFormat1.format(simpleDateFormat1.parse(endDate));


            dateVO1.setStartDate(wellFormattedStartDate);
            dateVO1.setEndDate(wellFormattedEndDate);
            dateVOs.add(dateVO1);
        }
        return dateVOs;
    }

    public DateVO getPreviousCalendarMonthDateRange(int month)
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO = new DateVO();
        Date beginning,end;
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -month);
            setTimeToBeginningOfDay(cal);
            cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
            beginning=cal.getTime();
        }
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -month);
            setTimeToEndofDay(cal);
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            end=cal.getTime();
        }
        dateVO.setStartDate(targetFormat.format(beginning));
        dateVO.setEndDate(targetFormat.format(end));
        return dateVO;
    }

    public DateVO getPreviousThreeCalendarMonthDateRange(int month)
    {
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateVO dateVO=new DateVO();
        //System.out.println("month===="+month);
        Date beginning,end;
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -month);

            setTimeToBeginningOfDay(cal);
            cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
            beginning=cal.getTime();
        }
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, ((3 - month)-1));
            setTimeToEndofDay(cal);
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            end=cal.getTime();
        }
        dateVO.setStartDate(targetFormat.format(beginning));
        dateVO.setEndDate(targetFormat.format(end));
        return dateVO;
    }

    public String getWeeklyMonitorPeriod()
    {
        Calendar c=Calendar.getInstance();

        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        c.add(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());

        Date weekStart=c.getTime();
        c.add(Calendar.DATE,6);

        Date weekEnd=c.getTime();

        String firstDate=new SimpleDateFormat("dd").format(weekStart);
        String lastDate=new SimpleDateFormat("dd MMM,YYYY").format(weekEnd);
        String weeklyMonitorPeriod=firstDate+"-"+lastDate;

        return weeklyMonitorPeriod;
    }

    public String getLastWeeklyMonitorPeriod()
    {
        Calendar c = Calendar.getInstance();

        c.add(Calendar.WEEK_OF_YEAR, -1);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 1);

        Date weekStart = c.getTime();
        c.add(Calendar.DATE, 6);

        Date weekEnd = c.getTime();

        String firstDate=new SimpleDateFormat("dd").format(weekStart);
        String lastDate=new SimpleDateFormat("dd MMM,YYYY").format(weekEnd);
        String weeklyMonitorPeriod=firstDate+"-"+lastDate;

        return weeklyMonitorPeriod;
    }

    public List<DateVO> getTodaysLineChartQuarter(int graphDivider)throws ParseException
    {
        Calendar cal = Calendar.getInstance();

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);

        int totalQuarters = hour / graphDivider;

        DateVO dateVO =getCurrentDayDateRange();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = simpleDateFormat.format(simpleDateFormat.parse(dateVO.getStartDate()));

        Calendar c = getCalendarForNow();

        c.add(Calendar.HOUR, -hour);
        c.add(Calendar.MINUTE, -minutes);
        c.add(Calendar.SECOND, -seconds);

        List<DateVO> dateVOs = new LinkedList();

        for (int i = 1; i <= totalQuarters; i++)
        {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            DateVO dateVO1 = new DateVO();

            String firstDate = todayDate + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

            c.add(Calendar.HOUR, graphDivider);
            String endDate = todayDate + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

            String wellFormattedStartDate = simpleDateFormat1.format(simpleDateFormat1.parse(firstDate));
            String wellFormattedEndDate = simpleDateFormat1.format(simpleDateFormat1.parse(endDate));

            dateVO1.setStartDate(wellFormattedStartDate);
            dateVO1.setEndDate(wellFormattedEndDate);

            String time[]=wellFormattedEndDate.split(" ");
            String timeData[]=time[1].split(":");
            dateVO1.setDateLabel(timeData[0]+":"+timeData[1]);

            dateVOs.add(dateVO1);
        }
        return dateVOs;
    }

    public List<DateVO> getTodaysTimeForStackedBarChart(int graphDivider)throws ParseException
    {
        Calendar cal = Calendar.getInstance();

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);

        int totalQuarters = hour / graphDivider;

        DateVO dateVO =getCurrentDayDateRange();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = simpleDateFormat.format(simpleDateFormat.parse(dateVO.getStartDate()));
        Calendar c = getCalendarForNow();

        c.add(Calendar.HOUR, -hour);
        c.add(Calendar.MINUTE, -minutes);
        c.add(Calendar.SECOND, -seconds);

        List<DateVO> dateVOs = new LinkedList();

        for (int i = 1; i <= totalQuarters; i++)
        {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateVO dateVO1 = new DateVO();

            String firstDate = todayDate + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

            c.add(Calendar.HOUR, graphDivider);
            String endDate = todayDate + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + (c.get(Calendar.SECOND)-1);

            String wellFormattedStartDate = simpleDateFormat1.format(simpleDateFormat1.parse(firstDate));
            String wellFormattedEndDate = simpleDateFormat1.format(simpleDateFormat1.parse(endDate));

            dateVO1.setStartDate(wellFormattedStartDate);
            dateVO1.setEndDate(wellFormattedEndDate);

            String startTime[] = wellFormattedStartDate.split(" ");
            String startTimeData[] = startTime[1].split(":");

            String time[]=wellFormattedEndDate.split(" ");
            String timeData[]=time[1].split(":");
            dateVO1.setDateLabel(startTimeData[0]+":"+startTimeData[1]+" - "+timeData[0]+":"+timeData[1]);

            dateVOs.add(dateVO1);
        }
        return dateVOs;
    }

    public DateVO getWeeklyCompositeViewDateRange()
    {
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        DateVO dateVO =getPreviousDayDateRange(dayOfMonth);

        //System.out.println("start date===" + dateVO.getStartDate());
        //System.out.println("end date==="+dateVO.getEndDate());

        return new DateVO();
    }

    public int differenceInMonths(Date d1, Date d2)
    {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        int diff = 0;
        if (c2.after(c1))
        {
            while (c2.after(c1))
            {
                c1.add(Calendar.MONTH, 1);
                if (c2.after(c1))
                {
                    diff++;
                }
            }
        }
        else if (c2.before(c1))
        {
            while (c2.before(c1))
            {
                c1.add(Calendar.MONTH, -1);
                if (c1.before(c2))
                {
                    diff--;
                }
            }
        }
        return diff;
    }

    public List<DateVO> getMonthsDateRange(String startDate, String endDate)
    {
        List<DateVO> dateVO = new LinkedList<DateVO>();
        DateVO dateVO1 = null;
        Date begining, end;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            Date date1 = dateFormat.parse(startDate);
            Date date2 = dateFormat.parse(endDate);

            int months = monthsBetweenDates(date1, date2);
            Calendar cal = Calendar.getInstance();
            for (int i = 1; i <= months; i++)
            {
                if (i == 1){
                    cal.setTime(date1);
                }
                else{
                    cal.add(Calendar.MONTH, 1);
                }

                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                setTimeToBeginningOfDay(cal);
                begining = cal.getTime();
                String start = dateFormat.format(begining);

                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                setTimeToEndofDay(cal);
                end = cal.getTime();
                String stop = dateFormat.format(end);

                dateVO1 = new DateVO();
                if(!(i==1)) {
                    dateVO1.setStartDate(start);
                }
                else{
                    dateVO1.setStartDate(dateFormat.format(date1));
                }
                if(!(i == months)) {
                    dateVO1.setEndDate(stop);
                }
                else{
                    dateVO1.setEndDate(dateFormat.format(date2));
                }
                dateVO.add(dateVO1);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException--->", e);
        }
        return dateVO;
    }

    public String modifyDate(String action, String strDate, int hour, int min, int sec) throws Exception
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(strDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if ("Plus".equals(action))
        {
            calendar.add(Calendar.HOUR, hour);
            calendar.add(Calendar.MINUTE, min);
            calendar.add(Calendar.SECOND, sec);
        }
        else if ("Minus".equals(action))
        {
            calendar.add(Calendar.HOUR, -hour);
            calendar.add(Calendar.MINUTE, -min);
            calendar.add(Calendar.SECOND, -sec);
        }
        Date date1 = calendar.getTime();
        String modifiedDate = simpleDateFormat.format(date1);
        return modifiedDate;
    }

}