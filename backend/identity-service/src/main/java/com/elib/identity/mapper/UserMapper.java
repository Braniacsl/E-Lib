package com.elib.identity.mapper;

import com.elib.identity.dto.UserRequest;
import com.elib.identity.dto.UserResponse;
import com.elib.identity.dto.UserSummaryResponse;
import com.elib.identity.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserRequest request);

    UserResponse toResponse(User user);

    UserSummaryResponse toSummaryResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy =
        NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UserRequest request,
        @MappingTarget User user);
}
