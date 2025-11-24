package org.example.repository;

import org.example.model.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Integer> {

    /**
     * Find all unread notifications ordered by creation date (newest first)
     */
    List<ThongBao> findByDaDocFalseOrderByNgayTaoDesc();

    /**
     * Count unread notifications
     */
    long countByDaDocFalse();

    /**
     * Find top 10 most recent notifications
     */
    List<ThongBao> findTop10ByOrderByNgayTaoDesc();

    /**
     * Find unread notifications for a specific admin
     * If maNguoiNhan is null, it's a broadcast notification
     */
    List<ThongBao> findByDaDocFalseAndMaNguoiNhanIsNullOrderByNgayTaoDesc();
}
