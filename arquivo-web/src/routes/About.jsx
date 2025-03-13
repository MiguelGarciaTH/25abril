import React from "react";
import { Link } from "react-router-dom";
import Header from "../components/Header";
import "../index.css";

const About = () => {
    return (
        <div className="text-container">
            <Header isHome={false} />
            <div class="text">
                <h2>Motivação</h2>
                <strong>O 25 de Abril de 1974 encerrou um periodo 48 anos em que Portugal viveu em ditadura (7 anos de Ditudura Militar seguidos de 41 anos de Estado Novo), dando lugar ao regime democrático que prevalece até aos nossos dias. </strong>
                É por isso, o evento mais importante do século XX na História portuguesa.


                <br /><br />
                <strong>O principal objectivo deste projecto é promover a memória do que foi a divulgação do maior acontecimento do secúlo XX em Portugal. </strong>
                Além disso, este projecto também pretende destacar a relevância da imprensa online como um arquivo histórico através de um olhar jornalistico.
                A imprensa online é por excelência um meio de comunicação acessivel, em particular aos mais novos.
                Por isso, é de relevar a importância da imprensa como a fonte de informação verdadeira e isenta.
                E claro, este projecto também serve de homenagem aos que lutaram por um Portugal livre e sem o qual iniciativas como o Prémio Arquivo.pt não existiriam.

                <br /><br />
                <strong>O Arquivo.pt preserva toda Web Portuguesa desde 1991. </strong>
                Neste projecto o Arquivo.pt funcionou como um motor de busca de artigos de jornais, visto que é possível fazer pesquisas por um termo, num determinado site e num intervalo temporal.
                Sem este trabalho do Arquivo.pt seria impossível recolher alguns dos artigos que já não estão acessiveis através dos sites actuais.
                O Arquivo.pt foi por isso a principal fonte de dados e sem ele seria muito complicado aceder a todos os diferentes sites e conseguir "viajar no tempo".


                <br /><br />
                <strong>O principal resultado foi a criação de um arquivo temático com uma visão jornalista da história do 25 de Abril.</strong>
                O número considerável de artigos que foram escritos ao longo destes anos na imprensa portuguesa sobre o 25 de Abril (de uma forma geral), contribuiram para que este
                resultado seja (1) mais completo pois tem uma cobertura maior e (2) mais rico pois é possivel cruzar diferentes fontes para a mesma história.
                Quando iniciei o projecto não esperava encontrar tantos artigos publicados sobre este tema e os seus actores.


                O resultado final deste projecto é futro de várias decisões pessoais, em particular na selecção das fontes noticiosas.
                Fram escolhidas como fontes noticiosas os sites do:
                <strong> Público, Diário de Notícias, Jornal de Notícias, Expresso, Observador, SIC Notícias e TSF</strong>.


                <h2>Considerações pessoais</h2>
                Em relação, às entidades de pesquisa foram selecionadas com base no conhecimento do autor do tema através de livros, podcasts, documentários, e de conversas
                com pessoas que viveram o 25 de Abril na primeira pessoa. Portanto, o autor pede desde já desculpa por não ter sido tão completo como o tema exige.


                <br /><br />
                Em particular, queria referir que falhei na menção ou referência de vários dos resistentes que sobreviveram ou não à opressão.
                No entanto, queria sugerir a visita ao site do Museu do Aljube onde podem encontrar <Link to="https://www.museudoaljube.pt/centro-de-documentacao/testemunhos/">testemunhos dos que sobreviveram </Link>
                e referências aos que <Link to="https://www.museudoaljube.pt/centro-de-documentacao/biografias/">lutaram mas não sobreviveram para contar a história</Link>.


                <br /><br />
                Uma nota final sobre as personalidades selecionadas.
                Existem várias personalidades que nem sempre estiveram do lado "certo", ou sempre do lado errado.
                Ou independentemente do lado onde estiveram,
                tiveram contributos enormes para Portugal, por exemplo, Duarte Pacheco.
                Outras personalidades que sairam como heróis da revolução e que mais tarde estiveram ligadas a movimentos terroristas no pós-revolução, por exemplo Spinola ou Otelo.
                No entanto, por uma questão de categorização e organização tentei colocar estas personalidades no que me pareceu ser "a categoria" mais correcta tendo em conta o tema principal: o 25 de Abril.

                <h2>Agradecimentos</h2>
                Quero agradecer ao historiador Jorge Martins e ao meu mestre José Oliveira, duas pessoas que viveram o 25 de Abril durante a juventude e dispensaram o seu tempo para
                sugerir nomes e melhorias.


                <h2>Referências</h2>
                <h3>Livros</h3>
                <li>25 de Abril: Documento (2ª Edição, revista e aumentada). Afonso Praça, Albertino Antunes, António Amori, Cesário Borga, Fernando Cascais</li>
                <li>Censura - O Lápis Azul do Silêncio. Ana Aranha</li>
                <li>Portugal no Século XX - Os Anos 70. César Santos Silva</li>
                <li>Os Últimos do Estado Novo. José Pedro Castanheira</li>
                <li>1973: Uma Cronologia do Ano Zero. Os Factos, as Figuras e os Figurantes do Último Ano do Estado Novo. Tiago Beato</li>
                <li>Ensaios de Abril. Fernando Rosas</li>

                <h3>Podcasts</h3>
                <li>Histórias da Classe Trabalhadora</li>
                <li>De Cravo ao Peito</li>
                <li>Antes da Revolução</li>
                <li>Inquietação, inquietação!</li>
                <li>Avenida da Liberdade</li>
                <li>Retratos de Abril</li>
                <li>Clandestinos</li>

                <h3>Documentários</h3>
                <li>A Pide Leninha (RTP Play)</li>
                <li>Prisioneiros de Guerra (RTP Play)</li>
                <li>Conceição Matos: Coragem hoje, abraços amanhã (RTP Play)</li>
                <li>Salgueiro Maia - Rumo à Eternidade (RTP Play)</li>
                <li>Os Últimos Dias da PIDE (RTP Play)</li>

            </div>
        </div>
    )
}

export default About;