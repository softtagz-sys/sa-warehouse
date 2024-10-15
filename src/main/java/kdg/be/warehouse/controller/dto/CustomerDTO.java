package kdg.be.warehouse.controller.dto;

import java.util.UUID;

public class CustomerDTO {
    private UUID UUID;
    private String name;
    private String address;

    public CustomerDTO(UUID UUID, String name, String address) {
        this.UUID = UUID;
        this.name = name;
        this.address = address;
    }
}