package kdg.be.warehouse.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InvoiceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql(value = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void GetOpenInvoicesShouldReturn200() throws Exception {
        mockMvc.perform(
                        post("/api/purchase-orders/receive")
                                .contentType(APPLICATION_JSON)
                                .accept(APPLICATION_JSON)
                                .content("""
                                          {
                                              "poNumber": "PO123456",
                                              "referenceUUID": "550e8400-e29b-41d4-a716-446655440000",
                                              "customerParty": {
                                                "UUID": "56efaea4-953c-44bf-9f41-9700fffa2f28",
                                                "name": "Joske Vermeulen",
                                                "address": "Trammesantlei 122, Schoten, Belgium "
                                              },
                                              "sellerParty": {
                                                "UUID": "9ae35800-5fdf-4932-a713-251f49e11012",
                                                "name": "Desmet NV",
                                                "address": "Het adres van de klant van KDG"
                                              },
                                              "vesselNumber": "VSL7891011",
                                              "orderLines": [
                                                {
                                                  "lineNumber": 1,
                                                  "materialName": "PetCoke",
                                                  "quantity": 100,
                                                  "uom": "t"
                                                },
                                                {
                                                  "lineNumber": 2,
                                                  "materialName": "Slak",
                                                  "quantity": 50,
                                                  "uom": "t"
                                                }
                                              ]
                                            }
                                        """))
                .andExpect(status().isOk());

        mockMvc.perform(
                post("/api/purchase-orders/complete")
                        .param("sellerId", "9ae35800-5fdf-4932-a713-251f49e11012")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        [
                                          "PO123456"
                                        ]
                                        """)
        ).andExpect(status().isOk());

        mockMvc.perform(
                        get("/api/invoices/upcoming/Desmet NV")
                                .accept(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").isMap())
                .andExpect(jsonPath("$[0].invoiceId").isString())
                .andExpect(jsonPath("$[0].createdDate").isString())
                .andExpect(jsonPath("$[0].customerName").value("Desmet NV"))
                .andExpect(jsonPath("$[0].totalPrice").value(290))
                .andExpect(jsonPath("$[0].invoiceLines").isArray())
                .andExpect(jsonPath("$[0].invoiceLines[0].lineNumber").value(1))
                .andExpect(jsonPath("$[0].invoiceLines[0].description").value("Commission PetCoke"))
                .andExpect(jsonPath("$[0].invoiceLines[0].amountOfUnits").value(100))
                .andExpect(jsonPath("$[0].invoiceLines[0].unitPrice").value(2.1))
                .andExpect(jsonPath("$[0].invoiceLines[0].totalPrice").value(210))
                .andExpect(jsonPath("$[0].invoiceLines[1].lineNumber").value(2))
                .andExpect(jsonPath("$[0].invoiceLines[1].description").value("Commission Slak"))
                .andExpect(jsonPath("$[0].invoiceLines[1].amountOfUnits").value(50))
                .andExpect(jsonPath("$[0].invoiceLines[1].unitPrice").value(1.6))
                .andExpect(jsonPath("$[0].invoiceLines[1].totalPrice").value(80));
    }

    @Test
    @Sql(value = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void GetOpenInvoicesShouldReturn404WhenWrongSellerIsGiven() throws Exception {
        mockMvc.perform(
                        get("/api/invoices/upcoming/Desmet BVBA")
                                .accept(APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql(value = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void GetOpenInvoicesShouldReturn200WithEmptyListWhenNoOpenInvoiceExists() throws Exception {
        mockMvc.perform(
                        get("/api/invoices/upcoming/Desmet NV")
                                .accept(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

    }

}
