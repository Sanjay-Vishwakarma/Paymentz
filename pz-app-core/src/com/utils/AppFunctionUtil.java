package com.utils;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.enums.IdentificationType;
import com.enums.AddressType;
import com.enums.AppCurrencyEnum;
//import com.manager.enums.PZTransactionCurrency;
import com.manager.enums.PZTransactionCurrency;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/3/14
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppFunctionUtil
{   //common usage instance
    final static ResourceBundle RB2 = LoadProperties.getProperty("com.directi.pg.CountryList");
    final static ResourceBundle RB3 = LoadProperties.getProperty("com.directi.pg.merchant_category_code");
    final static ResourceBundle RB4 = LoadProperties.getProperty("com.directi.pg.EuCountryList");
    final static ResourceBundle RB5 = LoadProperties.getProperty("com.directi.pg.currency");
    final static ResourceBundle RB8 = LoadProperties.getProperty("com.directi.pg.countrycodenamepairlist");

    Functions functions = new Functions();
    private static Logger logger = new Logger(AppFunctionUtil.class.getName());

    //this method is for getting List of country code & country
    public static StringBuffer getCountryDetails(String selectedItem)
    {
        Functions functions = new Functions();

        StringBuffer optionList = new StringBuffer(" <option value=\"\">Select a Country</option>");
        Enumeration<String> country = RB2.getKeys();

        List<String> countryCode = Collections.list(country);
        TreeMap<String, String> stringTreeMap = new TreeMap<String, String>();
        for (String countryKey : countryCode)
        {
            String Key = countryKey;
            stringTreeMap.put(RB2.getString(Key), Key);
        }

        Set set = stringTreeMap.keySet();
        Iterator<String> stringIterator = set.iterator();
        while (stringIterator.hasNext())
        {
            String value = stringIterator.next();
            String key = stringTreeMap.get(value);

            if (functions.isValueNull(selectedItem) && key.startsWith(selectedItem))
            {
                optionList.append(" <option value='" + key + "' >" + value + "</option>");
            }
            else
            {
                optionList.append(" <option value='" + key + "' selected>" + value + "</option>");
            }
        }
        return optionList;

    }

    public static StringBuffer getCountryDetail(String selectedItem)
    {
        System.out.println("Selected Item:::"+selectedItem);
        Functions functions = new Functions();

        StringBuffer optionList = new StringBuffer(" <option value=\"\">Select a Country</option>");
        Enumeration<String> country = RB8.getKeys();

        List<String> countryCode = Collections.list(country);
        TreeMap<String, String> stringTreeMap = new TreeMap<String, String>();
        for (String countryKey : countryCode)
        {
            String Key = countryKey;
            stringTreeMap.put(RB8.getString(Key), Key);
        }

        Set set = stringTreeMap.keySet();
        Iterator<String> stringIterator = set.iterator();
        while (stringIterator.hasNext())
        {
            String value = stringIterator.next();
            String key = stringTreeMap.get(value);

            if (functions.isValueNull(selectedItem) && key.startsWith(selectedItem))
            {
                optionList.append(" <option value='" + key + "' selected>" + value + "</option>");
            }
            else
            {
                optionList.append(" <option value='" + key + "'>" + value + "</option>");
            }
        }
        return optionList;

    }
    /*public  String convertTimestampToDatepickerspecific(String timestamp)
    {
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-M0M-dd HH:mm:ss"); // "yy/MM/dd HH:mm:ss");
        SimpleDateFormat datePickerFormat = new SimpleDateFormat("dd/MM/yyyy"); // "dd/MM/yyyy HH:mm:ss");
        Date date = null;
        String datePicker = null;
        try
        {   if(functions.isValueNull(timestamp))
        {
            date=datePickerFormat.parse(timestamp);
            datePicker=timestampFormat.format(date);
            if("0000-00-00 00:00:00".equals(timestamp))
            {
                datePicker="00/00/0000";
            }
        }

        }
        catch (Exception e)
        {
            logger.error(" conversion error for timestamp to datepicker format::",e);
        }
        return datePicker;
    }*/

    //this method is for getting List of country code & country
    public static String getCountryDetailsForAPI(String selectedItem)
    {
        Functions functions = new Functions();

        Enumeration<String> country = RB2.getKeys();

        List<String> countryCode = Collections.list(country);
        TreeMap<String, String> stringTreeMap = new TreeMap<String, String>();
        for (String countryKey : countryCode)
        {
            if (countryKey.startsWith(selectedItem))
            {
                return countryKey;
            }
            else if (countryKey.endsWith(selectedItem))
            {
                return countryKey;
            }
            else if (RB2.getString(countryKey) != null && functions.isValueNull(selectedItem) && (RB2.getString(countryKey).trim().replaceAll("\\s", "")).equalsIgnoreCase(selectedItem.trim().replaceAll("\\s", "")))
            {
                return countryKey;
            }
        }

        return null;
    }

    public static String getMerchantCodeForAPI(String requestedMerchantCode)
    {
        Enumeration<String> country = RB3.getKeys();

        List<String> countryCode = Collections.list(country);
        for (String countryKey : countryCode)
        {
            if (countryKey.equals(requestedMerchantCode))
            {
                return countryKey;
            }
        }
        return null;
    }

    //EU company
    public static StringBuffer getCountryDetailsEU(String selectedItem)
    {
        Functions functions = new Functions();

        StringBuffer optionList = new StringBuffer(" <option value=\"\">Select a Country</option>");
        Enumeration<String> country = RB4.getKeys();

        List<String> countryCode = Collections.list(country);
        TreeMap<String, String> stringTreeMap = new TreeMap<String, String>();
        for (String countryKey : countryCode)
        {
            String Key = countryKey;
            stringTreeMap.put(RB4.getString(Key), Key);
        }

        Set set = stringTreeMap.keySet();
        Iterator<String> stringIterator = set.iterator();
        while (stringIterator.hasNext())
        {
            String value = stringIterator.next();
            String key = stringTreeMap.get(value);

            if (functions.isValueNull(selectedItem) && key.startsWith(selectedItem))
            {
                optionList.append(" <option value='" + key + "' selected>" + value + "</option>");
            }
            else
            {
                optionList.append(" <option value='" + key + "'>" + value + "</option>");
            }
        }
        return optionList;
    }

    public static String getCountryDetailsEUForAPI(String selectedItem)
    {
        Functions functions = new Functions();

        Enumeration<String> country = RB4.getKeys();

        List<String> countryCode = Collections.list(country);
        TreeMap<String, String> stringTreeMap = new TreeMap<String, String>();
        for (String countryKey : countryCode)
        {

            if (countryKey.startsWith(selectedItem))
            {
                return countryKey;
            }
            else if (countryKey.endsWith(selectedItem))
            {
                return countryKey;
            }
            else if (RB4.getString(countryKey) != null && functions.isValueNull(selectedItem) && (RB4.getString(countryKey).trim().replaceAll("\\s", "")).equalsIgnoreCase(selectedItem.trim().replaceAll("\\s", "")))
            {
                return countryKey;
            }
        }

        return null;
    }

    //this method is for getting country code
    public static String getCountry3LetterAbbreviation(String selectedItem)
    {
        Functions functions = new Functions();

        Enumeration<String> country = RB2.getKeys();
        List<String> countryCode = Collections.list(country);
        Collections.sort(countryCode);
        for (String countryKey : countryCode)
        {
            String Key = countryKey;
            if (functions.isValueNull(selectedItem) && Key.startsWith(selectedItem))
            {
                return Key.split("\\|")[3];
            }
        }

        return null;
    }

    //this method is for getting List of Merchant Category code & country
    public static StringBuffer getmerchant_category_code(String selectedItem, List<String> merchantCodeList)
    {
        Functions functions = new Functions();
        String selectMerchantCode = "";
        for(String merchantCode : merchantCodeList)
        {
            logger.debug("merchantCode from CommonFunctionUtil----"+merchantCode);
        }
        StringBuffer optionList = new StringBuffer();
        Enumeration<String> merchant_category_code = RB3.getKeys();
        List<String> merchant_category_code_enum = Collections.list(merchant_category_code);
        Collections.sort(merchant_category_code_enum);
        for (String merchant_country_code : merchant_category_code_enum)
        {
            String Key = merchant_country_code;
            if (merchantCodeList!=null && merchantCodeList.contains(Key))
            {
                optionList.append(" <option value=" + Key + " selected>" + Key.replaceAll("\\|.*", "") + "_" + RB3.getString(Key) + "</option>");
            }
            else
            {
                optionList.append(" <option value=" + Key + ">" + Key.replaceAll("\\|.*", "") + "_" + RB3.getString(Key) + "</option>");
            }

        }
        return optionList;
    }
    //this method is for getting List of Currency for step 3
    public static StringBuffer getCurrencyProductSold(String selectedItem, List<String> ProductSoldCurrencyList)
    {

        for(String currencySelected : ProductSoldCurrencyList)
        {
            logger.debug("Selected currency----"+currencySelected);
        }
        StringBuffer optionList = new StringBuffer();
        Enumeration<String> currencySelected = RB5.getKeys();
        List<String> currencySelected_enum = Collections.list(currencySelected);
        Collections.sort(currencySelected_enum);
        for (String currencySelected_code : currencySelected_enum)
        {
            String Key = currencySelected_code;
            if (ProductSoldCurrencyList!=null && ProductSoldCurrencyList.contains(Key))
            {
                optionList.append(" <option value=" + Key + " selected>" +  RB5.getString(Key) + "</option>");
            }
            else
            {
                optionList.append(" <option value=" + Key + ">"+ RB5.getString(Key) + "</option>");
            }

        }
        return optionList;
    }

    //this method is for getting List of Bank Currency for step 4
    public static StringBuffer getcurrencybank(String selectedItem, List<String> bankCurrencyList)
    {

        for(String bankcurrencySelected : bankCurrencyList)
        {
            logger.debug("Selected bank currency----"+bankcurrencySelected);
        }
        StringBuffer optionList = new StringBuffer();
        Enumeration<String> bankcurrencySelected = RB5.getKeys();
        List<String> bankcurrencySelected_enum = Collections.list(bankcurrencySelected);
        Collections.sort(bankcurrencySelected_enum);
        for (String bankcurrencySelected_code : bankcurrencySelected_enum)
        {
            String Key = bankcurrencySelected_code;
            if (bankCurrencyList!=null && bankCurrencyList.contains(Key))
            {
                optionList.append(" <option value=" + Key + " selected>" +  RB5.getString(Key) + "</option>");
            }
            else
            {
                optionList.append(" <option value=" + Key + ">"+ RB5.getString(Key) + "</option>");
            }

        }
        return optionList;
    }

    public static void main(String[] args)
    {
        String key = "1111|441";
        //System.out.println(key.replaceAll("\\|.*", ""));
    }

    //converting timestamp to datepicker 3 different parameters time
    public String convertTimestampToDatepicker(String timestamp)
    {
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "yy/MM/dd HH:mm:ss");
        SimpleDateFormat datePickerFormat = new SimpleDateFormat("dd/MM/yyyy"); // "dd/MM/yyyy HH:mm:ss");
        Date date = null;
        String datePicker = null;
        try
        {   if(functions.isValueNull(timestamp))
        {
            date=timestampFormat.parse(timestamp);
            datePicker=datePickerFormat.format(date);
            if ("0000-00-00 00:00:00".equals(timestamp))
            {
                datePicker = "00/00/0000";
            }
        }

        }
        catch (Exception e)
        {
            logger.error(" conversion error for timestamp to datepicker format::",e);
        }
        return datePicker;
    }

    public String convertDatepickerFromTimestamp(String timestamp)
    {
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "yy/MM/dd HH:mm:ss");
        SimpleDateFormat datePickerFormat = new SimpleDateFormat("dd/MM/yyyy"); // "dd/MM/yyyy HH:mm:ss");
        Date date = null;
        String datePicker = null;
        try
        {
            if (functions.isValueNull(timestamp))
        {
            date = timestampFormat.parse(timestamp);
            datePicker = datePickerFormat.format(date);

        }

        }
        catch (Exception e)
        {
            logger.error(" conversion error for timestamp to datepicker format::", e);
        }
        return datePicker;
    }

    //converting datepicker to timestamp 3 parameters time
    public String convertDatepickerToTimestamp(String datePicker, String hh, String mm, String ss)
    {
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "yy/MM/dd HH:mm:ss");
        SimpleDateFormat datePickerFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); // "dd/MM/yyyy HH:mm:ss");
        Date date = null;
        String timeStamp=null;
        if(functions.isValueNull(datePicker))
        {
            if("00/00/0000".equals(datePicker))
            {
                timeStamp="0000-00-00 00:00:00";
                return timeStamp;
            }
            datePicker = datePicker + " " + hh + ":" + mm + ":" + ss;
            try
            {
                date=datePickerFormat.parse(datePicker);
                timeStamp=timestampFormat.format(date);

            }
            catch (Exception e)
            {
                logger.error(" conversion error for datepicker to timestamp format::",e);
            }
        }
        return timeStamp;
    }

    //add  timestamp to daylight time
    public String addTimeStampWithDayLightTime(String timeStamp, String dayLightTime)
    {                                                        // "yy/MM/dd HH:mm:ss");
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "yy/MM/dd HH:mm:ss");
        SimpleDateFormat dayLightFormat = new SimpleDateFormat("HH:mm:ss"); // "dd/MM/yyyy HH:mm:ss");
        Date date = null;
        String totalTimestamp = null;
        try
        {
            //setting Timestamp to calender instance;
            Calendar cTimestamp = Calendar.getInstance();
            date = timestampFormat.parse(timeStamp);
            cTimestamp.setTime(date);
            //setting daylight time to calender instance;
            Calendar cDayLightTime = Calendar.getInstance();
            date = dayLightFormat.parse(dayLightTime);
            cDayLightTime.setTime(date);


            cTimestamp.add(Calendar.HOUR, cDayLightTime.get(Calendar.HOUR));
            cTimestamp.add(Calendar.MINUTE, cDayLightTime.get(Calendar.MINUTE));
            cTimestamp.add(Calendar.SECOND, cDayLightTime.get(Calendar.SECOND));

            totalTimestamp = timestampFormat.format(cTimestamp.getTime());
        }
        catch (Exception e)
        {
            logger.error(" timestamp adding error with day Light", e);
        }
        return totalTimestamp;
    }

    public String manageBankTimeDifference(String date, String time)//sandip
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        try
        {
            calendar.setTime(simpleDateFormat.parse(date));
        }
        catch (ParseException e)
        {
            logger.error("ParseException---",e);
        }

        String timeArr[] = time.split(":");

        String hour = timeArr[0];
        String min = timeArr[1];
        String sec = timeArr[2];

        calendar.add(Calendar.HOUR, Integer.parseInt(hour));
        calendar.add(Calendar.MINUTE, Integer.parseInt(min));
        calendar.add(Calendar.SECOND, Integer.parseInt(sec));

        String newDate = simpleDateFormat.format(calendar.getTime());

        return newDate;
    }

    // converting to datepicker using one parameter of time
    public String convertDatepickerToTimestamp(String datePicker, String time)
    {
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "yy/MM/dd HH:mm:ss");
        SimpleDateFormat datePickerFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); // "dd/MM/yyyy HH:mm:ss");
        Date date = null;
        String timeStamp = null;
        if (functions.isValueNull(datePicker))
        {
            if ("00/00/0000".equals(datePicker))
            {
                timeStamp = "0000-00-00 00:00:00";
                return timeStamp;
            }
            datePicker = datePicker + " " + time;
            try
            {
                date = datePickerFormat.parse(datePicker);
                timeStamp = timestampFormat.format(date);

            }
            catch (Exception e)
            {
                logger.error(" conversion error for datepicker to timestamp format::", e);
            }
        }
        return timeStamp;
    }

    //converting to Datepicker using one parameter of time (temporary)
    public String[] convertTimestampToDateTimePicker(String timestamp)
    {
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "yy/MM/dd HH:mm:ss");
        SimpleDateFormat datePickerFormat = new SimpleDateFormat("dd/MM/yyyy"); // "dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat timePickerformat = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        String datePicker = null;
        String time = null;
        String dateTiempicker[] = new String[2];
        try
        {
            date = timestampFormat.parse(timestamp);

            if ("0000-00-00 00:00:00".equals(timestamp))
            {
                datePicker = "00/00/0000";
                time = "00:00:00";
            }
            else
            {
                if (timestamp.contains("0000-00-00"))
                {
                    datePicker = "00/00/0000";
                }
                else
                {
                    datePicker = datePickerFormat.format(date);
                }
                time = timePickerformat.format(date);
                dateTiempicker[0] = datePicker;
                dateTiempicker[1] = time;
            }
        }
        catch (Exception e)
        {
            logger.error(" conversion error for timestamp to datepicker format::", e);
        }
        return dateTiempicker;
    }

    //comparing to date provided as String and resultant is Boolean
    public String compareTwoTimestamp(String comparedTimestamp, String withTimestamp, boolean Min)
    {
        int comparison = 0;
        boolean comparisonReturn = false;
        if (functions.isValueNull(comparedTimestamp) && functions.isValueNull(withTimestamp))
        {
            SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try
            {
                Date firstDate = timestampFormat.parse(comparedTimestamp);
                Date secondDate = timestampFormat.parse(withTimestamp);

                comparison = firstDate.compareTo(secondDate);
            }
            catch (Exception e)
            {
                logger.error("date parsing exception while comparing two date", e);
            }
            if (!Min)
            {
                if (comparison > 0)
                {
                    return comparedTimestamp;
                }
                else
                {
                    return withTimestamp;
                }
            }
            else
            {
                if (comparison > 0)
                {
                    return withTimestamp;
                }
                else
                {
                    return comparedTimestamp;
                }
            }
        }
        else
        {
            logger.error("Comparing two timestamp occurred with problem NullPointerException", new Exception("NullPointer Exception:: since one of the two timestamp contained null value,comparingDate::" + comparedTimestamp + "::comparedTo::" + withTimestamp));
            return comparedTimestamp;

        }
    }

    //comparison between two dates (not done from me)
    public String newValidateDate(String newStartD, String newEndD, String previousStartD, String previousEndD)
    {

        String message = null;
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date newStartDate = sdf.parse(newStartD);
            Date newEndDate = sdf.parse(newEndD);
            if (!newStartDate.before(newEndDate))
            {
                return message = "Invalid Date, End Date should not be less then Start Date.";
            }
            if (previousStartD != null && previousEndD != null)
            {
                //new start date-1 = previous end date;
                Date previousStartDate = sdf.parse(previousStartD);
                Date dateBefore = new Date(newStartDate.getTime() - 1 * 24 * 3600 * 1000);
                if (!dateBefore.after(previousStartDate))
                {
                    return message = "Invalid Date, New Start Date Should not be grater then Previous version Start Date. ";
                }
            }
        }
        catch (ParseException e)
        {
            logger.error("Date Parsing Exception", e);
        }

        return null;
    }

    public String newValidateDate1(String newStartD, String newEndD, String previousStartD, String previousEndD)
    {

        String message = null;
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date newStartDate = sdf.parse(newStartD);
            Date newEndDate = sdf.parse(newEndD);
            if (!newStartDate.before(newEndDate))
            {
                return message = "Invalid Date, End Date should not be less then Start Date.";
            }
            if (previousStartD != null && previousEndD != null)
            {
                //new start date-1 = previous end date;
                Date previousStartDate = sdf.parse(previousStartD);
                Date dateBefore = new Date(newStartDate.getTime() - 1 * 24 * 3600 * 1000);
                if (!dateBefore.after(previousStartDate))
                {
                    return message = "Invalid Date, New Start Date Should not be grater then Previous version Start Date. ";
                }
            }
        }
        catch (ParseException e)
        {
            logger.error("Date Parsing Exception", e);
        }

        return null;
    }

    public String newValidateDateWithNewFormat(String startDate, String endDate, String previousStartDate, String previousEndDate)
    {

        String message = null;
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newStartDate = sdf.parse(startDate);
            Date newEndDate = sdf.parse(endDate);
            if (!newStartDate.before(newEndDate))
            {
                return message = "Invalid Date, End Date should not be less then Start Date.";
            }
            if (previousStartDate != null && previousEndDate != null)
            {
                //new start date-1 = previous end date;
                Date previousStartDate1 = sdf.parse(previousStartDate);
                Date dateBefore = new Date(newStartDate.getTime() - 1 * 24 * 3600 * 1000);
                if (!dateBefore.after(previousStartDate1))
                {
                    return message = "Invalid Date, New Start Date Should not be grater then Previous version Start Date. ";
                }
            }
        }
        catch (ParseException e)
        {
            logger.error("Date Parsing Exception", e);
        }

        return null;
    }

    //this method is for validation purpose for showing handling message
    public void getErrorMessage(StringBuffer errorMessage, ValidationErrorList validationErrorList)
    {

        for (ValidationException validationExceptionList : validationErrorList.errors())
        {
            if (errorMessage.length() > 0)
            {
                errorMessage.append(", ");
            }
            errorMessage.append(validationExceptionList.getMessage());

        }
    }

    //this method is for getting country code
    public boolean is2LetterCountryCodeValid(String selectedItem)
    {
        Functions functions= new Functions();

        Enumeration<String> country = RB2.getKeys();
        List<String> countryCode = Collections.list(country);
        Collections.sort(countryCode);
        for (String countryKey : countryCode)
        {
            String Key = countryKey;
            if (functions.isValueNull(selectedItem) && Key.startsWith(selectedItem))
            {
                return true;
            }
        }

        return false;
    }

    //this method is for getting List of Currency Unit
    /*public StringBuffer getCurrencyUnit(String selectedItem)
    {
        Functions functions = new Functions();

        StringBuffer optionList = new StringBuffer(" <option value=\"\">Select a Currency</option>");

        for (PZTransactionCurrency pzTransactionCurrency : PZTransactionCurrency.values())
        {
            if (functions.isValueNull(selectedItem) && pzTransactionCurrency.toString().contains(selectedItem))
            {
                optionList.append("<option value=\"" + pzTransactionCurrency.name() + "\" selected>" + pzTransactionCurrency.name() + "</option>");
            }
            else
            {
                optionList.append("<option value=\"" + pzTransactionCurrency.name() + "\" >" + pzTransactionCurrency.name() + "</option>");
            }
        }

        return optionList;
    }*/

    //this method using for Application manager Currency

    public static StringBuffer getCurrencyEnum(String selectedItem)
    {
        Functions functions = new Functions();

        StringBuffer optionList = new StringBuffer(" <option value=\"\">Select a Currency</option>");

        for (AppCurrencyEnum currencyEnum : AppCurrencyEnum.values())
        {
            if (functions.isValueNull(selectedItem) && currencyEnum.toString().contains(selectedItem))
            {
                optionList.append("<option value=\"" + currencyEnum.name() + "\" selected>" + currencyEnum.name() + "</option>");
            }
            else
            {
                optionList.append("<option value=\"" + currencyEnum.name() + "\" >" + currencyEnum.name() + "</option>");
            }
        }

        return optionList;
    }

    //this method is for getting list of identification type
    public StringBuffer getIdentificationType(String selectedItem)
    {
        Functions functions = new Functions();

        StringBuffer optionList = new StringBuffer(" <option value=\"\">Select a Type</option>");

        for (IdentificationType identificationType : IdentificationType.values())
        {
            if (functions.isValueNull(selectedItem) && identificationType.toString().contains(selectedItem))
            {
                optionList.append("<option value=\"" + identificationType.name() + "\" selected>" + identificationType.name() + "</option>");
            }
            else
            {
                optionList.append("<option value=\"" + identificationType.name() + "\" >" + identificationType.name() + "</option>");
            }
        }
        return optionList;
    }

    //this method is for getting list of address proof type
    public StringBuffer getAddressProofTypes(String selectedItem)
    {
        Functions functions = new Functions();
        StringBuffer optionList = new StringBuffer(" <option value=\"\">Select a Type</option>");
        for (AddressType addressType : AddressType.values())
        {
            if (functions.isValueNull(selectedItem) && addressType.toString().contains(selectedItem))
            {
                optionList.append("<option value=\"" + addressType.name() + "\" selected>" + addressType.name() + "</option>");
            }
            else
            {
                optionList.append("<option value=\"" + addressType.name() + "\" >" + addressType.name() + "</option>");
            }
        }
        return optionList;
    }

    //this method is for getting List of Merchant Category code & country
    public static StringBuffer getCurrency(String selectedItem)
    {
        Functions functions = new Functions();
        StringBuffer optionList = new StringBuffer(" <option value=\"\">Select a Type</option>");
        for (AppCurrencyEnum currencyEnum : AppCurrencyEnum.values())
        {
            if (functions.isValueNull(selectedItem) && currencyEnum.toString().contains(selectedItem))
            {
                optionList.append("<option value=\"" + currencyEnum.name() + "\" selected>" + currencyEnum.name() + "</option>");
            }
            else
            {
                optionList.append("<option value=\"" + currencyEnum.name() + "\" >" + currencyEnum.name() + "</option>");
            }
        }
        return optionList;
    }

    public StringBuffer getCurrencyUnit(String selectedItem)
    {
        Functions functions = new Functions();

        StringBuffer optionList = new StringBuffer(" <option value=\"\">Select a Currency</option>");

        for (PZTransactionCurrency pzTransactionCurrency : PZTransactionCurrency.values())
        {
            if (functions.isValueNull(selectedItem) && pzTransactionCurrency.toString().contains(selectedItem))
            {
                optionList.append("<option value=\"" + pzTransactionCurrency.name() + "\" selected>" + pzTransactionCurrency.name() + "</option>");
            }
            else
            {
                optionList.append("<option value=\"" + pzTransactionCurrency.name() + "\" >" + pzTransactionCurrency.name() + "</option>");
            }
        }

        return optionList;
    }


    /*public StringBuffer getCurrencyUnit(String selectedItem, List<PZTransactionCurrency> statusListNotNeeded)
    {
        Functions functions= new Functions();

        StringBuffer optionList = new StringBuffer(" <option value=\"\">Select a Currency</option>");

        for (PZTransactionCurrency pzTransactionCurrency : PZTransactionCurrency.values())
        {
            if (statusListNotNeeded != null && statusListNotNeeded.contains(pzTransactionCurrency))
            {

            }
            else
            {
                if (functions.isValueNull(selectedItem) && pzTransactionCurrency.toString().contains(selectedItem))
                {
                    optionList.append("<option value=\"" + pzTransactionCurrency.name() + "\" selected>" + pzTransactionCurrency.name() + "</option>");
                }
                else
                {
                    optionList.append("<option value=\"" + pzTransactionCurrency.name() + "\" >" + pzTransactionCurrency.name() + "</option>");
                }
            }
        }
        return optionList;
    }*/

}
