import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const Politicians = () => {
    const [politicos, setPosts] = useState([]);

    useEffect(() => {
        fetch("http://127.0.0.1:8082/entity/type/POLITICO")
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);

    if (politicos.length) {
        return (
            <div>
                <h1>Politicos</h1>
                <div className="politicos">
                    {politicos.map((post) => (
                        <div className="post" key={post.id}>
                            <Link to={`/articles/${post.id}`}>{post.name}</Link>
                        </div>
                    ))}
                </div>
            </div>
        );
    }
};

export default Politicians;