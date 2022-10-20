package pl.kucharski.Kordi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.kucharski.Kordi.KordiApplication;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.kucharski.Kordi.CollectionData.ITEM_DTO_TO_ADD;
import static pl.kucharski.Kordi.controller.CollectionControllerTest.EXISTING_COLLECTION_ID;
import static pl.kucharski.Kordi.controller.CollectionControllerTest.NOT_EXISTING_COLLECTION_ID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KordiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "ewa")
    void shouldReturnOkOnAddNewItem() throws Exception {
        mvc.perform(post("/collections/" + EXISTING_COLLECTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ITEM_DTO_TO_ADD)
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "ewa")
    void shouldReturn404OnAddNewItem() throws Exception {
        mvc.perform(post("/collections/" + NOT_EXISTING_COLLECTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ITEM_DTO_TO_ADD)
                )
                .andExpect(status().isNotFound());
    }

}