package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.MerchantModulesMappingVO;
import com.manager.vo.MerchantSubModuleVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by admin on 4/26/2016.
 */
public class MerchantModuleDAO
{
    private static Logger logger = new Logger(MerchantModuleDAO.class.getName());
    public Set<String> getMerchantAccessModuleSetOld(String userId)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        Set<String> set=new HashSet<String>();
        try
        {
            //conn = Database.getConnection();
            conn=Database.getRDBConnection();
            query = "SELECT dmm.modulename FROM merchant_users_modules_mapping AS amm JOIN merchant_modules_master AS dmm ON amm.moduleid=dmm.moduleid WHERE userid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,userId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                set.add(rs.getString("modulename"));
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "getMerchantAccessModuleSet()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "getMerchantAccessModuleSet()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return set;
    }
    public Set<String> getMerchantAccessModuleSet(String userId)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        Set<String> set=new HashSet();
        try
        {
            conn = Database.getConnection();
            query = "SELECT dmm.modulename FROM merchant_users_modules_mapping AS amm JOIN merchant_modules_master AS dmm ON amm.moduleid=dmm.moduleid WHERE userid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,userId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                set.add(rs.getString("modulename"));
            }

            query = "SELECT msmm.sub_module_name FROM merchant_users_submodules_mapping AS musmm JOIN merchant_submodules_master AS msmm ON musmm.submoduleid=msmm.id WHERE userid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,userId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                set.add(rs.getString("sub_module_name"));
            }
        }
        catch (SystemError se){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "getMerchantAccessModuleSet()", null, "common", "SQLException:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "getMerchantAccessModuleSet()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return set;
    }
    public Set<String> getMerchantRestrictedModuleSet(String memberId)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        Set<String> restrictedModules=new HashSet();
        try
        {
            //conn = Database.getConnection();
            conn=Database.getRDBConnection();
            query = "SELECT isappmanageractivate,is_recurring,iscardregistrationallowed,dashboard_access,accounting_access,setting_access,transactions_access,invoicing_access,virtualterminal_access,merchantmgt_access,settings_fraudrule_config_access,settings_merchant_config_access,accounts_account_summary_access,accounts_charges_summary_access,accounts_transaction_summary_access,accounts_reports_summary_access,settings_merchant_profile_access,settings_organisation_profile_access,settings_checkout_page_access,settings_generate_key_access,settings_invoice_config_access,transmgt_transaction_access,transmgt_capture_access,transmgt_reversal_access,transmgt_payout_access,invoice_generate_access,invoice_history_access,tokenmgt_registration_history_access,tokenmgt_register_card_access,merchantmgt_user_management_access FROM `members` AS m JOIN merchant_configuration AS mc ON m.`memberid`=mc.`memberid` WHERE m.`memberid`=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,memberId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if("N".equals(rs.getString("dashboard_access"))){
                    restrictedModules.add("DASHBOARD");
                }
                if ("N".equals(rs.getString("accounting_access"))){
                    restrictedModules.add("ACCOUNT_DETAILS");
                }
                if ("N".equals(rs.getString("setting_access"))){
                    restrictedModules.add("SETTINGS");
                }
                if ("N".equals(rs.getString("transactions_access"))){
                    restrictedModules.add("TRANSACTION_MANAGEMENT");
                }
                if ("N".equals(rs.getString("invoicing_access"))){
                    restrictedModules.add("INVOICE");
                }
                if ("N".equals(rs.getString("iscardregistrationallowed"))){
                    restrictedModules.add("TOKEN_MANAGEMENT");
                }
                if ("N".equals(rs.getString("virtualterminal_access"))){
                    restrictedModules.add("VIRTUAL_TERMINAL");
                }
                if ("N".equals(rs.getString("merchantmgt_access"))){
                    restrictedModules.add("MERCHANT_MANAGEMENT");
                }
                if("N".equals(rs.getString("isappmanageractivate"))){
                    restrictedModules.add("MERCHANT_APPLICATION");
                }
                if ("N".equals(rs.getString("is_recurring"))){
                    restrictedModules.add("RECURRING_MODULE");
                }
                if ("N".equals(rs.getString("accounts_account_summary_access"))){
                    restrictedModules.add("ACCOUNT_SUMMARY");
                }
                if ("N".equals(rs.getString("accounts_charges_summary_access"))){
                    restrictedModules.add("CHARGES_SUMMARY");
                }
                if ("N".equals(rs.getString("accounts_transaction_summary_access"))){
                    restrictedModules.add("TRANSACTION_SUMMARY");
                }
                if ("N".equals(rs.getString("accounts_reports_summary_access"))){
                    restrictedModules.add("REPORTS_SUMMARY");
                }
                if ("N".equals(rs.getString("settings_merchant_profile_access"))){
                    restrictedModules.add("MERCHANT_PROFILE");
                }
                if ("N".equals(rs.getString("settings_organisation_profile_access"))){
                    restrictedModules.add("ORGANISATION_PROFILE");
                }
                if ("N".equals(rs.getString("settings_checkout_page_access"))){
                    restrictedModules.add("CHECKOUT_PAGE");
                }
                if ("N".equals(rs.getString("settings_generate_key_access"))){
                    restrictedModules.add("GENERATE_KEY");
                }
                if ("N".equals(rs.getString("settings_merchant_config_access"))){
                    restrictedModules.add("MERCHANT_CONFIG");
                }
                if ("N".equals(rs.getString("settings_fraudrule_config_access"))){
                    restrictedModules.add("FRAUD_RULE_CONFIG");
                }
                if ("N".equals(rs.getString("settings_invoice_config_access"))){
                    restrictedModules.add("INVOICE_CONFIGURATION");
                }
                if ("N".equals(rs.getString("transmgt_transaction_access"))){
                    restrictedModules.add("TRANSACTIONS");
                }
                if ("N".equals(rs.getString("transmgt_capture_access"))){
                    restrictedModules.add("CAPTURE");
                }
                if ("N".equals(rs.getString("transmgt_reversal_access"))){
                    restrictedModules.add("REVERSAL");
                }
                if ("N".equals(rs.getString("transmgt_payout_access"))){
                    restrictedModules.add("PAYOUT");
                }
                if ("N".equals(rs.getString("invoice_generate_access"))){
                    restrictedModules.add("GENERATE_INVOICE");
                }
                if ("N".equals(rs.getString("invoice_history_access"))){
                    restrictedModules.add("INVOICE_HISTORY");
                }
                if ("N".equals(rs.getString("tokenmgt_registration_history_access"))){
                    restrictedModules.add("REGISTRATION_HISTORY");
                }
                if ("N".equals(rs.getString("tokenmgt_register_card_access"))){
                    restrictedModules.add("REGISTER_CARD");
                }
                if ("N".equals(rs.getString("merchantmgt_user_management_access"))){
                    restrictedModules.add("USER_MANAGEMENT");
                }
            }
        }
        catch (SystemError se){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "getMerchantRestrictedModuleSet()", null, "common", "SQLException:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "getMerchantRestrictedModuleSet()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return restrictedModules;
    }
    public String addNewModuleMapping(MerchantModulesMappingVO merchantModulesMappingVO)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        String status="";
        try
        {
            conn = Database.getConnection();
            query = "insert into merchant_users_modules_mapping(mappingid,memberid,moduleid,userid)values(null,?,?,?)";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,merchantModulesMappingVO.getMerchantId());
            pstmt.setString(2,merchantModulesMappingVO.getModuleId());
            pstmt.setString(3,merchantModulesMappingVO.getUserId());
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                status="success";
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "addNewModuleMapping()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "addNewModuleMapping()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return status;
    }
    public String addNewSubModuleMapping(MerchantModulesMappingVO merchantModulesMappingVO)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        String query=null;
        String status="";
        try{
            conn = Database.getConnection();
            query = "insert into merchant_users_submodules_mapping(mappingid,memberid,submoduleid,userid)values(null,?,?,?)";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,merchantModulesMappingVO.getMerchantId());
            pstmt.setString(2,merchantModulesMappingVO.getSubModuleId());
            pstmt.setString(3,merchantModulesMappingVO.getUserId());
            int k=pstmt.executeUpdate();
            if(k>0){
                status="success";
            }
        }
        catch(SystemError se){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "addNewSubModuleMapping()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "addNewModuleMapping()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return status;
    }
    public boolean isMappingAvailable(MerchantModulesMappingVO merchantModulesMappingVO)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        boolean status=false;
        String query=null;

        try
        {
            conn = Database.getConnection();
            query = "select moduleid from merchant_users_modules_mapping where userid=? and moduleid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1, merchantModulesMappingVO.getUserId());
            pstmt.setString(2, merchantModulesMappingVO.getModuleId());
            resultSet=pstmt.executeQuery();
            if(resultSet.next())
            {
                status=true;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "isMappingAvailable()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "isMappingAvailable()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return status;
    }
    public boolean isMappingRemoved(String userId)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        String query=null;
        try{
            conn = Database.getConnection();
            query = "delete from merchant_users_modules_mapping where userid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,userId);
            int result=pstmt.executeUpdate();
            if(result>0){
                status=true;
            }
        }
        catch (SystemError se){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "isMappingRemoved()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "isMappingAvailable()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return status;
    }
    public boolean isSubMappingRemoved(String userId)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        String query=null;
        try{
            conn = Database.getConnection();
            query = "delete from merchant_users_submodules_mapping where userid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,userId);
            int result=pstmt.executeUpdate();
            if(result>0){
                status=true;
            }
        }
        catch (SystemError se){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "isSubMappingRemoved()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "isMappingAvailable()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return status;
    }
    public boolean removeMerchantModuleMapping(String mappingId)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        String query=null;
        try{
            conn = Database.getConnection();
            query = "delete from merchant_users_modules_mapping where mappingid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,mappingId);
            int k=pstmt.executeUpdate();
            if(k>0){
                status=true;
            }
        }
        catch(SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "removeMerchantModuleMapping()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "removeMerchantModuleMapping()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return status;
    }
    public HashMap<String, List> merchantModuleList()throws PZDBViolationException
    {
        LinkedHashMap<String,List> subModules=new LinkedHashMap();
        List<MerchantSubModuleVO> subModuleList=null;
        MerchantSubModuleVO merchantSubModuleVO=null;

        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        ResultSet resultSet1=null;
        String query=null;
        try{
            //conn = Database.getConnection();
            conn=Database.getRDBConnection();
            query = "select * from merchant_modules_master";
            pstmt= conn.prepareStatement(query);
            resultSet=pstmt.executeQuery();
            while(resultSet.next()){
                subModuleList=new ArrayList();
                String moduleId=resultSet.getString("moduleid");
                String moduleName=resultSet.getString("moduleid")+"-"+resultSet.getString("modulename");

                String query1="select * from merchant_submodules_master WHERE master_module_id=?";
                pstmt= conn.prepareStatement(query1);
                pstmt.setString(1,moduleId);
                resultSet1=pstmt.executeQuery();
                while(resultSet1.next()){
                    merchantSubModuleVO=new MerchantSubModuleVO();
                    merchantSubModuleVO.setSubModuleId(resultSet1.getString("id"));
                    merchantSubModuleVO.setSubModuleName(resultSet1.getString("sub_module_name"));
                    subModuleList.add(merchantSubModuleVO);
                }
                subModules.put(moduleName,subModuleList);
            }
            logger.error("merchantModuleList method  statement --->"+pstmt);
        }
        catch (SystemError se){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "merchantModuleList()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "merchantModuleList()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeResultSet(resultSet1);
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return subModules;
    }

    public HashMap<String, List> merchantUserModuleList(String userId)throws PZDBViolationException
    {
        LinkedHashMap<String,List> subModules=new LinkedHashMap();
        List<MerchantSubModuleVO> subModuleList=null;
        MerchantSubModuleVO merchantSubModuleVO=null;

        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        ResultSet resultSet1=null;
        String query=null;
        try{
            //conn = Database.getConnection();
            conn=Database.getRDBConnection();
            query = "SELECT dmm.modulename AS modulename , dmm.moduleid AS moduleid FROM merchant_users_modules_mapping AS amm JOIN merchant_modules_master AS dmm ON amm.moduleid=dmm.moduleid WHERE userid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,userId);
            resultSet=pstmt.executeQuery();
            while(resultSet.next()){
                subModuleList=new ArrayList();
                String moduleId=resultSet.getString("moduleid");
                String moduleName=resultSet.getString("moduleid")+"-"+resultSet.getString("modulename");

                String query1="SELECT msmm.id AS id, msmm.sub_module_name AS sub_module_name FROM merchant_users_submodules_mapping AS musmm JOIN merchant_submodules_master AS msmm ON musmm.submoduleid=msmm.id WHERE userid=? and master_module_id=?";
                pstmt= conn.prepareStatement(query1);
                pstmt.setString(1,userId);
                pstmt.setString(2,moduleId);
                resultSet1=pstmt.executeQuery();
                while(resultSet1.next()){
                    merchantSubModuleVO=new MerchantSubModuleVO();
                    merchantSubModuleVO.setSubModuleId(resultSet1.getString("id"));
                    merchantSubModuleVO.setSubModuleName(resultSet1.getString("sub_module_name"));
                    subModuleList.add(merchantSubModuleVO);
                }
                subModules.put(moduleName,subModuleList);
            }
            logger.error("merchantUserModuleList method  statement --->"+pstmt);

        }
        catch (SystemError se){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "merchantModuleList()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "merchantModuleList()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeResultSet(resultSet1);
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return subModules;
    }

    public boolean isMappingAvailable(String userid,String modulename)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        boolean status=false;
        String query=null;

        try
        {
            conn = Database.getConnection();
            query = "SELECT dmm.modulename FROM merchant_users_modules_mapping AS amm JOIN merchant_modules_master AS dmm ON amm.moduleid=dmm.moduleid WHERE amm.userId=? AND dmm.modulename=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1, userid);
            pstmt.setString(2, modulename);
            logger.error("query---"+query);
            resultSet=pstmt.executeQuery();
            if(resultSet.next())
            {
                status=true;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "isMappingAvailable()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("MerchantModuleDAO.java", "isMappingAvailable()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return status;
    }
}
