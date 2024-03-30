import { useEffect, useState } from "react";
import { useParams } from "react-router";
import Entity from "./Entity";

const Entities = () => {

    const { type } = useParams();

    let entities = getEntities(type);

    if (entities.length) {
        return (
            <div>
                    {entities.map((entity) => (
                        <Entity entityId={entity.id} entityName={entity.name} entityBio={entity.biography} entityImage={entity.imageUrl} />
                    ))}
            </div>
        )
    }
}

function getEntities(type) {
    const [entities, setEntities] = useState([]);

    useEffect(() => {
        setEntities([])
        async function fetchData() {
            const result = await fetch(import.meta.env.VITE_REST_URL + "/entity/type/" + type);
            const body = await result.json();
            setEntities(body);
        }
        fetchData();
    }, [type]);

    return entities;
}

export default Entities;