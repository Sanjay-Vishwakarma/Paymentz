package com.manager.vo.payoutVOs;

import com.manager.vo.RollingReserveDateVO;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/2/14
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReserveRefundVO  extends ChargeDetailsVO
{

    RollingReserveDateVO rollingReserveDateVO;

    public RollingReserveDateVO getRollingReserveDateVO()
    {
        return rollingReserveDateVO;
    }

    public void setRollingReserveDateVO(RollingReserveDateVO rollingReserveDateVO)
    {
        this.rollingReserveDateVO = rollingReserveDateVO;
    }
}
