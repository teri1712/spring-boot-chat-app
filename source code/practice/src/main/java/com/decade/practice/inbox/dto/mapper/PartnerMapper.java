package com.decade.practice.inbox.dto.mapper;


import com.decade.practice.inbox.dto.PartnerResponse;
import com.decade.practice.users.api.UserInfo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.Map;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PartnerMapper {

      PartnerResponse map(UserInfo userInfo);
      
      default UserInfo resolveUser(UUID senderId, @Context Map<UUID, UserInfo> userMap) {
            return userMap.get(senderId);
      }
}
