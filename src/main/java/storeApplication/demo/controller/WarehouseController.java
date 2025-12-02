package storeApplication.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import storeApplication.demo.dto.CreateWarehouseRequest;
import storeApplication.demo.model.Warehouse;
import storeApplication.demo.service.WarehouseService;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<List<Warehouse>> getAllWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseService.getAllWarehouses();
            return ResponseEntity.ok(warehouses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Warehouse> createWarehouse(@RequestBody CreateWarehouseRequest request) {
        try {
            Warehouse warehouse = warehouseService.createWarehouse(request);
            return ResponseEntity.ok(warehouse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
