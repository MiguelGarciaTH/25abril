import { useEffect, useState } from "react";
import { useParams } from "react-router";
import Entity from "../components/Entity";

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
                <ul>
                    {entities.map((entity) => (
                        <Entity entityId={entity.id} entityName={entity.name} entityBio={entity.biography} entityImage={entity.imageUrl}/>
                    ))}
                </ul>
            </div>
        )
    }
}

export default Entities;