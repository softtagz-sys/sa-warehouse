package kdg.be.warehouse.controller.dto.mapper;

import kdg.be.warehouse.controller.dto.CustomerDTO;
import kdg.be.warehouse.controller.dto.OrderLineDTO;
import kdg.be.warehouse.controller.dto.PurchaseOrderDTO;
import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.purchaseorder.OrderLine;
import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PurchaseOrderMapper {
    PurchaseOrderMapper INSTANCE = Mappers.getMapper(PurchaseOrderMapper.class);

    PurchaseOrder toEntity(PurchaseOrderDTO purchaseOrderDTO);
    Customer toEntity(CustomerDTO customerDTO);
    OrderLine toEntity(OrderLineDTO orderLineDTO);
}