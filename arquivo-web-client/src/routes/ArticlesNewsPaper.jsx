import { useEffect, useState } from "react";
import { useParams } from "react-router";
import Article from "../components/Article";

const ArticlesNewsPaper = () => {
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
            <div class="news-paper-head">
                <div class="headerobjectswrapper">
                    <header>{entityName}</header>
                </div>
                <div class="subhead">York, MA - Thursday August 30, 1978 - Seven Pages</div>
                <div class="content">
                    <div class="collumns">
                        {articles.map((post) => (<Article siteId={post.siteId} siteName={post.siteName} postTitle={post.title} postUrl={post.url} postScore={post.score} postText={post.text} />))}
                    </div>
                </div>
            </div>
        );
    }
};

export default ArticlesNewsPaper;