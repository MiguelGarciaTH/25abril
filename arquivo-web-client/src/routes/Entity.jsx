import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useParams } from "react-router";

const Entity = () => {
    const [entity, setPosts] = useState([]);
    const { type } = useParams();

    useEffect(() => {
        fetch("http://127.0.0.1:8082/entity/type/"+type)
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, [type]);

    if (entity.length) {
        return (
            <div>
                <h1>{type}</h1>
                <div>
                    {entity.map((post) => (
                        <div className="post" key={post.id}>
                            <Link className="navbar-link" to={`/articles/${post.id}`}>{post.name}</Link>
                        </div>
                    ))}
                </div>
            </div>
        );
    }
};

export default Entity;