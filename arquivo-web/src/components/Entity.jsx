import "../Entity.css"; // Optional for custom styling

function Entity({ entityId, entityName, entityImage, entityBio }) {
    return (
        <div className="polaroid">
            <img src={entityImage} alt={`${entityName}`} />
            <div className="label">{entityName}</div>
        </div>
    );
}

export default Entity;