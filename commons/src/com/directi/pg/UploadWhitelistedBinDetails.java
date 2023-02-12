package com.directi.pg;

import com.manager.WhiteListManager;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Vivek
 * Date: 9/3/13
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadWhitelistedBinDetails
{
    private static Logger log = new Logger(UploadWhitelistedBinDetails.class.getName());
    public StringBuilder processSettlement(String memberId,String accountId,StringBuilder sSuccessMessage,StringBuilder sErrorMessage,List<String> queryBatch)
    {
        int k = 0;
        int isRecordInserted=0;

        try
        {
            WhiteListManager whiteListManager=new WhiteListManager();
            isRecordInserted=whiteListManager.uploadBins(queryBatch);
        }
        catch(Exception e){
            log.debug("Exception:::::"+e);
        }
        sSuccessMessage.append("Total Updated Records --> "+isRecordInserted);
        sErrorMessage.append(" " +"Total Failed Records(EXISTING RECORDS IN DATABASE FOR "+memberId+" & "+accountId+") --> "+k);
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append(sErrorMessage.toString());
        return chargeBackMessage;
    }
}