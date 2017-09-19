package com.yunphant.coin;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.yunphant.coin.common.CommonUtils.logError;

public class YunphantHFClient {

    private static YunphantHFClient instance;

    private static final Logger logger = LoggerFactory.getLogger(YunphantHFClient.class);
    private final HFClient client;

    public YunphantHFClient(HFClient client) {
//        this.tmpFile = tmpFile;
        this.client = client;
    }

    public void setUserContext(User userContext) {
        try {
            if (userContext != null) {
                client.setUserContext(userContext);
            }
        } catch (InvalidArgumentException e) {
            logError(logger,"Fail to set user context of client",e);
        }
    }
    /**
     * Get client
     */
    public HFClient getClient() {
        return client;
    }

    public synchronized static YunphantHFClient getInstance() {

        if (instance != null) {
            return instance;
        }

        HFClient client = HFClient.createNewInstance();
        try {
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        } catch (CryptoException | InvalidArgumentException e) {
            logError(logger,"Error setting crypto suite of client",e);
        }

        return new YunphantHFClient(client);
    }


}
