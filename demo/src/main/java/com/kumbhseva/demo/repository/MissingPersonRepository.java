package com.kumbhseva.demo.repository;

import com.kumbhseva.demo.entity.MissingPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissingPersonRepository extends JpaRepository<MissingPerson, Long> {
    List<MissingPerson> findByStatus(String status);
    Optional<MissingPerson> findByLostItemId(Long lostItemId);
    List<MissingPerson> findByUserId(Long userId);
    long countByStatus(String status);
}
