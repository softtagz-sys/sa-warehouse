package kdg.be.warehouse.controller.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CustomerDTO {
    private String UUID;
    private String name;
    private String address;

    public CustomerDTO(String UUID, String name, String address) {
        this.UUID = UUID;
        this.name = name;
        this.address = address;
    }
}