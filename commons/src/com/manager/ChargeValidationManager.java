package com.manager;

import com.manager.vo.payoutVOs.ChargeMasterVO;
import org.owasp.esapi.ESAPI;

/**
 * Created by IntelliJ IDEA.
 * User: sandip1
 * Date: 7/06/15
 * Time: 11:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChargeValidationManager
{
    public String performMandatoryChargeMasterValidation(ChargeMasterVO chargeMasterVO)
    {
        StringBuffer sb=new StringBuffer();
        if(!ESAPI.validator().isValidInput("chargename",chargeMasterVO.getChargeName(), "SafeString", 255,false))
        {
            sb.append("Invalid Charge Name,");
        }
        if(!ESAPI.validator().isValidInput("chargeunit",chargeMasterVO.getValueType(), "SafeString", 50,false))
        {
            sb.append("Invalid Charge Unit,");
        }
        if(!ESAPI.validator().isValidInput("category",chargeMasterVO.getCategory(), "SafeString", 50,false))
        {
            sb.append("Invalid Charge Category,");
        }
        if(!ESAPI.validator().isValidInput("keyword",chargeMasterVO.getKeyword(), "SafeString", 50,false))
        {
            sb.append("Invalid Charge Keyword,");
        }
        if(!ESAPI.validator().isValidInput("Sub-Keyword",chargeMasterVO.getSubKeyword(), "SafeString", 50,false))
        {
            sb.append("Invalid Charge Sub-Keyword,");
        }
        if(!ESAPI.validator().isValidInput("Frequency",chargeMasterVO.getFrequency(),"SafeString", 50,false))
        {
            sb.append("Invalid Charge Frequency,");
        }
        if(!ESAPI.validator().isValidInput("sequencenum",chargeMasterVO.getSequenceNumber(),"Numbers",10,false))
        {
            sb.append("Invalid Charge Sequence Number,");
        }
        //System.out.println(sb.toString());
        return sb.toString();

    }

    public String performOptionalChargeMasterValidation(ChargeMasterVO chargeMasterVO)
    {
        StringBuffer sb=new StringBuffer();
        if(!ESAPI.validator().isValidInput("chargeid",chargeMasterVO.getChargeId(), "Numbers",10,true))
        {
            sb.append("Invalid Charge Id,");
        }
        if(!ESAPI.validator().isValidInput("keyname",chargeMasterVO.getKeyName(), "SafeString", 255,true))
        {
            sb.append("Invalid Kay Name,");
        }
        return sb.toString();
    }

}
