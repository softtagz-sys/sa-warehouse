package kdg.be.warehouse.service;


import kdg.be.warehouse.config.WarehouseConfig;
import kdg.be.warehouse.domain.warehouse.Warehouse;
import kdg.be.warehouse.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseConfig warehouseConfig;

    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseConfig warehouseConfig) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseConfig = warehouseConfig;
    }

    public boolean GetAvailabilityStatus(UUID customerId, String materialName) {
        Optional<Warehouse> warehouseOptional = warehouseRepository.findByOwner_customerIdAndMaterial_name(customerId, materialName);
        return warehouseOptional.filter(warehouse -> (warehouse.getCurrentCapacity() / warehouse.getMaxCapacity()) <= warehouseConfig.getMaxCapacityRatioForNewDeliveries()).isPresent();
    }
}
