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

    @ManyToOne(fetch = FetchType.EAGER)
    private Article article;

    @ManyToOne(fetch = FetchType.EAGER)
    private SearchEntity searchEntity;

    private int score;

    @org.hibernate.annotations.Type(type = "io.hypersistence.utils.hibernate.type.json.JsonBinaryType")
    @Column(columnDefinition = "jsonb")
    private JsonNode individualScore;

    public ArticleSearchEntityAssociation() {
    }

    public ArticleSearchEntityAssociation(Article article, SearchEntity searchEntity) {
        this.article = article;
        this.searchEntity = searchEntity;
    }

    public ArticleSearchEntityAssociation(Article article, SearchEntity searchEntity, int score, JsonNode individualScore) {
        this.article = article;
        this.searchEntity = searchEntity;
        this.score = score;
        this.individualScore = individualScore;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public JsonNode getIndividualScore() {
        return individualScore;
    }

    public void setIndividualScore(JsonNode individualScore) {
        this.individualScore = individualScore;
    }
}
