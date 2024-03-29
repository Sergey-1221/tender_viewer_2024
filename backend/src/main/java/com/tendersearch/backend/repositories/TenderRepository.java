package com.tendersearch.backend.repositories;

import com.tendersearch.backend.models.Tender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TenderRepository extends JpaRepository<Tender, Long> {
    @Query(value = "SELECT * FROM tender WHERE v_key = :vKey", nativeQuery = true)
    public List<Tender> findByvKey(String vKey);
        @Query("SELECT t FROM Tender t WHERE " +
                "(:keywords IS NULL OR (t.name IS NOT NULL AND t.name LIKE %:keywords%) OR " +
                "(t.description IS NOT NULL AND t.description LIKE %:keywords%)) AND " +
                "(:createdAt IS NULL OR t.createdAt > :createdAt) AND " +
                "(:closedAt IS NULL OR t.closedAt < :closedAt) AND " +
                "(:customer IS NULL OR t.customer LIKE %:customer%)")
        Page<Tender> findByCriteria(@Param("keywords") String keywords,
                                    @Param("createdAt") LocalDateTime createdAt,
                                    @Param("closedAt") LocalDateTime closedAt,
                                    @Param("customer") String customer,
                                    Pageable pageable);
}
