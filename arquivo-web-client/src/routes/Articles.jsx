import { useEffect, useState } from "react";
import { useParams } from "react-router";
import { Link } from "react-router-dom";
import observador from '../images/observador.png';
import dn from '../images/dn.png';
import publico from '../images/publico.png';

/*
1	Publico
2	Diário de Noticias
3	Jornal de Noticias
4	Expresso
5	Observador
6	Sic Noticias
7	TSF
*/
function getSiteImage(siteId) {
    let imageSrc;
    switch (siteId) {
        case 1: imageSrc = publico; break;
        case 2: imageSrc = dn; break; 
        case 5: imageSrc = observador;break;
        default: imageSrc = publico;
    }
    console.log(">>" + imageSrc)

    return (
        <img src={imageSrc} />
    );
}

const Articles = () => {
    const [articles, setPosts] = useState([]);
    const { id } = useParams();
    const userId = id;

    useEffect(() => {
        fetch("http://127.0.0.1:8082/article/" + userId)
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);

    if (articles.length) {
        return (
            <div>
                <h1>Artigos</h1>
                <div className="articles">
                    {articles.map((post) => (
                        <main class="l-card">
                            <section class="l-card__text">
                                <p>{post.title}</p>
                            </section>
                            <section class="l-card__user">
                                <div class="l-card__userImage">
                                    {getSiteImage(post.site.id)}
                                </div>
                                <div class="l-card__userInfo">
                                    <Link to={`${post.url}`}>Artigo</Link>
                                </div>
                            </section>
                        </main>
                    ))}
                </div>
            </div>
        );
    }
};

export default Articles;