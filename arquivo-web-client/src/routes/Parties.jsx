import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const Parties = () => {
    const [partidos, setPosts] = useState([]);

    useEffect(() => {
        fetch("http://127.0.0.1:8082/entity/type/PARTIDO")
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);

    if (partidos.length) {
        return (
            <div>
                <h1>Partidos</h1>
                <div className="partidos">
                    {partidos.map((post) => (
                        <div className="post" key={post.id}>
                            <Link to={`/articles/${post.id}`}>{post.name}</Link>
                        </div>
                    ))}
                </div>
            </div>
        );
    }
};

export default Parties;