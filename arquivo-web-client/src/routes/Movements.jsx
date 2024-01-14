import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const Movements = () => {
    const [movments, setPosts] = useState([]);

    useEffect(() => {
        fetch("http://127.0.0.1:8082/entity/type/MOVIMENTO")
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);

    if (movments.length) {
        return (
            <div>
                <h1>Movimentos</h1>
                <div className="movimentos">
                    {movments.map((post) => (
                        <div className="post" key={post.id}>
                            <Link to={`/articles/${post.id}`}>{post.name}</Link>
                        </div>
                    ))}
                </div>
            </div>
        );
    }
};

export default Movements;