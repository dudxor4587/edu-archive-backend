package com.backend.user.domain.repository;

import com.backend.user.domain.SignupRequest;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SignupRequestRepository extends JpaRepository<SignupRequest, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM SignupRequest u WHERE u.userName = :userName")
    Optional<SignupRequest> findByUserNameWithLock(@Param("userName") String userName);
}
