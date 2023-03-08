package pl.kucharski.Kordi.model.collection;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.kucharski.Kordi.enums.CollectionStatus;
import pl.kucharski.Kordi.model.address.AddressMapper;
import pl.kucharski.Kordi.model.collection_item.CollectionItemMapper;

import java.util.Objects;

import static pl.kucharski.Kordi.enums.CollectionStatus.IN_PROGRESS;

@Mapper(uses = {AddressMapper.class, CollectionItemMapper.class}, componentModel = "spring",
injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CollectionMapper {

    @Mapping(target = "userFirstname", source = "user.firstName")
    @Mapping(target = "userLastname", source = "user.lastName")
    CollectionDTO mapToCollectionDTO(Collection collection);

    @Mapping(target = "donates", source = "donates", defaultValue = "0L")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    Collection mapToCollection(CollectionDTO collection);

    @Named("mapStatus")
    static CollectionStatus mapStatus(CollectionStatus collectionStatus) {
        return Objects.requireNonNullElse(collectionStatus, IN_PROGRESS);
    }

}
