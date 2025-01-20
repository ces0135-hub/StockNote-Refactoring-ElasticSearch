package org.com.stocknote.domain.user.repository;

import org.com.stocknote.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
