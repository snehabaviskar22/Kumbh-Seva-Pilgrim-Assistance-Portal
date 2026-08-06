package com.kumbhseva.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kumbhseva.demo.entity.LostItem;

public interface LostItemRepository extends JpaRepository<LostItem, Long> {
}