package packet.clientPacket;

import java.io.Serializable;

public enum ClientEnum implements Serializable {

    CHAT_ROOM,
    LEADER_BOARD,
    MATCH_HISTORY,
    SHOP,
    COLLECTION,
    GET_MONEY,
    SAVE,
    CANCEL_WAITING_FOR_MULTI_PLAYER_GAME,
    EXIT_CHAT_ROOM,
}
