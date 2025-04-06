import React from "react";
import { Link } from "react-router-dom";
import Header from "../components/Header";
import "../index.css";

const Architecture = () => {
    return (
        <div className="text-container">
            <Header isHome={false} />
            <div className="text">
                <h2>Arquitectura</h2>
                Este projecto tem como objectivo criar um arquivo digital sobre o Estado Novo e o 25 de Abril, 
                focando-se em artigos noticiosos, personalidades, eventos e locais relevantes desta época histórica.
                A arquitectura do projecto é composta por várias componentes especializadas, divididas em duas partes principais: 
                backend (Java) e frontend (React). Os dados são persistidos numa base de dados PostgreSQL e a comunicação 
                entre componentes é feita através do Apache Kafka.

                <br />
                <img src="arquitectura.png" alt="Architecture" className="architecture-image" />

                <h3>(1) arquivo-crawler</h3>
                Este componente é responsável pela recolha de dados de várias fontes externas. O projecto assenta em dois conceitos principais:
                entidades de pesquisa (pessoas, eventos, locais) e artigos noticiosos. Para cada entidade de pesquisa, recolhemos uma biografia, 
                uma fotografia e artigos noticiosos relacionados. Isto é feito através de três crawlers especializados.

                <div style={{ marginLeft: '20px', marginRight: '50px' }}>
                    <h4>arquivo-bio-crawler</h4>
                    Cada entidade de pesquisa tem uma biografia associada. Esta biografia permite ao utilizador do site saber quem é a entidade de pesquisa.
                    Este crawler utiliza a API da OpenAI para obter uma biografia tendo em conta o contexto Histórico do Estado Novo
                    e do 25 de Abril com as entidades de pesquisa.
                    <br />
                    <br />
                    <strong>Porquê IA?</strong>
                    <br />
                    Numa versão anterior do projecto, utilizámos o Wikipedia (DBPedia) para obter a biografia, no entanto,
                    os resultados não eram completos e a informação não estava abreviada. 
                    Muitas vezes os resultados tinham metacaracteres e não eram legíveis ou relevantes neste contexto. 
                    No entanto, a OpenIA, mesmo utilizando o <code>gpt-4-turbo</code> não é uma solução perfeita. 
                    Isto é, não conseguimos obter uma biografia completa e relevante para todas as entidades. 
                    Estas LLMs ainda têm algumas limitações e não conseguem obter informação relevante para todas as entidades. 
                    Em particular, tivemos maus resultados para PIDEs, o que indica que há pouca documentação sobre estas pessoas. 
                    Além disso, algumas das pessoas faleceram nos últimos anos e essas datas ainda não estão disponíveis na OpenIA. 
                    Portanto, foram adicionadas manualmente para garantir que a informação está correcta.
                </div>

                <div style={{ marginLeft: '20px', marginRight: '50px' }}>
                    <h4>arquivo-crawler</h4>
                    Este componente recolhe os dados do Arquivo.pt através da <Link to="https://github.com/arquivo/pwa-technologies/wiki/APIs">API</Link>.
                    Para cada uma das entidades de pesquisa e para cada site é criado um URL para fazer a recolha de artigos:
                    <br/>
                    <code>https://arquivo.pt/textsearch?q=ENTIDADE&prettyPrint=false&siteSearch=SITE&from=%s&to=%s&maxItems=500&type=html&dedupValue=1&dedupField=title&fields=title,linkToArchive,linkToExtractedText,linkToScreenshot</code>
                    Este URL vai retornar todos os resultados do Arquivo.pt entre a data inicial que o Arquivo.pt permite e o dia corrente. 
                    O próprio crawler itera sobre as várias páginas.
                    Este componente não faz nenhum tipo de processamento, apenas faz o crawling e envia os dados para o <code>arquivo-processor</code>.
                </div>

                <div style={{ marginLeft: '20px', marginRight: '50px' }}>
                    <h4>arquivo-image-crawler</h4>
                    Este componente recolhe fotografias para as entidades de pesquisa. 
                    A fonte de dados é o Arquivo.pt através da API de imagens. 
                    Para garantir que as imagens são relevantes, utilizámos vários critérios de selecção para garantir que das imagens devolvidas pelo Arquivo.pt escolhemos a mais 
                    correcta para a entidade de pesquisa em questão. 
                    Os dois critérios principais foram encontrar referências do nome da entidade nos campos da imagem: <code>pageTitle</code>, 
                    <code>imgAlt</code>, e <code>imgCaption</code>. 
                    Depois utilizámos um detector de caras da <code>google-api</code>. 
                    Estes foram os critérios para escolher a melhor imagem. Naturalmente, nem sempre conseguimos um match perfeito entre a imagem e a entidade de pesquisa. 
                    Portanto, algumas (mas muito poucas) imagens foram adicionadas manualmente para garantir que a imagem é relevante para a entidade de pesquisa 
                    (em particular para entidades que não são pessoas).

                    <br/>
                    A query utilizada foi a seguinte:
                    <br/>
                    <code>https://arquivo.pt/imagesearch?q=ENTIDADE&offset=0&maxItems=100&size=lg&prettyPrint=true&type=jpg,png&siteSearch=*.publico.pt,*.expresso.pt,*.tsf.pt,*.dn.pt,*.jn.pt,*.observador.pt"</code>
                </div>

                <h3>(2) arquivo-processor</h3>
                Este componente é o orquestrador principal do sistema. É responsável por:
                - Receber dados dos crawlers através do Kafka
                - Fazer uma primeira análise de relevância dos artigos usando palavras-chave contextuais
                - Distribuir o trabalho para os componentes especializados (arquivo-text e arquivo-image)
                - Gerir o estado do processamento de cada artigo
                
                O scoring inicial é feito através de expressões regulares que procuram termos relacionados com 
                o Estado Novo e o 25 de Abril, bem como referências às entidades de pesquisa. Apenas os artigos 
                que ultrapassem um threshold mínimo são enviados para processamento adicional.

                <h3>(3) arquivo-text</h3>
                Este componente utiliza IA para processar e analisar o conteúdo dos artigos. O processamento é feito em duas fases:
                1. Geração de um resumo do artigo usando o VertexAI (PaLM2)
                2. Análise de relevância do resumo em relação ao contexto histórico e às entidades associadas

                <br />
                <br />
                <strong>Porquê IA?</strong>
                <br />
                Na primeira versão deste projeto (2024), um dos problemas com que nos deparámos foi a dificuldade em limpar
                o texto "raw" disponível em <code>linkToExtractedText</code>. Este texto é muito grande e contém muitos
                metacaracteres e tem todo o texto já sem tags de HTML presente na página. Por exemplo, em sites de notícias existem
                muitas colunas com notícias mais pequenas, cabeçalhos, rodapés, etc.
                Isto trazia dois problemas: (1) Por vezes era difícil perceber se o texto era realmente relevante no contexto por haver
                muito "lixo" à volta; (2) a forma como apresentávamos o texto do artigo de forma a mostrar ao utilizador do que se tratava o artigo,
                não era legível.
                Desta forma, decidimos utilizar um modelo de linguagem para fazer o resumo do texto. Ao fazer um resumo semântico de um texto
                "com lixo" conseguimos ter um texto que é focado no que realmente é relevante, e depois fazer uma análise do contexto do projecto sobre
                esse texto reduzido e focado.
                <br />

                <h3>(4) arquivo-image</h3>
                Este componente é responsável por fazer o download e processamento das imagens dos artigos.
                Recebe mensagens Kafka do arquivo-processor com os metadados do artigo e o URL da imagem (campo linkToScreenshot).
                As imagens são processadas, redimensionadas e optimizadas antes de serem armazenadas.
                Esta abordagem assíncrona permite-nos:
                - Reduzir a carga no Arquivo.pt
                - Optimizar o espaço em disco
                - Processar apenas imagens de artigos relevantes

                <h3>(5) arquivo-rest</h3>
                Este componente é responsável por fornecer uma API REST para disponibilizar os dados processados.
                Esta API é utilizada pela aplicação web.

                <h3>(6) arquivo-web</h3>
                Este componente é a aplicação web que suporta esta website. É uma aplicação React que utiliza o framework Vite.
            </div>
        </div>
    )
}

export default Architecture;