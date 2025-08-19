package com.sunshinecoder.auth.repository;

import com.sunshinecoder.auth.entity.TokenBlacklist;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenBlacklistRepository implements PanacheRepository<TokenBlacklist> {

    public boolean isBlacklisted(String token) {
        return find("token", token).firstResultOptional().isPresent();
    }

    public void blacklistToken(String token) {
        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.setToken(token);
        persist(tokenBlacklist);
    }
}
