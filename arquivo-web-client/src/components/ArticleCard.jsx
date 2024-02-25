import { Link } from "react-router-dom";


function ArticleCard({ siteId, postTitle, postUrl, postScore, imageUrl }) {

    return (
        <main class="l-card">
            <section class="l-card__text">
                <div class="l-card__userImage">{imageUrl}</div>
                <Link className="navbar-link" to={`${postUrl}`}>{postTitle}</Link>
            </section>
            <section class="l-card__user">
                <div class="l-card__userInfo">
                    Score: {postScore}
                </div>
            </section>
        </main>
    );

};

export default ArticleCard;