package pl.kucharski.Kordi.model.collection;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pl.kucharski.Kordi.model.address.AddressMapper;
import pl.kucharski.Kordi.model.collection_item.CollectionItemMapper;

@Mapper(uses = {AddressMapper.class, CollectionItemMapper.class}, componentModel = "spring",
injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CollectionMapper {

    CollectionMapper INSTANCE = Mappers.getMapper(CollectionMapper.class);

    @Mapping(target = "userFirstname", source = "user.firstName")
    @Mapping(target = "userLastname", source = "user.lastName")
    CollectionDTO mapToCollectionDTO(Collection collection);

    Collection mapToCollection(CollectionDTO collection);

}
