import { Link } from "react-router-dom";

function trimText(text) {
    //const result = text[0].substring(0, 500);
        
    //var splitted = text.split("."); 
    //console.log(splitted);
    //var initI = 1;
    //var textFinal ="";
    //for (let i = initI; i <= splitted.length; i++) {
    //    textFinal.concat(".<br/>", splitted[i]);
   // }
    return text;
}


function ArticlePreview({ item }) {

    return (
        <div class="collumn">
            <div class="head">
                <span class="headline hl3">
                    <Link class="headline hl3" to={`/article/${item.articleId}/${item.entityId}`}>{item.title}</Link>
                </span>
                <p>
                    <span class="headline hl4">{item.siteName}</span>
                    Relevância: {item.score}
                </p>
            </div>
            <p>
                {trimText(item.texts)}
            </p>
        </div>
    );
};

export default ArticlePreview;



