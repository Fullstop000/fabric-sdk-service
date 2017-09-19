package com.yunphant.coin.utils;

import com.yunphant.coin.common.FabricConfigHelper;
import com.yunphant.coin.common.configbeans.*;
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
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
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

/**
 * The type Sdk utils.
 */
public class SDKUtils {
    private static final String GRPC_PREFIX = "grpc://";
    private static final Logger logger = LoggerFactory.getLogger(SDKUtils.class);
    private static final FabricConfigHelper CONFIG_HELPER = FabricConfigHelper.getInstance();
    /**
     * Generate tar gz input stream input stream.
     *
     * @param src        the src
     * @param pathPrefix the path prefix
     * @return the input stream
     * @throws IOException the io exception
     */
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

    /**
     * Gets orgs from config.
     *
     * @param client the client
     * @return the orgs from config
     */
    public static Map<String,SampleOrg> getOrgsFromConfig(HFClient client) {
        Map<String,SampleOrg> result = new HashMap<>();
        // Should be use as Consortium and now only support solo
        FabricConfig helperConfig = CONFIG_HELPER.getConfig();
        OrdererConfig ordererConfig = helperConfig.getOrdererConfig();
        String cryptoDirPrefix = helperConfig.getOrgDirPrefix();
        for (OrganizationConfig config : helperConfig.getOrganizations()) {
            String orgName = config.getName();
            String mspid = config.getMspid();
            SampleOrg org = new SampleOrg(orgName, mspid);
            // ca config
            BasePeerConfig ca = config.getCa();
            org.setCAName(ca.getName());
            org.setCALocation("http://"+ ca.getAddress());
            boolean tlsEnabled = helperConfig.isTlsEnabled();
            if (tlsEnabled){
                // TODO: 2017/9/19 add tls support
                org.setCALocation(org.getCALocation().replaceAll("^http://", "https://"));
            }
            // build ca client
            HFCAClient hfcaClient = null;
            try {
                hfcaClient = HFCAClient.createNewInstance(org.getCAName(), org.getCALocation(), org.getCAProperties());
                hfcaClient.setCryptoSuite(client.getCryptoSuite());
            } catch (MalformedURLException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
                logError(logger,"Error create ca client",e);
            }
            org.setCAClient(hfcaClient);
            org.setDomainName(config.getDomain());

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
                    logError(logger,"Error enrolling admin user of org "+ orgName,e);
                }
                admin.setMspId(mspid);
            }
            org.setAdmin(admin);

            /*
         *  Peer admin use to create channels, join peers and install chaincode
         */
            SampleUser peerAdmin = null;
            try {
                peerAdmin = sampleStore.getMember(orgName+"Admin", orgName, mspid,
                        findFileSk(config.getAdminPrivateKey(cryptoDirPrefix)),
                        new File(config.getAdminCert(cryptoDirPrefix)));
            } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
                logError(logger,"Error get member org0Admin from sample store",e);
            }
            org.setPeerAdmin(peerAdmin);
            try {
                if (peerAdmin != null) {
                    client.setUserContext(peerAdmin);
                }
            } catch (InvalidArgumentException e) {
                logError(logger,"Error setting user context",e);
            }
            /*
             * Peer config
             * add peer instances or  peer configs ( name and grpc location)
             */
            for (PeerConfig peerConfig : config.getPeers()) {
                try {
                    org.addPeer(client.newPeer(peerConfig.getDomain(), GRPC_PREFIX+peerConfig.getAddress()));
                    org.addEventHubLocation(peerConfig.getDomain(),GRPC_PREFIX+peerConfig.getEventHubAddress());
                } catch (InvalidArgumentException e) {
                    logError(logger,"Error creating peer objects",e);
                }
            }
            /*
             * Orderer config
             * Here just one config
             */
            org.addOrdererLocation(ordererConfig.getName(),GRPC_PREFIX+ordererConfig.getAddress());
            result.put(orgName,org);
//            SampleUser user = sampleStore.getMember("User2", orgName);
//            if (!user.isRegistered()) {  // users need to be registered AND enrolled
//                RegistrationRequest rr = null;
//                try {
//                    rr = new RegistrationRequest(user.getName(), "org1.department1");
//                } catch (Exception e) {
//                    logError(logger,"Error build register request ",e);
//                }
//                try {
//                    if (hfcaClient != null && rr != null) {
//                        user.setEnrollmentSecret(hfcaClient.register(rr, admin));
//                    }
//                } catch (RegistrationException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
//                    logError(logger,"Error setting enrollment secret",e);
//                }
//                user.saveState();
//            }
//            if (!user.isEnrolled()) {
//                try {
//                    if (hfcaClient != null) {
//                        user.setEnrollment(hfcaClient.enroll(user.getName(), user.getEnrollmentSecret()));
//                    }
//                } catch (EnrollmentException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
//                    logError(logger,"Error enrolling user",e);
//                }
//                user.setMspId(mspid);
//            }
//            org.addUser(user); //Remember user belongs to this Org
        }
        return result;
    }

    /**
     * Construct channel channel.
     *
     * @param existent    the existent
     * @param channelName the channel name
     * @param client      the client
     * @param org         the org
     * @return the channel
     */
    public static Channel constructChannel(boolean existent ,String channelName, HFClient client, SampleOrg org) {
        try {
            client.setUserContext(org.getPeerAdmin());
        } catch (InvalidArgumentException e) {
            logError(logger,"Error setting user context",e);
        }
        Collection<Orderer> orderers = new LinkedList<>();
        for (String orderName : org.getOrdererNames()) {
            Properties ordererProperties = new Properties();
            ordererProperties.put("ordererWaitTimeMilliSecs","10000");
            //example of setting keepAlive to avoid timeouts on inactive http2 connections.
            // Under 5 minutes would require changes to server side to accept faster ping rates.
            ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES});
            ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS});
            ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[] {true});

            try {
                orderers.add(client.newOrderer(orderName, org.getOrdererLocation(orderName),ordererProperties));
            } catch (InvalidArgumentException e) {
                logError(logger,"Error creating orderer",e);
            }
        }

        //Just pick the first orderer in the list to create the channel.

        Orderer anOrderer = orderers.iterator().next();
        orderers.remove(anOrderer);
        Channel newChannel = null;
        if (existent) {
            try {
                if (client.getChannel(channelName) != null){
                    newChannel = client.getChannel(channelName);
                }else {
                    newChannel = client.newChannel(channelName);
                }
            } catch (InvalidArgumentException e) {
                logError(logger,"Error creating a existent channel instance with channel name:"+channelName,e);
            }
        }else {
            ChannelConfiguration channelConfiguration = null;
            String path = null;
            try {
                 path = Paths.get(CONFIG_HELPER.getConfig().getArtifactsDir(),"channel.tx").toString();
                channelConfiguration = new ChannelConfiguration(new File(path));
            } catch (IOException e) {
                logError(logger,"Error reading channel configuration from path:"+path);
            }
            try {
                if (channelConfiguration != null) {
                    newChannel = client.newChannel(channelName, anOrderer, channelConfiguration ,client.getChannelConfigurationSignature(channelConfiguration, org.getPeerAdmin()));
                }
            } catch (TransactionException | InvalidArgumentException e) {
                logError(logger,"Error creating new channel instance",e);
            }
        }
        assert newChannel != null;
//        client.getChannelConfigurationSignature(channelConfiguration, org.getAdmin())
        // Create channel that has only one signer that is this orgs peer admin.
        // If channel creation policy needed more signature they would need to be added too.
//
        try {
            newChannel.addOrderer(anOrderer);
            for (Orderer orderer : orderers) { //add remaining orderers if any.
                newChannel.addOrderer(orderer);
            }
        } catch (InvalidArgumentException e) {
            logger.info(format("Created channel %s", channelName));
        }
        for (String eventHubName : org.getEventHubNames()) {
            // need tls enabled
            // final Properties eventHubProperties = testConfig.getEventHubProperties(eventHubName);
            Properties eventHubProperties = new Properties();
            eventHubProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES});
            eventHubProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS});
//            eventHubProperties.put()
            try {
                newChannel.addEventHub(client.newEventHub(eventHubName, org.getEventHubLocation(eventHubName), eventHubProperties));
            } catch (InvalidArgumentException e) {
                logError(logger,"Error creating event hub instance ", e);
            }
        }
        newChannel.setTransactionWaitTime(5);
        newChannel.setDeployWaitTime(5);
        try {
            newChannel.initialize();
        } catch (InvalidArgumentException | TransactionException e) {
            logError(logger,"Error initializing the channel instance ", e);
        }
        return newChannel;
    }

    /**
     * Join peers channel.
     *
     * @param peers   the peers
     * @param channel the channel
     * @param client  the client
     * @return the channel
     */
    public static Channel joinPeers(Collection<Peer> peers, Channel channel , HFClient client) {
        for (Peer peer : peers) {
            if (hasPeerJointChannel(client,peer,channel.getName())) {
                try {
                    channel.addPeer(peer);
                } catch (InvalidArgumentException e) {
                    logError(logger,"Error adding peer into channel",e);
                }
            }else {
                try {
                    channel.joinPeer(peer);
                } catch (ProposalException e) {
                    logError(logger,"Error joining peer into channel",e);
                }
            }
        }
        return channel;
    }

    /**
     * Join all peers channel.
     *
     * @param client  the client
     * @param orgMap  the org map
     * @return the channel
     */
    public static Channel joinAllPeers(String channelName , HFClient client , Map<String,SampleOrg> orgMap){
        Channel channel = null;
        for (Map.Entry<String, SampleOrg> orgEntry : orgMap.entrySet()) {
            SampleOrg org = orgEntry.getValue();
            channel = constructChannel(true,channelName,client,org);
            try {
                client.setUserContext(org.getPeerAdmin());
            } catch (InvalidArgumentException e) {
                logError(logger,"Error setting user context",e);
            }
            joinPeers(org.getPeers(),channel,client);
        }
        return channel;
    }

    /**
     * Find file sk file.
     *
     * @param directories the directories
     * @return the file
     */
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


    /**
     * Has peer joint channel boolean.
     *
     * @param client      the client
     * @param peer        the peer
     * @param channelName the channel name
     * @return the boolean
     */
    public static boolean hasPeerJointChannel(HFClient client , Peer peer , String channelName) {
        try {
            return client.queryChannels(peer).contains(channelName);
        } catch (InvalidArgumentException | ProposalException e) {
            logError(logger,"Error query channels info by peer",e);
        }
        return false;
    }
}


