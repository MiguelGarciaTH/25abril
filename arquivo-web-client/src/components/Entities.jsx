import { useEffect, useState } from "react";
import { useParams } from "react-router";
import Entity from "./Entity";

const Entities = () => {
    const [entities, setPosts] = useState([]);
    const { type } = useParams();

    useEffect(() => {
        fetch("http://127.0.0.1:8082/entity/type/" + type)
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, [type]);


    if (entities.length) {
        return (
            <div>
                <h1>{type}</h1>
                <div class="text">{getDescription(type)}</div>
                <ul>
                    {entities.map((entity) => (
                        <Entity entityId={entity.id} entityName={entity.name} entityBio={entity.biography} entityImage={entity.imageUrl} />
                    ))}
                </ul>
            </div>
        )
    }
}

function getDescription(type) {
    let description;
    switch (type) {
        case "ARTISTAS":
            description = "Nesta página listamos vários artistas que estiveram ligados à revolução de Abril."
                + " Alguns são os chamados canta-autores da chamada música de intervençao. Outros são pintores ou poetas.";
                break;
        case "POLITICOS":
            description = "Nesta página listamos vários actores que de uma forma partidária ou executiva tiveram acção política no antes,"
                + " durante e no pós 25 de Abril";
            break;
        case "MOVIMENTOS":
            description = "Nesta página listamos vários actores que de uma forma partidária ou executiva tiveram acção política no antes,"
                + " durante e no pós 25 de Abril";
            break;
        case "LOCAIS":
            description = "Nesta página listamos vários actores que de uma forma partidária ou executiva tiveram acção política no antes,"
                + " durante e no pós 25 de Abril";
            break;
        case "EVENTOS":
            description = "Nesta página listamos vários actores que de uma forma partidária ou executiva tiveram acção política no antes,"
                + " durante e no pós 25 de Abril";
            break;
        case "PARTIDOS":
            description = "Nesta página listamos vários actores que de uma forma partidária ou executiva tiveram acção política no antes,"
                + " durante e no pós 25 de Abril";
            break;
    }

    return (
        description
    );
}

export default Entities;