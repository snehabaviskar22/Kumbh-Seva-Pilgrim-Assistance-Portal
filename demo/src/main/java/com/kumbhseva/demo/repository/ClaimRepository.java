package com.kumbhseva.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kumbhseva.demo.entity.Claim;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    long countByStatus(String status);

}

