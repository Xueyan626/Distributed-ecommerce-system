package storeApplication.demo.repository;

import storeApplication.demo.model.OrderAllocation;
import storeApplication.demo.model.OrderAllocationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderAllocationRepository extends JpaRepository<OrderAllocation, OrderAllocationId> {
    List<OrderAllocation> findByOrderId(Integer orderId);
    
    @Modifying
    @Query("DELETE FROM OrderAllocation oa WHERE oa.id.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") Integer orderId);
}


