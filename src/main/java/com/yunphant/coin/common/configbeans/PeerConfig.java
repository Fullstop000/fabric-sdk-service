package com.yunphant.coin.common.configbeans;

/**
 * The type Peer config.
 */
public class PeerConfig extends BasePeerConfig {

    private static final long serialVersionUID = 682332533971928543L;

    private String domain;

    /**
     * Gets domain.
     *
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets domain.
     *
     * @param domain the domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getEventHubAddress() {
        return this.getHost() + ":" +this.getPort().substring(0,this.getPort().length()-1)+"3";
    }
}
