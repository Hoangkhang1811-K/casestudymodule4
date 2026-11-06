package com.codegym.casestudymodul4.repository;

import com.codegym.casestudymodul4.entity.CustomerAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Long> {
  Optional<CustomerAccount> findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
