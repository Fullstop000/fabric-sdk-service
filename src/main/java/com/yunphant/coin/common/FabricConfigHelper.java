package com.yunphant.coin.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.yunphant.coin.common.configbeans.OrdererConfig;
import com.yunphant.coin.common.configbeans.OrganizationConfig;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;

import static com.yunphant.coin.common.CommonUtils.logError;

/**
 * The type Fabric config helper.
 */
public class FabricConfigHelper {
    private static final Logger logger = LoggerFactory.getLogger(FabricConfigHelper.class);
    private FabricConfig config ;

    /**
     * Instantiates a new Fabric config helper.
     */
    FabricConfigHelper(){
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            this.config = mapper.readValue(Thread.currentThread().getContextClassLoader().getResource("config.yaml"),FabricConfig.class);
        } catch (IOException e) {
            logError(logger,"Error read config.yaml file ");
        }
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public FabricConfig getConfig() {
        return config;
    }

    /**
     * Sets config.
     *
     * @param config the config
     */
    public void setConfig(FabricConfig config) {
        this.config = config;
    }

    private static class FabricConfigHelpHolder {
        private static final FabricConfigHelper instance = new FabricConfigHelper();
    }

    /**
     * Get instance fabric config helper.
     *
     * @return the fabric config helper
     */
    public synchronized static FabricConfigHelper getInstance(){
        return FabricConfigHelpHolder.instance;
    }
}

/**
 * The type Fabric config.
 */
class FabricConfig implements Serializable{
    private static final long serialVersionUID = 2425641123495817910L;
    private OrdererConfig ordererConfig;
    private String orgDirPrefix;
    private OrganizationConfig[] organizations;

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

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
