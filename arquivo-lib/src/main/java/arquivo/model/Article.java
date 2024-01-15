package arquivo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

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
    private String url;

    @Column(columnDefinition = "text")
    private String noFrameUrl;

    @Column(columnDefinition = "text")
    private String textUrl;

    @Column(columnDefinition = "text")
    private String metadataUrl;

    @Column(length = 50)
    private String digest;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "article_entity_association",
            joinColumns = {@JoinColumn(name = "article_id")},
            inverseJoinColumns = {@JoinColumn(name = "search_entity_id")}
    )
    private Set<SearchEntity> articleEntityAssociation;

    public Article() {

    }

    public Article(String digest, String url, String noFrameUrl, String textUrl, String metadataUrl, LocalDateTime date, Site site) {
        this.digest = digest;
        this.url = url;
        this.noFrameUrl = noFrameUrl;
        this.textUrl = textUrl;
        this.metadataUrl = metadataUrl;
        this.date = date;
        this.site = site;
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

    public Set<SearchEntity> getArticleEntityAssociation() {
        return articleEntityAssociation;
    }

    public void setArticleEntityAssociation(Set<SearchEntity> articleEntityAssociation) {
        this.articleEntityAssociation = articleEntityAssociation;
    }
}
