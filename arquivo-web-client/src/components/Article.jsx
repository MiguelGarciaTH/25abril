import { Link } from "react-router-dom";

import observador from '../images/observador.png';
import dn from '../images/dn.png';
import publico from '../images/publico.png';
import jn from '../images/jn.png';
import expresso from '../images/expresso.jpg';
import sic_noticias from '../images/sic_noticias.png';
import tsf from '../images/tsf.png';

/*
1	Publico
2	Diário de Noticias
3	Jornal de Noticias
4	Expresso
5	Observador
6	Sic Noticias
7   Visão
8	TSF
*/
function getSiteImage(siteId) {
    let imageSrc;
    switch (siteId) {
        case 1: imageSrc = publico; break;
        case 2: imageSrc = dn; break;
        case 3: imageSrc = jn; break;
        case 4: imageSrc = expresso; break;
        case 5: imageSrc = observador; break;
        case 6: imageSrc = sic_noticias; break;
        case 7: imageSrc = tsf; break;
        default: imageSrc = publico;
    }

    return (
        <img src={imageSrc} />
    );
}

function Article({ siteId, postTitle, postUrl, postScore }) {

    return (
        <main class="l-card">
            <section class="l-card__text">
                <div class="l-card__userImage">{getSiteImage(siteId)}</div>
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

export default Article;