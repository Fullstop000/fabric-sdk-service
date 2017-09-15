package com.yunphant.coin.utils;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ChainCodeUtils {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ChainCodeUtils.class);

    /**
     *
     * demoCCInstallProposal
     *
     * @param client
     * @return InstallProposalRequest
     * @throws ProposalException
     * @throws InvalidArgumentException
     * @throws IOException
     */
    public static InstallProposalRequest demoCCInstallProposal(HFClient client,ChaincodeID chaincodeID) throws ProposalException, InvalidArgumentException, IOException {
        InstallProposalRequest request = client.newInstallProposalRequest();
        request.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);
        request.setChaincodeID(chaincodeID);
        request.setChaincodeSourceLocation(new File("src/main/resources"));
        return request;
    }

    public static InstantiateProposalRequest demoCCInstantiateProposal(HFClient client,ChaincodeID chaincodeID) throws InvalidArgumentException, IOException {
        InstantiateProposalRequest request = client.newInstantiationProposalRequest();
        request.setChaincodeID(chaincodeID);
        request.setFcn("init");
        request.setProposalWaitTime(30000);
        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
        request.setTransientMap(tm);
        ChaincodeEndorsementPolicy policy = new ChaincodeEndorsementPolicy();
        policy.fromStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ccEndoresementPolicy.yaml"));
        request.setChaincodeEndorsementPolicy(policy);
        return request;
    }
    /**
     * installChainCode
     *
     * @param client
     * @param request
     * @param targetPeers
     * @return
     */
    public static Collection<ProposalResponse> installChainCode(HFClient client, InstallProposalRequest request, List<Peer> targetPeers) {
        try {
            return client.sendInstallProposal(request,targetPeers);
        } catch (ProposalException | InvalidArgumentException e) {
            LOGGER.error("Fail to install ChainCode",e);
        }
        return null;
    }

    public static void instantiateChainCode() {

    }
}
