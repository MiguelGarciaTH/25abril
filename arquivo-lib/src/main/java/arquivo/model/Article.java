package arquivo.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
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
    private String originalTitle;

    @Column(columnDefinition = "text")
    private String url;

    @Column(columnDefinition = "text")
    private String noFrameUrl;

    @Column(columnDefinition = "text")
    private String textUrl;

    @Column(columnDefinition = "text")
    private String metadataUrl;

    @Column(length = 50)
    private String digest;

    @Column(columnDefinition = "text")
    private String text;

    public Article() {

    }

    public Article(String digest, String title, String originalTitle, String url, String noFrameUrl, String textUrl,  String text, String metadataUrl, LocalDateTime date, Site site) {
        this.digest = digest;
        this.url = url;
        this.noFrameUrl = noFrameUrl;
        this.textUrl = textUrl;
        this.metadataUrl = metadataUrl;
        this.date = date;
        this.site = site;
        this.title = title;
        this.originalTitle = originalTitle;
        this.text = text;
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

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
