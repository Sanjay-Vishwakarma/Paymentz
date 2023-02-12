package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.PartnerModulesMappingVO;
import com.manager.vo.PartnerSubModuleVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Trupti on 4/26/2016.
 */
public class PartnerModuleDAO
{
    private static Logger logger = new Logger(PartnerModuleDAO.class.getName());
    public Set<String> getPartnerAccessModuleSetOld(String userId)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        Set<String> set=new HashSet();
        try
        {
            conn = Database.getConnection();
            query = "SELECT dmm.modulename FROM partner_users_modules_mapping AS amm JOIN partner_modules_master AS dmm ON amm.moduleid=dmm.moduleid WHERE userid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,userId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                set.add(rs.getString("modulename"));
            }
        }
        catch (SystemError se){
            logger.error("SystemError::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "getPartnerAccessModuleSetOld()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "getPartnerAccessModuleSetOld()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return set;
    }

    //Method to fetch assign module for partners
    public Set<String> getPartnerAccessModuleList(String partnerID)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        Set<String> set=new HashSet();
        try{
            conn = Database.getConnection();
            query = "SELECT dmm.modulename FROM partner_users_modules_mapping AS amm JOIN partner_modules_master AS dmm ON amm.moduleid=dmm.moduleid WHERE userid='0' AND partnerid =?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,partnerID);
            rs = pstmt.executeQuery();
            while (rs.next()){
                set.add(rs.getString("modulename"));
            }

            query = "SELECT psmm.sub_module_name FROM partner_users_submodules_mapping AS pusmm JOIN partner_submodules_master AS psmm ON pusmm.submoduleid=psmm.id WHERE userid='0' AND partnerid =?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,partnerID);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                set.add(rs.getString("sub_module_name"));
            }
        }
        catch (SystemError se){
            logger.error("SystemError::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "getPartnerAccessModuleSet()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "getPartnerAccessModuleSet()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return set;
    }

    //Method to fetch  modules for partners
    public Set<String> getPartnerModuleList()throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        Set<String> set=new HashSet();
        try{
            conn = Database.getConnection();
            query = "select * from partner_modules_master ";
            pstmt= conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()){
                set.add(rs.getString("modulename"));
            }

            query = "select * from partner_submodules_master";
            pstmt= conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                set.add(rs.getString("sub_module_name"));
            }
        }
        catch (SystemError se){
            logger.error("SystemError::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "getPartnerAccessModuleSet()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "getPartnerAccessModuleSet()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return set;
    }

    //Method to fetch assign module for partners user
    public Set<String> getPartnerAccessModuleSet(String userId)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        Set<String> set=new HashSet();
        try{
            conn = Database.getConnection();
            query = "SELECT dmm.modulename FROM partner_users_modules_mapping AS amm JOIN partner_modules_master AS dmm ON amm.moduleid=dmm.moduleid WHERE userid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,userId);
            rs = pstmt.executeQuery();
            while (rs.next()){
                set.add(rs.getString("modulename"));
            }

            query = "SELECT psmm.sub_module_name FROM partner_users_submodules_mapping AS pusmm JOIN partner_submodules_master AS psmm ON pusmm.submoduleid=psmm.id WHERE userid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,userId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                set.add(rs.getString("sub_module_name"));
            }
        }
        catch (SystemError se){
            logger.error("SystemError::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "getPartnerAccessModuleSet()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "getPartnerAccessModuleSet()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return set;
    }
    public String addNewModuleMapping(PartnerModulesMappingVO partnerModulesMappingVO)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        String query=null;
        String status="";
        try{
            conn = Database.getConnection();
            query = "insert into partner_users_modules_mapping(mappingid,partnerid,moduleid,userid)values(null,?,?,?)";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,partnerModulesMappingVO.getPartnerId());
            pstmt.setString(2,partnerModulesMappingVO.getModuleId());
            pstmt.setString(3,partnerModulesMappingVO.getUserId());
            int k=pstmt.executeUpdate();
            if(k>0){
                status="success";
            }
        }
        catch (SystemError se){
            logger.error("SystemError:::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "addNewModuleMapping()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException:::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "addNewModuleMapping()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return status;
    }
    public String addNewSubModuleMapping(PartnerModulesMappingVO partnerModulesMappingVO)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        String query=null;
        String status="";
        try{
            conn = Database.getConnection();
            query = "insert into partner_users_submodules_mapping(mappingid,partnerid,submoduleid,userid)values(null,?,?,?)";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,partnerModulesMappingVO.getPartnerId());
            pstmt.setString(2,partnerModulesMappingVO.getSubModuleId());
            pstmt.setString(3,partnerModulesMappingVO.getUserId());
            int k=pstmt.executeUpdate();
            if(k>0){
                status="success";
            }
        }
        catch (SystemError se){
            logger.error("SystemError:::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "addNewSubModuleMapping()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException:::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "addNewSubModuleMapping()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return status;
    }

    //To fetch roles of login partner as per id.
    public String getRoleofPartner(String partnerid)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String v_roll="";
        try
        {

            con = Database.getRDBConnection();
            String qry = "select roles from user u , partners p where u.login=p.login and p.partnerId=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            logger.debug("partner query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                v_roll = v_roll+","+rs.getString("roles");
            }
        }
        catch (Exception e)
        {
            logger.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return v_roll;
    }
    public boolean isMappingRemoved(String id)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        String query=null;
        int result;
        try{
            conn = Database.getConnection();
            query = "DELETE FROM `partner_users_modules_mapping` WHERE userid="+id;
            pstmt= conn.prepareStatement(query);
            result=pstmt.executeUpdate();
            if(result>0){
                status=true;
            }
        }
        catch (SystemError se){
            logger.error("SystemError:::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "isMappingRemoved()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException:::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "isMappingRemoved()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return status;
    }

    //Removing mapping of modules for partnerid
    public boolean isPartnerMappingRemoved(String id)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        String query=null;
        int result;
        try{
            conn = Database.getConnection();
            query = "DELETE FROM `partner_users_modules_mapping` WHERE partnerid="+id;
            pstmt= conn.prepareStatement(query);
            result=pstmt.executeUpdate();
            if(result>0){
                status=true;
            }
        }
        catch (SystemError se){
            logger.error("SystemError:::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "isMappingRemoved()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException:::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "isMappingRemoved()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return status;
    }


    public boolean isPartnerMappingRemovedNew(String id)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        String query=null;
        int result;
        try{
            conn = Database.getConnection();
            query = "DELETE FROM `partner_users_modules_mapping` WHERE partnerid="+id+" AND userid='0'";
            pstmt= conn.prepareStatement(query);
            result=pstmt.executeUpdate();
            if(result>0){
                status=true;
            }
        }
        catch (SystemError se){
            logger.error("SystemError:::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "isMappingRemoved()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException:::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "isMappingRemoved()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return status;
    }


    public boolean isSubMappingRemoved(String id)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        String query=null;
        int result;
        try{
            conn = Database.getConnection();
            query = "DELETE from partner_users_submodules_mapping where userid="+id;
            pstmt= conn.prepareStatement(query);
            result =pstmt.executeUpdate();
            if(result>0){
                status=true;
            }
        }
        catch (SystemError se){
            logger.error("SystemError:::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "isSubMappingRemoved()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException:::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "isSubMappingRemoved()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return status;
    }

    //
    public boolean isPartnersSubMappingRemoved(String id)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        String query=null;
        int result;
        try{
            conn = Database.getConnection();
            query = "DELETE from partner_users_submodules_mapping where partnerid="+id;
            pstmt= conn.prepareStatement(query);
            result =pstmt.executeUpdate();
            if(result>0){
                status=true;
            }
        }
        catch (SystemError se){
            logger.error("SystemError:::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "isSubMappingRemoved()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException:::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "isSubMappingRemoved()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return status;
    }

    public boolean isPartnersSubMappingRemovedNew(String id)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        String query=null;
        int result;
        try{
            conn = Database.getConnection();
            query = "DELETE from partner_users_submodules_mapping where partnerid="+id+" AND userid='0'";
            pstmt= conn.prepareStatement(query);
            result =pstmt.executeUpdate();
            if(result>0){
                status=true;
            }
        }
        catch (SystemError se){
            logger.error("SystemError:::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "isSubMappingRemoved()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException:::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "isSubMappingRemoved()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return status;
    }



    public boolean removePartnerModuleMapping(String mappingId)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        String query=null;
        int k;
        try{
            conn = Database.getConnection();
            query = "delete from partner_users_modules_mapping where mappingid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1, mappingId);
            k=pstmt.executeUpdate();
            if(k>0){
                status=true;
            }
        }
        catch (SystemError se){
            logger.error("SystemError:::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "removePartnerModuleMapping()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException:::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "removePartnerModuleMapping()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return status;
    }
    public HashMap<String, List> partnerModulesList()throws PZDBViolationException
    {
        LinkedHashMap<String,List> subModules=new LinkedHashMap<>();
        List<PartnerSubModuleVO> subModuleList=null;
        PartnerSubModuleVO partnerSubModuleVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        ResultSet resultSet1=null;
        String query=null;
        String query1=null;
        try{
            conn = Database.getConnection();
            query = "select * from partner_modules_master ";
            pstmt= conn.prepareStatement(query);
            resultSet=pstmt.executeQuery();
            while(resultSet.next()){
                subModuleList= new ArrayList();

                String moduleId=resultSet.getString("moduleid");
                String moduleName=resultSet.getString("moduleid")+"-"+resultSet.getString("modulename");

                query1 = "select * from partner_submodules_master where master_module_id=?";
                pstmt= conn.prepareStatement(query1);
                pstmt.setString(1,moduleId);
                resultSet1=pstmt.executeQuery();
                while(resultSet1.next()){
                    partnerSubModuleVO=new PartnerSubModuleVO();

                    partnerSubModuleVO.setSubModuleId(resultSet1.getString("id"));
                    partnerSubModuleVO.setSubModuleName(resultSet1.getString("sub_module_name"));

                    subModuleList.add(partnerSubModuleVO);
                }
                subModules.put(moduleName,subModuleList);
            }
        }
        catch (SystemError se){
            logger.error("SystemError:::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "partnerModulesList()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException:::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "partnerModulesList()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return subModules;
    }

    //Module list as per partner access of user
    public HashMap<String, List> partnerUserModulesList(String partnerid)throws PZDBViolationException
    {
        LinkedHashMap<String,List> subModules=new LinkedHashMap<>();
        List<PartnerSubModuleVO> subModuleList=null;
        PartnerSubModuleVO partnerSubModuleVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        ResultSet resultSet1=null;
        String query=null;
        String query1=null;

        try{
            conn = Database.getConnection();
            query = "select pm.moduleid AS moduleid , pm.modulename AS modulename from partner_modules_master pm join partner_users_modules_mapping pum ON pm.moduleid =pum.moduleid WHERE pum.userid='0' AND pum.partnerid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,partnerid);
            resultSet=pstmt.executeQuery();
            while(resultSet.next()){
                subModuleList= new ArrayList();

                String moduleId=resultSet.getString("moduleid");
                String moduleName=resultSet.getString("moduleid")+"-"+resultSet.getString("modulename");

                query1 = "select pm.id AS id ,pm.sub_module_name AS sub_module_name from partner_submodules_master pm JOIN partner_users_submodules_mapping psm ON pm.id = psm.submoduleid WHERE psm.userid='0' AND psm.partnerid=? AND pm.master_module_id=?";
                pstmt= conn.prepareStatement(query1);
                pstmt.setString(1,partnerid);
                pstmt.setString(2,moduleId);
                resultSet1=pstmt.executeQuery();
                while(resultSet1.next()){
                    partnerSubModuleVO=new PartnerSubModuleVO();
                    partnerSubModuleVO.setSubModuleId(resultSet1.getString("id"));
                    partnerSubModuleVO.setSubModuleName(resultSet1.getString("sub_module_name"));

                    subModuleList.add(partnerSubModuleVO);
                }
                subModules.put(moduleName,subModuleList);
            }
        }
        catch (SystemError se){
            logger.error("SystemError:::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "partnerModulesList()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException:::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "partnerModulesList()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return subModules;
    }

    //Module list as per partner access of user
    public HashMap<String, List> partnerUserAssignModulesList(String userID)throws PZDBViolationException
    {
        LinkedHashMap<String,List> subModules=new LinkedHashMap<>();
        List<PartnerSubModuleVO> subModuleList=null;
        PartnerSubModuleVO partnerSubModuleVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        ResultSet resultSet1=null;
        String query=null;
        String query1=null;

        try{
            conn = Database.getConnection();
            query = "select pm.moduleid AS moduleid , pm.modulename AS modulename from partner_modules_master pm join partner_users_modules_mapping pum ON pm.moduleid =pum.moduleid WHERE pum.userid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,userID);
            resultSet=pstmt.executeQuery();
            while(resultSet.next()){
                subModuleList= new ArrayList();

                String moduleId=resultSet.getString("moduleid");
                String moduleName=resultSet.getString("moduleid")+"-"+resultSet.getString("modulename");

                query1 = "select pm.id AS id ,pm.sub_module_name AS sub_module_name from partner_submodules_master pm JOIN partner_users_submodules_mapping psm ON pm.id = psm.submoduleid WHERE psm.userid=? AND pm.master_module_id=?";
                pstmt= conn.prepareStatement(query1);
                pstmt.setString(1,userID);
                pstmt.setString(2,moduleId);
                resultSet1=pstmt.executeQuery();
                while(resultSet1.next()){
                    partnerSubModuleVO=new PartnerSubModuleVO();
                    partnerSubModuleVO.setSubModuleId(resultSet1.getString("id"));
                    partnerSubModuleVO.setSubModuleName(resultSet1.getString("sub_module_name"));

                    subModuleList.add(partnerSubModuleVO);
                }
                subModules.put(moduleName,subModuleList);
            }
        }
        catch (SystemError se){
            logger.error("SystemError:::::"+se);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "partnerModulesList()", null, "common", "SystemError:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            logger.error("SQLException:::::"+e);
            PZExceptionHandler.raiseDBViolationException("PartnerModuleDAO.java", "partnerModulesList()", null, "common", "SQLException:::::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return subModules;
    }

    public boolean isPartneridExistforModules(String partnerid) throws SystemError
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String status = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String selquery = "SELECT 'X' FROM partner_users_modules_mapping  WHERE userid='0' AND partnerid = ? ";

            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, partnerid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = rs.getString(1);
                if(status.equals("X")){
                    return  true;
                }
            }

        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMemberUser method: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    public String  getSuperAdminId(String partnerId)
    {
        String superAdminId=null;
        Connection conn=null;
        try
        {
            //connection = Database.getConnection();
            conn = Database.getConnection();
            String query3 = "select superadminid from partners where partnerId=?";
            PreparedStatement preparedStatement=conn.prepareStatement(query3);
            preparedStatement.setString(1,partnerId);
            ResultSet rs=preparedStatement.executeQuery();
            if(rs.next()){
                superAdminId=rs.getString("superadminid");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLEXCEPTION", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return superAdminId;
    }

    public String  getRefundFlag(String partnerId)
    {
        String isRefund=null;
        Connection conn=null;
        try
        {
            //connection = Database.getConnection();
            conn = Database.getConnection();
            String query3 = "select isRefund from partners where partnerId=?";
            PreparedStatement preparedStatement=conn.prepareStatement(query3);
            preparedStatement.setString(1,partnerId);
            ResultSet rs=preparedStatement.executeQuery();
            if(rs.next()){
                isRefund=rs.getString("isRefund");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLEXCEPTION", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isRefund;
    }

    public String  getEMIconfigFlag(String partnerId)
    {
        String emi_configuration=null;
        Connection conn=null;
        try
        {
            //connection = Database.getConnection();
            conn = Database.getConnection();
            String query3 = "select emi_configuration from partners where partnerId=?";
            PreparedStatement preparedStatement=conn.prepareStatement(query3);
            preparedStatement.setString(1,partnerId);
            ResultSet rs=preparedStatement.executeQuery();
            if(rs.next()){
                emi_configuration=rs.getString("emi_configuration");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLEXCEPTION", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return emi_configuration;
    }

}
