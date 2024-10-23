package kdg.be.warehouse.service;

import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.warehouse.Warehouse;
import kdg.be.warehouse.domain.warehouse.WarehouseTransaction;
import kdg.be.warehouse.repository.CustomerRepository;
import kdg.be.warehouse.repository.MaterialRepository;
import kdg.be.warehouse.repository.WarehouseRepository;
import kdg.be.warehouse.repository.WarehouseTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class WarehouseTransactionService {

    private final CustomerRepository customerRepository;
    private final MaterialRepository materialRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseTransactionRepository warehouseTransactionRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseTransactionService.class);

    public WarehouseTransactionService(CustomerRepository customerRepository, MaterialRepository materialRepository, WarehouseRepository warehouseRepository, WarehouseTransactionRepository warehouseTransactionRepository) {
        this.customerRepository = customerRepository;
        this.materialRepository = materialRepository;
        this.warehouseRepository = warehouseRepository;
        this.warehouseTransactionRepository = warehouseTransactionRepository;
    }

    @Transactional
    public void addDelivery(UUID customerId, String materialName, float weight, LocalDateTime timestamp) {
        Optional<Customer> customerOptional =  customerRepository.findById(customerId);
        Optional<Material> materialOptional = materialRepository.findByNameIgnoreCase(materialName);

        if (customerOptional.isEmpty() || materialOptional.isEmpty()) {
            LOGGER.warn("ADD DELIVERY - No customer with id {} r material with name {} found", customerId, materialName);
        } else {
            Optional<Warehouse> warehouseOptional = warehouseRepository.findByOwnerAndMaterial(customerOptional.get(), materialOptional.get());
            if (warehouseOptional.isEmpty()) {
                LOGGER.error("ADD DELIVERY - Warehouse not found");
            } else {
                Warehouse warehouse = warehouseOptional.get();

                WarehouseTransaction warehouseTransaction = new WarehouseTransaction(
                        materialOptional.get(),
                        weight,
                        warehouse,
                        timestamp,
                        customerOptional.get()
                );

                warehouseTransactionRepository.save(warehouseTransaction);
                warehouse.setCurrentCapacity( calculateWarehouseCapacity(warehouseOptional.get()));
                warehouseRepository.save(warehouse);
            }
        }
    }

    private float calculateWarehouseCapacity(Warehouse warehouse) {

        return warehouseTransactionRepository.sumOfAmount(warehouse.getWarehouseId());


    }
}
