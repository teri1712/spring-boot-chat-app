package com.decade.practice.inbox.unit;

import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.domain.ConversationInfo;
import com.decade.practice.inbox.domain.Partner;
import com.decade.practice.inbox.domain.Room;
import com.decade.practice.inbox.domain.services.ConversationInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ConversationInfoServiceTest {

      @InjectMocks
      ConversationInfoService conversationInfoService;

      @Test
      void givenRoomHas2RepresentativesIncludingMe_whenGettingThatRoomInfo_thenConversationNameIsTheOtherName() {
            Partner me = new Partner(
                      UUID.randomUUID(),
                      "teri",
                      "teri.jpg"
            );
            Partner other = new Partner(
                      UUID.randomUUID(),
                      "other",
                      "other.jpg"
            );
            Room room = new Room("123", me.id(), null, null, Set.of(me.id(), other.id()));


            PartnerLookUp lookUp = Mockito.mock(PartnerLookUp.class);
            Mockito.when(lookUp.lookUp(other.id())).thenReturn(other);

            ConversationInfo info = conversationInfoService.getInfo(me.id(), room, lookUp);

            assertThat(info.name()).isEqualTo("other");
            assertThat(info.avatar()).isEqualTo("other.jpg");
      }


      @Test
      void givenRoomHas2RepresentativesNotIncludingMe_whenGettingThatRoomInfo_thenConversationNameIsTheOtherNames() {
            Partner me = new Partner(
                      UUID.nameUUIDFromBytes("teri".getBytes()),
                      "teri",
                      "teri.jpg"
            );
            Partner other1 = new Partner(
                      UUID.nameUUIDFromBytes("other1".getBytes()),
                      "other1",
                      "other1.jpg"
            );
            Partner other2 = new Partner(
                      UUID.nameUUIDFromBytes("other2".getBytes()),
                      "other2",
                      "other2.jpg"
            );
            Room room = new Room("123", me.id(), null, null, Set.of(other1.id(), other2.id(), me.id()));

            PartnerLookUp lookUp = Mockito.mock(PartnerLookUp.class);

            Mockito.when(lookUp.lookUp(other1.id())).thenReturn(other1);
            Mockito.when(lookUp.lookUp(other2.id())).thenReturn(other2);

            ConversationInfo info = conversationInfoService.getInfo(me.id(), room, lookUp);

            assertThat(Set.of("other2, other1", "other1, other2")).contains(info.name());
      }
}