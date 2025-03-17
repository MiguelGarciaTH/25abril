import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import "../Entity.css";

function Entity({ entityId, entityName, entityImage, entityBio }) {
    const [showBio, setShowBio] = useState(false);

    return (
        <Link 
            to={`/articles/${entityId}/${entityName}/entity`}
            className="polaroid-wrapper"
            onMouseEnter={() => setShowBio(true)}
            onMouseLeave={() => setShowBio(false)}
        >
            <div className="polaroid">
                {entityImage ? (
                    <img src={entityImage} alt={entityName} />
                ) : (
                    <div className="placeholder">No Image Available</div>
                )}
                <div className="label">{entityName}</div>
            </div>
            
            {showBio && entityBio && (
                <div className={`bio-popup ${showBio ? 'show' : ''}`}>
                    <div className="bio-content">
                        {entityBio}
                    </div>
                </div>
            )}
        </Link>
    );
}

export default Entity;


