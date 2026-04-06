package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.in.FileCommand;
import com.decade.practice.inbox.application.ports.out.RoomEventRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.FileRoomEvent;
import com.decade.practice.inbox.domain.RoomEvent;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import com.decade.practice.resources.files.api.DownloadPathGenerator;
import org.springframework.stereotype.Service;

@Service
public class FilePlacement extends AbstractParticipantPlacement<FileCommand> {

      private final DownloadPathGenerator pathGenerator;

      public FilePlacement(DownloadPathGenerator pathGenerator, RoomEventRepository events, RoomRepository rooms, ChatEventMapper chatEventMapper) {
            super(events, rooms, chatEventMapper);
            this.pathGenerator = pathGenerator;
      }

      @Override
      protected RoomEvent newInstance(FileCommand participantCommand) {
            String uri = this.pathGenerator.generateDownload(participantCommand.getFile());
            return new FileRoomEvent(
                      participantCommand.getPostingId(),
                      participantCommand.getChatId(),
                      participantCommand.getSenderId(),
                      uri,
                      participantCommand.getFilename(),
                      participantCommand.getSize()
            );
      }
}
