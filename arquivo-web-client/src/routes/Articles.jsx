import { useEffect, useState } from "react";
import { useParams } from "react-router";

const Articles = () => {
    const [articles, setPosts] = useState([]);
    const {id} = useParams();
    const userId = id;

    useEffect(() => {
        fetch("http://127.0.0.1:8082/article/" + userId)
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);

    if (articles.length) {
        return (
            <div>
                <h1>Artigos</h1>
                <div className="articles">
                    {articles.map((post) => (
                        <div className="post" key={post.id}>
                            <div> {post.url} </div>
                        </div>
                    ))}
                </div>
            </div>
        );
    }
};

export default Articles;