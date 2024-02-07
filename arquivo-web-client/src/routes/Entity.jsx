import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useParams } from "react-router";
import ArticlesCounter from "./ArticlesCounter";

const Entity = () => {
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
                <ul class="team">
                    {entities.map((entity) => (
                        <li class="member co-funder">
                            <div class="thumb">
                                <img src={entity.imageUrl} alt={`${entity.name}`}></img>
                            </div>
                            <div class="description">
                                <h3>{entity.name}</h3>
                                <p>{entity.biography}</p><br />
                                <Link className="link-button" to={`/articles/${entity.id}/${entity.name}`}>Artigos (<ArticlesCounter entityId={entity.id}/>) </Link> 
                                
                            </div>
                        </li>
                    ))}
                </ul>
            </div>
        )
    }
}

export default Entity;