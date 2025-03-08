import React from 'react';
import "../Entity.css";

function Entity({ entityId, entityName, entityImage, entityBio }) {
    // Generate random overlay styles
    const getRandomOverlayStyle = () => {
        return {
            '--overlay-opacity': `${0.2 + Math.random() * 0.3}`,
            '--noise-opacity': `${0.05 + Math.random() * 0.15}`,
            '--spot1-x': `${Math.random() * 100}%`,
            '--spot1-y': `${Math.random() * 100}%`,
            '--spot2-x': `${Math.random() * 100}%`,
            '--spot2-y': `${Math.random() * 100}%`,
            '--scratch-angle': `${Math.random() * 90 - 45}deg`
        };
    };

    // Function to add random transformations to each letter
    const randomizeText = (text) => {
        // Split text into words and maintain spaces
        return text.split(' ').map((word, wordIndex) => {
            // Create randomized letters for each word
            const letters = word.split('').map((char, charIndex) => {
                const rotation = Math.random() * 2 - 1; // Reduced rotation range: -1 to 1 degrees
                const yOffset = Math.random() * 1 - 0.5; // Reduced offset range: -0.5 to 0.5 pixels
                const style = {
                    '--random-transform': `rotate(${rotation}deg)`,
                    '--random-offset': `${yOffset}px`
                };
                
                return (
                    <span key={`${wordIndex}-${charIndex}`} style={style}>
                        {char}
                    </span>
                );
            });

            // Return word with space after it (except for last word)
            return (
                <React.Fragment key={`word-${wordIndex}`}>
                    {letters}
                    {wordIndex < text.split(' ').length - 1 ? ' ' : ''}
                </React.Fragment>
            );
        });
    };

    return (
        <div className="polaroid" style={getRandomOverlayStyle()}>
            {entityImage ? (
                <img src={entityImage} alt={`${entityName}`} />
            ) : (
                <div className="placeholder">No Image Available</div>
            )}
            <div className="label">{randomizeText(entityName)}</div>
        </div>
    );
}

export default Entity;


