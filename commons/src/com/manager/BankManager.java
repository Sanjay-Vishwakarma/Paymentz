package com.manager;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.dao.BankDao;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.*;
import com.manager.vo.gatewayVOs.GatewayAccountVO;
import com.manager.vo.gatewayVOs.GatewayTypeVO;

import java.sql.SQLException;
import java.util.List;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/2/14
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankManager
{
    Logger logger = new Logger(BankManager.class.getName());
    BankDao bankDao = new BankDao();
    Functions functions= new Functions();
    CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
    public List<BankRecievedSettlementCycleVO> getBankSettlementCycleList(InputDateVO inputDateVO,PaginationVO paginationVO,GatewayAccountVO gatewayAccountVO,GatewayTypeVO gatewayTypeVO)
    {
        return bankDao.getBankSettlementCycleList(inputDateVO,paginationVO,gatewayAccountVO,gatewayTypeVO);
    }
    //getting single bankReceivedSettlementCycle
    public BankRecievedSettlementCycleVO getSingleBankSettlementCycleActionSpecific(ActionVO actionVO)
    {
        BankRecievedSettlementCycleVO bankRecievedSettlementCycleVO= null;
        String searchActionPair[]=actionVO.getActionCriteria().split("_");
        if(searchActionPair[1].equals("View"))
        {
            actionVO.setView();
            actionVO.setActionCriteria(searchActionPair[0]);
        }
        else if(searchActionPair[1].equals("Edit"))
        {
            actionVO.setEdit();
            actionVO.setActionCriteria(searchActionPair[0]);
        }
        bankRecievedSettlementCycleVO=bankDao.getSingleBankSettlementCycleActionSpecific(actionVO);
        return bankRecievedSettlementCycleVO;
    }
    //updating bankReceivedSettlement
    public boolean updateBankReceivedSettlementCycle(BankRecievedSettlementCycleVO bankRecievedSettlementCycleVO,ActionVO actionVO,GatewayTypeVO gatewayTypeVO) throws SystemError
    {
        GatewayManager gatewayManager = new GatewayManager();
        boolean b=false;
        String searchActionPair[]=actionVO.getActionCriteria().split("_");
        if(functions.isValueNull(bankRecievedSettlementCycleVO.getSettlementDate()))
        {
            bankRecievedSettlementCycleVO.setSettlementDate(commonFunctionUtil.convertDatepickerToTimestamp(bankRecievedSettlementCycleVO.getSettlementDate(),"00","00","00"));
        }
        bankRecievedSettlementCycleVO.setExpected_startDate(commonFunctionUtil.convertDatepickerToTimestamp(bankRecievedSettlementCycleVO.getExpected_startDate(),"00","00","00"));
        bankRecievedSettlementCycleVO.setExpected_endDate(commonFunctionUtil.convertDatepickerToTimestamp(bankRecievedSettlementCycleVO.getExpected_endDate(),"23","59","59"));
        if(functions.isValueNull(actionVO.getYesOrNoCriteria()))
        {
            if(actionVO.getYesOrNoCriteria().equals("Y"))
            {
                bankRecievedSettlementCycleVO.setActual_startDate(commonFunctionUtil.addTimeStampWithDayLightTime(bankRecievedSettlementCycleVO.getExpected_startDate(),gatewayTypeVO.getGatewayType().getTime_difference_daylight()));
                bankRecievedSettlementCycleVO.setActual_endDate(commonFunctionUtil.addTimeStampWithDayLightTime(bankRecievedSettlementCycleVO.getExpected_endDate(),gatewayTypeVO.getGatewayType().getTime_difference_daylight()));
            }
            else
            {
                bankRecievedSettlementCycleVO.setActual_startDate(commonFunctionUtil.addTimeStampWithDayLightTime(bankRecievedSettlementCycleVO.getExpected_startDate(),gatewayTypeVO.getGatewayType().getTime_difference_normal()));
                bankRecievedSettlementCycleVO.setActual_endDate(commonFunctionUtil.addTimeStampWithDayLightTime(bankRecievedSettlementCycleVO.getExpected_endDate(),gatewayTypeVO.getGatewayType().getTime_difference_normal()));
            }
        }
        else
        {
            bankRecievedSettlementCycleVO.setActual_startDate(commonFunctionUtil.convertDatepickerToTimestamp(bankRecievedSettlementCycleVO.getActual_startDate(),"00","00","00"));
            bankRecievedSettlementCycleVO.setActual_endDate(commonFunctionUtil.convertDatepickerToTimestamp(bankRecievedSettlementCycleVO.getActual_endDate(),"23","59","59"));
        }
        //getting GateWayAccountVO from pgTypeid and accountId
        GatewayAccountVO gatewayAccountVO = gatewayManager.getGatewayAccountAsPerPgTypeIdAndAccountId(bankRecievedSettlementCycleVO.getAccountId(),bankRecievedSettlementCycleVO.getPgTypeId());
        //setting merchantId
        if(functions.isValueNull(gatewayAccountVO.getAccountId()))
        {
            bankRecievedSettlementCycleVO.setMerchantId(gatewayAccountVO.getGatewayAccount().getMerchantId());
        }
        else
        {
            //once again setting the initial values
            if(functions.isValueNull(bankRecievedSettlementCycleVO.getSettlementDate()))
            {
                bankRecievedSettlementCycleVO.setSettlementDate(commonFunctionUtil.convertTimestampToDatepicker(bankRecievedSettlementCycleVO.getSettlementDate()));
            }
            bankRecievedSettlementCycleVO.setExpected_startDate(commonFunctionUtil.convertTimestampToDatepicker(bankRecievedSettlementCycleVO.getExpected_startDate()));
            bankRecievedSettlementCycleVO.setExpected_endDate(commonFunctionUtil.convertTimestampToDatepicker(bankRecievedSettlementCycleVO.getExpected_endDate()));
            bankRecievedSettlementCycleVO.setActual_startDate(commonFunctionUtil.convertTimestampToDatepicker(bankRecievedSettlementCycleVO.getActual_startDate()));
            bankRecievedSettlementCycleVO.setActual_endDate(commonFunctionUtil.convertTimestampToDatepicker(bankRecievedSettlementCycleVO.getActual_endDate()));
            if(searchActionPair[1].equals("Update"))
            {
                actionVO.setEdit();
                actionVO.setUpdate();
            }
            else if(searchActionPair[1].equals("Add"))
            {
                actionVO.setAdd();
            }
            throw new SystemError("accountId and pgTypeId not matching");
        }
        if(searchActionPair[1].equals("Update"))
        {
            actionVO.setEdit();
            actionVO.setUpdate();
            b= bankDao.updateBankReceivedSettlementCycle(bankRecievedSettlementCycleVO);
        }
        else if(searchActionPair[1].equals("Add"))
        {
            actionVO.setAdd();
            b= bankDao.insertNewBankReceivedSettlementCycle(bankRecievedSettlementCycleVO);

        }
        if(functions.isValueNull(bankRecievedSettlementCycleVO.getSettlementDate()))
        {
            bankRecievedSettlementCycleVO.setSettlementDate(commonFunctionUtil.convertTimestampToDatepicker(bankRecievedSettlementCycleVO.getSettlementDate()));
        }
        bankRecievedSettlementCycleVO.setExpected_startDate(commonFunctionUtil.convertTimestampToDatepicker(bankRecievedSettlementCycleVO.getExpected_startDate()));
        bankRecievedSettlementCycleVO.setExpected_endDate(commonFunctionUtil.convertTimestampToDatepicker(bankRecievedSettlementCycleVO.getExpected_endDate()));
        bankRecievedSettlementCycleVO.setActual_startDate(commonFunctionUtil.convertTimestampToDatepicker(bankRecievedSettlementCycleVO.getActual_startDate()));
        bankRecievedSettlementCycleVO.setActual_endDate(commonFunctionUtil.convertTimestampToDatepicker(bankRecievedSettlementCycleVO.getActual_endDate()));

        return b;
    }
    //getting list of bankRollingReserveVO as per Date and accountId
    public List<BankRollingReserveVO> getBankRollingReserveList(InputDateVO inputDateVO, PaginationVO paginationVO, GatewayAccountVO gatewayAccountVO)throws SystemError,SQLException
    {
        return bankDao.getBankRollingReserveList(inputDateVO, paginationVO, gatewayAccountVO);
    }
    //getting single BankRollingReserveVO according to the action
    public BankRollingReserveVO getSingleBankRollingReserveVOActionSpecific(ActionVO actionVO)
    {
        BankRollingReserveVO bankRollingReserveVO= null;
        String searchActionPair[]=actionVO.getActionCriteria().split("_");
        if(searchActionPair[1].equals("View"))
        {
            actionVO.setView();
            actionVO.setActionCriteria(searchActionPair[0]);
        }
        else if(searchActionPair[1].equals("Edit"))
        {
            actionVO.setEdit();
            actionVO.setActionCriteria(searchActionPair[0]);
        }
        bankRollingReserveVO=bankDao.getSingleBankRollingReserveActionSpecific(actionVO);
        String RollingReleaseDateTime[] = commonFunctionUtil.convertTimestampToDateTimePicker(bankRollingReserveVO.getRollingReserveDateUpTo());
        bankRollingReserveVO.setRollingReserveDateUpTo(RollingReleaseDateTime[0]);
        bankRollingReserveVO.setRollingRelease_time(RollingReleaseDateTime[1]);
        return bankRollingReserveVO;
    }
    //updating Bank Rolling reserve
    public boolean updateBankRollingReserve(BankRollingReserveVO bankRollingReserveVO, ActionVO actionVO)
    {
        boolean b =false;
        String searchActionPair[]=actionVO.getActionCriteria().split("_");
        bankRollingReserveVO.setRollingReserveDateUpTo(commonFunctionUtil.convertDatepickerToTimestamp(bankRollingReserveVO.getRollingReserveDateUpTo(),"23","59","59"));
        if(searchActionPair[1].equals("Update"))
        {
            actionVO.setEdit();
            actionVO.setUpdate();
            b= bankDao.updateBankRollingReserve(bankRollingReserveVO);
        }
        else if(searchActionPair[1].equals("Add"))
        {
            actionVO.setAdd();
            b= bankDao.insertNewBankRollingReserve(bankRollingReserveVO);
        }
        String rollingReleaseDateTime[]=commonFunctionUtil.convertTimestampToDateTimePicker(bankRollingReserveVO.getRollingReserveDateUpTo());
        bankRollingReserveVO.setRollingReserveDateUpTo(rollingReleaseDateTime[0]);
        bankRollingReserveVO.setRollingRelease_time(rollingReleaseDateTime[1]);
        return b;
    }
    public List<BankRecievedSettlementCycleVO> getBankReceivedSettlemntByCycleId(int cycleId)//sandip
    {
        return bankDao.getBankReceivedSettlementByCycleId(cycleId);
    }
    //getting list of bankMerchantSettlementVO
    public List<BankMerchantSettlementVO> getBankMerchantSettlementList(InputDateVO inputDateVO, PaginationVO paginationVO, GatewayAccountVO gatewayAccountVO, MerchantDetailsVO merchantDetailsVO,GatewayTypeVO gatewayTypeVO)
    {
        return bankDao.getBankMerchantSettlementList(inputDateVO, paginationVO, gatewayAccountVO, merchantDetailsVO,gatewayTypeVO);
    }
    //getting single BankMerchantSettlementVO
    public BankMerchantSettlementVO getSingleBankMerchantSettlementActionSpecific(ActionVO actionVO)
    {
        BankMerchantSettlementVO bankMerchantSettlementVO= null;
        String searchActionPair[]=actionVO.getActionCriteria().split("_");
        if(searchActionPair[1].equals("View"))
        {
            actionVO.setView();
            actionVO.setActionCriteria(searchActionPair[0]);
        }
        else if(searchActionPair[1].equals("Edit"))
        {
            actionVO.setEdit();
            actionVO.setActionCriteria(searchActionPair[0]);
        }
        bankMerchantSettlementVO=bankDao.getSingleBankMerchantSettlementActionSpecific(actionVO);
        return bankMerchantSettlementVO;
    }
    //adding or updating BankMerchantSettlement
    public boolean updateBankMerchantSettlement(BankMerchantSettlementVO bankMerchantSettlementVO, ActionVO actionVO)
    {
        String searchActionPair[]=actionVO.getActionCriteria().split("_");
        if(searchActionPair[1].equals("Update"))
        {
            actionVO.setEdit();
            actionVO.setUpdate();
            return bankDao.updateBankMerchantSettlement(bankMerchantSettlementVO);
        }
        else if(searchActionPair[1].equals("Add"))
        {
            actionVO.setAdd();
            return bankDao.insertNewBankMerchantSettlement(bankMerchantSettlementVO);
        }
        return false;
    }
    //getting list of bank wire manager
    public List<BankWireManagerVO> getBankWireManagerList(InputDateVO inputDateVO, PaginationVO paginationVO, String accountid, String pgtypeid,String bankwireid, String parent_bankwireId/*GatewayAccountVO gatewayAccountVO, GatewayTypeVO gatewayTypeVO*/)
    {
        return bankDao.getBankWireManagerListNew(inputDateVO, paginationVO, accountid, pgtypeid, bankwireid,parent_bankwireId);
    }
    public List<BankWireManagerVO> getBankWireManagerListBasedOnPartner(InputDateVO inputDateVO, PaginationVO paginationVO, String accountid, String bankwireId,String listOfAccountId)
    {
        return bankDao.getBankWireManagerListNew1(inputDateVO, paginationVO, accountid, bankwireId,listOfAccountId);
    }
    //getting single bank wire manager according to the action specified
    public BankWireManagerVO getSingleBankWireManagerActionSpecific(ActionVO actionVO)
    {
        BankWireManagerVO bankWireManagerVO= null;

        String searchActionPair[]=actionVO.getActionCriteria().split("_");
        if(searchActionPair[1].equals("View"))
        {
            actionVO.setView();
            actionVO.setActionCriteria(searchActionPair[0]);
        }
        else if(searchActionPair[1].equals("Edit"))
        {
            actionVO.setEdit();
            actionVO.setActionCriteria(searchActionPair[0]);
        }
        bankWireManagerVO=bankDao.getSingleBankWireManagerActionSpecific(actionVO);
        String[] bank_startTimestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getBank_start_date());
        String[] bank_endTimestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getBank_end_date());
        String[] server_startTimestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getServer_start_date());
        String[] server_endTimestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getServer_end_date());
        if(functions.isValueNull(bankWireManagerVO.getSettleddate()))
        {
            String[] settled_timestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getSettleddate());
            bankWireManagerVO.setSettleddate(settled_timestamp[0]);
            bankWireManagerVO.setSettled_timestamp(settled_timestamp[1]);
        }
        else
        {
            bankWireManagerVO.setSettleddate("");
            bankWireManagerVO.setSettled_timestamp("");
        }

        String[] rollingreserve_timestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getRollingreservereleasedateupto());
        String[] decline_timestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getDeclinedcoveredupto());
        String[] reverse_timestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getReversedCoveredUpto());
        String[] chargeback_timestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getChargebackcoveredupto());

        logger.error("bankWireManagerVO.getDeclinedcoveredStartdate()--"+bankWireManagerVO.getDeclinedcoveredStartdate());
        logger.error("bankWireManagerVO.getDeclinedcoveredStartdate()=="+bankWireManagerVO.getReversedCoveredStartdate());
        logger.error("bankWireManagerVO.getDeclinedcoveredStartdate()---"+bankWireManagerVO.getChargebackcoveredStartdate());
        logger.error("bankWireManagerVO.getDeclinedcoveredStartdate()==="+bankWireManagerVO.getRollingreservereleaseStartdate());

        String[] decline_timestampStart=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getDeclinedcoveredStartdate());
        String[] reverse_timestampStart=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getReversedCoveredStartdate());
        String[] chargeback_timestampStart=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getChargebackcoveredStartdate());
        String[] rollingreserve_timestampStart=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getRollingreservereleaseStartdate());

        bankWireManagerVO.setBank_start_date(bank_startTimestamp[0]);
        bankWireManagerVO.setBank_end_date(bank_endTimestamp[0]);
        bankWireManagerVO.setServer_start_date(server_startTimestamp[0]);
        bankWireManagerVO.setServer_end_date(server_endTimestamp[0]);


        bankWireManagerVO.setRollingreservereleasedateupto(rollingreserve_timestamp[0]);
        bankWireManagerVO.setDeclinedcoveredupto(decline_timestamp[0]);
        bankWireManagerVO.setReversedCoveredUpto(reverse_timestamp[0]);
        bankWireManagerVO.setChargebackcoveredupto(chargeback_timestamp[0]);
        //has to be removed temporary
        bankWireManagerVO.setBank_start_timestamp(bank_startTimestamp[1]);
        bankWireManagerVO.setBank_end_timestamp(bank_endTimestamp[1]);
        bankWireManagerVO.setServer_start_timestamp(server_startTimestamp[1]);
        bankWireManagerVO.setServer_end_timestamp(server_endTimestamp[1]);

        bankWireManagerVO.setDeclinedcoveredStartdate(decline_timestampStart[0]);
        bankWireManagerVO.setReversedCoveredStartdate(reverse_timestampStart[0]);
        bankWireManagerVO.setChargebackcoveredStartdate(chargeback_timestampStart[0]);
        bankWireManagerVO.setRollingreservereleaseStartdate(rollingreserve_timestampStart[0]);

        bankWireManagerVO.setRollingreservetime(rollingreserve_timestamp[1]);
        bankWireManagerVO.setDeclinedcoveredtime(decline_timestamp[1]);
        bankWireManagerVO.setReversedcoveredtime(reverse_timestamp[1]);
        bankWireManagerVO.setChargebackcoveredtime(chargeback_timestamp[1]);

        bankWireManagerVO.setDeclinedcoveredtimeStarttime(decline_timestampStart[1]);
        bankWireManagerVO.setReversedcoveredtimeStarttime(reverse_timestampStart[1]);
        bankWireManagerVO.setChargebackcoveredtimeStarttime(chargeback_timestampStart[1]);
        bankWireManagerVO.setRollingreservereleaseStarttime(rollingreserve_timestampStart[1]);
        //till here
        return bankWireManagerVO;
    }
    //updating or inserting into bank wire manager according to the action specified
    public String updateBankWireManager(BankWireManagerVO bankWireManagerVO, ActionVO actionVO/*, GatewayTypeVO gatewayTypeVO*/) throws SystemError
    {
        boolean b =false;
        GatewayManager gatewayManager=new GatewayManager();
        GatewayAccountVO gatewayAccountVO = null;
        GatewayTypeVO gatewayTypeVO = null;
        String trueResult = "";
        String falseResult = "";
        String result = "";



        String searchActionPair[]=actionVO.getActionCriteria().split("_");
        bankWireManagerVO.setSettleddate(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getSettleddate(),bankWireManagerVO.getSettled_timestamp()));
        bankWireManagerVO.setBank_start_date(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getBank_start_date(), bankWireManagerVO.getBank_start_timestamp()));
        bankWireManagerVO.setBank_end_date(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getBank_end_date(), bankWireManagerVO.getBank_end_timestamp()));

        bankWireManagerVO.setRollingreservereleasedateupto(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getRollingreservereleasedateupto(), bankWireManagerVO.getRollingreservetime()));
        bankWireManagerVO.setDeclinedcoveredupto(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getDeclinedcoveredupto(),bankWireManagerVO.getDeclinedcoveredtime()));
        bankWireManagerVO.setChargebackcoveredupto(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getChargebackcoveredupto(),bankWireManagerVO.getChargebackcoveredtime()));
        bankWireManagerVO.setReversedCoveredUpto(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getReversedCoveredUpto(),bankWireManagerVO.getReversedcoveredtime()));

        bankWireManagerVO.setDeclinedcoveredStartdate(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getDeclinedcoveredStartdate(), bankWireManagerVO.getDeclinedcoveredtimeStarttime()));
        bankWireManagerVO.setChargebackcoveredStartdate(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getChargebackcoveredStartdate(),bankWireManagerVO.getChargebackcoveredtimeStarttime()));
        bankWireManagerVO.setReversedCoveredStartdate(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getReversedCoveredStartdate(),bankWireManagerVO.getReversedcoveredtimeStarttime()));
        bankWireManagerVO.setRollingreservereleaseStartdate(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getRollingreservereleaseStartdate(),bankWireManagerVO.getRollingreservereleaseStarttime()));
        bankWireManagerVO.setParent_bankwireid(bankWireManagerVO.getParent_bankwireid()!=null?bankWireManagerVO.getParent_bankwireid():null);


        if (functions.isValueNull(bankWireManagerVO.getAccountId()) && bankWireManagerVO.getAccountId().contains(",")){
            String[] accIds = bankWireManagerVO.getAccountId().split(",");
            for (String accId : accIds){
                bankWireManagerVO.setAccountId(accId);
                gatewayAccountVO=gatewayManager.getGatewayAccountForAccountId(accId);
                gatewayTypeVO=gatewayManager.getGatewayTypeForPgTypeId(gatewayAccountVO.getGatewayAccount().getPgTypeId());
                bankWireManagerVO.setPgtypeId(gatewayAccountVO.getGatewayAccount().getPgTypeId());
                bankWireManagerVO.setCurrency(gatewayAccountVO.getGatewayAccount().getCurrency());
                bankWireManagerVO.setMid(gatewayAccountVO.getGatewayAccount().getMerchantId());

                if(functions.isValueNull(actionVO.getYesOrNoCriteria()))
                {
                    if(actionVO.getYesOrNoCriteria().equals("Y"))
                    {
                        bankWireManagerVO.setServer_start_date(commonFunctionUtil.manageBankTimeDifference(bankWireManagerVO.getBank_start_date(), gatewayTypeVO.getGatewayType().getTime_difference_daylight()));
                        bankWireManagerVO.setServer_end_date(commonFunctionUtil.manageBankTimeDifference(bankWireManagerVO.getBank_end_date(), gatewayTypeVO.getGatewayType().getTime_difference_daylight()));
                    }
                    else
                    {
                        if (functions.isValueNull(bankWireManagerVO.getParent_bankwireid())){
                            bankWireManagerVO.setServer_start_date(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getServer_start_date(), bankWireManagerVO.getServer_start_timestamp()));
                            bankWireManagerVO.setServer_end_date(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getServer_end_date(), bankWireManagerVO.getServer_end_timestamp()));
                        }else{
                            bankWireManagerVO.setServer_start_date(commonFunctionUtil.manageBankTimeDifference(bankWireManagerVO.getBank_start_date(), gatewayTypeVO.getGatewayType().getTime_difference_normal()));
                            bankWireManagerVO.setServer_end_date(commonFunctionUtil.manageBankTimeDifference(bankWireManagerVO.getBank_end_date(), gatewayTypeVO.getGatewayType().getTime_difference_normal()));
                        }
                    }
                }
                else
                {
                    bankWireManagerVO.setServer_start_date(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getServer_start_date(), bankWireManagerVO.getServer_start_timestamp()));
                    bankWireManagerVO.setServer_end_date(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getServer_end_date(), bankWireManagerVO.getServer_end_timestamp()));
                }

                if(searchActionPair[1].equals("Add"))
                {
                    actionVO.setAdd();
                    b= bankDao.insertNewBankWireManager(bankWireManagerVO);
                    if (b){
                        trueResult += ","+accId;
                    } else if (!b){
                        falseResult += ","+accId;

                    }
                }

                String[] server_startTimestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getServer_start_date());
                String[] server_endTimestamp =commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getServer_end_date());

                bankWireManagerVO.setServer_start_date(server_startTimestamp[0]);
                bankWireManagerVO.setServer_start_timestamp(server_startTimestamp[1]);
                bankWireManagerVO.setServer_end_date(server_endTimestamp[0]);
                bankWireManagerVO.setServer_end_timestamp(server_endTimestamp[1]);
            }
            if (functions.isValueNull(trueResult) && functions.isValueNull(falseResult)){
                result = "For Account Id "+ trueResult + " And Failed for "+falseResult;
            } else if (functions.isValueNull(trueResult)){
                result = "For Account Id "+ trueResult + ".";
            } else if (functions.isValueNull(falseResult)){
                result =  "Failed for "+falseResult;
            }

        } else {
            gatewayAccountVO=gatewayManager.getGatewayAccountForAccountId(bankWireManagerVO.getAccountId());
            gatewayTypeVO=gatewayManager.getGatewayTypeForPgTypeId(gatewayAccountVO.getGatewayAccount().getPgTypeId());
            bankWireManagerVO.setPgtypeId(gatewayAccountVO.getGatewayAccount().getPgTypeId());
            bankWireManagerVO.setCurrency(gatewayAccountVO.getGatewayAccount().getCurrency());
            bankWireManagerVO.setMid(gatewayAccountVO.getGatewayAccount().getMerchantId());

            if(functions.isValueNull(actionVO.getYesOrNoCriteria()))
            {
                if(actionVO.getYesOrNoCriteria().equals("Y"))
                {
                    bankWireManagerVO.setServer_start_date(commonFunctionUtil.manageBankTimeDifference(bankWireManagerVO.getBank_start_date(), gatewayTypeVO.getGatewayType().getTime_difference_daylight()));
                    bankWireManagerVO.setServer_end_date(commonFunctionUtil.manageBankTimeDifference(bankWireManagerVO.getBank_end_date(), gatewayTypeVO.getGatewayType().getTime_difference_daylight()));
                }
                else
                {
                    if (functions.isValueNull(bankWireManagerVO.getParent_bankwireid())){
                        bankWireManagerVO.setServer_start_date(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getServer_start_date(), bankWireManagerVO.getServer_start_timestamp()));
                        bankWireManagerVO.setServer_end_date(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getServer_end_date(), bankWireManagerVO.getServer_end_timestamp()));
                    }else
                    {
                        bankWireManagerVO.setServer_start_date(commonFunctionUtil.manageBankTimeDifference(bankWireManagerVO.getBank_start_date(), gatewayTypeVO.getGatewayType().getTime_difference_normal()));
                        bankWireManagerVO.setServer_end_date(commonFunctionUtil.manageBankTimeDifference(bankWireManagerVO.getBank_end_date(), gatewayTypeVO.getGatewayType().getTime_difference_normal()));
                    }
                }
            }
            else
            {
                bankWireManagerVO.setServer_start_date(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getServer_start_date(), bankWireManagerVO.getServer_start_timestamp()));
                bankWireManagerVO.setServer_end_date(commonFunctionUtil.convertDatepickerToTimestamp(bankWireManagerVO.getServer_end_date(), bankWireManagerVO.getServer_end_timestamp()));
            }

            if(searchActionPair[1].equals("Add"))
            {
                actionVO.setAdd();
                b= bankDao.insertNewBankWireManager(bankWireManagerVO);
                result = String.valueOf(b);
            }
        }
        if(searchActionPair[1].equals("Update"))
        {
            actionVO.setEdit();
            actionVO.setUpdate();
            b= bankDao.updateBankWireManager(bankWireManagerVO);
            result = String.valueOf(b);
        }
        /*else if(searchActionPair[1].equals("Add"))
        {
            actionVO.setAdd();
            b= bankDao.insertNewBankWireManager(bankWireManagerVO);
        }*/

        if(functions.isValueNull(bankWireManagerVO.getSettleddate()))
        {
            String[] settled_startTimestamp= commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getSettleddate());
            bankWireManagerVO.setSettleddate(settled_startTimestamp[0]);
            bankWireManagerVO.setSettled_timestamp(settled_startTimestamp[1]);
        }
        String[] bank_startTimestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getBank_start_date());
        String[] bank_endTimestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getBank_end_date());
        String[] server_startTimestamp=commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getServer_start_date());
        String[] server_endTimestamp =commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getServer_end_date());

        bankWireManagerVO.setBank_start_date(bank_startTimestamp[0]);
        bankWireManagerVO.setBank_end_date(bank_endTimestamp[0]);
        bankWireManagerVO.setServer_start_date(server_startTimestamp[0]);
        bankWireManagerVO.setServer_end_date(server_endTimestamp[0]);

        bankWireManagerVO.setRollingreservereleasedateupto(commonFunctionUtil.convertTimestampToDatepicker(bankWireManagerVO.getRollingreservereleasedateupto()));
        bankWireManagerVO.setDeclinedcoveredupto(commonFunctionUtil.convertTimestampToDatepicker(bankWireManagerVO.getDeclinedcoveredupto()));
        bankWireManagerVO.setChargebackcoveredupto(commonFunctionUtil.convertTimestampToDatepicker(bankWireManagerVO.getChargebackcoveredupto()));
        bankWireManagerVO.setReversedCoveredUpto(commonFunctionUtil.convertTimestampToDatepicker(bankWireManagerVO.getReversedCoveredUpto()));

        bankWireManagerVO.setDeclinedcoveredStartdate(commonFunctionUtil.convertTimestampToDatepicker(bankWireManagerVO.getDeclinedcoveredStartdate()));
        bankWireManagerVO.setChargebackcoveredStartdate(commonFunctionUtil.convertTimestampToDatepicker(bankWireManagerVO.getChargebackcoveredStartdate()));
        bankWireManagerVO.setReversedCoveredStartdate(commonFunctionUtil.convertTimestampToDatepicker(bankWireManagerVO.getReversedCoveredStartdate()));
        bankWireManagerVO.setRollingreservereleaseStartdate(commonFunctionUtil.convertTimestampToDatepicker(bankWireManagerVO.getRollingreservereleaseStartdate()));

        return result;
    }
    public boolean isRollingReserveAvailable(String accountId)throws SystemError,SQLException
    {
        return bankDao.isRollingReserveAvailable(accountId);
    }
    public boolean addNewBankRollingReserve(BankRollingReserveVO bankRollingReserveVO)throws SystemError,SQLException
    {
        return bankDao.insertNewBankRollingReserveNew(bankRollingReserveVO);
    }
    public BankRollingReserveVO getBankRollingReserveForAction(String accountId)throws SystemError,SQLException
    {
        return bankDao.getBankRollingReserveForAction(accountId);
    }
    public boolean updateBankRollingReserveNew(BankRollingReserveVO bankRollingReserveVO)throws SystemError,SQLException //sandip
    {
        return bankDao.updateBankRollingReserveNew(bankRollingReserveVO);
    }

    public List<BankRollingReserveVO> getBankRollingReserveHistory(String accountId)throws SystemError,SQLException
    {
        return bankDao.getBankRollingReserveHistory(accountId);
    }
    public TreeMap<String, BankWireManagerVO> getBankWiresForRandomCharges()
    {
        return bankDao.getBankWiresForRandomCharges();
    }
    public TreeMap<String, BankWireManagerVO> getBankWiresForAgent(String accountId)
    {
        return bankDao.getBankWiresForAgent(accountId);
    }
    public List<String> getparentBankwireIdForRandomCharges()
    {
        return bankDao.getparentBankwireIdForRandomCharges();
    }
    public List<String> getCardtypeIdByAccountId(String accountID)
    {
        return bankDao.getCardtypeIdByAccountId(accountID);
    }
    public List<String> getPaymodeIdByAccountId(String accountID)
    {
        return bankDao.getPaymodeIdByAccountId(accountID);
    }
    /*public SettlementCycleVO getSettlementCycleVO(String settlementCycleId)
    {
        return bankDao.getSettlementCycleVO(settlementCycleId);
    }*/
}