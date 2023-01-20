package pl.kucharski.Kordi;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.kucharski.Kordi.enums.ItemType;
import pl.kucharski.Kordi.enums.VerificationType;
import pl.kucharski.Kordi.model.address.Address;
import pl.kucharski.Kordi.model.address.AddressDTO;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection.CollectionDTO;
import pl.kucharski.Kordi.model.collection_item.CollectionItemDTO;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItemDTO;
import pl.kucharski.Kordi.model.comment.Comment;
import pl.kucharski.Kordi.model.comment.CreateCommentDTO;
import pl.kucharski.Kordi.model.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CollectionData {

    public static final Pageable PAGING = PageRequest.of(0, 10);
    public static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    public static final LocalDateTime TOMORROW = LocalDateTime.now().plusDays(1);
    public static final LocalDateTime YESTERDAY = LocalDateTime.now().minusDays(1);
    public static final String COMMENT_CONTENT = "New content";

    public static final LocalDateTime NEW_END_TIME = LocalDateTime.now().plusDays(100);
    public static final String NEW_TITLE = "NewTitle";
    public static final String NEW_DESC = "NewDescription";

    public static final Address ADDRESS = new Address("Warszawa", "Wielka");
    public static final AddressDTO ADDRESS_DTO = AddressDTO.builder()
            .city("Warszawa")
            .street("Wielka")
            .build();
    public static final AddressDTO ADDRESS_DTO_V2 = AddressDTO.builder()
            .city("Krakow")
            .street("Maly")
            .build();


    public static final String USERNAME = "test123";
    public static final String USERNAME_FROM_DB = "test";
    public static final String USERNAME_OF_COLLECTION_4 = "jan";
    public static final User USER = new User(1L,"Test", "test", USERNAME, "qwerty",
            "test@mail.com", "110339332", true, VerificationType.EMAIL);

    private static List<Address> crateListOfAddresses() {
        return new ArrayList<>() {
            {
                add(ADDRESS);
            }
        };
    }

    public static Collection createCollectionWithId() {
        return new Collection(1L, "New Collection", "desc",
                CURRENT_TIME, null, 0L, USER, crateListOfAddresses(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public static CollectionDTO createCollectionDTOWithId() {
        return CollectionDTO.builder()
                .id(1L)
                .title("New Collection")
                .description("desc")
                .startTime(CURRENT_TIME)
                .userFirstname("Test")
                .userLastname("test")
                .donates(0L)
                .addresses(List.of(ADDRESS_DTO))
                .items(new ArrayList<>())
                .build();
    }

    public static CollectionDTO createCollectionDTOWithoutId() {
        return CollectionDTO.builder()
                .title("New Collection")
                .description("desc")
                .startTime(CURRENT_TIME)
                .userId(1L)
                .addresses(List.of(ADDRESS_DTO))
                .items(new ArrayList<>())
                .build();
    }

    public static CollectionItemDTO createItemDTOWithId() {
        return CollectionItemDTO.builder()
                .id(1L)
                .name("item")
                .currentAmount(3)
                .maxAmount(10)
                .type(ItemType.AMOUNT)
                .build();
    }

    public static CollectionItemDTO createSecondItemDTOWithId() {
        return CollectionItemDTO.builder()
                .id(2L)
                .name("itemV2")
                .currentAmount(1)
                .maxAmount(10)
                .type(ItemType.WEIGHT)
                .build();
    }

    public static SubmittedItemDTO createSubmittedItemDTO() {
        return SubmittedItemDTO.builder()
                .userId(1L)
                .collectionId(1L)
                .collectionItemId(1L)
                .submitTime(LocalDateTime.now())
                .amount(1)
                .build();
    }

    public static CreateCommentDTO createCommentDTO() {
        return CreateCommentDTO.builder()
                .content(COMMENT_CONTENT)
                .build();
    }

    public static List<Comment> createListOfComments() {
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment(1L, COMMENT_CONTENT, 1L, 1L);
        comments.add(comment);
        return comments;
    }

    public static final String COLLECTION_TO_CREATE = "{" +
            "\"title\":\"TestCollection\", " +
            "\"description\":\"TestDescription\"," +
            "\"addresses\": " +
            "[{\"city\":\"TestCity\"," +
            "\"street\":\"TestStreet\"}]," +
            "\"items\": " +
            "[{\"name\":\"TestName\"," +
            "\"type\":\"AMOUNT\"," +
            "\"category\":\"OTHER\"," +
            "\"currentAmount\":\"0\"," +
            "\"maxAmount\":\"10\"}]" +
            "}";

    public static final String COLLECTION_TO_CREATE_WITH_NOT_EXISTING_USER = "{" +
            "\"title\":\"TestCollection\", " +
            "\"description\":\"TestDescription\"," +
            "\"userId\":\"10\"," +
            "\"addresses\": " +
            "[{\"city\":\"TestCity\"," +
            "\"street\":\"TestStreet\"}]," +
            "\"items\": " +
            "[{\"name\":\"TestName\"," +
            "\"type\":\"AMOUNT\"," +
            "\"category\":\"OTHER\"," +
            "\"currentAmount\":\"0\"," +
            "\"maxAmount\":\"10\"}]" +
            "}";

    public static final String COLLECTION_TO_CREATE_WITH_EMPTY_TITLE = "{" +
            "\"title\":\"\", " +
            "\"description\":\"TestDescription\"," +
            "\"userId\":\"1\"," +
            "\"addresses\": " +
            "[{\"city\":\"TestCity\"," +
            "\"street\":\"TestStreet\"}]," +
            "\"items\": " +
            "[{\"name\":\"TestName\"," +
            "\"type\":\"AMOUNT\"," +
            "\"category\":\"OTHER\"," +
            "\"currentAmount\":\"0\"," +
            "\"maxAmount\":\"10\"}]" +
            "}";

    public static final String COLLECTION_TO_CREATE_WITHOUT_ITEM_TYPE = "{" +
            "\"title\":\"TestCollection\", " +
            "\"description\":\"TestDescription\"," +
            "\"userId\":\"1\"," +
            "\"addresses\": " +
            "[{\"city\":\"TestCity\"," +
            "\"street\":\"TestStreet\"}]," +
            "\"items\": " +
            "[{\"name\":\"TestName\"," +
            "\"currentAmount\":\"0\"," +
            "\"category\":\"OTHER\"," +
            "\"maxAmount\":\"10\"}]" +
            "}";

    public static final String COLLECTION_TO_CREATE_WITHOUT_ITEM_CATEGORY = "{" +
            "\"title\":\"TestCollection\", " +
            "\"description\":\"TestDescription\"," +
            "\"addresses\": " +
            "[{\"city\":\"TestCity\"," +
            "\"street\":\"TestStreet\"}]," +
            "\"items\": " +
            "[{\"name\":\"TestName\"," +
            "\"type\":\"AMOUNT\"," +
            "\"currentAmount\":\"0\"," +
            "\"maxAmount\":\"10\"}]" +
            "}";

    public static final String COLLECTION_TO_CREATE_WITH_NOT_EXISTING_ITEM_TYPE = "{" +
            "\"title\":\"TestCollection\", " +
            "\"description\":\"TestDescription\"," +
            "\"userId\":\"1\"," +
            "\"addresses\": " +
            "[{\"city\":\"TestCity\"," +
            "\"street\":\"TestStreet\"}]," +
            "\"items\": " +
            "[{\"name\":\"TestName\"," +
            "\"type\":\"UNKNOWN\"," +
            "\"category\":\"OTHER\"," +
            "\"currentAmount\":\"0\"," +
            "\"maxAmount\":\"10\"}]" +
            "}";

    public static final String COLLECTION_TO_UPDATE = "{" +
            "\"id\":\"4\", " +
            "\"title\":\"" + NEW_TITLE + "\", " +
            "\"description\":\"" + NEW_DESC + "\"," +
            "\"endTime\":\"" + NEW_END_TIME + "\"}";

    public static final String NOT_EXISTING_COLLECTION_TO_UPDATE = "{" +
            "\"id\":\"55\", " +
            "\"title\":\"" + NEW_TITLE + "\", " +
            "\"description\":\"" + NEW_DESC + "\"," +
            "\"endTime\":\"" + NEW_END_TIME + "\"}";

    public static final String COMMENT_TO_ADD = "{" +
            "\"content\":\"" + COMMENT_CONTENT + "\"}";

    public static final String COMMENT_TO_ADD_WITH_WRONG_USERID = "{" +
            "\"content\":\"" + COMMENT_CONTENT + "\"}";

    public static final String ITEM_DTO_TO_ADD = "{" +
            "\"name\":\"Koszulki\", " +
            "\"currentAmount\":\"5\"," +
            "\"maxAmount\":\"10\"," +
            "\"category\":\"CLOTHES\"," +
            "\"type\":\"AMOUNT\"}";

    public static final String NOT_VALID_ITEM_DTO_TO_ADD = "{" +
            "\"name\":\"Koszulki\", " +
            "\"currentAmount\":\"5\"," +
            "\"maxAmount\":\"10\"," +
            "\"type\":\"AMOUNT\"}";

    public static final String ITEM_TO_UPDATE = "{" +
            "\"currentAmount\":\"5\"," +
            "\"maxAmount\":\"20\"}";

    public static final String ITEM_TO_UPDATE_WITH_NOT_VALID_VALUES = "{" +
            "\"currentAmount\":\"20\"," +
            "\"maxAmount\":\"10\"}";

    public static final String ITEMS_DTO_TO_SUBMIT = "[{" +
            "\"amount\":\"2\", " +
            "\"username\":\"test123\"," +
            "\"submitTime\":\"2022-06-21T10:00\"," +
            "\"collectionId\":\"1\"," +
            "\"collectionItemId\":\"1\"}]";

    public static final String ITEMS_DTO_TO_SUBMIT_WITH_WRONG_VALUES = "[{" +
            "\"amount\":\"22\", " +
            "\"username\":\"test123\"," +
            "\"collectionId\":\"1\"," +
            "\"collectionItemId\":\"1\"}]";

    public static final String ADDRESS_TO_ADD = "{" +
            "\"city\":\"NewCity\", " +
            "\"street\":\"NewStreet\"}";
}
