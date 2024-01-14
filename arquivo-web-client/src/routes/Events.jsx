import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const Events = () => {
    const [events, setPosts] = useState([]);

    useEffect(() => {
        fetch("http://127.0.0.1:8082/entity/type/EVENTO")
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);

    if (events.length) {
        return (
            <div>
                <h1>Eventos</h1>
                <div className="eventos">
                    {events.map((post) => (
                        <div className="post" key={post.id}>
                            <Link to={`/articles/${post.id}`}>{post.name}</Link>
                        </div>
                    ))}
                </div>
            </div>
        );
    }
};

export default Events;