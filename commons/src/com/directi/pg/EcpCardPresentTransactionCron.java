package com.directi.pg;

import com.directi.pg.core.GatewayAccountService;
import com.manager.MerchantConfigManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.ecomprocessing.ECPCPPaymentGateway;
import com.payment.ecomprocessing.ECPPaymentGateway;
import com.payment.ecomprocessing.EcpResponseVo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by Balaji on 19-Dec-19.
 */
public class EcpCardPresentTransactionCron
{

    private static TransactionLogger transactionLogger= new TransactionLogger(EcpCardPresentTransactionCron.class.getName());

    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.ecp");
    String accountId="";
    Functions functions = new Functions();
    Connection con=null;

    public String ecpCardPresentCron(Hashtable hashtable)
    {
        transactionLogger.error("inside ecpCardPresent Cron--------------");
        int transactionCounter=0;
        StringBuffer success=new StringBuffer();
        String from_date = RB.getString("FROM_DATE");
        String to_date = RB.getString("TO_DATE");

        transactionLogger.error("from_date--------------"+from_date);
        transactionLogger.error("to_date--------------"+to_date);

        String processingType = "card_present";//"card_not_present";//"all";


        try
        {
            transactionLogger.error("Inside ecpCardPresentCron()::inside try-------");
            con=Database.getConnection();
            Set<String> accountIdSet = getAccountId();
//            String accountId = getAccountId();

            for(String accountId : accountIdSet)
            {
//              ECPPaymentGateway ecpPaymentGateway = new ECPPaymentGateway("2973");
                ECPCPPaymentGateway ecpcpPaymentGateway = new ECPCPPaymentGateway(accountId);

                List<EcpResponseVo> responseVoList = ecpcpPaymentGateway.processCardPresentTransactionByDate(from_date, to_date, processingType);
                transactionCounter = responseVoList.size();

                transactionLogger.error("total card_present transaction found :::: "+transactionCounter);
                for (EcpResponseVo ecpResponseVo : responseVoList)
                {
                    //remove unwanted '.' form cardType (coming from response)
                    String cardType = ecpResponseVo.getCardBrand();

                    if(cardType.contains(".")){
                        cardType = cardType.replace(".","");
                    }

                    String cardTypeId = "1";
                    if(cardType.equalsIgnoreCase("visa")){
                        cardTypeId="1";
                    }
                    if(cardType.equalsIgnoreCase("MasterCard")){
                        cardTypeId="2";
                    }
                    if(cardType.equalsIgnoreCase("MasterCard")){
                        cardTypeId="2";
                    }

                    boolean isPresent = true;
                    //check already same transaction present
                    String paymentId = ecpResponseVo.getOriginalTransactionUniqueId();
                    if (functions.isValueNull(paymentId))
                    {
                        isPresent = checkEntryPresent(ecpResponseVo.getOriginalTransactionUniqueId());
                    }

                    //insert entry to transaction_card_present table if not present
                    transactionLogger.error(ecpResponseVo.getOriginalTransactionUniqueId() + " : isTransactionPresent-----" + isPresent);
                    if (!isPresent)
                    {
//                        transactionLogger.error("ecpResponseVo.getServiceTypeDesc()-------------"+ecpResponseVo.getServiceTypeDesc().trim());

                        String paymodeId = "32";//CP - CardPresent
                        transactionLogger.error("cardTypeId="+cardTypeId+"  accountId="+accountId+"  paymodeId="+paymodeId);
                        TransactionDetailsVO transactionDetailsVO = getTerminalDetails(accountId, cardTypeId, paymodeId);
                        String memberId = transactionDetailsVO.getToid();
                        if(functions.isValueNull(memberId))
                        {
                            MerchantDetailsVO merchantDetailsVO = new MerchantConfigManager().getMerchantDetailFromToId(transactionDetailsVO.getToid());

                            //insert into transaction_card_present
                            String trackingId = insertEntry(ecpResponseVo, transactionDetailsVO, merchantDetailsVO);

                            //insert into transaction_common_details_card_present
                            insertTransactionCommonDetailsCardPresentEntry(ecpResponseVo, transactionDetailsVO, merchantDetailsVO, trackingId);
                        }
                    }
                    else{
                        //update transaction_common_card_present

                    }
                }
            }

        }catch (Exception e){
            transactionLogger.error("Exception ---"+e);
        }finally
        {
            Database.closeConnection(con);
        }

        return "";
    }

    public boolean checkEntryPresent(String paymetnId){
        boolean isPresent = false;
        try{
            String query  = "select paymentid from transaction_card_present where paymentid = ?";
            PreparedStatement ps =con.prepareStatement(query);
            ps.setString(1,paymetnId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }catch(Exception e){
            transactionLogger.error(e);
        }
        return false;
    }

//    public void updateEntry(EcpResponseVo ecpResponseVo, String trackingId){
//        //update transaction_common_card_present
//        try
//        {
//            String updateQuery = " UPDATE transaction_common_card_present  SET --- WHERE trackingid = '"+trackingId+"' ";
//            PreparedStatement pst = con.prepareStatement(updateQuery);
//            pst.executeUpdate();
//        }catch (Exception e){
//            transactionLogger.error("Exception --"+e);
//        }
//    }

    public void insertTransactionCommonDetailsCardPresentEntry(EcpResponseVo ecpResponseVo, TransactionDetailsVO transactionDetailsVO,MerchantDetailsVO merchantDetailsVO,String trackingid){
        try
        {
            String merchantId = merchantDetailsVO.getMemberId();
            String trackingId = trackingid;
            transactionLogger.error("tracking id ---------"+trackingId);
            String action = "Capture Successful";
            String actionExecutorName = "AdminCron";
            String status = "capturesuccess";
            String amount = ecpResponseVo.getAmount();
            String currency = ecpResponseVo.getCurrency();
            String remark = "SYS: remark";
            String responseTransactionId = ecpResponseVo.getOriginalTransactionUniqueId();
    //        String responseDescription = "SYS: response Description";
            String arn = ecpResponseVo.getArn();

            String query = "insert into transaction_common_details_card_present (trackingid,action,status,amount,currency,remark,responsetransactionid,arn,actionexecutorname,actionexecutorid) values(?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,trackingId);
            ps.setString(2,action);
            ps.setString(3,status);
            ps.setString(4,amount);
            ps.setString(5,currency);
            ps.setString(6,remark);
            ps.setString(7,responseTransactionId);
            ps.setString(8,arn);
            ps.setString(9,actionExecutorName);
            ps.setString(10,merchantId);
            int result = ps.executeUpdate();

        }catch(Exception e){
            transactionLogger.error("Exception in insertTransactionCommonDetailsCardPresentEntry() ---------"+e);
        }
    }

    public String insertEntry(EcpResponseVo ecpResponseVo, TransactionDetailsVO transactionDetailsVO,MerchantDetailsVO merchantDetailsVO)
    {
        String amount= ecpResponseVo.getAmount();
        String currency=ecpResponseVo.getCurrency();
        String cardType = ecpResponseVo.getCardBrand();
        String paymentId = ecpResponseVo.getOriginalTransactionUniqueId();
        String orderid = ecpResponseVo.getMerchantNumber();
        String binCountry = ecpResponseVo.getBinCountry();
        String cardNumber = ecpResponseVo.getCardNumber();
        String transactionType = ecpResponseVo.getTransactionType();
        String transactionTime = (ecpResponseVo.getTransactionDate()).substring(0,19);

        String toid = transactionDetailsVO.getToid();
        String accountId = transactionDetailsVO.getAccountId();
        String paymodeId = transactionDetailsVO.getPaymodeId();
        String cardTypeId = transactionDetailsVO.getCardTypeId();
        String terminalId = transactionDetailsVO.getTerminalId();


        transactionLogger.error("cardTypeId====="+cardTypeId);

        String totype=merchantDetailsVO.getPartnerName();
        String fromid="";

        String captureamount=amount;
        String templateamount = amount;
        String templateCurrency=currency;

        String orderdesc="SYS: order description";
        String description="SYS: description";
        String remark ="SYS: remark";
        String status = "capturesuccess";
        String fromType="ecpcp";

        String query = "INSERT INTO transaction_card_present(amount,currency,cardtype,paymentid,status,fromtype,country,accountid,paymodeid,cardtypeid,description,orderdescription,captureamount,toid,templateamount,terminalid,templatecurrency,remark,totype,ccnum,transactionTime,dtstamp)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()))";
        try
        {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,amount);
            ps.setString(2,currency);
            ps.setString(3,cardType);
            ps.setString(4,paymentId);
            ps.setString(5,status);
            ps.setString(6,fromType);
            ps.setString(7,binCountry);
            ps.setString(8,accountId);
            ps.setString(9,paymodeId);
            ps.setString(10,cardTypeId);
            ps.setString(11,description);
            ps.setString(12,orderdesc);
            ps.setString(13,captureamount);
            ps.setString(14,toid);
            ps.setString(15,templateamount);
            ps.setString(16,terminalId);
            ps.setString(17,templateCurrency);
            ps.setString(18,remark);
            ps.setString(19,totype);
            ps.setString(20,cardNumber);
            ps.setString(21,transactionTime);

            int result = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
               return rs.getString(1);
            }
        }catch (Exception e){
            transactionLogger.error("Exception in insertEntry()-------"+e);
        }
        return "";
    }

    public Set getAccountId(){
//        String accountId="";
        Set<String> set = new HashSet<String>();
        try{
//            String query = "SELECT accountid FROM transaction_common WHERE fromtype = 'ecp'";
            String query = "SELECT accountid FROM gateway_accounts WHERE pgtypeid IN (SELECT pgtypeid FROM gateway_type WHERE gateway='ecpcp')";
            PreparedStatement ps=con.prepareStatement(query);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                set.add(rs.getString("accountid"));
                transactionLogger.error("accoutid in getAccountId : ----"+rs.getString("accountid"));
            }

        }catch(Exception e){
            transactionLogger.error("Exception in getAccountId() ---"+e);
        }
        return set;
    }

    public TransactionDetailsVO getTerminalDetails(String accountId, String cardTypeId,String paymodeId){
        transactionLogger.error("inside getTransactonDetails()------------");
//        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        TransactionDetailsVO transactionDetailsVO = new TransactionDetailsVO();
        try{
            String query = "Select memberid,accountid,paymodeid,cardtypeid,terminalid from member_account_mapping where accountid = ? and cardtypeid = ? and paymodeid = ? ";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,accountId);
            ps.setString(2,cardTypeId);
            ps.setString(3,paymodeId);

            ResultSet rs = ps.executeQuery();
            System.out.println("query-----"+ps);
            if(rs.next()){

                transactionDetailsVO.setToid(rs.getString("memberid"));
                transactionDetailsVO.setAccountId(rs.getString("accountid"));
                transactionDetailsVO.setPaymodeId(rs.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(rs.getString("cardtypeid"));
                transactionDetailsVO.setTerminalId(rs.getString("terminalid"));
            }

        }catch(Exception e){
            transactionLogger.error("Exception in getTransactionDetails() ----------"+e);
        }
        return transactionDetailsVO;
    }
    public String getPaymodeFromPaymentType(String payModeVal){
        String paymode="";
        if("Debit Card".equalsIgnoreCase(payModeVal)){
            paymode="DC";
        }
        if("Credit Card".equalsIgnoreCase(payModeVal)){
            paymode="CC";
        }
        return paymode;
    }
}
