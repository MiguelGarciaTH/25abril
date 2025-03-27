package arquivo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


@Entity
public class RateLimiter {

    @Id
    @GenericGenerator(
            name = "sequence-per-table",
            strategy = "enhanced-sequence",
            parameters = {
                    @Parameter(name = "prefer_sequence_per_entity", value = "true")
            })
    @GeneratedValue(generator = "sequence-per-table")
    private int id;

    @Column(columnDefinition = "text")
    private String description;

    private int counter;

    private int counterLimit;

    private int sleepTime;

    private boolean locked;

    public RateLimiter() {

    }

    public RateLimiter(String description, int counter, int counterLimit) {
        this.description = description;
        this.counter = counter;
        this.counterLimit = counterLimit;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounterLimit() {
        return counterLimit;
    }

    public void setCounterLimit(int counterLimit) {
        this.counterLimit = counterLimit;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
