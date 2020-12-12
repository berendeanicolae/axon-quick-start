package io.axoniq.labs.chat.query.rooms.participants;

import io.axoniq.labs.chat.coreapi.*;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class RoomParticipantsProjection {

    private final RoomParticipantsRepository repository;

    public RoomParticipantsProjection(RoomParticipantsRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void on(ParticipantJoinedRoomEvent evt) {
        repository.save(new RoomParticipant(evt.getRoomId(), evt.getParticipant()));
    }

    @EventHandler
    public void on(ParticipantLeftRoomEvent evt) {
        repository.deleteByParticipantAndRoomId(evt.getParticipant(), evt.getRoomId());
    }

    @QueryHandler
    public List<String> query(RoomParticipantsQuery query) {
        return repository.findRoomParticipantsByRoomId(query.getRoomId()).stream().map(RoomParticipant::getParticipant).sorted().collect(toList());
    }
}
