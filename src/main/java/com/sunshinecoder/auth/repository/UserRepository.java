package com.sunshinecoder.auth.repository;

import com.sunshinecoder.auth.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    // Find user by email
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    //Find user by email or phone number
    public boolean isEmailOrPhoneRegistered(String email, String phoneNumber) {
        return count(
                "email = :email or phoneNumber = :phone",
                Parameters.with("email", email).and("phone", phoneNumber)
        ) > 0;
    }
}
