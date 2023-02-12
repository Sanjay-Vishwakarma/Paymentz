package com.manager;


import com.directi.pg.Logger;

/**
 * Created by NIKET on 23-06-2016.
 */
public class AsynchApplicationManager
{
    //private Session session;
   /* public void loadAllMembersDynamicMafApplication()
    {
        Thread thread = new Thread(new ApplicationLoadCallable());
        thread.start();
    }*/


    public class ApplicationLoadCallable implements Runnable
    {
        private Logger logger = new Logger(ApplicationLoadCallable.class.getName());


        public void run()
        {
            ApplicationManager.loadPropertiesFileForMandatoryAndOptionalValidation();
            /*try
            {
                ApplicationManager.loadAllMerchantBankMapping();
            }
            catch (PZDBViolationException e)
            {
                logger.error("PZDBViolationException while loadMerchantBankMapping ",e);
                PZExceptionHandler.handleDBCVEException(e,"","load Merchant BankMapping for Appliaction");
            }*/
        }
    }


}
