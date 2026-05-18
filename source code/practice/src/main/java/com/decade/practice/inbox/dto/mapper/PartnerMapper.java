package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.domain.Partner;
import com.decade.practice.inbox.dto.PartnerResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PartnerMapper {

    default PartnerResponse toPartner(UUID id, @Context PartnerLookUp lookUp) {
        if (id == null) return null;
        Partner partner = lookUp.lookUp(id);
        return partner == null ? null : new PartnerResponse(partner.id(), partner.name(), partner.avatar());
    }

}
