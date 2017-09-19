package com.yunphant.coin.common.configbeans;

import java.io.Serializable;

/**
 * The type Organization config.
 */
public class OrganizationConfig implements Serializable {
    private static final long serialVersionUID = 4427794269492493827L;
    private String name;
    private String domain;
    private String mspid;
    private BasePeerConfig ca;
    private PeerConfig[] peers;

    /**
     * Gets msp id.
     *
     * @return the msp id
     */
    public String getMspid() {
        return mspid;
    }

    /**
     * Sets msp id.
     *
     * @param mspId the msp id
     */
    public void setMspid(String mspId) {
        this.mspid = mspId;
    }

    /**
     * Gets ca.
     *
     * @return the ca
     */
    public BasePeerConfig getCa() {
        return ca;
    }

    /**
     * Sets ca.
     *
     * @param ca the ca
     */
    public void setCa(BasePeerConfig ca) {
        this.ca = ca;
    }

    /**
     * Get peers peer config [ ].
     *
     * @return the peer config [ ]
     */
    public PeerConfig[] getPeers() {
        return peers;
    }

    /**
     * Sets peers.
     *
     * @param peers the peers
     */
    public void setPeers(PeerConfig[] peers) {
        this.peers = peers;
    }

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
}