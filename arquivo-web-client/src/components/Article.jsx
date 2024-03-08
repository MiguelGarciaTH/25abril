import React from 'react';
import { useParams } from "react-router";
import { useEffect, useState } from "react";
import ArticleEntities from './ArticleEntities';
import { Link } from "react-router-dom";

function arr(array) {
    console.log(">>"+array);
    var myArray = JSON.parse(array);
    console.log(">"+myArray)
    for (let i = 0; i < myArray.length; i++) {
        console.log(myArray[i]);
        x += myArray[i] + "<br>";
    }
    //document.getElementById("my-div").innerHTML = x;
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
            <div class="subhead-2"> {item.title}</div>
            <div class="content"></div>
            <div class="collumn-2">
                <div class="head">
                    <span class="headline hl3">
                    </span>
                    <p>
                        <span class="headline hl4-2">{item.entityName}</span>
                        <br />
                        Score: {item.score}
                    </p>
                </div>

                {item.texts}
            </div>
            <br />
            <Link class="navbar-link" to={item.url}>Ver este artigo no Arquivo.pt</Link>

            <ArticleEntities articleId={aId} entityId={item.entityId} />
        </div>
    )


}
export default Article;