package com.code.supportportal.service.login;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {
    public static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    public static final int ATTEMPT_INCREMENT = 1;
    public LoadingCache<String, Integer> attemptCache;

    public LoginAttemptService() {
        attemptCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(15, MINUTES)
                .build(
                        new CacheLoader<String, Integer>() {
                            @Override
                            public Integer load(String username) throws Exception {
                                return 0;
                            }
                        }
                );
    } // end method

    public void deleteUserFromCache(String username) {
        attemptCache.invalidate(username);
    } // end method

    public void addUserToCache(String username) {
        int attempts = 0;
        try {
            attempts = attemptCache.get(username) + ATTEMPT_INCREMENT;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        attemptCache.put(username, attempts);
    }

    public boolean hasExceedMaxAttempts(String username)  {
        try {
            return attemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

} // end service
