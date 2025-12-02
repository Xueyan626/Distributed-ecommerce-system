package storeApplication.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "warehouse_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStock {
    @EmbeddedId
    private WarehouseStockId id;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Item item;
    
    @ManyToOne
    @JoinColumn(name = "warehouse_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Warehouse warehouse;
}
