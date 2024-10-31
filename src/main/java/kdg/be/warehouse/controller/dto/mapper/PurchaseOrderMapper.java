package kdg.be.warehouse.controller.dto.mapper;

import kdg.be.warehouse.controller.dto.CustomerDTO;
import kdg.be.warehouse.controller.dto.OrderLineDTO;
import kdg.be.warehouse.controller.dto.PurchaseOrderDTO;
import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.purchaseorder.OrderLine;
import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {

    @Mapping(source = "buyer", target = "customerParty")
    @Mapping(source = "seller", target = "sellerParty")
    @Mapping(source = "referenceUUID", target = "referenceUUID", qualifiedByName = "uuidToString")
    PurchaseOrderDTO purchaseOrderDTO(PurchaseOrder purchaseOrder);

    @Mapping(source = "customerParty", target = "buyer")
    @Mapping(source = "sellerParty", target = "seller")
    @Mapping(source = "referenceUUID", target = "referenceUUID", qualifiedByName = "stringToUUID")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", ignore = true)
    @Mapping(target = "completedDate", ignore = true)
    PurchaseOrder purchaseOrder(PurchaseOrderDTO purchaseOrderDTO);
    
    
    OrderLineDTO orderLineDTO(OrderLine orderLine);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "purchaseOrder", ignore = true)
    OrderLine orderLine(OrderLineDTO orderLineDTO);
    
    
    @Mapping(source = "customerId", target = "UUID", qualifiedByName = "uuidToString")
    CustomerDTO customerDTO(Customer customer);

    @Mapping(source = "UUID", target = "customerId", qualifiedByName = "stringToUUID")
    Customer customer(CustomerDTO customerDTO);
    
    
    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    @Named("stringToUUID")
    default UUID stringToUUID(String uuid) {
        return uuid != null ? UUID.fromString(uuid) : null;
    }
    
}
