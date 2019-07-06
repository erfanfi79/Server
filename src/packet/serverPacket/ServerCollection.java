package packet.serverPacket;

import models.Collection;

public class ServerCollection extends ServerPacket {
    private boolean isShop = false;
    private Collection collection;
    private Collection shopCollection;

    public ServerCollection(Collection collection) {
        this.collection = collection;
        isShop = false;
    }

    public ServerCollection(Collection collection, Collection shopCollection) {
        this.shopCollection = shopCollection;
        this.collection = collection;
        isShop = true;
    }

    public Collection getCollection() {
        return collection;
    }

    public Collection getShopCollection() {
        return shopCollection;
    }

    public boolean getIsShop() {
        return isShop;
    }
}
