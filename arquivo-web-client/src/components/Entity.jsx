import { Link } from "react-router-dom";
import ArticlesCounter from "./ArticlesCounter";

function Entity({ entityId, entityName, entityImage, entityBio }) {

    return (
        <div class="paper">
            <div class="paper-content">
                <h1>{entityName}</h1>
                <textarea>{entityBio}</textarea>
                <Link class="paper-button" to={`/articles/${entityId}/${entityName}`}>Artigos(<ArticlesCounter entityId={entityId} />)</Link>
            </div>
            <div class="item">
                <div class="polaroid"><img src={entityImage} alt={`${entityName}`}></img></div>
            </div>
        </div>

    );
};

export default Entity;

