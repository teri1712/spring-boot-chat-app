package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.in.ImageCommand;
import com.decade.practice.inbox.application.ports.out.RoomEventRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.ImageRoomEvent;
import com.decade.practice.inbox.domain.RoomEvent;
import com.decade.practice.inbox.dto.mapper.ChatEventMapper;
import com.decade.practice.resources.files.api.DownloadPathGenerator;
import org.springframework.stereotype.Service;

@Service
public class ImagePlacement extends AbstractParticipantPlacement<ImageCommand> {

      private final DownloadPathGenerator pathGenerator;

      public ImagePlacement(DownloadPathGenerator pathGenerator, RoomEventRepository events, RoomRepository rooms, ChatEventMapper chatEventMapper) {
            super(events, rooms, chatEventMapper);
            this.pathGenerator = pathGenerator;
      }

      @Override
      protected RoomEvent newInstance(ImageCommand participantCommand) {
            String uri = this.pathGenerator.generateDownload(participantCommand.getFile());
            return new ImageRoomEvent(
                      participantCommand.getPostingId(),
                      participantCommand.getChatId(),
                      participantCommand.getSenderId(),
                      uri,
                      participantCommand.getWidth(),
                      participantCommand.getHeight(),
                      participantCommand.getFilename(),
                      participantCommand.getFormat()
            );
      }
}
