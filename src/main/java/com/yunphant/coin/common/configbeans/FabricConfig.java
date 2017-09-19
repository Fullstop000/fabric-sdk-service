package com.yunphant.coin.common.configbeans;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * The type Fabric config.
 */
public class FabricConfig {
    /**
     * The type Fabric config.
     */
    private static final long serialVersionUID = 2425641123495817910L;
    private OrdererConfig ordererConfig;
    private String channelName;
    private String artifactsDir;
    private String orgDirPrefix;
    private OrganizationConfig[] organizations;
    private boolean tlsEnabled;

    /**
     * Gets orderer config.
     *
     * @return the orderer config
     */
    public OrdererConfig getOrdererConfig() {
        return ordererConfig;
    }

    /**
     * Sets orderer config.
     *
     * @param ordererConfig the orderer config
     */
    public void setOrdererConfig(OrdererConfig ordererConfig) {
        this.ordererConfig = ordererConfig;
    }

    /**
     * Gets org dir prefix.
     *
     * @return the org dir prefix
     */
    public String getOrgDirPrefix() {
        return orgDirPrefix;
    }

    /**
     * Sets org dir prefix.
     *
     * @param orgDirPrefix the org dir prefix
     */
    public void setOrgDirPrefix(String orgDirPrefix) {
        this.orgDirPrefix = orgDirPrefix;
    }

    /**
     * Get organizations organization config [ ].
     *
     * @return the organization config [ ]
     */
    public OrganizationConfig[] getOrganizations() {
        return organizations;
    }

    /**
     * Sets organizations.
     *
     * @param organizations the organizations
     */
    public void setOrganizations(OrganizationConfig[] organizations) {
        this.organizations = organizations;
    }

    /**
     * Is tls enabled boolean.
     *
     * @return the boolean
     */
    public boolean isTlsEnabled() {
        return tlsEnabled;
    }

    /**
     * Sets tls enabled.
     *
     * @param tlsEnabled the tls enabled
     */
    public void setTlsEnabled(boolean tlsEnabled) {
        this.tlsEnabled = tlsEnabled;
    }

    /**
     * Gets artifacts dir.
     *
     * @return the artifacts dir
     */
    public String getArtifactsDir() {
        return artifactsDir;
    }

    /**
     * Sets artifacts dir.
     *
     * @param artifactsDir the artifacts dir
     */
    public void setArtifactsDir(String artifactsDir) {
        this.artifactsDir = artifactsDir;
    }

    /**
     * Gets channel name.
     *
     * @return the channel name
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * Sets channel name.
     *
     * @param channelName the channel name
     */
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
