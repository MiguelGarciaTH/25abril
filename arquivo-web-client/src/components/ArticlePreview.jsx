import { Link } from "react-router-dom";

function trimText(text) {
    //const result = text[0].substring(0, 500);
    return text;
    
}

function ArticlePreview({ siteId, siteName, postTitle, postUrl, postScore, postText }) {

    return (
        <div class="collumn">
            <div class="head">
                <span class="headline hl3">
                    <Link className="headline hl3" to={`${postUrl}`}>{postTitle}</Link>
                </span>
                <p>
                    <span class="headline hl4">fonte {siteName}</span>
                    Score: {postScore}
                </p>
            </div>
            <p>
                {trimText(postText)}
           </p>
           </div>
    );

};

export default ArticlePreview;



