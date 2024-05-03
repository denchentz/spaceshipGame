import java.util.HashMap;
import java.util.Set;

public abstract class ObjectPool<T> {
    private long expirationTime;
  
    private HashMap<T, Long> locked, unlocked;

    public abstract void setUpNewCordinator(T o);
  
    protected abstract T create();

    public ObjectPool() {
      expirationTime = 30000; // 30 seconds
      locked = new HashMap<T, Long>();
      unlocked = new HashMap<T, Long>();
    }
  
    
    public void clear(){
        locked.clear();
        unlocked.clear();
    };
    
    public synchronized T checkOut() {
      long now = System.currentTimeMillis();
      T t;
      if (unlocked.size() > 0) {
        Set<T> e = unlocked.keySet();
        for (T t2 : e) {
            if ((now - unlocked.get(t2)) > expirationTime) {
                unlocked.remove(t2);
                t2 = null;
            } else{
                unlocked.remove(t2);
                locked.put(t2, now);
                setUpNewCordinator(t2);
                return t2;
            }
           
        }
      }
      // no objects available, create a new one
      t = create();
      locked.put(t, now);
      return (t);
    }
  
    public synchronized void checkIn(T t) {
      locked.remove(t);
      unlocked.put(t, System.currentTimeMillis());
    }
  }