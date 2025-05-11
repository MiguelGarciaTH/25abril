package arquivo.model;

import jakarta.persistence.*;
import org.codehaus.commons.nullanalysis.NotNull;

@Entity
public class SearchEntity {

    public enum Type {
        CAPITÃES,
        ARTISTAS,
        JUNTA_DE_SALVAÇÃO_NACIONAL,
        JORNALISTAS,
        MOVIMENTOS,
        EVENTOS,
        LOCAIS,
        OPRESSORES,
        RESISTENTES,
        POLÍTICOS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private boolean done;


    public SearchEntity() {

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

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
