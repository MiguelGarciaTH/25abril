import React from 'react';
import "../Entity.css";

function Entity({ entityId, entityName, entityImage, entityBio }) {
    return (
        <div className="polaroid">
            {entityImage ? (
                <img src={entityImage} alt={`${entityName}`} />
            ) : (
                <div className="placeholder">No Image Available</div>
            )}
            <div className="label">{entityName}</div>
        </div>
    );
}

export default Entity;


