package arquivo.model;

import com.sun.istack.NotNull;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class IntegrationLog {

    public enum Status {
        /** Terminated with success */
        TS,
        /** Terminated with error */
        TE,
        /** Terminated with error, but will be retried again */
        TR
    }


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
    private String url;

    @NotNull
    private LocalDateTime eventTimestamp;

    @NotNull
    @Column(length = 40)
    private String origin;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 2)
    private Status status;

    @Column(columnDefinition = "text")
    private String inputData;

    @Column(columnDefinition = "text")
    private String outputData;

    public IntegrationLog(String url, LocalDateTime eventTimestamp, String origin, Status status, String inputData) {
        this.url = url;
        this.eventTimestamp = eventTimestamp;
        this.origin = origin;
        this.status = status;
        this.inputData = inputData;
    }

    public IntegrationLog(String url, LocalDateTime eventTimestamp, String origin, Status status, String inputData, String outputData) {
        this(url, eventTimestamp, origin, status, inputData);
        this.outputData = outputData;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getOutputData() {
        return outputData;
    }

    public void setOutputData(String outputData) {
        this.outputData = outputData;
    }
}
