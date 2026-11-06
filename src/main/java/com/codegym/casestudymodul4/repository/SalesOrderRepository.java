package com.codegym.casestudymodul4.repository;

import com.codegym.casestudymodul4.entity.SalesOrder;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
  @EntityGraph(attributePaths = "items")
  List<SalesOrder> findAllByOrderByCreatedAtDesc();

  @EntityGraph(attributePaths = "items")
  List<SalesOrder> findByUsernameOrderByCreatedAtDesc(String username);

  @Query(
      "select salesOrder.createdAt as createdAt, salesOrder.totalAmount as totalAmount, "
          + "salesOrder.status as status "
          + "from SalesOrder salesOrder "
          + "where salesOrder.createdAt >= :start and salesOrder.createdAt < :end")
  List<SalesOrderReportRow> findReportRows(
      @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
