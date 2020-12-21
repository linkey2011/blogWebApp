package blog.cser.blogWebApp.mapper;

import blog.cser.blogWebApp.entity.Post;
import blog.cser.blogWebApp.entity.PostDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@Mapper
public interface  PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);
    //@Mapping(source = "author", target = "author")
    PostDto PostToPostDto(Post post);
    List<PostDto> PostListToPostDtoList(List<Post> people);

}
