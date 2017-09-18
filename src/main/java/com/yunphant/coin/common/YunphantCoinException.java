package com.yunphant.coin.common;

/**
 * The type Yunphant coin exception.
 */
public class YunphantCoinException extends RuntimeException {

    private static final long serialVersionUID = 976909549781056480L;

    /**
     * Gets serial version uid.
     *
     * @return the serial version uid
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    private String errorCode;

    /**
     * Instantiates a new Yunphant coin exception.
     *
     * @param errorMessage the error message
     */
    public YunphantCoinException (String errorMessage){
        super(errorMessage);
    }

    /**
     * Instantiates a new Yunphant coin exception.
     *
     * @param cause the cause
     */
    public YunphantCoinException (Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Yunphant coin exception.
     *
     * @param errorCode the error code
     * @param message   the message
     */
    public YunphantCoinException (String errorCode , String message) {
        super(message);
        this.setErrorCode(errorCode);
    }

    /**
     * Instantiates a new Yunphant coin exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public YunphantCoinException (String message , Throwable cause) {
        super(message,cause);
    }

    /**
     * Gets error code.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets error code.
     *
     * @param errorCode the error code
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
