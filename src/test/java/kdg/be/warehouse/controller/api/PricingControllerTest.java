package kdg.be.warehouse.controller.api;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class PricingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql(value = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void addingNewStorageCostShouldReturn201() throws Exception {
        mockMvc.perform(post("/api/pricing/gips/storageCost")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content("""
                                {
                                  "newPrice": 3,
                                  "validFrom": "2024-11-24T14:22:08.4"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.materialId").isString())
                .andExpect(jsonPath("$.materialName").value("Gips"))
                .andExpect(jsonPath("$.storageCosts").isArray())
                .andExpect(jsonPath("$.storageCosts[1].validTo").isNotEmpty())
                .andExpect(jsonPath("$.storageCosts[1].validTo").isString())
                .andExpect(jsonPath("$.storageCosts[1].validTo").value("2024-11-24T14:22:08.4"))
                .andExpect(jsonPath("$.storageCosts[2].validFrom").isNotEmpty())
                .andExpect(jsonPath("$.storageCosts[2].validFrom").isString())
                .andExpect(jsonPath("$.storageCosts[2].validFrom").value("2024-11-24T14:22:08.4"))
                .andExpect(jsonPath("$.storageCosts[2].validTo").isEmpty())
                .andExpect(jsonPath("$.storageCosts[2].price").isNumber())
                .andExpect(jsonPath("$.storageCosts[2].price").value(3));
    }

    @Test
    @Sql(value = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void addingNewStorageCostWithNegativePriceShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/pricing/gips/storageCost")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content("""
                                {
                                  "newPrice": -1,
                                  "validFrom": "2024-11-24T14:22:08.4"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(value = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void addingNewStorageCostWithPastDateShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/pricing/gips/storageCost")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content("""
                                {
                                  "newPrice": 4,
                                  "validFrom": "2024-10-01T14:22:08.4"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(value = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void addingNewStorageCostWithValidDateBeforeLatestShouldReturn409() throws Exception {
        mockMvc.perform(post("/api/pricing/gips/storageCost")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content("""
                                {
                                  "newPrice": 4,
                                  "validFrom": "2024-11-01T08:59:59"
                                }
                                """));

        mockMvc.perform(post("/api/pricing/gips/storageCost")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content("""
                                {
                                  "newPrice": 4,
                                  "validFrom": "2024-10-31T12:00:00"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON.toString()));
    }

    @Test
    @Sql(value = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void changingSellPriceShouldReturn200() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        var mvcResult = mockMvc.perform(patch("/api/pricing/gips/sellPrice")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content("""
                                {
                                  "sellPrice": 500.0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.materialId").isString())
                .andExpect(jsonPath("$.materialName").value("Gips"))
                .andExpect(jsonPath("$.sellPrice").isMap())
                .andExpect(jsonPath("$.sellPrice.pricingInfoId").isString())
                .andExpect(jsonPath("$.sellPrice.price").isNumber())
                .andExpect(jsonPath("$.sellPrice.price").value(500))
                .andExpect(jsonPath("$.sellPrice.validFrom").isNotEmpty())
                .andExpect(jsonPath("$.sellPrice.validTo").isEmpty())
                .andReturn();

            var validFrom = LocalDateTime.parse((String) JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.sellPrice.validFrom"));
            Duration tolerance = Duration.ofSeconds(2);
            assertTrue(Duration.between(now, validFrom).abs().compareTo(tolerance) < 0);
    }

    @Test
    @Sql(value = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void changingSellPriceWithNegativePriceShouldReturn400() throws Exception {
        mockMvc.perform(patch("/api/pricing/gips/sellPrice")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content("""
                                {
                                  "sellPrice": -500
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

}