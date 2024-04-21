import React from 'react';
import { useParams } from "react-router";
import { useEffect, useState } from "react";
import ArticleEntities from './ArticleEntities';
import { Link } from "react-router-dom";

function myFunction(myArray) {
    if (myArray != undefined) {
        var initI= 1;
        var splitted = myArray.split(".");
        for (let i = initI; i <= splitted.length; i++) {
            document.getElementById("text-div")
                .innerHTML += splitted[i] + "<br/><br/>"
        }
    }

}

const Article = () => {

    const [item, setPosts] = useState([]);
    const { articleId, entityId } = useParams();
    const aId = articleId;
    const eId = entityId;

    useEffect(() => {
        fetch(import.meta.env.VITE_REST_URL+"/article/" + eId + "/" + aId)
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);


    const myArray = item.texts;
    myFunction(myArray);

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
                        Relevância: {item.score}
                    </p>
                </div>
                <div id="text-div" class="text2">

                </div>
            </div>
            <br />
            <Link class="navbar-link" to={item.url}>Ver este artigo no Arquivo.pt</Link>

            <ArticleEntities articleId={aId} entityId={item.entityId} />
        </div>
    )


}
export default Article;