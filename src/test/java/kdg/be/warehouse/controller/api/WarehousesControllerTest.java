package kdg.be.warehouse.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WarehousesControllerTest {

    @Autowired MockMvc mockMvc;

    @Test
    public void fetchingStatusOfAnExistingWarehouseShouldReturnTrue() throws Exception {
        mockMvc.perform(
                    get("/api/warehouses/9ae35800-5fdf-4932-a713-251f49e11012/Gips")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void fetchingStatusOfAnExistingWarehouseShouldReturnFalse() throws Exception {
        mockMvc.perform(
                        get("/api/warehouses/9ae35800-5fdf-4932-a713-251f49e11012/Ijzererts")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.available").value(false));

    }

    @Test
    public void fetchingStatusOfAnExistingWarehouseWithWrongCaseMaterialShouldReturnTrue() throws Exception {
        mockMvc.perform(
                        get("/api/warehouses/9ae35800-5fdf-4932-a713-251f49e11012/gips")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.available").value(true));

    }

    @Test
    public void fetchingStatusOfAnExistingWarehouseWithWrongMaterialShouldReturnNotFound() throws Exception {
        mockMvc.perform(
                        get("/api/warehouses/9ae35800-5fdf-4932-a713-251f49e11012/cocacola")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }



}