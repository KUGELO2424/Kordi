package pl.kucharski.Kordi.model.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {


    Comment mapToComment(CreateCommentDTO comment);

    @Mapping(target = "username", source = "user.username")
    CommentDTO mapToCommentDTO(Comment comment);

}
