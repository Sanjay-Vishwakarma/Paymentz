package com.manager.vo.payoutVOs;

import com.manager.vo.PaginationVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/12/14
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class WireReportsVO
{
  List<WireVO> wireReportsList = new ArrayList<WireVO>();

  PaginationVO paginationVO;


  //setter and getter
    public List<WireVO> getWireReportsList()
    {
        return wireReportsList;
    }

    public void setWireReportsList(List<WireVO> wireReportsList)
    {
        this.wireReportsList = wireReportsList;
    }

    public PaginationVO getPaginationVO()
    {
        return paginationVO;
    }

    public void setPaginationVO(PaginationVO paginationVO)
    {
        this.paginationVO = paginationVO;
    }
}
