package com.yunphant.coin.common.configbeans;

import java.io.Serializable;

/**
 * The type Base peer config.
 */
public class BasePeerConfig implements Serializable {
    private static final long serialVersionUID = 3874206634750020358L;
    private String name;
    private String host;
    private String port;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets host.
     *
     * @param host the host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets port.
     *
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * Sets port.
     *
     * @param port the port
     */
    public void setPort(String port) {
        this.port = port;
    }
}
