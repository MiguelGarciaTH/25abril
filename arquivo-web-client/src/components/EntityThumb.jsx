import { Link } from "react-router-dom";

function EntityThumb({ entityId, entityName, entityImage }) {

    return (
        <div class="item-2">
            <Link to={`/articles/${entityId}/${entityName}`}>
                <div class="polaroid"><img src={entityImage} alt={`${entityName}`}></img>
                    <div class="caption">{entityName}</div>
                </div>
            </Link>
        </div>
    );
};

export default EntityThumb;

