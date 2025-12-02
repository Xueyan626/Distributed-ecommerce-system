package storeApplication.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import storeApplication.demo.dto.AddItemToWarehouseRequest;
import storeApplication.demo.model.WarehouseStock;
import storeApplication.demo.service.WarehouseStockService;

@RestController
@RequestMapping("/api/warehouse-stocks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WarehouseStockController {

    private final WarehouseStockService warehouseStockService;

    @PostMapping
    public ResponseEntity<WarehouseStock> addItemToWarehouse(@RequestBody AddItemToWarehouseRequest request) {
        try {
            WarehouseStock stock = warehouseStockService.addItemToWarehouse(request);
            return ResponseEntity.ok(stock);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
