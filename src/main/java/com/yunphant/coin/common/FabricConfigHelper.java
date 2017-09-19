package com.yunphant.coin.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.yunphant.coin.common.configbeans.FabricConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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


