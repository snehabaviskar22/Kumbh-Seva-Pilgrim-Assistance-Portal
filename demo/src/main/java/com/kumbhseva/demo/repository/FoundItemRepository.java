package com.kumbhseva.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kumbhseva.demo.entity.FoundItem;

public interface FoundItemRepository extends JpaRepository<FoundItem, Long> {
}