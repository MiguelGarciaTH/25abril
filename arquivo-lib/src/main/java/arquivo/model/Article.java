package arquivo.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NamedEntityGraph(name = "Article.searchEntities",
        attributeNodes = @NamedAttributeNode("searchEntities")
)
public class Article {

    @Id
    @GenericGenerator(
            name = "sequence-per-table",
            strategy = "enhanced-sequence",
            parameters = {
                    @Parameter(name = "prefer_sequence_per_entity", value = "true")
            })
    @GeneratedValue(generator = "sequence-per-table")
    private int id;

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.EAGER)
    private Site site;

    @Column(columnDefinition = "text")
    private String title;

    @Column(columnDefinition = "text")
    private String url;

    @Column(columnDefinition = "text")
    private String trimmedUrl;

    @Column(columnDefinition = "text")
    private String summary;

    private double textScore;

    @org.hibernate.annotations.Type(type = "io.hypersistence.utils.hibernate.type.json.JsonBinaryType")
    @Column(columnDefinition = "jsonb")
    private JsonNode textScoreDetails;

    private double summaryScore;

    @org.hibernate.annotations.Type(type = "io.hypersistence.utils.hibernate.type.json.JsonBinaryType")
    @Column(columnDefinition = "jsonb")
    private JsonNode summaryScoreDetails;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "article_search_entity_association",
            joinColumns = { @JoinColumn(name = "article_id") },
            inverseJoinColumns = { @JoinColumn(name = "search_entity_id") }
    )
    Set<SearchEntity> searchEntities;

    public Article() {

    }

    public Article(String title, String url, String trimmedUrl, LocalDateTime date, Site site, double textScore, JsonNode textScoreDetails) {
        this.url = url;
        this.trimmedUrl = trimmedUrl;
        this.date = date;
        this.site = site;
        this.title = title;
        this.summary = null;
        this.textScore = textScore;
        this.textScoreDetails = textScoreDetails;
        this.searchEntities = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTrimmedUrl() {
        return trimmedUrl;
    }

    public void setTrimmedUrl(String trimmedUrl) {
        this.trimmedUrl = trimmedUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public double getSummaryScore() {
        return summaryScore;
    }

    public void setSummaryScore(double summaryScore) {
        this.summaryScore = summaryScore;
    }

    public double getTextScore() {
        return textScore;
    }

    public void setTextScore(double textScore) {
        this.textScore = textScore;
    }

    public JsonNode getTextScoreDetails() {
        return textScoreDetails;
    }

    public void setTextScoreDetails(JsonNode textScoreDetails) {
        this.textScoreDetails = textScoreDetails;
    }

    public JsonNode getSummaryScoreDetails() {
        return summaryScoreDetails;
    }

    public void setSummaryScoreDetails(JsonNode summaryScoreDetails) {
        this.summaryScoreDetails = summaryScoreDetails;
    }

    public Set<SearchEntity> getSearchEntities() {
        return searchEntities;
    }

    public void setSearchEntities(Set<SearchEntity> searchEntities) {
        this.searchEntities = searchEntities;
    }
}
