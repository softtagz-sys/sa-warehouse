package kdg.be.warehouse.service;

import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.warehouse.Warehouse;
import kdg.be.warehouse.domain.warehouse.WarehouseTransaction;
import kdg.be.warehouse.repository.WarehouseTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseTransactionRepository warehouseTransactionRepository;

    public void deliverMaterial(Warehouse warehouse, Material material, float quantity, Customer customer) {
        if (warehouse.getAmountInStorage() + quantity > warehouse.getCapacity()) {
            throw new RuntimeException("Warehouse is at full capacity");
        }
        WarehouseTransaction transaction = new WarehouseTransaction(material, quantity, warehouse, customer);
        warehouseTransactionRepository.save(transaction);
        warehouse.setAmountInStorage(warehouse.getAmountInStorage() + quantity);
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
        warehouse.setAmountInStorage(warehouse.getAmountInStorage() - amount);
    }

    public boolean hasWarehouseOfMaterial(Customer customer, Material material) {
    }

    public Warehouse getWarehouseOfMaterialFromCustomer(Customer customer, Material material) {

    }
}
