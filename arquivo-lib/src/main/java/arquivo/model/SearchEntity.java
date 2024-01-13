package arquivo.model;

import com.sun.istack.NotNull;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
public class SearchEntity {

    public enum Type {
        PARTIDO,
        POLITICO,
        ARTISTA,
        JORNAL,
        MOVIMENTO,
        EVENTO,
        LOCAL,
        PRISAO
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

    @Column(length = 255)
    private String name;

    @Column(length = 50)
    private String abbreviation;

    @Column(length = 200)
    private String keywords;

    @Column(columnDefinition = "text")
    private String aliases;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Type type;

    public SearchEntity(){

    }

    public SearchEntity(String name, String abbreviation, String aliases, Type type) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.aliases = aliases;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAliases() {
        return aliases;
    }

    public void setAliases(String aliases) {
        this.aliases = aliases;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
