package com.elib.catalog.mapper;

import com.elib.catalog.dto.BookRequest;
import com.elib.catalog.dto.BookResponse;
import com.elib.catalog.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {
    Book toEntity(BookRequest request);
    BookResponse toResponse(Book book);

    void updateEntityFromRequest(BookRequest request, @MappingTarget Book book);
}