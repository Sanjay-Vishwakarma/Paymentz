package com.payment.payon.core.message;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 10:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class Validation
{
    private String instruction;
    private String rejectionPolicy;

    public void setInstruction(String instruction)
    {
        this.instruction = instruction;
    }

    public void setRejectionPolicy(String rejectionPolicy)
    {
        this.rejectionPolicy = rejectionPolicy;
    }
}
