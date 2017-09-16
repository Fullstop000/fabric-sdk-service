package com.yunphant.coin.models;

import com.jsoniter.output.JsonStream;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.bouncycastle.pqc.math.linearalgebra.RandUtils;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The type Coin transaction.
 */
public class CoinTransaction implements Serializable{

    private static final long serialVersionUID = 8156282684875921996L;

    private String sender;

    private String receiver;

    private double senderBalance;

    private double receiverBalance;

    private double amount;

    private String type;

    private long timestamp;

    /**
     * Gets sender.
     *
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Sets sender.
     *
     * @param sender the sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Gets receiver.
     *
     * @return the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Sets receiver.
     *
     * @param receiver the receiver
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * Gets sender balance.
     *
     * @return the sender balance
     */
    public double getSenderBalance() {
        return senderBalance;
    }

    /**
     * Sets sender balance.
     *
     * @param senderBalance the sender balance
     */
    public void setSenderBalance(double senderBalance) {
        this.senderBalance = senderBalance;
    }

    /**
     * Gets receiver balance.
     *
     * @return the receiver balance
     */
    public double getReceiverBalance() {
        return receiverBalance;
    }

    /**
     * Sets receiver balance.
     *
     * @param receiverBalance the receiver balance
     */
    public void setReceiverBalance(double receiverBalance) {
        this.receiverBalance = receiverBalance;
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
    public void setAmount(double amount) {
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
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                     .withinRange('a', 'z')
                     .build();
        transaction.setSender(generator.generate(10));
        transaction.setReceiver(generator.generate(10));
        transaction.setSenderBalance(RandomUtils.nextDouble(10.0,100.0));
        transaction.setReceiverBalance(RandomUtils.nextDouble(20.0,60.0));
        transaction.setAmount(RandomUtils.nextDouble(5.0,10.0));
        transaction.setTimestamp(System.currentTimeMillis());
        transaction.setType("transfer");
        return transaction;
    }
}
