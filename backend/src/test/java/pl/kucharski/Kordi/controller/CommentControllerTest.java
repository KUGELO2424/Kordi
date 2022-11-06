package pl.kucharski.Kordi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.kucharski.Kordi.KordiApplication;
import pl.kucharski.Kordi.repository.CommentRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.kucharski.Kordi.CollectionData.COMMENT_TO_ADD;
import static pl.kucharski.Kordi.CollectionData.COMMENT_TO_ADD_WITH_WRONG_USERID;
import static pl.kucharski.Kordi.CollectionData.USERNAME_FROM_DB;
import static pl.kucharski.Kordi.config.ErrorCodes.CURRENT_USER_NOT_AN_OWNER;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KordiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CommentControllerTest {

    private final static Long EXISTING_COLLECTION_ID = 1L;
    private final static Long EXISTING_COLLECTION_V2_ID = 2L;
    private final static Long NOT_EXISTING_COLLECTION_ID = 55L;
    private final static String COMMENT_CONTENT_01 = "New content for comment 01";
    private final static String COMMENT_CONTENT_02 = "New content for comment 02";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @WithMockUser
    void shouldReturnListOfComments() throws Exception {
        mvc.perform(get("/collections/" + EXISTING_COLLECTION_ID + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].content", is(COMMENT_CONTENT_01)))
                .andExpect(jsonPath("$[1].content", is(COMMENT_CONTENT_02)));
    }

    @Test
    @WithMockUser
    void shouldReturn404IfCollectionNotFound() throws Exception {
        mvc.perform(get("/collections/" + NOT_EXISTING_COLLECTION_ID + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldAddCommentToCollection() throws Exception {
        mvc.perform(post("/collections/" + EXISTING_COLLECTION_V2_ID + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COMMENT_TO_ADD)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(4)));
    }

    @Test
    @WithMockUser
    void shouldReturn404IfCollectionNotFoundOnCommentAdd() throws Exception {
        mvc.perform(post("/collections/" + NOT_EXISTING_COLLECTION_ID + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COMMENT_TO_ADD)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldReturn404IfUserNotFoundOnCommentAdd() throws Exception {
        mvc.perform(post("/collections/" + EXISTING_COLLECTION_ID + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COMMENT_TO_ADD_WITH_WRONG_USERID)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "ewa")
    void shouldRemoveCommentFromCollectionIfUserIsOwner() throws Exception {
        int numOfComments = commentRepository.getAllByCollectionId(EXISTING_COLLECTION_V2_ID, PageRequest.of(0, 10)).size();

        mvc.perform(delete("/collections/" + EXISTING_COLLECTION_V2_ID + "/comments/" + 3)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mvc.perform(get("/collections/" + EXISTING_COLLECTION_V2_ID + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(numOfComments - 1)));
    }

    @Test
    @WithMockUser(username = USERNAME_FROM_DB)
    void shouldNotRemoveCommentFromCollectionIfUserIsNotAnOwner() throws Exception {
        mvc.perform(delete("/collections/" + EXISTING_COLLECTION_V2_ID + "/comments/" + 3)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is(CURRENT_USER_NOT_AN_OWNER)));


    }

    @Test
    @WithMockUser
    void shouldReturn404IfCollectionNotFoundOnRemoveComment() throws Exception {
        mvc.perform(delete("/collections/" + NOT_EXISTING_COLLECTION_ID + "/comments/" + 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COMMENT_TO_ADD)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "ewa")
    void shouldReturn404IfCommentNotFoundOnRemoveComment() throws Exception {
        mvc.perform(delete("/collections/" + EXISTING_COLLECTION_V2_ID + "/comments/" + 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COMMENT_TO_ADD)
                )
                .andExpect(status().isNotFound());
    }

}