import { Link } from "react-router-dom";
import ArticlesCounter from "./ArticlesCounter";

function Entity({ entityId, entityName, entityImage, entityBio }) {

    return (
        <li class="member co-funder">
            <div class="thumb">
                <img src={entityImage} alt={`${entityName}`}></img>
            </div>
            <div class="description">
                <h3>{entityName}</h3>
                <p>{entityBio}</p><br />
                <Link className="link-button" to={`/articles/${entityId}/${entityName}`}>Artigos (<ArticlesCounter entityId={entityId} />) </Link>
            </div>
        </li>
    );

};

export default Entity;