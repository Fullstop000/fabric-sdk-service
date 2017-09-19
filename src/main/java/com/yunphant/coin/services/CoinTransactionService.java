package com.yunphant.coin.services;

import com.yunphant.coin.common.YunphantCoinException;
import org.hyperledger.fabric.sdk.Channel;

/**
 * The interface Coin transaction service.
 */
public interface CoinTransactionService {

    /**
     * Query transaction info string.
     *
     * @param queryStr the query str
     * @param channel  the channel
     * @return the string
     * @throws YunphantCoinException the yunphant coin exception
     */
    String queryTransactionInfo(String queryStr, Channel channel) throws YunphantCoinException;

    /**
     * Create transaction.
     *
     * @param jsonStr the json format transaction
     * @param channel the channel
     * @throws YunphantCoinException the yunphant coin exception
     */
    void createTransaction(String jsonStr, Channel channel) throws YunphantCoinException;

    /**
     * Create an account for user
     *
     * @param jsonStr the json format paylaod
     * @param channel channel
     * @throws YunphantCoinException the yunphant coin exception
     */
    void createAccount (String jsonStr, Channel channel) throws YunphantCoinException;

    /**
     * Query balance string.
     *
     * @param userID  the user id
     * @param channel the channel
     * @return the string
     * @throws YunphantCoinException the yunphant coin exception
     */
    String queryBalance (String userID , Channel channel) throws YunphantCoinException;
}
