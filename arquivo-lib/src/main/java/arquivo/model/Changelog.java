package arquivo.model;

import jakarta.persistence.*;
import org.codehaus.commons.nullanalysis.NotNull;

import java.time.LocalDateTime;

@Entity
public class Changelog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private LocalDateTime fromTimestamp;

    @NotNull
    private LocalDateTime toTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    private SearchEntity searchEntity;

    private int totalEntries;

    Changelog(){}

    public Changelog(LocalDateTime fromTimestamp, LocalDateTime toTimestamp, Site site, SearchEntity searchEntity) {
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.site = site;
        this.searchEntity = searchEntity;
    }

    public Changelog(LocalDateTime fromTimestamp, LocalDateTime toTimestamp, Site site, SearchEntity searchEntity, int totalEntries) {
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.site = site;
        this.searchEntity = searchEntity;
        this.totalEntries = totalEntries;
    }

    public LocalDateTime getFromTimestamp() {
        return fromTimestamp;
    }

    public void setFromTimestamp(LocalDateTime fromTimestamp) {
        this.fromTimestamp = fromTimestamp;
    }

    public LocalDateTime getToTimestamp() {
        return toTimestamp;
    }

    public void setToTimestamp(LocalDateTime toTimestamp) {
        this.toTimestamp = toTimestamp;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public SearchEntity getSearchEntity() {
        return searchEntity;
    }

    public void setSearchEntity(SearchEntity searchEntity) {
        this.searchEntity = searchEntity;
    }

    public int getTotalEntries() {
        return totalEntries;
    }

    public void setTotalEntries(int totalEntries) {
        this.totalEntries = totalEntries;
    }
}
