import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useParams } from "react-router";

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
                                <p>Chris is a front-end developer and designer. He writes a bunch of HTML, CSS, and JavaScript and shakes the pom-poms for CodePen.</p><br />
                                <Link className="card__btn" to={`/articles/${entity.id}`}>Artigos</Link>
                            </div>
                        </li>
                    ))};
                </ul>
            </div>
        );
    }
};

export default Entity;