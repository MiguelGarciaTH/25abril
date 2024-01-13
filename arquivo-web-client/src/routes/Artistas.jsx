import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const Artistas = () => {
    const [artistas, setPosts] = useState([]);

    useEffect(() => {
        fetch("http://127.0.0.1:8082/entity/type/ARTISTA")
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);

    if (artistas.length) {
        return (
            <div>
                <h1>Artistas</h1>
                <div className="artistas">
                    {artistas.map((post) => (
                        <div className="post" key={post.id}>
                            <Link to={`/articles/${post.id}`}>{post.name}</Link>
                        </div>
                    ))}
                </div>
            </div>
        );
    }
};

export default Artistas;