package arquivo.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class ArticleSearchEntityAssociation {

    @Id
    @GenericGenerator(
            name = "sequence-per-table",
            strategy = "enhanced-sequence",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "prefer_sequence_per_entity", value = "true")
            })
    @GeneratedValue(generator = "sequence-per-table")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    private SearchEntity searchEntity;

    private Double entityScore = 0.0;

    @org.hibernate.annotations.Type(type = "io.hypersistence.utils.hibernate.type.json.JsonBinaryType")
    @Column(columnDefinition = "jsonb")
    private JsonNode entityScoreDetails;

    public ArticleSearchEntityAssociation() {
    }

    public ArticleSearchEntityAssociation(Article article, SearchEntity searchEntity) {
        this.article = article;
        this.searchEntity = searchEntity;
        this.entityScore = 0.0;
        this.entityScoreDetails = null;
    }

    public ArticleSearchEntityAssociation(Article article, SearchEntity searchEntity, double entityScore, JsonNode entityScoreDetails) {
        this.article = article;
        this.searchEntity = searchEntity;
        this.entityScore = entityScore;
        this.entityScoreDetails = entityScoreDetails;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public SearchEntity getSearchEntity() {
        return searchEntity;
    }

    public void setSearchEntity(SearchEntity searchEntity) {
        this.searchEntity = searchEntity;
    }

    public JsonNode getEntityScoreDetails() {
        return entityScoreDetails;
    }

    public void setEntityScoreDetails(JsonNode entityScoreDetails) {
        this.entityScoreDetails = entityScoreDetails;
    }

    public Double getEntityScore() {
        return entityScore;
    }

    public void setEntityScore(Double entityScore) {
        this.entityScore = entityScore;
    }
}
