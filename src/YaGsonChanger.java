import com.gilecode.yagson.*;
import packet.clientPacket.ClientPacket;
import packet.serverPacket.ServerPacket;

public class YaGsonChanger {

    private static YaGsonBuilder yaGsonBuilder = new YaGsonBuilder();
    private static com.gilecode.yagson.YaGson yaGson = yaGsonBuilder.create();

    public static ClientPacket readClientPacket(String object) {
        return yaGson.fromJson(object, ClientPacket.class);
    }

    public static ServerPacket readServerPocket(String object) {
        return yaGson.fromJson(object, ServerPacket.class);
    }

    public static String write(Object packet) {
        return yaGson.toJson(packet);
    }
}
