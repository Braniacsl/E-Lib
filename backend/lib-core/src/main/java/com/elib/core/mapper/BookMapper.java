package com.elib.core.mapper;

import com.elib.core.dto.BookRequest;
import com.elib.core.dto.BookResponse;
import com.elib.core.entity.Book;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {
    Book toEntity(BookRequest request);
    BookResponse toResponse(Book book);

    void updateEntityFromRequest(BookRequest request, @MappingTarget Book book);
}