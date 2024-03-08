import { useEffect, useState } from "react";
import EntityThumb from "./EntityThumb";

const ArticleEntities = ({articleId, entityId}) => {
    const [entities, setPosts] = useState([]);

    useEffect(() => {
        fetch("http://127.0.0.1:8082/article/" + articleId+"/entities")
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);


    if (entities.length) {
        return (
            <div>
                <h1>Outras entidades mencionadas neste artigo:</h1>
                <ul>
                    {entities.map((entity) => (
                        <EntityThumb currentEntityId={entityId} entityId={entity.id} entityName={entity.name} entityImage={entity.imageUrl}/>
                    ))}
                </ul>
            </div>
        )
    }
}

export default ArticleEntities;