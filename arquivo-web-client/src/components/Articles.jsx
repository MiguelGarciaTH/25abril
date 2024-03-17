import { useEffect, useState } from "react";
import { useParams } from "react-router";
import ArticlePreview from "./ArticlePreview"

const Articles = () => {
    const [articles, setPosts] = useState([]);
    const { id, name } = useParams();
    const entityId = id;
    const entityName = name;

    useEffect(() => {
        fetch(import.meta.env.VITE_REST_URL + "/article/" + entityId)
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);

    if (articles.length) {
        return (
            
            <div class="news-paper-head">
                <div class="headerobjectswrapper">
                    <header class="news-paper-header">{randomName()}</header>
                </div>
                <div class="subhead"> {entityName}</div>
                <div class="content">
                    <div class="collumns">
                        {articles.map((post) => (<ArticlePreview item={post} />))}
                    </div>
                </div>
            </div>
        );
    }
};

function randomName() {
    let idx = Math.floor(Math.random() * (4) + 1);
    let name;
    switch (idx) {
        case 1: name = "Diário Popular"; break;
        case 2: name = "República"; break;
        case 3: name = "A Mosca"; break;
        case 4: name = "Seara Nova"; break;
    }

    return (
        name
    );
}

export default Articles;