import { Link } from "react-router-dom";
import ArticlesCounter from "./ArticlesCounter";

function Entity({ entityId, entityName, entityImage, entityBio }) {

    return (
        <table class="entity-table" border={0}>
            <tr>
                <td colSpan={2}>
                    <h3>{entityName}</h3>
                </td>
            </tr>
            <tr>
                <td class="entity-thumb">
                    <img src={entityImage} alt={`${entityName}`}></img>
                </td>
                <td>
                    <div class="entity-description">{entityBio}</div>
                </td>
            </tr>
            <tr>
                <td colSpan={2}>
                    <Link className="entity-button" to={`/articles/${entityId}/${entityName}`}>Artigos(<ArticlesCounter entityId={entityId} />)</Link>
                </td>
            </tr>
        </table>
    );
};

export default Entity;

