package com.fraud.dao;

import com.directi.pg.*;
import com.fraud.fourstop.FourStopResponseVO;
import com.fraud.vo.*;
import com.manager.vo.fraudruleconfVOs.RuleMasterVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SurajT on 4/30/2018.
 */
public class FraudDAO
{
    static Logger logger = new Logger(FraudDAO.class.getName());
    static TransactionLogger transactionLogger = new TransactionLogger(FraudDAO.class.getName());
    Functions functions=new Functions();

    public int allocateCustomerId(PZFraudCustRegRequestVO pzFraudCustRegRequestVO,String fsid)
    {
        int customerId = 0;
        Connection con = null;

        try
        {
            con = Database.getConnection();
            String query="insert into customer_registration(partnerId,fsid,firstName,cust_request_id,lastName,countryCode,emailId,address1,address2,city,state,province,id_type,id_value,phone,reg_date,customerIpAddress,website,dob,zip,reg_status,action,dtstamp) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))";
            PreparedStatement p=con.prepareStatement(query);
            p.setString(1,pzFraudCustRegRequestVO.getPartnerId());
            p.setString(2,fsid);
            p.setString(3, pzFraudCustRegRequestVO.getFirstName());
            p.setString(4, pzFraudCustRegRequestVO.getCust_request_id());
            p.setString(5, pzFraudCustRegRequestVO.getLastName());
            p.setString(6, pzFraudCustRegRequestVO.getCountryCode());
            p.setString(7, pzFraudCustRegRequestVO.getEmailId());
            p.setString(8, pzFraudCustRegRequestVO.getAddress1());
            p.setString(9, pzFraudCustRegRequestVO.getAddress2());
            p.setString(10, pzFraudCustRegRequestVO.getCity());
            p.setString(11, pzFraudCustRegRequestVO.getState());
            p.setString(12, pzFraudCustRegRequestVO.getProvince());
            p.setString(13, pzFraudCustRegRequestVO.getId_type());
            p.setString(14, pzFraudCustRegRequestVO.getId_value());
            p.setString(15, pzFraudCustRegRequestVO.getPhone());
            p.setString(16, pzFraudCustRegRequestVO.getCustomerRegDate());
            p.setString(17, pzFraudCustRegRequestVO.getCustomerIpAddress());
            p.setString(18,pzFraudCustRegRequestVO.getWebsite());
            p.setString(19,pzFraudCustRegRequestVO.getBirthDate());
            p.setString(20,pzFraudCustRegRequestVO.getZip());
            p.setString(21,"Pending");
            p.setString(22,"Cust_registration");
            //System.out.println("sql query for reg ::::-"+p.toString());
            int num = p.executeUpdate();
            if (num ==1)
            {
                ResultSet rs = p.getGeneratedKeys();
                if(rs.next())
                {
                    customerId = rs.getInt(1);
                }
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving FraudTransactionDAO", se);

        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransactionDAO", e);
        }
        finally{
            Database.closeConnection(con);
        }
        return customerId;
    }

    public boolean updateCustomerInfo(FourStopResponseVO fourStopResponseVO, int id)
    {
        boolean result=false;
        Connection con = null;
        JSONObject jsonObject=fourStopResponseVO.getJsonObject();
        PreparedStatement p = null;
        try
        {
            con = Database.getConnection();
            String query="update customer_registration set customer_registration_id=? , score=? , recommendation=? , confidence_level=? , description=?, reg_status=? where id=?";
            p=con.prepareStatement(query);
            p.setString(1, String.valueOf(fourStopResponseVO.getCustomerRegId()));
            p.setString(2, String.valueOf(fourStopResponseVO.getScore()));
            p.setString(3, fourStopResponseVO.getRecommendation());
            p.setString(4, String.valueOf(fourStopResponseVO.getConfidence_level()));
            p.setString(5, fourStopResponseVO.getDescription());
            p.setString(6, "Completed");
            p.setString(7, String.valueOf(id));

            int num = p.executeUpdate();
            if (num ==1)
            {
                result=true;
                if(jsonObject!=null)
                {
                    if(jsonObject.has("rules_triggered"))
                    {
                        JSONArray arr =jsonObject.getJSONArray("rules_triggered");
                        for (int j = 0; j < arr.length(); j++)
                        {
                            String ruleName = arr.getJSONObject(j).getString("name");
                            String ruleScore = arr.getJSONObject(j).getString("score");
                            String sql = "INSERT INTO fraud_custreg_rules_triggered(rulename,rulescore,fraud_custregid)VALUES(?,?,?);";
                            p= con.prepareStatement(sql);
                            p.setString(1,ruleName);
                            p.setString(2,ruleScore);
                            p.setString(3,String.valueOf(id));
                            int triggeredRule=p.executeUpdate();
                        }
                    }
                }
            }
        }
        catch (SQLException se){
            logger.error("SQLException ::::::: Leaving FraudTransactionDAO", se);
        }
        catch(SystemError e){
            logger.error("SystemError ::::::: Leaving FraudTransactionDAO", e);
        }
        catch(Exception e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        finally{
            Database.closeConnection(con);
        }
        return result;
    }


   /* public int insertDocVerifyInfo(PZFraudDocVerifyRequestVO pzFraudDocVerifyRequestVO)
    {
        // boolean result=false;
        int customer_reg_Id = 0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            StringBuffer query=new StringBuffer("update customer_registration set file_name=?");//"update customer_registration set file_name=? , file_name2=? , method=? ,verify_status=? , action=? where id=?";





            if (pzFraudDocVerifyRequestVO.getFileName2()!=null && !"".equals(pzFraudDocVerifyRequestVO.getFileName2())){
                query.append(",file_name2='"+pzFraudDocVerifyRequestVO.getFileName2().toString()+"'");
                //p.setString(2,pzFraudDocVerifyRequestVO.getFileName2());
            }

            if (pzFraudDocVerifyRequestVO.getFileName3()!=null && !"".equals(pzFraudDocVerifyRequestVO.getFileName3())){
                query.append(",file_name3='"+pzFraudDocVerifyRequestVO.getFileName3().toString()+"'");
                //p.setString(3,pzFraudDocVerifyRequestVO.getFileName3());
            }

            if (pzFraudDocVerifyRequestVO.getFileName4()!=null && !"".equals(pzFraudDocVerifyRequestVO.getFileName4())){
                query.append(",file_name4='"+pzFraudDocVerifyRequestVO.getFileName4().toString()+"'");
               // p.setString(4,pzFraudDocVerifyRequestVO.getFileName4());
            }
            query.append(",method = ? , verify_status=? , action=? where id=?");

            PreparedStatement p=con.prepareStatement(query.toString());
            p.setString(1,pzFraudDocVerifyRequestVO.getFileName());
            p.setString(2,pzFraudDocVerifyRequestVO.getMethod());
            p.setString(3,"Pending");
            p.setString(4,"Cust_verification");
            p.setString(5,pzFraudDocVerifyRequestVO.getCustomer_registration_id());

            System.out.println("db query : "+p.toString());


            int num = p.executeUpdate();
            if (num ==1)
            {

                String query1="select customer_registration_id FROM customer_registration where id=?";
                PreparedStatement p1=con.prepareStatement(query1);

                p1.setString(1,pzFraudDocVerifyRequestVO.getCustomer_registration_id());
                ResultSet rs = p1.executeQuery();

                System.out.println("query : "+p1);
                if (rs.next()){
                    customer_reg_Id=rs.getInt("customer_registration_id");
                }

                //result=true;
            }
        }
        catch (SQLException se){
            logger.error("SQLException ::::::: Leaving FraudTransactionDAO", se);
        }
        catch(SystemError e){
            logger.error("SystemError ::::::: Leaving FraudTransactionDAO", e);
        }
        finally{
            Database.closeConnection(con);
        }
        return customer_reg_Id;
    }
*/

    public int insertDocVerifyInfo(PZFraudDocVerifyRequestVO pzFraudDocVerifyRequestVO)
    {
        PZCustomerDetailsVO pzCustomerDetailsVO=pzFraudDocVerifyRequestVO.getPzCustomerDetailsVO();
        // boolean result=false;
        int customerId = 0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            //String query="UPDATE customer_registration set file_name=?,file_name2=?,file_name3=?,file_name4=?,method=?,notification_Url=?,verify_status='Pending',action='Cust_verification',dtstamp=unix_timestamp(now()) where id=?";//update customer_registration set file_name=? , file_name2=? , method=? ,verify_status=? , action=? where id=?";
            String query="insert into customer_registration (cust_request_id,customer_registration_id,file_name,file_name2,file_name3,file_name4,method,notification_Url,verify_status,action,dtstamp) VALUES (?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))";//update customer_registration set file_name=? , file_name2=? , method=? ,verify_status=? , action=? where id=?";
            PreparedStatement p=con.prepareStatement(query);


            p.setString(1,pzCustomerDetailsVO.getCust_request_id());
            p.setString(2,pzCustomerDetailsVO.getCustomer_registration_id());
            p.setString(3,pzFraudDocVerifyRequestVO.getFileName());
            p.setString(4,pzFraudDocVerifyRequestVO.getFileName2());
            p.setString(5,pzFraudDocVerifyRequestVO.getFileName3());
            p.setString(6,pzFraudDocVerifyRequestVO.getFileName4());
            p.setString(7,pzFraudDocVerifyRequestVO.getMethod());
            p.setString(8,pzFraudDocVerifyRequestVO.getNotificationUrl());
            p.setString(9,"Pending");
            p.setString(10,"Cust_verification");

            /*p.setString(1,pzFraudDocVerifyRequestVO.getFileName());
            p.setString(2,pzFraudDocVerifyRequestVO.getFileName2());
            p.setString(3,pzFraudDocVerifyRequestVO.getFileName3());
            p.setString(4,pzFraudDocVerifyRequestVO.getFileName4());
            p.setString(5,pzFraudDocVerifyRequestVO.getMethod());
            p.setString(6,pzFraudDocVerifyRequestVO.getNotificationUrl());
            p.setString(7,pzFraudDocVerifyRequestVO.getCustomer_registration_id());*/


           logger.error("doc verify insert query : "+p.toString());

            int num = p.executeUpdate();

            if (num ==1)
            {
                ResultSet rs = p.getGeneratedKeys();
                if(rs.next())
                {
                    customerId = rs.getInt(1);
                }
            }
        }
        catch (SQLException se){
            logger.error("SQLException ::::::: Leaving FraudTransactionDAO", se);
        }
        catch(SystemError e){
            logger.error("SystemError ::::::: Leaving FraudTransactionDAO", e);
        }
        finally{
            Database.closeConnection(con);
        }
        return customerId;
    }

    public boolean updateDocVerifyInfo(FourStopResponseVO fourStopResponseVO, int id)
    {
        //System.out.println("inside update doc verify");
        boolean result=false;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query="update customer_registration set reference_id=? , verify_status=? ,doc_description=? where id=?";
            PreparedStatement p=con.prepareStatement(query);
            p.setString(1, fourStopResponseVO.getReference_id());
            p.setString(2, "Completed");
            p.setString(3, fourStopResponseVO.getDescription1());
            p.setInt(4, id);

            //System.out.println("doc verify query : "+p.toString());
            int num = p.executeUpdate();
            if (num ==1)
            {
                if (fourStopResponseVO.getReference_id2()!=null && !"".equals(fourStopResponseVO.getReference_id2())){
                    String query1="update customer_registration set reference_id2=? where id=?";
                    PreparedStatement p1=con.prepareStatement(query1);
                    p1.setString(1,fourStopResponseVO.getReference_id2());
                    p1.setString(2, String.valueOf(id));
                    result=true;
                }
                else {
                    result=true;
                }

            }
        }
        catch (SQLException se){
            logger.error("SQLException ::::::: Leaving FraudTransactionDAO", se);
        }
        catch(SystemError e){
            logger.error("SystemError ::::::: Leaving FraudTransactionDAO", e);
        }
        finally{
            Database.closeConnection(con);
        }
        return result;
    }

    public PZCustomerDetailsVO getCustomerRegistrationDetails(PZFraudDocVerifyRequestVO pzFraudDocVerifyRequestVO)
    {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        PZCustomerDetailsVO pzCustomerDetailsVO=null;
        try
        {
            con= Database.getConnection();
            String Query = "select cust_request_id, customer_registration_id from customer_registration where id=? ";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,pzFraudDocVerifyRequestVO.getCustomer_registration_id());

            logger.error("query : "+preparedStatement.toString());

            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                pzCustomerDetailsVO= new PZCustomerDetailsVO();
                pzCustomerDetailsVO.setCust_request_id(rs.getString("cust_request_id"));
                pzCustomerDetailsVO.setCustomer_registration_id(rs.getString("customer_registration_id"));

               //pzFraudDocVerifyRequestVO.setPzCustomerDetailsVO(pzCustomerDetailsVO);
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving ", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return pzCustomerDetailsVO;
    }

    public boolean isKeyValid(String key){

        boolean result=false;


        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;

        try
        {
            con= Database.getConnection();
            String Query = "select login from partners where clkey=? ";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,key);

            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                result=true;
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException in key validation ::::::: Leaving", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError in key validation ::::::: Leaving ", e);
        }
        finally
        {
            Database.closeConnection(con);
        }

        return result;
    }

    public String getuserName(String key){

        String username="";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;

        try
        {
            con= Database.getConnection();
            String Query = "select login from partners where clkey=? ";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,key);

            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                username=rs.getString("login");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException in key validation ::::::: Leaving", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError in key validation ::::::: Leaving ", e);
        }
        finally
        {
            Database.closeConnection(con);
        }

        return username;
    }

    public boolean updateDocVerifyScore(PZFraudDocVerifyResponseVO pzFraudDocVerifyResponseVO)
    {
        boolean result=false;
        Connection con = null;
        JSONObject jsonObject=pzFraudDocVerifyResponseVO.getJsonObject();
        PreparedStatement p = null;

        String filedecision1_decision ="" ;
        String filedecision1_classtype = "";
        String filedecision1_doctype = "";
        String filedecision2_decision = "";
        String filedecision2_classtype = "";
        String filedecision2_doctype = "";
        String filedecision3_decision ="";
        String filedecision3_classtype = "";
        String filedecision3_doctype = "";
        String filedecision4_decision ="" ;
        String filedecision4_classtype = "";
        String filedecision4_doctype = "";

        if (functions.isValueNull(pzFraudDocVerifyResponseVO.getFiledecision1_decision()))
            filedecision1_decision=pzFraudDocVerifyResponseVO.getFiledecision1_decision();

        if (functions.isValueNull(pzFraudDocVerifyResponseVO.getFiledecision2_decision()))
            filedecision2_decision=pzFraudDocVerifyResponseVO.getFiledecision2_decision();

        if (functions.isValueNull(pzFraudDocVerifyResponseVO.getFiledecision3_decision()))
            filedecision3_decision=pzFraudDocVerifyResponseVO.getFiledecision3_decision();

        if (functions.isValueNull(pzFraudDocVerifyResponseVO.getFiledecision4_decision()))
            filedecision4_decision=pzFraudDocVerifyResponseVO.getFiledecision4_decision();


        if (functions.isValueNull(pzFraudDocVerifyResponseVO.getFiledecision1_classtype()))
            filedecision1_classtype=pzFraudDocVerifyResponseVO.getFiledecision1_classtype();

        if (functions.isValueNull(pzFraudDocVerifyResponseVO.getFiledecision1_classtype()))
            filedecision2_classtype=pzFraudDocVerifyResponseVO.getFiledecision2_classtype();

        if (functions.isValueNull(pzFraudDocVerifyResponseVO.getFiledecision1_classtype()))
            filedecision3_classtype=pzFraudDocVerifyResponseVO.getFiledecision3_classtype();

        if (functions.isValueNull(pzFraudDocVerifyResponseVO.getFiledecision1_classtype()))
            filedecision4_classtype=pzFraudDocVerifyResponseVO.getFiledecision4_classtype();

        if (functions.isValueNull(pzFraudDocVerifyResponseVO.getFiledecision1_doctype()))
            filedecision1_doctype=pzFraudDocVerifyResponseVO.getFiledecision1_doctype();

        if (functions.isValueNull(pzFraudDocVerifyResponseVO.getFiledecision2_doctype()))
            filedecision2_doctype=pzFraudDocVerifyResponseVO.getFiledecision2_doctype();

        if (functions.isValueNull(pzFraudDocVerifyResponseVO.getFiledecision3_doctype()))
            filedecision3_doctype=pzFraudDocVerifyResponseVO.getFiledecision3_doctype();

        if (functions.isValueNull(pzFraudDocVerifyResponseVO.getFiledecision4_doctype()))
            filedecision4_doctype=pzFraudDocVerifyResponseVO.getFiledecision4_doctype();

        try
        {
            con = Database.getConnection();
            String query="INSERT INTO document_verification (score,reference_id,score_complete,kyc_source,FileDecision1_Doctype,FileDecision1_Classtype,Filedecision1_Decision,FileDecision2_Doctype,FileDecision2_Classtype,FileDecision2_Decision,FileDecision3_Doctype,FileDecision3_Classtype,FileDecision3_Decision,FileDecision4_Doctype,FileDecision4_Classtype,FileDecision4_Decision) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            p=con.prepareStatement(query);
            p.setString(1, String.valueOf(pzFraudDocVerifyResponseVO.getScore()));
            p.setString(2, String.valueOf(pzFraudDocVerifyResponseVO.getReference_id()));
            p.setString(3, String.valueOf(pzFraudDocVerifyResponseVO.getScore_complete()));
            p.setString(4, String.valueOf(pzFraudDocVerifyResponseVO.getKyc_source()));

            p.setString(5,filedecision1_doctype);
            p.setString(6,filedecision1_classtype);
            p.setString(7,filedecision1_decision);

            p.setString(8,filedecision2_doctype);
            p.setString(9,filedecision2_classtype);
            p.setString(10,filedecision2_decision);

            p.setString(11,filedecision3_doctype);
            p.setString(12,filedecision3_classtype);
            p.setString(13,filedecision3_decision);

            p.setString(14,filedecision4_doctype);
            p.setString(15,filedecision4_classtype);
            p.setString(16,filedecision4_decision);

            int num = p.executeUpdate();
            if (num ==1)
            {
                result=true;
                if(jsonObject!=null)
                {
                    String id=pzFraudDocVerifyResponseVO.getReference_id();
                    if(jsonObject.has("rules_triggered"))
                    {
                        logger.error("inside doc verify rule triggered");
                        JSONArray arr =jsonObject.getJSONArray("rules_triggered");
                        for (int j = 0; j < arr.length(); j++)
                        {
                            String ruleName = arr.getJSONObject(j).getString("name");
                            String ruleScore = arr.getJSONObject(j).getString("score");
                            String sql = "INSERT INTO fraud_docverify_rules_triggered(rulename,rulescore,fraud_referenceId)VALUES(?,?,?);";
                            p= con.prepareStatement(sql);
                            p.setString(1,ruleName);
                            p.setString(2,ruleScore);
                            p.setString(3,String.valueOf(id));
                            logger.error("doc verify rule trigger query :"+p.toString());
                            int triggeredRule=p.executeUpdate();
                            if (triggeredRule>0){
                                logger.error("doc verify rule triggered successfully .");
                            }
                        }
                    }

                }
            }
        }
        catch (SQLException se){
            logger.error("SQLException ::::::: Leaving FraudTransactionDAO", se);
        }
        catch(SystemError e){
            logger.error("SystemError ::::::: Leaving FraudTransactionDAO", e);
        }
        catch(Exception e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        finally{
            Database.closeConnection(con);
        }
        return result;
    }

    public String getNotificationUrl(PZFraudDocVerifyResponseVO pzFraudDocVerifyResponseVO)
    {
        Connection connection=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        String url="";
        try
        {
            connection=Database.getConnection();
            String query="SELECT notification_Url from customer_registration where reference_id=?";
            ps=connection.prepareStatement(query);
            ps.setString(1,pzFraudDocVerifyResponseVO.getReference_id());
            rs=ps.executeQuery();
            logger.error("notification query : "+ps.toString());
            if (rs.next()){
                url=rs.getString("notification_Url");
            }
        }
        catch (SQLException se){
            logger.error("SQLException ::::::: Leaving FraudTransactionDAO", se);
        }
        catch(SystemError e){
            logger.error("SystemError ::::::: Leaving FraudTransactionDAO", e);
        }
        catch(Exception e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        finally{
            Database.closeConnection(connection);
        }
        return url;
    }

    public void insertTriggeredRuleEntry(RuleMasterVO internalFraudVO, String fraud_trans_id, boolean isRulePass)
    {
        PreparedStatement preparedStatement = null;
        Connection con = null;
        try
        {
            con= Database.getConnection();

            String sql = "INSERT INTO fraudtransaction_rules_triggered(id,rulename,rulescore,fraud_transid,status)VALUES(NULL,?,?,?,?);";
            preparedStatement= con.prepareStatement(sql);
            preparedStatement.setString(1,internalFraudVO.getRuleName());
            preparedStatement.setString(2,"0");
            preparedStatement.setString(3,fraud_trans_id);
            preparedStatement.setBoolean(4, isRulePass);
            int triggeredRule=preparedStatement.executeUpdate();
            transactionLogger.debug("fraudtransaction_rules_triggered Query---"+preparedStatement);
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving FraudTransaction", se);
        }
        catch(SystemError e)
        {
            logger.error("SystemError ::::::: Leaving FraudTransaction", e);
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
    }
}