package com.pavel.account_service.services.cacheSettings;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.map.IMap;
import com.pavel.account_service.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    private final AccountDao accountDao;
    private final int maxSizeBeforeEviction;

    @Autowired
    public HazelcastConfig(AccountDao accountDao, @Value("${eviction.size:100000}") int maxSizeBeforeEviction) {
        this.accountDao = accountDao;
        System.out.println(maxSizeBeforeEviction);
        this.maxSizeBeforeEviction = maxSizeBeforeEviction;
    }

    @Bean
    public IMap<Integer, Long> cache() {
        Config config = new Config();

        //disable multicast
        MulticastConfig multicastConfig = new MulticastConfig();
        multicastConfig.setEnabled(false);
        AutoDetectionConfig autoDetectionConfig = new AutoDetectionConfig();
        autoDetectionConfig.setEnabled(false);
        JoinConfig joinConfig = new JoinConfig();
        joinConfig.setMulticastConfig(multicastConfig);
        joinConfig.setAutoDetectionConfig(autoDetectionConfig);
        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setJoin(joinConfig);
        config.setNetworkConfig(networkConfig);

        //configure eviction policy
        MapConfig mapCfg = new MapConfig();
        mapCfg.setName("accountCache");
        mapCfg.setBackupCount(0);
        mapCfg.setTimeToLiveSeconds(0);
        EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setEvictionPolicy(EvictionPolicy.LRU);
        evictionConfig.setSize(maxSizeBeforeEviction);
        evictionConfig.setMaxSizePolicy(MaxSizePolicy.PER_NODE);
        mapCfg.setEvictionConfig(evictionConfig);

        //configure data store link
        MapStoreConfig mapStoreCfg = new MapStoreConfig();
        mapStoreCfg.setImplementation(new AccountIMapLoader(accountDao));
        mapStoreCfg.setEnabled(true);
        mapCfg.setMapStoreConfig(mapStoreCfg);
        config.addMapConfig(mapCfg);

        return Hazelcast.newHazelcastInstance(config).getMap("accountCache");
    }
}
