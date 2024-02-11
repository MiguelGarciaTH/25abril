import { useEffect, useState } from "react";
import { useParams } from "react-router";
import Article from "../components/Article";

const Articles = () => {
    const [articles, setPosts] = useState([]);
    const { id, name } = useParams();
    const entityId = id;
    const entityName = name;

    useEffect(() => {
        fetch("http://127.0.0.1:8082/article/" + entityId)
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);

    if (articles.length) {
        return (
            <div>
                <h1>Artigos sobre {entityName}</h1>
                <div className="articles">
                    {articles.map((post) => (
                        <Article siteId={post.site.id} postTitle={post.title} postUrl={post.url} postScore={post.score} />
                    ))}
                </div>
            </div>
        );
    }else{
        return (
            <div className="space-top">
                Não foram econtrados artigos sobre {entityName}
            </div>
        )
    }
};

export default Articles;