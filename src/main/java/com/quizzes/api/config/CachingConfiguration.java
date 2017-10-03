package com.quizzes.api.config;

import com.google.code.ssm.CacheFactory;
import com.google.code.ssm.aop.CacheBase;
import com.google.code.ssm.config.DefaultAddressProvider;
import com.google.code.ssm.providers.CacheConfiguration;
import com.google.code.ssm.providers.elasticache.ElastiCacheConfiguration;
import com.google.code.ssm.providers.elasticache.MemcacheClientFactoryImpl;
import com.google.code.ssm.spring.SSMCache;
import com.google.code.ssm.spring.SSMCacheManager;
import lombok.Getter;
import lombok.Setter;
import net.spy.memcached.ClientMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Configuration
@EnableCaching
@ImportResource("simplesm-context.xml")
@EnableAspectJAutoProxy
@ConfigurationProperties(value = "spring.cache")
public class CachingConfiguration {

    private String serverUrl = "localhost:11211";
    private int expiration = 86400;

    @Bean
    public CacheManager cacheManager() throws Exception {
        List<SSMCache> ssmCaches = new ArrayList<>();
        ssmCaches.add(new SSMCache(cacheFactory().getObject(), getExpiration(), true));
        SSMCacheManager ssmCacheManager = new SSMCacheManager();
        ssmCacheManager.setCaches(ssmCaches);
        return ssmCacheManager;
    }

    @Bean
    public CacheFactory cacheFactory() {
        System.setProperty(CacheBase.DISABLE_CACHE_PROPERTY, "false");
        CacheFactory cacheFactory = new CacheFactory();
        cacheFactory.setCacheClientFactory(new MemcacheClientFactoryImpl());
        cacheFactory.setAddressProvider(new DefaultAddressProvider(getServerUrl()));
        cacheFactory.setConfiguration(cacheConfiguration());
        return cacheFactory;
    }

    @Bean
    public CacheConfiguration cacheConfiguration() {
        final ElastiCacheConfiguration cacheConfiguration = new ElastiCacheConfiguration();
        cacheConfiguration.setConsistentHashing(true);
        cacheConfiguration.setClientMode(ClientMode.Static);
        return cacheConfiguration;
    }

}
