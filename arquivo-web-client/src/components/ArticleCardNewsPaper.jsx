import { Link } from "react-router-dom";

function trimText(text) {
    const result = text.substring(560, 1000);
    return result;
    
}

function ArticleCardNewsPaper({ siteId, siteName, postTitle, postUrl, postScore, imageUrl, postText }) {

    return (
        <div class="collumn">
            <div class="head">
                <span class="headline hl3">
                    <Link className="headline hl3" to={`${postUrl}`}>{postTitle}</Link>
                </span>
                <p>
                    <span class="headline hl4">fonte {siteName}</span>
                </p>
            </div>
            <p>
                {trimText(postText)}
           </p>
           </div>
    );

};

export default ArticleCardNewsPaper;



