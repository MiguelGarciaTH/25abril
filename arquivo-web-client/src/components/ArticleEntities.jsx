import { useEffect, useState } from "react";
import EntityThumb from "./EntityThumb";

const ArticleEntities = ({articleId, entityId}) => {
    const [entities, setPosts] = useState([]);

    useEffect(() => {
        fetch(import.meta.env.VITE_REST_URL +"/article/" + articleId+"/entities")
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);


    if (entities.length > 1) {
        return (
            <div>
                <div class="article-h1">Outras entidades mencionadas neste artigo:</div>
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