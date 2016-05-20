package Tools;

import java.util.concurrent.locks.Lock;

public class Mutex{
    private String idHoldingLock;
    private Lock lock;

    public boolean lock(String id) {
        if (lock.tryLock()){
            idHoldingLock = id;
            return true;
        } else {
            return false;
        }
    }

    public boolean unlock() {
        lock.unlock();
        idHoldingLock = null;
        return true;
    }

    public String getLockHolder(){
        return idHoldingLock;
    }
}
