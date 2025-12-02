package storeApplication.demo.repository;

import storeApplication.demo.model.WarehouseStock;
import storeApplication.demo.model.WarehouseStockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, WarehouseStockId> {
    List<WarehouseStock> findByItemIdOrderByQuantityDesc(Integer itemId);
    Optional<WarehouseStock> findByItemIdAndWarehouseId(Integer itemId, Integer warehouseId);
}
