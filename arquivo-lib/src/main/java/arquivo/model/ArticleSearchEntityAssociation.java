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

    public ArticleSearchEntityAssociation() {
    }

    public ArticleSearchEntityAssociation(Article article, SearchEntity searchEntity) {
        this.article = article;
        this.searchEntity = searchEntity;
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
}
