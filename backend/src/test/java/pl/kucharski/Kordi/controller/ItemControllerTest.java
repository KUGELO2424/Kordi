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
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.KordiApplication;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.kucharski.Kordi.CollectionData.ITEM_DTO_TO_ADD;
import static pl.kucharski.Kordi.CollectionData.ITEM_DTO_TO_SUBMIT;
import static pl.kucharski.Kordi.CollectionData.ITEM_DTO_TO_SUBMIT_WITH_WRONG_VALUES;
import static pl.kucharski.Kordi.CollectionData.ITEM_TO_UPDATE;
import static pl.kucharski.Kordi.CollectionData.ITEM_TO_UPDATE_WITH_NOT_VALID_VALUES;
import static pl.kucharski.Kordi.CollectionData.NOT_VALID_ITEM_DTO_TO_ADD;
import static pl.kucharski.Kordi.controller.CollectionControllerTest.EXISTING_COLLECTION_ID;
import static pl.kucharski.Kordi.controller.CollectionControllerTest.EXISTING_ITEM_ID;
import static pl.kucharski.Kordi.controller.CollectionControllerTest.EXISTING_ITEM_ID_2;
import static pl.kucharski.Kordi.controller.CollectionControllerTest.NOT_EXISTING_COLLECTION_ID;
import static pl.kucharski.Kordi.controller.CollectionControllerTest.NOT_EXISTING_ITEM_ID;

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

    @Test
    @WithMockUser(username = "ewa")
    void shouldReturn400OnAddNewItemIfItemNotValid() throws Exception {
        mvc.perform(post("/collections/" + NOT_EXISTING_COLLECTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NOT_VALID_ITEM_DTO_TO_ADD)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Item category cannot be null.")));
    }

    @Test
    @WithMockUser(username = "ewa")
    @Transactional
    void shouldUpdateCollectionItem() throws Exception {
        mvc.perform(patch("/collections/" + EXISTING_COLLECTION_ID + "/items/" + EXISTING_ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ITEM_TO_UPDATE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxAmount", is(20)))
                .andExpect(jsonPath("$.currentAmount", is(5)));
    }

    @Test
    @WithMockUser(username = "ewa")
    void shouldReturn404OnItemUpdateIfCollectionNotFound() throws Exception {
        mvc.perform(patch("/collections/" + NOT_EXISTING_COLLECTION_ID + "/items/" + EXISTING_ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ITEM_TO_UPDATE)
                )
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Collection with id " + NOT_EXISTING_COLLECTION_ID + " not found in database"));
    }

    @Test
    @WithMockUser(username = "ewa")
    void shouldReturn404OnItemUpdateIfItemNotFound() throws Exception {
        mvc.perform(patch("/collections/" + EXISTING_COLLECTION_ID + "/items/" + NOT_EXISTING_ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ITEM_TO_UPDATE)
                )
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Item with id " + NOT_EXISTING_ITEM_ID +
                        " not found in collection with id " + EXISTING_COLLECTION_ID));
    }

    @Test
    @WithMockUser(username = "ewa")
    void shouldReturn400OnItemUpdateIfNewValuesNotValid() throws Exception {
        mvc.perform(patch("/collections/" + EXISTING_COLLECTION_ID + "/items/" + EXISTING_ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ITEM_TO_UPDATE_WITH_NOT_VALID_VALUES)
                )
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Current amount cannot be bigger than maximum"));
    }

    @Test
    @WithMockUser(username = "ewa")
    @Transactional
    void shouldReturnOkOnSubmitNewItem() throws Exception {
        mvc.perform(post("/collections/" + EXISTING_COLLECTION_ID + "/items/" + EXISTING_ITEM_ID + "/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ITEM_DTO_TO_SUBMIT)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxAmount", is(4)))
                .andExpect(jsonPath("$.currentAmount", is(2)));
    }

    @Test
    @WithMockUser(username = "ewa")
    @Transactional
    void shouldReturn400OnSubmitNewItemIfNewValuesNotValid() throws Exception {
        mvc.perform(post("/collections/" + EXISTING_COLLECTION_ID + "/items/" + EXISTING_ITEM_ID + "/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ITEM_DTO_TO_SUBMIT_WITH_WRONG_VALUES)
                )
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Current amount cannot be bigger than maximum"));
    }

    @Test
    @WithMockUser(username = "ewa")
    @Transactional
    void shouldReturnAllSubmittedItems() throws Exception {
        mvc.perform(get("/collections/" + EXISTING_COLLECTION_ID + "/submittedItems")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));

        mvc.perform(post("/collections/" + EXISTING_COLLECTION_ID + "/items/" + EXISTING_ITEM_ID + "/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ITEM_DTO_TO_SUBMIT)
        );

        mvc.perform(get("/collections/" + EXISTING_COLLECTION_ID + "/submittedItems")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    @WithMockUser(username = "ewa")
    @Transactional
    void shouldReturnAllSubmittedItemsForSpecificItem() throws Exception {
        mvc.perform(get("/collections/" + EXISTING_COLLECTION_ID + "/items/" + EXISTING_ITEM_ID_2 + "/submittedItems")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    @WithMockUser(username = "ewa")
    void shouldReturnAllCategories() throws Exception {
        mvc.perform(get("/collections/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(7)));
    }

}