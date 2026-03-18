package com.decade.practice.inbox.unit;

import com.decade.practice.inbox.application.ports.out.UserLookUp;
import com.decade.practice.inbox.domain.ConversationInfo;
import com.decade.practice.inbox.domain.Room;
import com.decade.practice.inbox.domain.services.ConversationInfoService;
import com.decade.practice.users.api.UserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ConversationInfoServiceTest {

      @Mock
      UserLookUp userLookUp;

      @InjectMocks
      ConversationInfoService conversationInfoService;

      @Test
      void givenRoomHas2RepresentativesIncludingMe_whenGettingThatRoomInfo_thenConversationNameIsTheOtherName() {
            UserInfo me = new UserInfo(
                      "teri",
                      "teri",
                      "teri.jpg",
                      UUID.randomUUID());
            UserInfo other = new UserInfo(
                      "other",
                      "other",
                      "other.jpg",
                      UUID.randomUUID());
            Room room = new Room("123", me.id(), null, null, Set.of(me.id(), other.id()));

            Mockito.when(userLookUp.lookUp(other.id())).thenReturn(other);

            ConversationInfo info = conversationInfoService.getInfo(me.id(), room);

            assertThat(info.name()).isEqualTo("other");
            assertThat(info.avatar()).isEqualTo("other.jpg");
      }


      @Test
      void givenRoomHas2RepresentativesNotIncludingMe_whenGettingThatRoomInfo_thenConversationNameIsTheOtherNames() {
            UserInfo me = new UserInfo(
                      "teri",
                      "teri",
                      "teri.jpg",
                      UUID.nameUUIDFromBytes("teri".getBytes()));
            UserInfo other1 = new UserInfo(
                      "other1",
                      "other1",
                      "other1.jpg",
                      UUID.nameUUIDFromBytes("other1".getBytes()));
            UserInfo other2 = new UserInfo(
                      "other2",
                      "other2",
                      "other2.jpg",
                      UUID.nameUUIDFromBytes("other2".getBytes()));
            Room room = new Room("123", me.id(), null, null, Set.of(other1.id(), other2.id(), me.id()));

            Mockito.when(userLookUp.lookUp(other1.id())).thenReturn(other1);
            Mockito.when(userLookUp.lookUp(other2.id())).thenReturn(other2);

            ConversationInfo info = conversationInfoService.getInfo(me.id(), room);

            assertThat(Set.of("other2, other1", "other1, other2")).contains(info.name());
      }
}