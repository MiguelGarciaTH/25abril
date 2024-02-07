package arquivo.model;

import com.sun.istack.NotNull;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
public class SearchEntity {

    public enum Type {
        PARTIDOS,
        POLITICOS,
        ARTISTAS,
        JORNAIS,
        MOVIMENTOS,
        EVENTOS,
        LOCAIS
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

    @Column(columnDefinition = "text")
    private String aliases;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Type type;

    @Column(columnDefinition = "text")
    private String imageUrl;

    @Column(length = 50)
    private String birthDate;

    @Column(length = 50)
    private String deathDate;

    @Column(columnDefinition = "text")
    private String biography;

    public SearchEntity(){

    }

    public SearchEntity(String name, String aliases, Type type) {
        this.name = name;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
