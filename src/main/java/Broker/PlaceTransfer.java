package Broker;

public class PlaceTransfer {
    private String oldOwner;
    private String newOwner;
    private Place place;

    public PlaceTransfer(String to, Place thePlace){
        place = thePlace;
        oldOwner = place.getOwner();
        newOwner = to;
    }

    public boolean rollback() {
        place.setOwner(oldOwner);
        return true;
    }

    public boolean commit() {
        place.setOwner(newOwner);
        return true;
    }
}
