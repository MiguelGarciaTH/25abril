import React from "react";

const About = () => {
    return (
        <div>
            <h1>SOBRE</h1>
            <div class="text">
                <h2>Motivação</h2>
                <strong>O 25 de Abril de 1974 é o evento mais importante do século XX. </strong>  
                Este dia terminou com uma ditadura que durou 41 anos - o Estado Novo - e deu lugar ao regime democrático que prevalece até aos nossos dias.
                
                <br /><br />
                Neste trabalho, procurámos divulgar os acontecimentos, personalidades, partidos e locais (entidades de pesquisa) que foram actores ou 
                palco não só da Revolução de Abril, mas também do Estado Novo e do periodo pós-revolucionário. 
                Em particular, procurámos fazer esta recolha histórica através da imprensa escrita online em vários jornais e orgãos de comunicação social de
                referência.
                Naturalmente, as fontes noticiosas utilizadas neste projecto não são contemporâneas dos períodos que dão o tema a este projecto. 
                No entanto, dada sua relevância na História nacional, têm existido ao longo dos anós vários artigos sobre os vários temas e personalidades.

                <br /><br />
                <strong>O Arquivo.pt preserva toda a Web Portuguesa com dados a partir de 1991.</strong> Por esta razão, o Arquivo.pt foi o suporte principal para concluir 
                este projecto. Através do Arquivo.pt foi possivel andar no tempo e recolher artigos desde que existe imprensa escrita online em Portugal.


                <br /><br />
                O resultado final deste projecto é futro de várias decisões pessoais, em particular no que toca às fontes noticiosas e às 
                entidades de pesquisa. No que toca às primeiras, que são mais fáceis de listar, foram escolhidas como fontes noticiosas os sites do: 
                <strong> Público, Diário de Notícias, Jornal de Notícias, Expresso, Observador, SIC Notícias e TSF</strong>. Em relação, às entidades 
                de pesquisa foram selecionadas com base no conhecimento do autor do tema através de livros, podcasts, documentários, e de conversas 
                 com pessoas que viveram o 25 de Abril na primeira pessoa. Portanto, o autor pede desde já desculpas por não ter sido tão completo como 
                 o tema exige.
        

                <h2>Descrição</h2>

                <img class="arch" src="../src/images/arch.png"/>


                <h2>Limitações</h2>
                A primeira limitação identificada neste proejcto é o próprio autor. A sua formação e experiência não abrange áreas como Processamento de 
                Linguagem Natural (PLN), Inteligência Artificial (IA) e programação Web. Estas três lacunas poderiam tornar pelo menos o trabalho mais
                interessante e rico. 

                <br /><br />

                Feita esta ressalva iremos listar algumas das questões que limitam este trabalho:
                <br /><br />
                
                <strong>Tratamento do texto:</strong> 
                Todos os textos de artigos de imprensa apresentados neste site são textos em bruto recolhidos 
                pelo Arquivo.pt. 
                Naturalmente, este texto introduz muito ruído, pois o Arquivo.pt captura todo o texto da página original: 
                cabeçalhos, rodapés, legendas, etc. Em contrapartida, fazer a extração do conteúdo com substância jornalistica de cada referência 
                do artigo seria bastante complicado. Pois, cada site (e.g., Público, Expresso, etc) têm o seu formato, este conteúdo não tem uma 
                estrutura bem definida (i.e., o HTML muitas vezes não têm qualquer semântica) e mesmo ao longo do tempo cada fonte foi mudando a 
                sua estrutura. Desta forma, optámnos por aproveitar o trabalho já feito pelo Arquivo.pt e utilizar o texto em bruto recolhido.
                Queremos acreditar que o tratamento feito ao texto para tentar focar no tema é o suficiente para levar o utilizador a ver a página 
                original no Arquivo.pt.
                <br /><br />

                <strong>Mini biografias:</strong>
                Nem todas as entidades de pesquisa utilizadas tiveram resultados na DBPedia, como tal nem todas as entidades contêm uma pequena biografia para
                contextualizar a História.
                <br /><br />

                <strong>Imagens:</strong>
                Algumas das imagens selecionadas para todas as entidades de pesquisa não representam de uma forma precisa a entidade em questão. 
                Esta limitação surge porque as imagens são automáticamente recolhidas através da API de imagens do Arquivo.pt. Como tal, dada um termo
                de pesquisa existem vários resultados retornados. Desses retornados foram aplicados alguns critérios com o objectivo de selecionar a melhor imagem.
                No entanto, nem sempre esses critérios foram eficazes.
                <br /><br />
                
                
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