package kdg.be.warehouse.service;

import kdg.be.warehouse.config.WarehouseConfig;
import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.warehouse.Warehouse;
import kdg.be.warehouse.domain.warehouse.WarehouseTransaction;
import kdg.be.warehouse.repository.WarehouseRepository;
import kdg.be.warehouse.repository.WarehouseTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WarehouseService {


    private final WarehouseTransactionRepository warehouseTransactionRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseConfig warehouseConfig;

    public WarehouseService(WarehouseTransactionRepository warehouseTransactionRepository, WarehouseRepository warehouseRepository, WarehouseConfig warehouseConfig) {
        this.warehouseTransactionRepository = warehouseTransactionRepository;
        this.warehouseRepository = warehouseRepository;
        this.warehouseConfig = warehouseConfig;
    }

    public void retrieveMaterial(Warehouse warehouse, Material material, float amount) {
        List<WarehouseTransaction> transactions = warehouseTransactionRepository.findByWarehouseAndMaterialOrderByTransactionTimeAsc(warehouse, material);
        float amountToRetrieve = amount;

        for (WarehouseTransaction transaction : transactions) {
            if (transaction.getRemainingAmount() >= amountToRetrieve) {
                transaction.setRemainingAmount(transaction.getRemainingAmount() - amountToRetrieve);
                warehouseTransactionRepository.save(transaction);
                break;
            } else {
                amountToRetrieve -= transaction.getRemainingAmount();
                transaction.setRemainingAmount(0);
                warehouseTransactionRepository.save(transaction);
            }
        }
        warehouse.setCurrentCapacity(warehouse.getCurrentCapacity() - amount);
    }

    public boolean hasWarehouseOfMaterial(Customer customer, Material material) {
        return warehouseRepository.findByOwnerAndMaterial(customer, material).isPresent();
    }

    // TODO Refactor this method --> no .get() without check
    public Warehouse getWarehouseOfMaterialFromCustomer(Customer customer, Material material) {
        return warehouseRepository.findByOwnerAndMaterial(customer, material).get();
    }

    public Optional<Boolean> getAvailabilityStatus(UUID customerId, String materialName) {
        Optional<Warehouse> warehouseOptional = warehouseRepository.findByOwner_customerIdAndMaterial_nameIgnoreCase(customerId, materialName);
        if (warehouseOptional.isPresent()) {
            Warehouse warehouse = warehouseOptional.get();
            return Optional.of(warehouse.getCurrentCapacity() / warehouse.getMaxCapacity() < warehouseConfig.getMaxCapacityRatioForNewDeliveries());
        }
        return Optional.empty();
    }

}
