import React, { useEffect, useState } from 'react';

const EmptyResults = () => {
    const [quote, setQuote] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchQuote = async () => {
            try {
                const response = await fetch(`${import.meta.env.VITE_REST_URL}/quotes`);
                if (!response.ok) {
                    throw new Error('Failed to fetch quote');
                }
                const data = await response.json();
                setQuote(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchQuote();
    }, []);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div className="empty-results" style={{
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            height: '100%',
            textAlign: 'left',
            marginTop: '5%',
            fontSize: '1vw',
            fontWeight: '500',
            fontFamily: 'Mona Sans Condensed, sans-serif',
            width: '100%',

        }}>
            <h1>Sem resultados</h1>
            {quote && (
                <blockquote style={{
                    margin: '2em 0',
                    padding: '1em',
                    maxWidth: '100%',
                    lineHeight: '1.6'
                }}>
                    <p style={{ 
                        whiteSpace: 'pre-wrap',
                        margin: '0 0 1em 0'
                    }} dangerouslySetInnerHTML={{ 
                        __html: quote.text 
                    }} />
                    {quote.author && <footer style={{ marginTop: '1em' }}>- {quote.author}</footer>}
                </blockquote>
            )}
            <img src="/android-chrome-512x512.png" style={{ width: '200px', height: '200px' }}/>
        </div>
    );
};

export default EmptyResults;
