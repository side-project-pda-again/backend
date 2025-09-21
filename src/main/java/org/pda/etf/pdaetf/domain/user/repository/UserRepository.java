package org.pda.etf.pdaetf.domain.user.repository;

import org.pda.etf.pdaetf.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
