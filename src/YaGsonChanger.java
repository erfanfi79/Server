import com.gilecode.yagson.*;
import packet.clientPacket.ClientPacket;
import packet.serverPacket.ServerPacket;

public class YaGsonChanger {

    public static ClientPacket readClientPacket(String object) {

        YaGsonBuilder yaGsonBuilder = new YaGsonBuilder();
        com.gilecode.yagson.YaGson yaGson = yaGsonBuilder.create();
        return yaGson.fromJson(object, ClientPacket.class);
    }

    public static ServerPacket readServerPocket(String object) {

        YaGsonBuilder yaGsonBuilder = new YaGsonBuilder();
        com.gilecode.yagson.YaGson yaGson = yaGsonBuilder.create();
        return yaGson.fromJson(object, ServerPacket.class);
    }

    public static String write(Object packet) {

        YaGsonBuilder yaGsonBuilder = new YaGsonBuilder();
        com.gilecode.yagson.YaGson yaGson = yaGsonBuilder.create();
        return yaGson.toJson(packet);
    }
}
