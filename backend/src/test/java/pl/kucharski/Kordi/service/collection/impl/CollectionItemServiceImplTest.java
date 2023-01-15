package pl.kucharski.Kordi.service.collection.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.exception.CollectionItemException;
import pl.kucharski.Kordi.exception.CollectionItemNotFoundException;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection_item.CollectionItemDTO;
import pl.kucharski.Kordi.model.collection_item.CollectionItemMapper;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItemDTO;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItemMapper;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItemMapperImpl;
import pl.kucharski.Kordi.repository.CollectionRepository;
import pl.kucharski.Kordi.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.kucharski.Kordi.CollectionData.USER;
import static pl.kucharski.Kordi.CollectionData.USERNAME;
import static pl.kucharski.Kordi.CollectionData.createCollectionWithId;
import static pl.kucharski.Kordi.CollectionData.createItemDTOWithId;
import static pl.kucharski.Kordi.CollectionData.createSecondItemDTOWithId;
import static pl.kucharski.Kordi.CollectionData.createSubmittedItemDTO;
import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_ITEM_CURRENT_BIGGER_THAN_MAX;
import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_ITEM_NOT_FOUND;
import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
@Transactional
class CollectionItemServiceImplTest {

    private final CollectionItemMapper itemMapper = Mappers.getMapper(CollectionItemMapper.class);
    private final SubmittedItemMapper submittedItemMapper = new SubmittedItemMapperImpl();
    private static Collection COLLECTION_WITH_ID;
    private static CollectionItemDTO COLLECTION_ITEM_DTO;
    private static CollectionItemDTO COLLECTION_ITEM_V2_DTO;
    private static SubmittedItemDTO ITEM_TO_SUBMIT;

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CollectionItemServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new CollectionItemServiceImpl(collectionRepository, userRepository, itemMapper, submittedItemMapper);

        COLLECTION_WITH_ID = createCollectionWithId();
        COLLECTION_ITEM_DTO = createItemDTOWithId();
        COLLECTION_ITEM_V2_DTO = createSecondItemDTOWithId();
        ITEM_TO_SUBMIT = createSubmittedItemDTO();
    }

    @Test
    void shouldAddCollectionItem() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when
        underTest.addCollectionItem(1L, COLLECTION_ITEM_DTO);
        Collection foundCollection = collectionRepository.findById(1L).orElse(null);

        // then
        assertNotNull(foundCollection);
        assertEquals(1, foundCollection.getItems().size());
        assertEquals("item", foundCollection.getItems().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({"2, 10", "10, 10", "0, 5", "0, 12"})
    void shouldUpdateCollectionItem(int newCurrent, int newMax) {
        // given
        COLLECTION_WITH_ID.addItem(itemMapper.mapToCollectionItem(COLLECTION_ITEM_DTO));
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when
        CollectionItemDTO updatedItem = underTest.updateCollectionItem(1L, 1L, newCurrent, newMax);

        // then
        assertEquals(newCurrent, updatedItem.getCurrentAmount());
        assertEquals(newMax, updatedItem.getMaxAmount());
    }

    @Test
    void shouldThrowCollectionItemExceptionOnUpdateWithWrongParameters() {
        // given
        COLLECTION_WITH_ID.addItem(itemMapper.mapToCollectionItem(COLLECTION_ITEM_DTO));
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when + then
        CollectionItemException exception =
                assertThrows(CollectionItemException.class,
                        () -> underTest.updateCollectionItem(1L, 1L, 5, 4));
        assertEquals(COLLECTION_ITEM_CURRENT_BIGGER_THAN_MAX, exception.getMessage());
    }

    @Test
    void shouldThrowCollectionNotFoundOnUpdateCollectionItem() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.empty());

        // when + then
        CollectionNotFoundException exception =
                assertThrows(CollectionNotFoundException.class,
                        () -> underTest.updateCollectionItem(1L, 1L, 1, 10));
        assertEquals(COLLECTION_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldThrowCollectionItemNotFoundOnUpdateCollectionItem() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when + then
        CollectionItemNotFoundException exception =
                assertThrows(CollectionItemNotFoundException.class,
                        () -> underTest.updateCollectionItem(1L, 1L, 1, 10));
        assertEquals(COLLECTION_ITEM_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldSubmitItem() {
        // given
        mockSecurityContextHolder();
        COLLECTION_WITH_ID.addItem(itemMapper.mapToCollectionItem(COLLECTION_ITEM_DTO));
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));
        given(userRepository.findUserByUsername(USER.getUsername())).willReturn(Optional.of(USER));
        assertEquals(3, COLLECTION_WITH_ID.getItems().get(0).getCurrentAmount());
        given(userRepository.findUserByUsername(USER.getUsername())).willReturn(Optional.of(USER));

        // when
        CollectionItemDTO itemDTO =
                underTest.submitItem(COLLECTION_WITH_ID.getId(), COLLECTION_ITEM_DTO.getId(), ITEM_TO_SUBMIT);

        // then
        assertEquals(4, itemDTO.getCurrentAmount());
    }

    @Test
    void shouldSubmitItems() {
        // given
        mockSecurityContextHolder();
        COLLECTION_WITH_ID.addItem(itemMapper.mapToCollectionItem(COLLECTION_ITEM_DTO));
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));
        given(userRepository.findUserByUsername(USER.getUsername())).willReturn(Optional.of(USER));
        assertEquals(3, COLLECTION_WITH_ID.getItems().get(0).getCurrentAmount());
        given(userRepository.findUserByUsername(USER.getUsername())).willReturn(Optional.of(USER));
        List<SubmittedItemDTO> itemsToSubmit = List.of(ITEM_TO_SUBMIT, ITEM_TO_SUBMIT);

        // when
        List<CollectionItemDTO> itemsDTO =
                underTest.submitItems(COLLECTION_WITH_ID.getId(), itemsToSubmit);

        // then
        assertEquals(2, itemsDTO.size());
        assertEquals(5, itemsDTO.get(1).getCurrentAmount());
    }

    @Test
    void shouldThrowCollectionItemExceptionWhenCurrentAmountEqualsMaxOnSubmitItem() {
        // given
        mockSecurityContextHolder();
        COLLECTION_ITEM_DTO.setCurrentAmount(10);
        COLLECTION_WITH_ID.addItem(itemMapper.mapToCollectionItem(COLLECTION_ITEM_DTO));
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));
        assertEquals(10, COLLECTION_WITH_ID.getItems().get(0).getCurrentAmount());
        given(userRepository.findUserByUsername(USER.getUsername())).willReturn(Optional.of(USER));

        // when + then
        CollectionItemException exception =
                assertThrows(CollectionItemException.class,
                        () -> underTest.submitItem(COLLECTION_WITH_ID.getId(), COLLECTION_ITEM_DTO.getId(), ITEM_TO_SUBMIT));
        assertEquals(COLLECTION_ITEM_CURRENT_BIGGER_THAN_MAX, exception.getMessage());
    }

    @Test
    void shouldThrowCollectionNotFoundOnSubmitItem() {
        // given
        mockSecurityContextHolder();
        given(collectionRepository.findById(1L)).willReturn(Optional.empty());
        given(userRepository.findUserByUsername(USER.getUsername())).willReturn(Optional.of(USER));

        // when + then
        CollectionNotFoundException exception =
                assertThrows(CollectionNotFoundException.class,
                        () -> underTest.submitItem(COLLECTION_WITH_ID.getId(), COLLECTION_ITEM_DTO.getId(), ITEM_TO_SUBMIT));
        assertEquals(COLLECTION_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldThrowCollectionItemNotFoundOnSubmitItem() {
        // given
        mockSecurityContextHolder();
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));
        given(userRepository.findUserByUsername(USER.getUsername())).willReturn(Optional.of(USER));

        // when + then
        CollectionItemNotFoundException exception =
                assertThrows(CollectionItemNotFoundException.class,
                        () -> underTest.submitItem(COLLECTION_WITH_ID.getId(), COLLECTION_ITEM_DTO.getId(), ITEM_TO_SUBMIT));
        assertEquals(COLLECTION_ITEM_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldGetAllSubmittedItems() {
        // given
        COLLECTION_WITH_ID.addItem(itemMapper.mapToCollectionItem(COLLECTION_ITEM_DTO));
        assertEquals(0, COLLECTION_WITH_ID.getSubmittedItems().size());
        COLLECTION_WITH_ID.addSubmittedItem(submittedItemMapper.mapToSubmittedItem(ITEM_TO_SUBMIT));
        COLLECTION_WITH_ID.addSubmittedItem(submittedItemMapper.mapToSubmittedItem(ITEM_TO_SUBMIT));
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when
        List<SubmittedItemDTO> submittedItems = underTest.getSubmittedItems(1L);

        // then
        assertEquals(2, submittedItems.size());
    }

    @Test
    void shouldGetAllSubmittedItemsForSpecificItem() {
        // given
        COLLECTION_WITH_ID.addItem(itemMapper.mapToCollectionItem(COLLECTION_ITEM_DTO));
        COLLECTION_WITH_ID.addItem(itemMapper.mapToCollectionItem(COLLECTION_ITEM_V2_DTO));
        assertEquals(0, COLLECTION_WITH_ID.getSubmittedItems().size());
        COLLECTION_WITH_ID.addSubmittedItem(submittedItemMapper.mapToSubmittedItem(ITEM_TO_SUBMIT));
        COLLECTION_WITH_ID.addSubmittedItem(submittedItemMapper.mapToSubmittedItem(ITEM_TO_SUBMIT));
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when
        List<SubmittedItemDTO> submittedItemsForFirstItem = underTest.getSubmittedItemsForSpecificItem(1L, 1L);
        List<SubmittedItemDTO> submittedItemsForSecondItem = underTest.getSubmittedItemsForSpecificItem(1L, 2L);

        // then
        assertEquals(2, submittedItemsForFirstItem.size());
        assertEquals(0, submittedItemsForSecondItem.size());
    }

    private void mockSecurityContextHolder() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(USERNAME);
    }
}