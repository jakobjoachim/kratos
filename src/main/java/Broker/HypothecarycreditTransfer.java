package Broker;

public class HypothecarycreditTransfer {

    private boolean oldHypo;
    private boolean newHypo;
    private Place place;

    HypothecarycreditTransfer(Place thePlace, boolean theNewHypo) {
        place = thePlace;
        oldHypo = place.isHypothecarycredit();
        newHypo = theNewHypo;
    }

    public boolean commit() {
        place.setHypothecarycredit(newHypo);
        return true;
    }

    public boolean rollback() {
        place.setHypothecarycredit(oldHypo);
        return true;
    }
}
