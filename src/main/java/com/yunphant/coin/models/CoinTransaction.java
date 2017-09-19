package com.yunphant.coin.models;

import com.jsoniter.output.JsonStream;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;


/**
 * The type Coin transaction.
 */
public class CoinTransaction implements Serializable{

    private static final long serialVersionUID = 8156282684875921996L;

    private int sender;

    private int receiver;

    private long amount;

    private String type;

    private long timestamp;

    /**
     * Gets sender.
     *
     * @return the sender
     */
    public int getSender() {
        return sender;
    }

    /**
     * Sets sender.
     *
     * @param sender the sender
     */
    public void setSender(int sender) {
        this.sender = sender;
    }

    /**
     * Gets receiver.
     *
     * @return the receiver
     */
    public int getReceiver() {
        return receiver;
    }

    /**
     * Sets receiver.
     *
     * @param receiver the receiver
     */
    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets amount.
     *
     * @param amount the amount
     */
    public void setAmount(long amount) {
        this.amount = amount;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets timestamp.
     *
     * @param timestamp the timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * To json str string.
     *
     * @return the string
     */
    public String toJsonStr() {
        return JsonStream.serialize(this);
    }

    public static CoinTransaction genRandom(){
        CoinTransaction transaction = new CoinTransaction();
        transaction.setSender(RandomUtils.nextInt());
        transaction.setReceiver(RandomUtils.nextInt());
        transaction.setAmount(RandomUtils.nextLong());
        transaction.setTimestamp(System.currentTimeMillis());
        transaction.setType("transfer");
        return transaction;
    }
}
