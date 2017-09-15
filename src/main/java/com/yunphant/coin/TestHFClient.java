package com.yunphant.coin;

import com.yunphant.coin.sample.SampleStore;
import com.yunphant.coin.sample.SampleUser;
import com.yunphant.coin.utils.SDKUtils;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;


public class TestHFClient {

    private static TestHFClient instance;

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

    public synchronized static TestHFClient getInstance() throws Exception {

        if (instance != null) {
            return instance;
        }
        // temp file
        File tempFile = File.createTempFile("teststore", "properties");
        tempFile.deleteOnExit();

        File sampleStoreFile = new File(System.getProperty("user.dir") + "/test.properties");
        if (sampleStoreFile.exists()) { //For testing start fresh
            sampleStoreFile.delete();
        }
        final SampleStore sampleStore = new SampleStore(sampleStoreFile);
        SampleUser org1AdminUser = sampleStore.getMember("org1Admin", "Org1", "Org1MSP",
                SDKUtils.findFileSk("src/main/resources/crypto-config/peerOrganizations/org1.myexample.com/users/Admin@org1.myexample.com/msp/keystore"),
                new File("src/main/resources/crypto-config/peerOrganizations/org1.myexample.com/users/Admin@org1.myexample.com/msp/signcerts/Admin@org1.myexample.com-cert.pem"));
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(org1AdminUser);
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
