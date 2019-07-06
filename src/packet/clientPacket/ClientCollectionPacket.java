package packet.clientPacket;

import models.Collection;

public class ClientCollectionPacket extends ClientPacket {
    private Collection myCollection;

    public ClientCollectionPacket(Collection myCollection) {
        this.myCollection = myCollection;
    }

    public Collection getMyCollection() {
        return myCollection;
    }
}
