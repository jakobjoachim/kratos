package Broker;

class PlaceTransfer {
    private String oldOwner;
    private String newOwner;
    private Place place;

    PlaceTransfer(String to, Place thePlace){
        place = thePlace;
        oldOwner = place.getOwner();
        newOwner = to;
    }

    boolean rollback() {
        place.setOwner(oldOwner);
        return true;
    }

    boolean commit() {
        place.setOwner(newOwner);
        return true;
    }
}
