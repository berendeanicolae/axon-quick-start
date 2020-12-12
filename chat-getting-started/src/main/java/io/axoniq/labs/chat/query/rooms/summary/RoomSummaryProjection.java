package io.axoniq.labs.chat.query.rooms.summary;

import io.axoniq.labs.chat.coreapi.*;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomSummaryProjection {

    private final RoomSummaryRepository roomSummaryRepository;

    public RoomSummaryProjection(RoomSummaryRepository roomSummaryRepository) {
        this.roomSummaryRepository = roomSummaryRepository;
    }

    @EventHandler
    public void on(CreateRoomCommand evt) {
        roomSummaryRepository.save(new RoomSummary(evt.getRoomId(), evt.getName()));
    }

    @EventHandler
    public void on(JoinRoomCommand evt) {
        roomSummaryRepository.getOne(evt.getRoomId()).addParticipant();
    }

    @EventHandler
    public void on(LeaveRoomCommand evt) {
        roomSummaryRepository.getOne(evt.getRoomId()).removeParticipant();
    }

    @QueryHandler
    public List<RoomSummary> query(AllRoomsQuery query) {
        return roomSummaryRepository.findAll();
    }
}
