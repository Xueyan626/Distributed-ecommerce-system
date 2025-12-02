package storeApplication.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddItemToWarehouseRequest {
    private Integer itemId;
    private Integer warehouseId;
    private Integer quantity;
}
