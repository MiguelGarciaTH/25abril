import React from "react";
import { useEffect, useState } from "react";

const Home = () => {

    const [quote, setPosts] = useState([]);

    useEffect(() => {
        fetch("http://127.0.0.1:8082/quotes")
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);

    return (
        <div>
            <h1>~</h1>
            <div className="quote-div">
                <h1></h1>
                <div className="quote">
                    <div dangerouslySetInnerHTML={{ __html: quote.text }} />
                </div>

                <div className="quote-author">
                    {quote.author}
                </div>
            </div>
        </div>
    )
}

export default Home;