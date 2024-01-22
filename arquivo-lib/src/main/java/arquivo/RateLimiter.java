package arquivo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimiter {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimiter.class);

    private int counter;
    private int limit;
    private long pauseTimeMilis;

    public RateLimiter(int limit, long pauseTimeMilis) {
        this.limit = limit;
        this.counter = 0;
        this.pauseTimeMilis = pauseTimeMilis;
    }

    public void increment() {
        counter++;
        if (counter == limit) {
            counter = 0;
            try {
                LOG.info("Reached limit {}, will sleep for {} mins", limit, (pauseTimeMilis / 1000) / 60);
                Thread.sleep(this.pauseTimeMilis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
