package pl.kucharski.Kordi.service.collection.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.kucharski.Kordi.CollectionData.PAGING;
import static pl.kucharski.Kordi.CollectionData.TOMORROW;
import static pl.kucharski.Kordi.CollectionData.USER;
import static pl.kucharski.Kordi.CollectionData.USERNAME;
import static pl.kucharski.Kordi.CollectionData.YESTERDAY;
import static pl.kucharski.Kordi.CollectionData.createCollectionDTOWithId;
import static pl.kucharski.Kordi.CollectionData.createCollectionDTOWithoutId;
import static pl.kucharski.Kordi.CollectionData.createCollectionWithId;
import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
@Transactional
class CollectionServiceImplTest {

    private final AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);
    private final CollectionItemMapper itemMapper = Mappers.getMapper(CollectionItemMapper.class);
    private final CollectionMapper collectionMapper = new CollectionMapperImpl(addressMapper, itemMapper);
    
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
        underTest = new CollectionServiceImpl(collectionRepository, userRepository, collectionMapper);
        COLLECTION_WITH_ID = createCollectionWithId();
        COLLECTION_DTO_WITH_ID = createCollectionDTOWithId();
        COLLECTION_DTO_WITHOUT_ID = createCollectionDTOWithoutId();
    }

    @BeforeAll
    static void setUpContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
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
                .findByTitleContaining("Zbiórka", PAGING))
                .willReturn(List.of(COLLECTION_WITH_ID));

        // when
        List<CollectionDTO> collections = underTest.getCollectionsWithFiltering("Zbiórka", "", "", "", PAGING);

        // then
        Assertions.assertEquals(1, collections.size());
        assertEquals(COLLECTION_DTO_WITH_ID, collections.get(0));
    }

    @Test
    void shouldReturnCollectionsByTitleAndAddress() {
        // given
        given(collectionRepository
                .findByTitleAndAddress("Zbiórka", "Warszawa", "", PAGING))
                .willReturn(List.of(COLLECTION_WITH_ID));

        // when
        List<CollectionDTO> collections = underTest.getCollectionsWithFiltering("Zbiórka", "Warszawa", "", "", PAGING);

        // then
        Assertions.assertEquals(1, collections.size());
        assertEquals(COLLECTION_DTO_WITH_ID, collections.get(0));
    }

    @Test
    void shouldReturnCollectionsByTitleAndAddressAndItemName() {
        // given
        given(collectionRepository
                .findByTitleAndAddressAndItem("Zbiórka", "Warszawa", "", "Buty", PAGING))
                .willReturn(List.of(COLLECTION_WITH_ID));

        // when
        List<CollectionDTO> collections = underTest.getCollectionsWithFiltering("Zbiórka", "Warszawa", "", "Buty", PAGING);

        // then
        Assertions.assertEquals(1, collections.size());
        assertEquals(COLLECTION_DTO_WITH_ID, collections.get(0));
    }

    @Test
    void shouldReturnCollectionsByAddress() {
        // given
        given(collectionRepository
                .findByTitleAndAddress("", "Warszawa", "",  PAGING))
                .willReturn(List.of(COLLECTION_WITH_ID));

        // when
        List<CollectionDTO> collections = underTest.getCollectionsWithFiltering("", "Warszawa", "", "", PAGING);

        // then
        Assertions.assertEquals(1, collections.size());
        assertEquals(COLLECTION_DTO_WITH_ID, collections.get(0));
    }

    @Test
    void shouldReturnCollectionsByItemName() {
        // given
        given(collectionRepository
                .findByTitleAndAddressAndItem("", "", "", "Buty", PAGING))
                .willReturn(List.of(COLLECTION_WITH_ID));

        // when
        List<CollectionDTO> collections = underTest.getCollectionsWithFiltering("", "", "", "Buty", PAGING);

        // then
        Assertions.assertEquals(1, collections.size());
        assertEquals(COLLECTION_DTO_WITH_ID, collections.get(0));
    }

    @Test
    void shouldReturnEmptyCollectionOnFiltering() {
        // when
        List<CollectionDTO> collections = underTest.getCollectionsWithFiltering("Zbiórka", "", "", "", PAGING);

        // then
        Assertions.assertEquals(0, collections.size());
    }

    @Test
    void shouldSaveCollection() {
        // given
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
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                collectionDTO.getEndTime().truncatedTo(ChronoUnit.MINUTES));
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