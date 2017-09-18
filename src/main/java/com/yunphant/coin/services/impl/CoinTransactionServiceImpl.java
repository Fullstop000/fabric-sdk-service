package com.yunphant.coin.services.impl;

import com.yunphant.coin.TestHFClient;
import com.yunphant.coin.common.YunphantCoinException;
import com.yunphant.coin.services.CoinTransactionService;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The type Coin transaction service.
 */
public class CoinTransactionServiceImpl implements CoinTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(CoinTransactionServiceImpl.class);
    private static final long PROPOSAL_WAIT_TIME = 20000L;
    private HFClient client;

    /**
     * Query transaction info string.
     *
     * @param queryStr the query str
     * @param channel  the channel
     * @return the string
     * @throws YunphantCoinException the yunphant coin exception
     */
    @Override
    public String queryTransactionInfo(String queryStr,Channel channel) throws YunphantCoinException {
        try {
            client = TestHFClient.getInstance().getClient();
        } catch (Exception e) {
            String error = "Error creating client instance";
            logger.error(error,e);
            throw new YunphantCoinException(error,e);
        }
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("yunphantCoinCC").setPath("yunphant/coin").setVersion("0.1").build();
        TransactionProposalRequest request = client.newTransactionProposalRequest();
        request.setChaincodeID(ccId);
        request.setFcn("query");
        request.setArgs(new String[]{queryStr});
        request.setProposalWaitTime(PROPOSAL_WAIT_TIME);
        Map<String, byte[]> map = new HashMap<>();
        map.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); //Just some extra junk in transient map
        map.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
        try {
            request.setTransientMap(map);
        } catch (InvalidArgumentException e) {
            String error = "Invalid argument exception when setting transient map";
            logger.error(error);
            throw new YunphantCoinException(error,e);
        }

        List<ProposalResponse> successfulResponses = new ArrayList<>();
        List<ProposalResponse> failureResponses = new ArrayList<>();
        try {
            channel.sendTransactionProposal(request,Collections.singleton(channel.getPeers().iterator().next())).forEach(response -> {
                if (response.getStatus() == ProposalResponse.Status.SUCCESS){
                    successfulResponses.add(response);
                }else {
                    failureResponses.add(response);
                }
            });
        } catch (ProposalException | InvalidArgumentException e) {
            String error = "Fail to send transaction proposal ";
            logger.error(error,e);
            throw new YunphantCoinException(error,e);
        }
        failureResponseProcess(failureResponses,"query");
        return successfulResponses.get(0).getProposalResponse().getResponse().getPayload().toStringUtf8();
    }

    /**
     * Create transaction.
     *
     * @param jsonStr     the json format transaction
     * @param channel     the channel
     * @throws YunphantCoinException the yunphant coin exception
     */
    @Override
    public void createTransaction(String  jsonStr,Channel channel) throws YunphantCoinException {
        try {
            client = TestHFClient.getInstance().getClient();
        } catch (Exception e) {
            String error = "Error creating client instance";
            logger.error(error,e);
            throw new YunphantCoinException(error,e);
        }
        ChaincodeID ccId = ChaincodeID.newBuilder().setName("yunphantCoinCC").setPath("yunphant/coin").setVersion("0.1").build();
        TransactionProposalRequest request = client.newTransactionProposalRequest();
        request.setChaincodeID(ccId);
        request.setFcn("addCoinTransaction");
        request.setArgs(new String[]{jsonStr});
        request.setProposalWaitTime(PROPOSAL_WAIT_TIME);
        Map<String, byte[]> map = new HashMap<>();
        map.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); //Just some extra junk in transient map
        map.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
        try {
            request.setTransientMap(map);
        } catch (InvalidArgumentException e) {
            String error = "Invalid argument exception when setting transient map";
            logger.error(error);
            throw new YunphantCoinException(error,e);
        }
        Collection<ProposalResponse> responses;
        try {
            responses = channel.sendTransactionProposal(request,channel.getPeers());
        } catch (ProposalException | InvalidArgumentException e) {
            String error = "Fail to send transaction proposal ";
            logger.error(error,e);
            throw new YunphantCoinException(error,e);
        }
        List<ProposalResponse> successfulResponse = new ArrayList<>();
        List<ProposalResponse> failureResponse = new ArrayList<>();
        responses.forEach(response -> {
            if (response.getStatus() != ProposalResponse.Status.SUCCESS){
                failureResponse.add(response);
            }else {
                successfulResponse.add(response);
            }
        });

        failureResponseProcess(failureResponse,"addCoinTransaction");

        channel.sendTransaction(successfulResponse).thenApply(transactionEvent -> {
            try {
                if (transactionEvent.isValid()){
                    TransactionInfo txInfo = channel.queryTransactionByID(transactionEvent.getTransactionID());
                    if (StringUtils.isNotBlank(txInfo.getTransactionID())){
                        logger.info("Success commit transaction to ledger");
                    }
                }else{
                    String error = "Fail ot commit transaction : invalid transaction event";
                    logger.error(error);
                    throw new YunphantCoinException("Fail ot commit transaction : invalid transaction event");
                }

            } catch (ProposalException | InvalidArgumentException e) {
                String error = "Fail to commit transaction by causing error";
                logger.error(error,e);
                throw new YunphantCoinException(e);
            }
            return null;
        });

    }

    private void failureResponseProcess(List<ProposalResponse> responses , String func) {
        if (responses.size()>0){
            responses.forEach(response -> logger.error("Endorsement error :" + response.getMessage() + "; Was verified : "+response.isVerified()));
            String error = "Not enough endorsers for invoke Chaincode func '"+ func +"' :" + responses.size();
            logger.error(error);
            throw new YunphantCoinException(error);
        }
    }
}
