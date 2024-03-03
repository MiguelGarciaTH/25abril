import React from 'react';
import { useParams } from "react-router";
import { useEffect, useState } from "react";

function trimText(text) {
    //const result = text[0].substring(0, 500);
    return text;
}


const Article = () => {

    const [item, setPosts] = useState([]);
    const { articleId, entityId } = useParams();
    const aId = articleId;
    const eId = entityId;

    useEffect(() => {
        fetch("http://127.0.0.1:8082/article/" + eId + "/" + aId)
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);



    return (
        <div class="news-paper-head">
            <div class="headerobjectswrapper">
                <header class="news-paper-header">{item.siteName}</header>
            </div>
            <div class="subhead"> {item.title}</div>
            <div class="content"></div>
            <div class="collumn">
                <div class="head">
                    <span class="headline hl3">
                    </span>
                    <p>
                        <span class="headline hl4">{item.entityName}</span>
                        Score: {item.score}
                    </p>
                </div>
                <p>
                    {trimText(item.texts)}
                </p>
            </div>
        </div>
    )


}
export default Article;