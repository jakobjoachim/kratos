package Tools;

public class Mutex{
    private String idHoldingLock;
    private boolean lock = false;

    public boolean lock(String id) {
        if (!lock){
            idHoldingLock = id;
            lock = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean unlock() {
        lock = false;
        idHoldingLock = null;
        return true;
    }

    public String getLockHolder(){
        return idHoldingLock;
    }
}
