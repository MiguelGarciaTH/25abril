import React from "react";
import { Link } from "react-router-dom";

const About = () => {
    return (
        <div>
            <div class="text">
                <h2>Motivação</h2>
                <strong>O 25 de Abril de 1974 encerrou uma ditadura que durou 41 anos - o Estado Novo - e deu lugar ao regime democrático que prevalece até aos nossos dias. </strong>  
                É por isso, o evento mais importante do século XX na História portuguesa.
            
                
                <br /><br />
                Este projecto tem como objectivo principal divulgar as personalidades, eventos, partidos, movimentos e locais que foram actores ou palco não só da Revolução de Abril, mas também do Estado Novo e do período pós-revolucionário. 
                De forma a atingir este objectivo, procurámos na imprensa escrita online de referência artigos que relacionem os objectos em estudo com o tempo histórico da Revolução de Abril.
                Naturalmente, alguns dos orgãos de comunicação social utilizados neste projecto não são contemporâneos deste periodo histórico, muito menos existia imprensa online na altura. 
                No entanto, dada a relevância do Estado Novo e do 25 de Abril, existem muitos artigos sobre estes temas desde que existe imprensa online.

                <br /><br />
                <strong>O Arquivo.pt preserva toda a Web Portuguesa com dados a partir de 1991.</strong> Por esta razão, o Arquivo.pt foi o suporte principal para realizar 
                este projecto. Através do Arquivo.pt foi possivel recuar no tempo e recolher artigos relevantes para o tema desde que existe imprensa escrita online em Portugal.


                <br /><br />
                O resultado final deste projecto é futro de várias decisões pessoais, em particular na selecção das fontes noticiosas e das 
                entidades de pesquisa. No que toca às primeiras, que são mais fáceis de listar, foram escolhidas como fontes noticiosas os sites do: 
                <strong> Público, Diário de Notícias, Jornal de Notícias, Expresso, Observador, SIC Notícias e TSF</strong>. Em relação, às entidades 
                de pesquisa foram selecionadas com base no conhecimento do autor do tema através de livros, podcasts, documentários, e de conversas 
                 com pessoas que viveram o 25 de Abril na primeira pessoa. Portanto, o autor pede desde já desculpa por não ter sido tão completo como o tema exige.
                

                <br /><br />
                Em particular, queria referir que falhei na menção ou referência de vários dos resistentes que sobreviveram ou não à opressão. No entanto, queria sugerir a visita ao site do Museu do Aljube onde podem encontrar <Link to="https://www.museudoaljube.pt/centro-de-documentacao/testemunhos/">testemunhos dos que sobreviveram</Link> 
                e referências aos que <Link to="https://www.museudoaljube.pt/centro-de-documentacao/biografias/">lutaram mas não sobreviveram para contar a história</Link>.
            

                <br /><br />
                Uma nota final sobre as decisões tomadas. Existem várias personalidades que nem sempre estiveram do lado "certo", ou sempre do lado errado. Ou independentemente do lado 
                tiveram contributos enormes para Portugal, por exemplo, Duarte Pacheco. Outras personalidades que sairam como heróis da revolução e que mais tarde estiveram ligadas a movimentos terroristas no pós-revolução, 
                por exemplo Spinola ou Otelo. No entanto, por uma questão de categorização e organização tentei colocar estas personalidades no que me pareceu ser "a categoria" mais correcta aos olhos 
                do tema principal: o 25 de Abril. 

                {/* <h2>Agradecimentos</h2> */}
                

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