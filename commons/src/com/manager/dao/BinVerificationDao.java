package com.manager.dao;

import com.directi.pg.Logger;
import com.directi.pg.*;
import com.manager.vo.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Jitendra on 05-Oct-18.
 */

public class BinVerificationDao
{
    Logger logger=new Logger(BinVerificationDao.class.getName());
    Functions functions = new Functions();
    public BinResponseVO getBinDetailsFromFirstSix(String bin) throws PZDBViolationException
    {
        Connection conn = null;
        BinResponseVO binResponseVO=new BinResponseVO();
        try
        {
            conn=Database.getBinConnection();
            String query = "select bin,brand,bank,type,level,isocountry,isoa2,isoa3,isonumber,www,phone from binbase where bin=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1,bin);
            logger.error("getting BinDetail---"+p);
            ResultSet resultSet = p.executeQuery();
            if(resultSet.next())
            {
                binResponseVO.setFirstsix(resultSet.getString("bin"));
                binResponseVO.setBrand(resultSet.getString("brand"));
                binResponseVO.setBank(resultSet.getString("bank"));
                binResponseVO.setCardcategory(resultSet.getString("level"));
                binResponseVO.setCountryname(resultSet.getString("isocountry"));
                binResponseVO.setCountrycodeA2(resultSet.getString("isoa2"));
                binResponseVO.setCountrycodeA3(resultSet.getString("isoa3"));
                binResponseVO.setCountryisonumber(resultSet.getString("isonumber"));
                binResponseVO.setBankwebsite(resultSet.getString("www"));
                binResponseVO.setBankphoneno(resultSet.getString("phone"));
                binResponseVO.setCardtype(resultSet.getString("type"));
            }
        }
        catch (SystemError se){
            PZExceptionHandler.raiseDBViolationException("BinVerificationDao.java", "getBinDetailsFromFirstSix()", null, "Common", "Error while fetching bin base details", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e){
            PZExceptionHandler.raiseDBViolationException("BinVerificationDao.java", "getBinDetailsFromFirstSix()", null, "Common", "Error while fetching bin base details", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally{
            Database.closeConnection(conn);
        }
        return binResponseVO;
    }
}
