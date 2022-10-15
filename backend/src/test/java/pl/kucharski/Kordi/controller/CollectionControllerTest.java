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

import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.kucharski.Kordi.CollectionData.COLLECTION_TO_CREATE;
import static pl.kucharski.Kordi.CollectionData.COLLECTION_TO_CREATE_WITH_EMPTY_TITLE;
import static pl.kucharski.Kordi.CollectionData.COLLECTION_TO_CREATE_WITH_NOT_EXISTING_ITEM_TYPE;
import static pl.kucharski.Kordi.CollectionData.COLLECTION_TO_CREATE_WITH_NOT_EXISTING_USER;
import static pl.kucharski.Kordi.CollectionData.COLLECTION_TO_UPDATE;
import static pl.kucharski.Kordi.CollectionData.NEW_DESC;
import static pl.kucharski.Kordi.CollectionData.NEW_END_TIME;
import static pl.kucharski.Kordi.CollectionData.NEW_TITLE;
import static pl.kucharski.Kordi.CollectionData.NOT_EXISTING_COLLECTION_TO_UPDATE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KordiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CollectionControllerTest {

    public final static Long EXISTING_COLLECTION_ID = 1L;
    public final static Long NOT_EXISTING_COLLECTION_ID = 555L;
    private final static String EXISTING_USERNAME = "ewa";
    private final static String NOT_EXISTING_USERNAME = "not_existing_username";
    private final static String TITLE_OF_COLLECTION_OF_EXISTING_USER = "Zbiórka dla Bartka";
    private final static String TITLE_02_OF_COLLECTION_OF_EXISTING_USER = "Zbiórka dla Oliwii";
    private final static String TITLE_FOR_SEARCH = "Oliwii";
    private final static String CITY_FOR_SEARCH = "Warszawa";
    private final static String ITEM_FOR_SEARCH = "Spodnie";

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser
    void shouldReturnEmptyListOfCollectionsIfUserNotExists() throws Exception {
        mvc.perform(get("/user/" + NOT_EXISTING_USERNAME + "/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));
    }

    @Test
    @WithMockUser
    void shouldReturnListOfCollectionsOfUser() throws Exception {
        mvc.perform(get("/user/" + EXISTING_USERNAME + "/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is(TITLE_OF_COLLECTION_OF_EXISTING_USER)))
                .andExpect(jsonPath("$[1].title", is(TITLE_02_OF_COLLECTION_OF_EXISTING_USER)));
    }

    @Test
    @WithMockUser
    void shouldReturnCollectionById() throws Exception {
        mvc.perform(get("/collections/" + EXISTING_COLLECTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(TITLE_OF_COLLECTION_OF_EXISTING_USER)));
    }

    @Test
    @WithMockUser
    void shouldThrowCollectionNotFound() throws Exception {
        mvc.perform(get("/collections/" + NOT_EXISTING_COLLECTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldReturnAllCollections() throws Exception {
        mvc.perform(get("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(4)));
    }

    @Test
    @WithMockUser
    void shouldReturnCollectionsWithTitleFiltering() throws Exception {
        mvc.perform(get("/collections?title=Zbiórka")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is(TITLE_OF_COLLECTION_OF_EXISTING_USER)))
                .andExpect(jsonPath("$[1].title", is(TITLE_02_OF_COLLECTION_OF_EXISTING_USER)));

    }

    @Test
    @WithMockUser
    void shouldReturnCollectionsWithTitleAndAddressFiltering() throws Exception {
        mvc.perform(get("/collections?title=" + TITLE_FOR_SEARCH + "&city=" + CITY_FOR_SEARCH)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].title", containsString(TITLE_FOR_SEARCH)))
                .andExpect(jsonPath("$[0].addresses[0].city", is(CITY_FOR_SEARCH)));
    }

    @Test
    @WithMockUser
    void shouldReturnCollectionsWithTitleAndItemNameFiltering() throws Exception {
        mvc.perform(get("/collections?title=" + TITLE_FOR_SEARCH + "&itemName=" + ITEM_FOR_SEARCH)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].title", containsString(TITLE_FOR_SEARCH)))
                .andExpect(jsonPath("$[0].items[0].name", is(ITEM_FOR_SEARCH)));
    }

    @Test
    @WithMockUser
    void shouldSaveNewCollection() throws Exception {
        mvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COLLECTION_TO_CREATE)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.addresses[0].city", is("TestCity")))
                .andExpect(jsonPath("$.items[0].id", is(5)));

    }

    @Test
    @WithMockUser
    void shouldThrowNotFoundIfUserWithGivenIdNotExists() throws Exception {
        mvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COLLECTION_TO_CREATE_WITH_NOT_EXISTING_USER)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldThrowBadRequestIfTitleIsEmpty() throws Exception {
        mvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COLLECTION_TO_CREATE_WITH_EMPTY_TITLE)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldThrowBadRequestIfItemTypeNotExists() throws Exception {
        mvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COLLECTION_TO_CREATE_WITH_NOT_EXISTING_ITEM_TYPE)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldUpdateExistingCollection() throws Exception {
        mvc.perform(patch("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COLLECTION_TO_UPDATE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(NEW_TITLE)))
                .andExpect(jsonPath("$.description", is(NEW_DESC)))
                .andExpect(jsonPath(("$.endTime"), containsString(String.valueOf(NEW_END_TIME.truncatedTo(ChronoUnit.SECONDS)))));
    }

    @Test
    @WithMockUser
    void shouldThrowNotFoundOnUpdateIfCollectionNotFound() throws Exception {
        mvc.perform(patch("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NOT_EXISTING_COLLECTION_TO_UPDATE)
                )
                .andExpect(status().isNotFound());
    }

}