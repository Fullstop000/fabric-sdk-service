package com.yunphant.coin.common.configbeans;

/**
 * The type Orderer config.
 */
public class OrdererConfig extends BasePeerConfig {
    private static final long serialVersionUID = -6521142584324405113L;
    private String dirPrefix;
    private String domain;
    private String mspdir;

    /**
     * Gets dir prefix.
     *
     * @return the dir prefix
     */
    public String getDirPrefix() {
        return dirPrefix;
    }

    /**
     * Sets dir prefix.
     *
     * @param dirPrefix the dir prefix
     */
    public void setDirPrefix(String dirPrefix) {
        this.dirPrefix = dirPrefix;
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

    /**
     * Gets msp dir.
     *
     * @return the msp dir
     */
    public String getMspdir() {
        return mspdir;
    }

    /**
     * Sets msp dir.
     *
     * @param mspdir the msp dir
     */
    public void setMspdir(String mspdir) {
        this.mspdir = mspdir;
    }
}
