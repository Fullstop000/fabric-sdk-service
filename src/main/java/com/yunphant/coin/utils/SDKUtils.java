package com.yunphant.coin.utils;

import com.yunphant.coin.sample.SampleOrg;
import com.yunphant.coin.sample.SampleStore;
import com.yunphant.coin.sample.SampleUser;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.helper.Utils;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.yunphant.coin.common.CommonUtils.logError;
import static java.lang.String.format;

public class SDKUtils {
    private static final Logger logger = LoggerFactory.getLogger(SDKUtils.class);

    public static InputStream generateTarGzInputStream(File src, String pathPrefix) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream(500000);

        String sourcePath = src.getAbsolutePath();

        TarArchiveOutputStream archiveOutputStream = new TarArchiveOutputStream(new GzipCompressorOutputStream(new BufferedOutputStream(bos)));
        archiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

        try {
            Collection<File> childrenFiles = org.apache.commons.io.FileUtils.listFiles(src, null, true);

            ArchiveEntry archiveEntry;
            FileInputStream fileInputStream;
            for (File childFile : childrenFiles) {
                String childPath = childFile.getAbsolutePath();
                String relativePath = childPath.substring((sourcePath.length() + 1), childPath.length());

                if (pathPrefix != null) {
                    relativePath = Utils.combinePaths(pathPrefix, relativePath);
                }

                relativePath = FilenameUtils.separatorsToUnix(relativePath);

                archiveEntry = new TarArchiveEntry(childFile, relativePath);
                fileInputStream = new FileInputStream(childFile);
                archiveOutputStream.putArchiveEntry(archiveEntry);

                try {
                    IOUtils.copy(fileInputStream, archiveOutputStream);
                } finally {
                    IOUtils.closeQuietly(fileInputStream);
                    archiveOutputStream.closeArchiveEntry();
                }
            }
        } finally {
            IOUtils.closeQuietly(archiveOutputStream);
        }

        return new ByteArrayInputStream(bos.toByteArray());
    }

    // TODO: 2017/9/12 read config from config.yaml
    public static SampleOrg getSampleOrg(HFClient client) {
        // read from config.yaml
        boolean tlsEnabled = false;
        String mspId = "Org1MSP";
        String orgName = "Org1";
        SampleOrg org = new SampleOrg(orgName, mspId);

        org.setCAName("ca-org1"); // ca name
        org.setCALocation("http://0.0.0.0:7054"); // the ca server url

        if (tlsEnabled) {
            // TODO: 2017/9/12 add ca tls properties
//            Properties properties = new Properties();
//            properties.setProperty("")
//            org.setCAProperties();
            org.setCALocation(org.getCALocation().replaceAll("^http://", "https://"));
        }
        HFCAClient hfcaClient = null;
        try {
            hfcaClient = HFCAClient.createNewInstance(org.getCAName(), org.getCALocation(), org.getCAProperties());
            hfcaClient.setCryptoSuite(client.getCryptoSuite());
        } catch (MalformedURLException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
            logError(logger,"Error create ca client",e);
        }
        org.setCAClient(hfcaClient);
        org.setDomainName("org1.myexample.com");

        /*
         * Peer config
         * add peer instances or  peer configs ( name and grpc location)
         */
        try {
            Arrays.asList(
                    client.newPeer("peer0.org1.myexample.com", "grpc://localhost:7051"),
                    client.newPeer("peer1.org1.myexample.com", "grpc://localhost:8051")
            ).forEach(org::addPeer);
        } catch (InvalidArgumentException e) {
            logError(logger,"Error creating peer objects",e);
        }

        /*
         * Orderer config
         */
        org.addOrdererLocation("orderer0", "grpc://localhost:7050");
        /*
         * Event hub config
         */
        org.addEventHubLocation("peer0.org1.myexample.com", "grpc://localhost:7053");
        org.addEventHubLocation("peer1.org1.myexmaple.com", "grpc://localhost:8053");

        // temp file
        File tempFile = null;
        try {
            tempFile = File.createTempFile("teststore", "properties");
        } catch (IOException e) {
            logError(logger,"Fail to create tmp file teststore.properties");
        }
        tempFile.deleteOnExit();

        File sampleStoreFile = new File(System.getProperty("user.dir") + "/" + orgName + ".properties");
//        if (sampleStoreFile.exists()) { //For testing start fresh
//            boolean isDeleted = sampleStoreFile.delete();
//            if (!isDeleted) {
//                logger.error("Fail to delete sample store file");
//            }
//        }
        final SampleStore sampleStore = new SampleStore(sampleStoreFile);
        SampleUser admin = sampleStore.getMember("admin", orgName);
        if (!admin.isEnrolled()) {  //Preregistered admin only needs to be enrolled with Fabric caClient.
            try {
                if (hfcaClient != null) {
                    admin.setEnrollment(hfcaClient.enroll(admin.getName(), "adminpw"));
                }
            } catch (EnrollmentException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
                logError(logger,"Error enrolling admin user of org "+orgName,e);
            }
            admin.setMspId(mspId);
        }
        org.setAdmin(admin);

        /*
         *  Peer admin use to create channels, join peers and install chaincode
         */
        SampleUser peerAdmin = null;
        try {
            peerAdmin = sampleStore.getMember("org1Admin", orgName, mspId,
                    findFileSk("src/main/resources/crypto-config/peerOrganizations/org1.myexample.com/users/Admin@org1.myexample.com/msp/keystore"),
                    new File("src/main/resources/crypto-config/peerOrganizations/org1.myexample.com/users/Admin@org1.myexample.com/msp/signcerts/Admin@org1.myexample.com-cert.pem"));
        } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            logError(logger,"Error get member org1Admin from sample store",e);
        }
        org.setPeerAdmin(peerAdmin);

        SampleUser user = sampleStore.getMember("User2", orgName);
        if (!user.isRegistered()) {  // users need to be registered AND enrolled
            RegistrationRequest rr = null;
            try {
                rr = new RegistrationRequest(user.getName(), "org1.department1");
            } catch (Exception e) {
                logError(logger,"Error");
            }
            try {
                if (hfcaClient != null && rr != null) {
                    user.setEnrollmentSecret(hfcaClient.register(rr, admin));
                }
            } catch (RegistrationException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
                e.printStackTrace();
            }
            user.saveState();
        }
        if (!user.isEnrolled()) {
            try {
                if (hfcaClient != null) {
                    user.setEnrollment(hfcaClient.enroll(user.getName(), user.getEnrollmentSecret()));
                }
            } catch (EnrollmentException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
                e.printStackTrace();
            }
            user.setMspId(mspId);
        }
        org.addUser(user); //Remember user belongs to this Org
        return org;
    }

    public static Channel constructChannel(boolean existent ,String channelName, HFClient client, SampleOrg org) throws TransactionException, InvalidArgumentException, ProposalException, IOException {
        client.setUserContext(org.getPeerAdmin());

        Collection<Orderer> orderers = new LinkedList<>();
        for (String orderName : org.getOrdererNames()) {
            Properties ordererProperties = new Properties();
            ordererProperties.put("ordererWaitTimeMilliSecs","10000");
            //example of setting keepAlive to avoid timeouts on inactive http2 connections.
            // Under 5 minutes would require changes to server side to accept faster ping rates.
            ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES});
            ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS});
            ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[] {true});

            orderers.add(client.newOrderer(orderName, org.getOrdererLocation(orderName),ordererProperties));
        }

        //Just pick the first orderer in the list to create the channel.

        Orderer anOrderer = orderers.iterator().next();
        orderers.remove(anOrderer);
        Channel newChannel;
        if (existent) {
            newChannel = client.newChannel(channelName);
        }else {
            ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File("src/main/resources/channel-artifacts/channel.tx"));
            newChannel = client.newChannel(channelName, anOrderer, channelConfiguration ,client.getChannelConfigurationSignature(channelConfiguration, org.getPeerAdmin()));
        }
//        client.getChannelConfigurationSignature(channelConfiguration, org.getAdmin())
        // Create channel that has only one signer that is this orgs peer admin.
        // If channel creation policy needed more signature they would need to be added too.
//
        newChannel.addOrderer(anOrderer);
        logger.info(format("Created channel %s", channelName));
        for (Orderer orderer : orderers) { //add remaining orderers if any.
            newChannel.addOrderer(orderer);
        }
        for (String eventHubName : org.getEventHubNames()) {
            // need tls enabled
            // final Properties eventHubProperties = testConfig.getEventHubProperties(eventHubName);
            Properties eventHubProperties = new Properties();
            eventHubProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES});
            eventHubProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS});
//            eventHubProperties.put()
            newChannel.addEventHub(
                    client.newEventHub(eventHubName, org.getEventHubLocation(eventHubName), eventHubProperties));
        }
        newChannel.setTransactionWaitTime(5);
        newChannel.setDeployWaitTime(5);
        newChannel.initialize();
        return newChannel;
    }

    public static Channel joinPeers(Collection<Peer> peers, Channel channel , HFClient client) throws InvalidArgumentException, ProposalException {
        for (Peer peer : peers) {
            if (hasPeerJointChannel(client,peer,channel.getName())) {
                channel.addPeer(peer);
            }else {
                channel.joinPeer(peer);
            }
        }
        return channel;
    }
    public static File findFileSk(String directories) {
        File directory = new File(directories);
        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));
        if (null == matches) {
            throw new RuntimeException(format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
        }
        if (matches.length != 1) {
            throw new RuntimeException(format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
        }
        return matches[0];
    }

    public static Properties getOrdererProperties(boolean tlsEnabled , String type ,  String domain) throws Exception {
        File crtFile = Paths.get("src/main/resources/crypto-config","ordererOrganizations", domain.substring(domain.indexOf(".")+1), type + "s",domain.substring(domain.indexOf(".")+1),"tls/server.crt").toFile();
        if (!crtFile.exists()) {
            throw new Exception("Fail to get crt file of "+type);
        }
        Properties properties = new Properties();
        if (tlsEnabled) {
            properties.setProperty("pemFile", crtFile.getAbsolutePath());
            //      ret.setProperty("trustServerCertificate", "true"); //testing environment only NOT FOR PRODUCTION!
//            ret.setProperty("hostnameOverride", name);
            properties.setProperty("sslProvider", "openSSL");
            properties.setProperty("negotiationType", "TLS");
            return properties;
        }
        return null;
    }

    public static boolean hasPeerJointChannel(HFClient client , Peer peer , String channelName) throws ProposalException, InvalidArgumentException {
        return client.queryChannels(peer).contains(channelName);
    }
}


