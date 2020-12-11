package io.axoniq.labs.chat.commandmodel;

import java.util.Set;

@Aggregate
@Profile("command")
public class ChatRoom {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @AggregateIdentifier
    private String roomId;
    private String name;
    private Set<String> participants;

    public ChatRoom() {}

    @CommandHandler
    public ChatRoom(CreateRoomCommand cmd) {
        apply(new RoomCreatedEvent(cmd.getRoomId(), cmd.getName()));
    }

    @EventSourcingHandler
    public void on(RoomCreatedEvent evt) {
        roomId = evt.getRoomId();
        name = evt.getName();
        participants = new HashSet<String>();
    }

    @CommandHandler
    public void handler(JoinRoomCommand cmd) {
        String participant = cmd.getParticipant();
        if (!participants.contains(participant)) {
            apply(new ParticipantJoinedRoomEvent(cmd.getRoomId(), participant));
        }

    }

    @EventSourcingHandler
    public void on(ParticipantJoinedRoomEvent evt) {
        participants.add(evt.getParticipant());
    }

    @CommandHandler
    public void handler(LeaveRoomCommand cmd) {
        String participant = cmd.getParticipant();
        if (participants.contains(participant)) {
            apply(new ParticipantLeftRoomEvent(cmd.getRoomId(), participant));
        }
    }

    @EventSourcingHandler
    public void on(ParticipantLeftRoomEvent evt) {
        participants.remove(evt.getParticipant());
    }

    @CommandHandler
    public void handler(PostMessageCommand cmd) {
        String participant = cmd.getParticipant();
        if (participants.contains(participant)) {
            apply(new MessagePostedEvent(cmd.getRoomId(), participant, cmd.getMessage()));
        }
        else {
            throw new IllegalStateException("You cannot post messages unless you've joined the chat room");
        }
    }

    @EventSourcingHandler
    public void on(MessagePostedEvent evt) {
    }
}
