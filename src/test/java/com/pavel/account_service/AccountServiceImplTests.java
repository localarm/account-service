package com.pavel.account_service;

import static org.mockito.Mockito.*;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.pavel.account_service.dao.AccountDao;
import com.pavel.account_service.services.AccountAccessException;
import com.pavel.account_service.services.cacheSettings.AccountIMapLoader;
import com.pavel.account_service.services.AccountServiceImpl;
import com.pavel.account_service.services.StatisticServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;

import java.sql.SQLException;

public class AccountServiceImplTests {

    private static AccountServiceImpl accountService;
    private static HazelcastInstance instance;
    private static IMap<Integer, Long> cache;
    private static AccountDao accountDaoMock;
    private static StatisticServiceImpl statisticServiceImplMock;

    @BeforeAll
    public static void setupForAllTests() {
        accountDaoMock = Mockito.mock(AccountDao.class);
        statisticServiceImplMock = Mockito.mock(StatisticServiceImpl.class);
        Config config = new Config();
        setupHazelcastConfig(config);
        instance = Hazelcast.newHazelcastInstance(config);

    }

    private static void setupHazelcastConfig(Config config) {
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

        MapConfig mapCfg = new MapConfig();
        mapCfg.setName("testMap");
        mapCfg.setBackupCount(0);
        mapCfg.setTimeToLiveSeconds(0);
        mapCfg.setMaxIdleSeconds(0);

        EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setEvictionPolicy(EvictionPolicy.LRU);
        evictionConfig.setMaxSizePolicy(MaxSizePolicy.PER_NODE);
        evictionConfig.setSize(300);
        mapCfg.setEvictionConfig(evictionConfig);
        MapStoreConfig mapStoreCfg = new MapStoreConfig();
        mapStoreCfg.setImplementation(new AccountIMapLoader(accountDaoMock));
        mapStoreCfg.setEnabled(true);
        mapCfg.setMapStoreConfig(mapStoreCfg);

        config.setProperty( "hazelcast.logging.type", "none");
        config.addMapConfig(mapCfg);
    }

    @BeforeEach
    public void initAccountServiceWithCacheAndMocks() {
        cache = instance.getMap("testMap");
        accountService = new AccountServiceImpl(cache, accountDaoMock, statisticServiceImplMock);
    }

    @AfterEach
    public void destroyCache() {
        Mockito.reset(accountDaoMock, statisticServiceImplMock);
        cache.destroy();
    }

    @Test
    public void catchAccountAccessException_whenSetAmount() {
        when(accountDaoMock.findBalance(1)).thenReturn(1L);
        doThrow(new DataAccessResourceFailureException(""))
                .when(accountDaoMock)
                .updateBalance(1, 1L);
        try {
            accountService.addAmount(1, 1L);
        } catch (AccountAccessException ignore) {
        }
    }

    @Test
    public void catchAccountAccessException_whenGetAmount() {
        when(accountDaoMock.findBalance(2))
                .thenThrow(new DataAccessResourceFailureException(""));
        try {
            accountService.getAmount(2);
        } catch (AccountAccessException ignore) {
        }
    }

    @Test
    public void invokeInsertIntoDB_whenSetAmount_andCacheMiss() throws AccountAccessException {
        when(accountDaoMock.findBalance(3)).thenThrow(new EmptyResultDataAccessException(1));
        accountService.addAmount(3, 5L);
        verify(accountDaoMock, times(1)).insertBalance(3, 5L);
        verify(accountDaoMock, times(1)).findBalance(3);
    }

    @Test
    public void invokeUpdateDB_whenSetAmount_andCacheHit() throws AccountAccessException {
        cache.put(6, 6L);
        accountService.addAmount(4, 12L);
        verify(accountDaoMock, times(1)).updateBalance(4, 12L);
    }

    @Test
    public void getAmount_whenIdNotInCache() throws AccountAccessException {
        when(accountDaoMock.findBalance(5)).thenThrow(new EmptyResultDataAccessException(1));
        Long actual = accountService.getAmount(5);
        Assertions.assertEquals(0L, actual);
    }

    @Test
    public void getAmount_returnAmount() throws AccountAccessException {
        when(accountDaoMock.findBalance(6)).thenReturn(16L);
        Long actual = accountService.getAmount(6);
        Assertions.assertEquals(16L, actual);
    }

    @Test
    public void insertedAmount_dontInvokeDaoAtGetAmount() throws AccountAccessException {
        when(accountDaoMock.findBalance(7)).thenReturn(null);
        accountService.addAmount(7, 10L);
        Long actual = accountService.getAmount(7);
        Assertions.assertEquals(10L, actual);
        verify(accountDaoMock, times(1)).findBalance(7);
    }

    @Test
    public void getAmount_invokeStatisticIncrement() throws AccountAccessException {
        when(accountDaoMock.findBalance(8)).thenReturn(1L);
        accountService.getAmount(8);
        verify(statisticServiceImplMock, times(1)).incrementTotalCountOfGetAmount();
    }

    @Test
    public void setAmount_invokeStatisticIncrement() throws AccountAccessException {
        when(accountDaoMock.findBalance(9)).thenReturn(11L);
        accountService.addAmount(9, 11L);
        verify(statisticServiceImplMock, times(1)).incrementTotalCountOfAddAmount();
    }

    @Test
    public void setAmount_onExistId() throws AccountAccessException {
        when(accountDaoMock.findBalance(10)).thenReturn(11L);
        accountService.addAmount(10, 12L);
        verify(accountDaoMock, times(1)).updateBalance(10, 23L);
    }

    @Test
    public void testBadSqlGrammarExceptionThrow(){
        when(accountDaoMock.findBalance(20))
                .thenThrow(new BadSqlGrammarException("test", "test", new SQLException()));
        try {
            accountService.getAmount(20);
        } catch (AccountAccessException ex) {
            Assertions.assertTrue(ex.getCause().getCause() instanceof BadSqlGrammarException);
        }
    }
}
