import { Link } from "react-router";
import "../Article.css";


function ArticlePreview({ item }) {

    return (
        <div class="collumn">
            <div class="head">
                <span class="headline hl3">
                <Link class="headline hl3" to={item.articleDetail.url}>{item.articleDetail.title}</Link>
                </span>
                <p>
                    <span class="headline hl4">{item.articleDetail.site.name}</span>
                    Relevância: {item.articleDetail.contextualScore*100 + item.articleDetail.summaryScore*10 }
                </p>
            </div>
            <p>
                {item.articleDetail.summary}
            </p>
        </div>
    );
}
export default ArticlePreview;