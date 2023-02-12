package payment.util;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 6/16/15
 * Time: 8:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class WriteJSONResponse
{
    private static Logger logger = new Logger(WriteJSONResponse.class.getName());

    public static String writeFullJSONStatusResponse(Map<String,String> resMap)
    {
        JSONObject obj = new JSONObject();
        Functions functions=new Functions();

        WriteJSONResponse writeJSONResponse = new WriteJSONResponse();
        resMap = writeJSONResponse.checkNullResponse(resMap);
        try
        {
            if(functions.isValueNull(resMap.get("orderid")))
            {
                obj.put("orderid",resMap.get("orderid"));
            }
            else
            {
                obj.put("orderid", "");
            }
            if(functions.isValueNull(resMap.get("trackingid")))
            {
                obj.put("trackingid",resMap.get("trackingid"));
            }
            else
            {
                obj.put("trackingid","");
            }
            if(functions.isValueNull(resMap.get("status")))
            {
                obj.put("status",resMap.get("status"));
            }
            else
            {
                obj.put("status","");
            }
            if(functions.isValueNull(resMap.get("statusdescription")))
            {
                obj.put("statusdescription",resMap.get("statusdescription"));
            }
            else
            {
                obj.put("statusdescription","");
            }
            if(functions.isValueNull(resMap.get("authamount")))
            {
                obj.put("authamount",resMap.get("authamount"));
            }
            else
            {
                obj.put("authamount","");
            }
            if(functions.isValueNull(resMap.get("captureamount")))
            {
                obj.put("captureamount",resMap.get("captureamount"));
            }
            else
            {
                obj.put("captureamount","");
            }
            if (functions.isValueNull(resMap.get("refundAmount")))
            {
                obj.put("refundAmount",resMap.get("refundAmount"));
            }
            else
            {
                obj.put("refundAmount","");
            }
            if(functions.isValueNull(resMap.get("newchecksum")))
            {
                obj.put("newchecksum",resMap.get("newchecksum"));
            }
            else
            {
                obj.put("newchecksum","");
            }
            if(functions.isValueNull(resMap.get("authcode")))
            {
                obj.put("authcode",resMap.get("authcode"));
            }
            else
            {
                obj.put("authcode","");
            }
            if(functions.isValueNull(resMap.get("resultcode")))
            {
                obj.put("resultcode",resMap.get("resultcode"));
            }
            else
            {
                obj.put("resultcode","");
            }
            if(functions.isValueNull(resMap.get("resultdescription")))
            {
                obj.put("resultdescription",resMap.get("resultdescription"));
            }
            else
            {
                obj.put("resultdescription","");
            }
            if(functions.isValueNull(resMap.get("cardsource")))
            {
                obj.put("cardsource",resMap.get("cardsource"));
            }
            else
            {
                obj.put("cardsource","");
            }
            if(functions.isValueNull(resMap.get("cardissuer")))
            {
                obj.put("cardissuer",resMap.get("cardissuer"));
            }
            else
            {
                obj.put("cardissuer","");
            }
            if(functions.isValueNull(resMap.get("eci")))
            {
                obj.put("eci",resMap.get("eci"));
            }
            else
            {
                obj.put("eci","");
            }
            if(functions.isValueNull(resMap.get("ecidescription")))
            {
                obj.put("ecidescription",resMap.get("ecidescription"));
            }
            else
            {
                obj.put("ecidescription","");
            }
            if(functions.isValueNull(resMap.get("cvvresult")))
            {
                obj.put("cvvresult",resMap.get("cvvresult"));
            }
            else
            {
                obj.put("cvvresult","");
            }
            if(functions.isValueNull(resMap.get("banktransid")))
            {
                obj.put("banktransid",resMap.get("banktransid"));
            }
            else
            {
                obj.put("banktransid","");
            }
            if(functions.isValueNull(resMap.get("cardcountrycode")))
            {
                obj.put("cardcountrycode",resMap.get("cardcountrycode"));
            }
            else
            {
                obj.put("cardcountrycode","");
            }
            if(functions.isValueNull(resMap.get("validationdescription")))
            {
                obj.put("validationdescription",resMap.get("validationdescription"));
            }
            else
            {
                obj.put("validationdescription","");
            }
            if(functions.isValueNull(resMap.get("banktransdate")))
            {
                obj.put("banktransdate",resMap.get("banktransdate"));
            }
            else
            {
                obj.put("banktransdate","");
            }

        }
        catch (Exception e)
        {

        }
        return obj.toString();
    }
    public static String writeFullSaleResponse(Map<String,String> resMap)
    {
        JSONObject obj = new JSONObject();

        WriteJSONResponse writeJSONResponse = new WriteJSONResponse();
        resMap = writeJSONResponse.checkNullResponse(resMap);

        try
        {
            obj.put("orderid", resMap.get("orderid"));
            obj.put("trackingid", resMap.get("trackingid"));
            obj.put("status", resMap.get("status"));
            obj.put("statusdescription", resMap.get("statusdescription"));
            obj.put("authamount", resMap.get("amount"));
            obj.put("checksum", resMap.get("checksum"));
            obj.put("authcode", resMap.get("authcode"));
            obj.put("resultcode", resMap.get("resultcode"));
            obj.put("resultdescription", resMap.get("resultdescription"));
            obj.put("cardsource", resMap.get("cardsource"));
            obj.put("cardissuer", resMap.get("cardissuername"));
            obj.put("eci", resMap.get("eci"));
            obj.put("ecidescription", resMap.get("ecidescription"));
            obj.put("cvvresult", resMap.get("cvvresult"));
            obj.put("banktransid", resMap.get("banktransid"));
            obj.put("cardcountrycode", resMap.get("cardcountrycode"));
            obj.put("validationdescription", resMap.get("validationdescription"));
            obj.put("banktransdate", resMap.get("banktransdate"));
            obj.put("billingdiscriptor", resMap.get("billingdiscriptor"));
            obj.put("token", resMap.get("token"));
            obj.put("fraudscore",resMap.get("fraudscore"));
            obj.put("rulestriggered",resMap.get("rulestriggered"));

        }
        catch (Exception e)
        {

        }
        return obj.toString();
    }
    public static String writeDefaultJSONStatusResponse(Map<String,String> resMap)
    {
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("orderid",resMap.get("orderid"));
            obj.put("trackingid",resMap.get("trackingid"));
            obj.put("status",resMap.get("status"));
            obj.put("statusdescription",resMap.get("statusdescription"));
            obj.put("authamount",resMap.get("authamount"));
            obj.put("captureamount",resMap.get("captureamount"));
            obj.put("captureamount",resMap.get("captureamount"));
            obj.put("newchecksum",resMap.get("newchecksum"));
        }
        catch (Exception e)
        {

        }
        return obj.toString();
    }
    public static String writeFullJSONResponseForCapture(Map<String,String> resMap)
    {
        JSONObject obj = new JSONObject();
        Functions functions=new Functions();

        WriteJSONResponse writeJSONResponse = new WriteJSONResponse();
        resMap = writeJSONResponse.checkNullResponse(resMap);

        try
        {
            if(functions.isValueNull(resMap.get("trackingid"))){
                obj.put("trackingid",resMap.get("trackingid"));
            }
            else{
                obj.put("trackingid", "");
            }
            if(functions.isValueNull(resMap.get("status"))){
                obj.put("status",resMap.get("status"));
            }
            else{
                obj.put("status","");
            }
            if(functions.isValueNull(resMap.get("statusdescription"))){
                obj.put("statusdescription",resMap.get("statusdescription"));
            }
            else{
                obj.put("statusdescription","");
            }
            if(functions.isValueNull(resMap.get("captureamount"))){
                obj.put("amount",resMap.get("captureamount"));
            }
            else{
                obj.put("amount","");
            }
            if(functions.isValueNull(resMap.get("checksum"))){
                obj.put("newchecksum",resMap.get("checksum"));
            }
            else{
                obj.put("newchecksum","");
            }
            if(functions.isValueNull(resMap.get("bankstatus"))){
                obj.put("bankstatus",resMap.get("bankstatus"));
            }
            else
            {
                obj.put("bankstatus","");
            }
            if(functions.isValueNull(resMap.get("resultcode"))){
                obj.put("resultcode",resMap.get("resultcode"));
            }
            else{
                obj.put("resultcode","");
            }
            if(functions.isValueNull(resMap.get("resultdescription"))){
                obj.put("resultdescription",resMap.get("resultdescription"));
            }
            else{
                obj.put("resultdescription","");
            }
            if(functions.isValueNull(resMap.get("lote"))){
                obj.put("lote",resMap.get("lote"));
            }
            else
            {
                obj.put("lote","");
            }
        }
        catch (Exception e)
        {

        }
        return obj.toString();
    }
    public static String writeFullJSONResponseForVoid(Map<String,String> resMap)
    {
        JSONObject obj = new JSONObject();
        Functions functions=new Functions();

        WriteJSONResponse writeJSONResponse = new WriteJSONResponse();
        resMap = writeJSONResponse.checkNullResponse(resMap);

        try
        {
            if(functions.isValueNull(resMap.get("orderid"))){
                obj.put("orderid",resMap.get("orderid"));
            }
            else{
                obj.put("orderid", "");
            }
            if(functions.isValueNull(resMap.get("status"))){
                obj.put("status",resMap.get("status"));
            }
            else{
                obj.put("status","");
            }
            if(functions.isValueNull(resMap.get("statusdescription"))){
                obj.put("statusdescription",resMap.get("statusdescription"));
            }
            else{
                obj.put("statusdescription","");
            }
            if(functions.isValueNull(resMap.get("trackingid"))){
                obj.put("trackingid",resMap.get("trackingid"));
            }
            else{
                obj.put("trackingid","");
            }
            if(functions.isValueNull(resMap.get("newchecksum"))){
                obj.put("newchecksum",resMap.get("newchecksum"));
            }
            else{
                obj.put("newchecksum","");
            }
            if(functions.isValueNull(resMap.get("bankstatus"))){
                obj.put("bankstatus",resMap.get("bankstatus"));
            }
            else
            {
                obj.put("bankstatus","");
            }
            if(functions.isValueNull(resMap.get("resultcode"))){
                obj.put("resultcode",resMap.get("resultcode"));
            }
            else{
                obj.put("resultcode","");
            }
            if(functions.isValueNull(resMap.get("resultdescription"))){
                obj.put("resultdescription",resMap.get("resultdescription"));
            }
            else{
                obj.put("resultdescription","");
            }
        }
        catch (Exception e)
        {

        }
        return obj.toString();
    }

    public static String writeJSONResponse(Map<String,String> resMap)
    {
        JSONObject obj = new JSONObject();
        Functions functions = new Functions();
        try
        {
            for(String key : resMap.keySet())
            {
                if(functions.isValueNull(resMap.get(key)))
                    obj.put(key ,resMap.get(key));
                else
                    obj.put(key ,"");
            }
        }
        catch (Exception e)
        {

        }
        return obj.toString();
    }

    public Map checkNullResponse(Map resMap)
    {
        String value = "";
        Functions f = new Functions();
        for(Object key : resMap.keySet())
        {
            logger.debug("value---" + key + "--" + resMap.get(key));
            if(f.isValueNull(String.valueOf(resMap.get(key))))
            {
                value = resMap.get(key).toString();
            }
            else
            {
                value = "";
            }
            resMap.put(key.toString(),value);
            logger.debug("value after---" + key + "--" + value);
        }
        return resMap;
    }
}
