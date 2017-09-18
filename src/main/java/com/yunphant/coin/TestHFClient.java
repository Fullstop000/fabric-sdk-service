package com.yunphant.coin;

import com.yunphant.coin.sample.SampleStore;
import com.yunphant.coin.sample.SampleUser;
import com.yunphant.coin.utils.SDKUtils;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import static com.yunphant.coin.common.CommonUtils.logError;

public class TestHFClient {

    private static TestHFClient instance;

    private static final Logger logger = LoggerFactory.getLogger(TestHFClient.class);
    private final File tmpFile;
    private final HFClient client;

    public TestHFClient(File tmpFile, HFClient client) {
        this.tmpFile = tmpFile;
        this.client = client;
    }

    /**
     *  get tmpFile
     */
    public File getTmpFile() {
        return tmpFile;
    }

    /**
     * Get client
     */
    public HFClient getClient() {
        return client;
    }

    public synchronized static TestHFClient getInstance() {

        if (instance != null) {
            return instance;
        }
        // temp file
        File tempFile = null;
        try {
            tempFile = File.createTempFile("teststore", "properties");
        } catch (IOException e) {
            logError(logger,"Fail to create tmp file for client",e);
        }
        tempFile.deleteOnExit();

        File sampleStoreFile = new File(System.getProperty("user.dir") + "/client.properties");
        if (sampleStoreFile.exists()) { //For testing start fresh
            sampleStoreFile.delete();
        }
        final SampleStore sampleStore = new SampleStore(sampleStoreFile);
        SampleUser org1AdminUser = null;
        try {
            org1AdminUser = sampleStore.getMember("org1Admin", "Org1", "Org1MSP",
                    SDKUtils.findFileSk("src/main/resources/crypto-config/peerOrganizations/org1.myexample.com/users/Admin@org1.myexample.com/msp/keystore"),
                    new File("src/main/resources/crypto-config/peerOrganizations/org1.myexample.com/users/Admin@org1.myexample.com/msp/signcerts/Admin@org1.myexample.com-cert.pem"));
        } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            logError(logger,"Fail to get member form sample store",e);
        }

        HFClient client = HFClient.createNewInstance();
        try {
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        } catch (CryptoException | InvalidArgumentException e) {
            logError(logger,"Error setting crypto suite of client",e);
        }

        try {
            if (org1AdminUser != null) {
                client.setUserContext(org1AdminUser);
            }
        } catch (InvalidArgumentException e) {
            logError(logger,"Fail to set user context of client",e);
        }

        return new TestHFClient(tempFile, client);
    }



    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (tmpFile != null) {
            try {
                tmpFile.delete();
            } catch (Exception e) {
                // // now harm done.
            }
        }
    }
}
