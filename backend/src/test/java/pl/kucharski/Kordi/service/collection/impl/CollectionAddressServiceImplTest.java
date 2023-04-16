package pl.kucharski.Kordi.service.collection.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.exception.AddressAlreadyExistsInCollectionException;
import pl.kucharski.Kordi.exception.AddressNotFoundException;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.model.address.AddressMapper;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.repository.CollectionRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static pl.kucharski.Kordi.CollectionData.ADDRESS_DTO;
import static pl.kucharski.Kordi.CollectionData.ADDRESS_DTO_V2;
import static pl.kucharski.Kordi.CollectionData.createCollectionWithId;

@ExtendWith(MockitoExtension.class)
@Transactional
class CollectionAddressServiceImplTest {

    private final AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);

    private static Collection COLLECTION_WITH_ID;

    @Mock
    private CollectionRepository collectionRepository;

    @InjectMocks
    private CollectionAddressServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new CollectionAddressServiceImpl(addressMapper, collectionRepository);
        COLLECTION_WITH_ID = createCollectionWithId();
    }

    @Test
    void shouldAddCollectionAddress() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when
        underTest.addCollectionAddress(1L, ADDRESS_DTO_V2);
        Collection foundCollection = collectionRepository.findById(1L).orElse(null);

        // then
        assertNotNull(foundCollection);
        assertEquals(2, foundCollection.getAddresses().size());
        assertEquals("Warszawa", foundCollection.getAddresses().get(0).getCity());
        assertEquals("Krakow", foundCollection.getAddresses().get(1).getCity());
    }

    @Test
    void shouldThrowAddressAlreadyExistsExceptionOnAddCollectionAddress() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when + then
        assertThrows(AddressAlreadyExistsInCollectionException.class, () -> underTest.addCollectionAddress(1L, ADDRESS_DTO));
    }

    @Test
    void shouldRemoveCollectionAddress() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when
        underTest.removeCollectionAddress(1L, 1L);
        Collection foundCollection = collectionRepository.findById(1L).orElse(null);

        // then
        assertNotNull(foundCollection);
        assertEquals(0, foundCollection.getAddresses().size());
    }

    @Test
    void shouldThrowCollectionNotFoundOnAddCollectionAddress() {
        // given
        given(collectionRepository.findById(5L)).willReturn(Optional.empty());

        // when + then
        assertThrows(CollectionNotFoundException.class, () -> underTest.addCollectionAddress(5L, ADDRESS_DTO));
    }

    @Test
    void shouldThrowCollectionNotFoundOnRemoveCollectionAddress() {
        // given
        given(collectionRepository.findById(5L)).willReturn(Optional.empty());

        // when + then
        assertThrows(CollectionNotFoundException.class, () -> underTest.removeCollectionAddress(5L, 1L));
    }

    @Test
    void shouldThrowAddressNotFoundOnRemoveCollectionAddress() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when + then
        assertThrows(AddressNotFoundException.class, () -> underTest.removeCollectionAddress(1L, 5L));
    }

}