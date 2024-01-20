import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useParams } from "react-router";

const Entity = () => {
    const [entities, setPosts] = useState([]);
    const { type } = useParams();

    useEffect(() => {
        fetch("http://127.0.0.1:8082/entity/type/"+type)
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, [type]);

    if (entities.length) {
        return (
            <div>
                <h1>{type}</h1>
                <div>
                    {entities.map((entity) => (

                        <div class="box" key={entity.id}>
                            {console.log("teste  " + entity.imageUrl)}
                            <div class="box-top">
                                <img class="box-image" src={entity.imageUrl} alt={`${entity.name}`}></img>
                            <div class="title-flex">
                            <h3 class="box-title">{entity.name}</h3>
                            <p class="user-follow-info">{type}</p>
                            </div>
                            <p class="description">Whipped steamed roast cream beans macchiato skinny grinder café. Iced grinder go mocha steamed grounds cultivar panna aroma.</p>
                            </div>
                            <Link className="navbar-link" to={`/articles/${entity.id}`}>Artigos</Link>
                        </div>
                    ))}
                </div>
            </div>
        );
    }
};

export default Entity;