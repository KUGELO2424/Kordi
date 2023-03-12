package pl.kucharski.Kordi.service.collection.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.kucharski.Kordi.enums.ItemCategory;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.model.address.AddressMapper;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection.CollectionDTO;
import pl.kucharski.Kordi.model.collection.CollectionMapper;
import pl.kucharski.Kordi.model.collection.CollectionMapperImpl;
import pl.kucharski.Kordi.model.collection_item.CollectionItemMapper;
import pl.kucharski.Kordi.repository.CollectionRepository;
import pl.kucharski.Kordi.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.kucharski.Kordi.CollectionData.PAGING;
import static pl.kucharski.Kordi.CollectionData.PAGING_WITH_WRONG_SORT;
import static pl.kucharski.Kordi.CollectionData.TOMORROW;
import static pl.kucharski.Kordi.CollectionData.USER;
import static pl.kucharski.Kordi.CollectionData.USERNAME;
import static pl.kucharski.Kordi.CollectionData.YESTERDAY;
import static pl.kucharski.Kordi.CollectionData.createCollectionDTOWithId;
import static pl.kucharski.Kordi.CollectionData.createCollectionDTOWithoutId;
import static pl.kucharski.Kordi.CollectionData.createCollectionWithId;
import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_END_DATE_INVALID;
import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
@Transactional
class CollectionServiceImplTest {

    private final AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);
    private final CollectionItemMapper itemMapper = Mappers.getMapper(CollectionItemMapper.class);
    private final CollectionMapper collectionMapper = new CollectionMapperImpl(addressMapper, itemMapper);
    private final CollectionHelper collectionHelper = new CollectionHelper();

    private static Collection COLLECTION_WITH_ID;
    private static CollectionDTO COLLECTION_DTO_WITH_ID;
    private static CollectionDTO COLLECTION_DTO_WITHOUT_ID;

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CollectionServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new CollectionServiceImpl(collectionRepository, userRepository, collectionMapper, collectionHelper);
        COLLECTION_WITH_ID = createCollectionWithId();
        COLLECTION_DTO_WITH_ID = createCollectionDTOWithId();
        COLLECTION_DTO_WITHOUT_ID = createCollectionDTOWithoutId();
    }

    @AfterAll
    static void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnCollectionById() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when
        CollectionDTO collection = underTest.getCollectionById(1L);

        // then
        Assertions.assertEquals(COLLECTION_DTO_WITH_ID, collection);
    }

    @Test
    void shouldThrowCollectionNotFound() {
        // when + then
        CollectionNotFoundException thrown =
                assertThrows(CollectionNotFoundException.class, () -> underTest.getCollectionById(10L));
        assertEquals(COLLECTION_NOT_FOUND, thrown.getMessage());
    }

    @Test
    void shouldReturnCollectionsByUsername() {
        // given
        given(collectionRepository.findByUserUsername("test123", PAGING)).willReturn(List.of(COLLECTION_WITH_ID));

        // when
        List<CollectionDTO> collections = underTest.getCollectionsByUser("test123", PAGING);

        // then
        Assertions.assertEquals(1, collections.size());
        assertEquals(COLLECTION_DTO_WITH_ID, collections.get(0));
    }

    @Test
    void shouldReturnEmptyCollectionsByUsername() {
        // when
        List<CollectionDTO> collections = underTest.getCollectionsByUser("not_existing_user", PAGING);

        // then
        Assertions.assertEquals(0, collections.size());
    }

    @Test
    void shouldReturnCollectionsByTitle() {
        // given
        given(collectionRepository
                .findWithFiltering("Zbiórka", "", "", "", 0, List.of(), PAGING))
                .willReturn(new PageImpl<>(List.of(COLLECTION_WITH_ID)));

        // when
        Page<CollectionDTO> collections =
                underTest.getCollectionsWithFiltering("Zbiórka", "", "", "", List.of(), PAGING);

        // then
        Assertions.assertEquals(1, collections.getContent().size());
        assertEquals(COLLECTION_DTO_WITH_ID, collections.getContent().get(0));
    }

    @Test
    void shouldReturnCollectionsByTitleAndAddress() {
        // given
        given(collectionRepository
                .findWithFiltering("Zbiórka", "Warszawa", "", "", 0, List.of(), PAGING))
                .willReturn(new PageImpl<>(List.of(COLLECTION_WITH_ID)));

        // when
        Page<CollectionDTO> collections = underTest.getCollectionsWithFiltering(
                "Zbiórka", "Warszawa", "", "", List.of(), PAGING);

        // then
        Assertions.assertEquals(1, collections.getContent().size());
        assertEquals(COLLECTION_DTO_WITH_ID, collections.getContent().get(0));
    }

    @Test
    void shouldReturnCollectionsByTitleAndAddressAndItemName() {
        // given
        given(collectionRepository
                .findWithFiltering("Zbiórka", "Warszawa", "", "Buty", 0, List.of(), PAGING))
                .willReturn(new PageImpl<>(List.of(COLLECTION_WITH_ID)));

        // when
        Page<CollectionDTO> collections = underTest.getCollectionsWithFiltering(
                "Zbiórka", "Warszawa", "", "Buty", List.of(), PAGING);

        // then
        Assertions.assertEquals(1, collections.getContent().size());
        assertEquals(COLLECTION_DTO_WITH_ID, collections.getContent().get(0));
    }

    @Test
    void shouldReturnCollectionsByTitleAndAddressAndItemNameAndItemCategory() {
        // given
        given(collectionRepository
                .findWithFiltering("Zbiórka", "Warszawa", "", "Buty", 1,
                        List.of(ItemCategory.CLOTHES), PAGING))
                .willReturn(new PageImpl<>(List.of(COLLECTION_WITH_ID)));

        // when
        Page<CollectionDTO> collections = underTest.getCollectionsWithFiltering(
                "Zbiórka", "Warszawa", "", "Buty", List.of(ItemCategory.CLOTHES), PAGING);

        // then
        Assertions.assertEquals(1, collections.getContent().size());
        assertEquals(COLLECTION_DTO_WITH_ID, collections.getContent().get(0));
    }

    @Test
    void shouldThrowBadRequestOnGetCollectionsIfSortByWithWrongName() {
        // given
        given(collectionRepository
                .findWithFiltering("", "", "", "", 0,
                        Collections.emptyList(), PAGING_WITH_WRONG_SORT))
                .willThrow(new InvalidDataAccessApiUsageException("wrong sortBy"));

        // when + then
        assertThrows(ResponseStatusException.class, () -> underTest.getCollectionsWithFiltering(
                        "", "", "", "", Collections.emptyList(), PAGING_WITH_WRONG_SORT));
    }

    @Test
    void shouldReturnCollectionsByAddress() {
        // given
        given(collectionRepository
                .findWithFiltering("", "Warszawa", "", "", 0, List.of(), PAGING))
                .willReturn(new PageImpl<>(List.of(COLLECTION_WITH_ID)));

        // when
        Page<CollectionDTO> collections = underTest.getCollectionsWithFiltering(
                "", "Warszawa", "", "", List.of(), PAGING);

        // then
        Assertions.assertEquals(1, collections.getContent().size());
        assertEquals(COLLECTION_DTO_WITH_ID, collections.getContent().get(0));
    }

    @Test
    void shouldReturnCollectionsByItemName() {
        // given
        given(collectionRepository
                .findWithFiltering("", "", "", "Buty", 0, List.of(), PAGING))
                .willReturn(new PageImpl<>(List.of(COLLECTION_WITH_ID)));

        // when
        Page<CollectionDTO> collections = underTest.getCollectionsWithFiltering(
                "", "", "", "Buty", List.of(), PAGING);

        // then
        Assertions.assertEquals(1, collections.getContent().size());
        assertEquals(COLLECTION_DTO_WITH_ID, collections.getContent().get(0));
    }

    @Test
    void shouldReturnEmptyCollectionOnFiltering() {
        // when
        Page<CollectionDTO> collections = underTest.getCollectionsWithFiltering(
                "Zbiórka", "", "", "", List.of(), PAGING);

        // then
        Assertions.assertEquals(0, collections.getContent().size());
    }

    @Test
    void shouldSaveCollection() {
        // given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(USERNAME);
        given(userRepository.findUserByUsername(USERNAME)).willReturn(Optional.of(USER));
        given(collectionRepository.save(any())).willReturn(COLLECTION_WITH_ID);

        // when
        CollectionDTO savedCollectionDTO = underTest.saveCollection(COLLECTION_DTO_WITHOUT_ID);

        // then
        assertEquals(COLLECTION_DTO_WITH_ID, savedCollectionDTO);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldThrowIllegalArgumentIfEndDateIsInThePast() {
        // given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(USERNAME);
        given(userRepository.findUserByUsername(USERNAME)).willReturn(Optional.of(USER));
        COLLECTION_DTO_WITHOUT_ID.setEndTime(YESTERDAY);

        // when + then
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> underTest.saveCollection(COLLECTION_DTO_WITHOUT_ID));
        assertEquals(COLLECTION_END_DATE_INVALID, exception.getMessage());
    }

    @Test
    void shouldUpdateCollectionIfUserIsOwner() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when
        CollectionDTO collectionDTO = underTest.updateCollection(1L, "new title", "new desc", TOMORROW);

        // then
        assertEquals("new title", collectionDTO.getTitle());
        assertEquals("new desc", collectionDTO.getDescription());
        assertEquals(TOMORROW, collectionDTO.getEndTime());
    }

    @Test
    void shouldUpdateCollectionWithEndTimeBeforeNow() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when
        CollectionDTO collectionDTO = underTest.updateCollection(1L, "", "", YESTERDAY);

        // then
        assertEquals(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.MINUTES),
                collectionDTO.getEndTime().truncatedTo(ChronoUnit.MINUTES));
    }

    @Test
    void shouldUpdateCollectionWithEndTimeEqualsNull() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when
        CollectionDTO collectionDTO = underTest.updateCollection(1L, "", "", null);

        // then
        assertNull(collectionDTO.getEndTime());
    }

    @Test
    void shouldThrowCollectionNotFoundOnUpdateCollection() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.empty());

        // when + then
        CollectionNotFoundException exception =
                assertThrows(CollectionNotFoundException.class, () -> underTest.updateCollection(1L, "new title", "new desc", TOMORROW));
        assertEquals(COLLECTION_NOT_FOUND, exception.getMessage());
    }

}