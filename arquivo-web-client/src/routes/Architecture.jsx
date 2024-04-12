import React from "react";

const Architecture = () => {
    return (
        <div>
            <div class="text">

                <h2>Descrição</h2>
                Este projecto é suportado por uma arquitectura cliente e servidor. O cliente é uma aplicação web que faz pedidos REST a um backend aplicacional servido por uma base 
                de dados SQL.

                <h3>Stack</h3>
                <li>Aplicação web: React;</li>
                <li>Backend: Java;</li>
                <li>Base de dados (BD): Postgres;</li>
                <li>Broker de Mensagens: Kafka.</li>

                <h2>Arquitectura e Processamento</h2>

                Antes de entrar nos detalhes mais técnicos é importante explicar que existiu um processo de investigação e levantamento de entidades a pesquisar: 
                pessoas, eventos, partidos, movimentos, locais, etc. 
                Estes foram devidamente categorizados e foram associados "alias" (quando existentes) e inseridos numa base de dados relacional. 
                Por exemplo:
                <br /><br />
                <li>Nome: José Afonso</li>
                <li>Alias: Zeca Afonso</li>
                <li>Tipo: Artista</li>
                <br />
                Além destes campos pré-preenchidos, cada entidade de pesquisa tem campos para imagem e biografia. 

                
                De uma forma mais simples, foram selecionados vários jornais de referência para fazer as pesquisas (através do arquivo). 
                Foram escolhidos os seguintes jornais: Público, Diário de Notícias, Jornal de Notícias, Expresso, Observador, SIC Notícias e TSF. 



                <img class="arch" src="../src/images/arch.png"/>

                <br /><br />
                <h3>Crawler</h3> 
                O <strong>Crawler</strong> é o primeiro componente do fluxo de processamento de dados. 
                Periódicamente o Crawler procura na BD termos de pesquisa para enriquecer com dados externos. Os dados externos são fornecidos de três fontes com três finalidades diferentes:
                
                <li>Procura de bigorafia que descreveam entidade (2) - fonte: DBPedia;</li>
                <li>Procura de imagens que representem a entidade (3) - fonte: Imagens do Arquivo.pt;</li>
                <li>Procura de artigos jornalisticos que correspondam à pesquisa por palavra chave num determinado site (4) - fonte: Arquivo.pt;</li>
                
                A biografia e fotografia são logo persistidos na BD. Em particular, para tentar selecionar a melhor fotografia, foram usados dois critérios como o tamanho da image, se o URL e 
                a legenda da imagem contêm o nome ou alias da entidade de pesquisa. 
                
                Em relação à pesquisa no Arquivo.pt: Foram construídos URLs para cada entidade em cada site de pesquisa com o intervalo máximo temporal (i.e., desde 1996 até ao dia corrente), 
                utilizando a paginação do Arquivo.pt para iterar sobre os resultados. Para cada resultado com sucesso é guardado o intervalo da pesquisa. Desta forma é possivel parar e retomar 
                as pesquisas, isto também permite actualizar o projecto ao longo do tempo partindo sempre da última data guardada com sucesso.

                Exemplo de um URL pedido à API do Arquivo.pt 
                <br /><br />

                <pre>
                    https://arquivo.pt/textsearch?q=Salgueiro Maia&siteSearch=www.publico.pt&from=19960101000000&to=20151022163016&maxItems=500
                </pre>
                
                A API do Arquivo.pt retorna uma lista de resultados com os vários URLs de cada referência arquivada. 
                Para cada um destes resultados é criada uma mensagem que associa uma entidade a um site (dos sites de noticias) devolvidos pelo Arquvio.pt e envia esta mensagem para o Kafa, desta forma separamos a recolha de artigos do seu processamento. 
                

                <h3>Processor</h3> 
                O <strong>Processor</strong> é o componente que está à escuta de eventos de Kafka (6) e que vai processar realmente cada resultado do Arquivo.pt. 
                A primeira coisa que este componente faz é normalizar o URL da artigo devolvido pelo Arquivo.pt. 
                Desta forma evitamos guardar registos duplicados. 
                No caso de ser um registo novo: 
                Fazemos a recolha de todo o texto contido na página (campo linkToExtractedText fornecido Arquivo.pt). e avaliamos a relevância do texto com um score. 
                Este processo consiste em identificar as referências no texto à entidade de pesquisa (nome e alias) mas também de enquadrar estas referências no tema do 
                projecto utilizando palavras chave de contexto histórico (e.g., revolução dos cravos, estado novo, 25 de abril, revolução abril ,guerra colonial, salazarismo, 
                salazarista, salazaristas, clandestinidade, abril de 1974). 
                Este "scoring" é feito com pattern matching utilizando regex.
                Quanto maior for o "score" resultante desta combinação maior é a relevância do artigo neste contexto.
                Se este "score" for maior do que zero guardamos o artigo, caso contrário descartamos por não ser considerado relevante para o projecto. 
                No fim do processamento guardamos o score de cada relação (artigo + entidade) assim como alguns metadados do artigo, por exemplo, o texto em bruto recolhido pelo Arquivo.pt, na BD (7).

                Se o artigo já estiver guardado, então utilizamos dados préviamente guardados mas processamos para a nova entidade de pesquisa. 
                Sendo que o mesmo artigo pode ter uma relevância diferente para duas entidades de pesquisa.

                
                <h3>REST</h3> 
                O <strong>REST</strong> é um servidor aplicacional que expõe uma API REST para aceder aos dados processados e armazeados na BD (8).

                <h3>Web</h3> 
                O <strong>Web</strong> é uma aplicação web que apresenta de uma forma estruturada e útil os dados servidos pelo componente REST (9).

                <h2>Desafios e limitações</h2>
                A primeira limitação identificada neste proejcto é o próprio autor. A sua formação e experiência não abrange áreas como Processamento de 
                Linguagem Natural (PLN), Inteligência Artificial (IA) e programação Web. Estas três lacunas poderiam tornar pelo menos o trabalho mais
                interessante e rico. 

                <br /><br />

                Feita esta ressalva listamos algumas das questões que limitam ou foram mais desafiantes neste trabalho:
                <br /><br />
                
                <strong>Artigos duplicados:</strong> 
                Foi necessário filtrar artigos para evitar guardar artigos duplicados na BD. A forma encontrada para lidar com este problema foi identificar o URL original do artigo e usar como chave.

                <strong>Tratamento do texto:</strong> 
                Todos os textos dos artigos de imprensa apresentados neste site são os textos em bruto recolhidos pelo Arquivo.pt. 
                Naturalmente, este texto introduz muito ruído, pois o Arquivo.pt captura todo o texto da página original: 
                cabeçalhos, rodapés, legendas, etc. Em contrapartida, fazer a extração do conteúdo com substância jornalistica de cada referência 
                do artigo seria bastante complicado. Pois, cada site (e.g., Público, Expresso, etc) têm o seu formato, este conteúdo não tem uma 
                estrutura bem definida (i.e., o HTML muitas vezes não têm qualquer semântica) e além disso estes sites mudaram a 
                sua estrutura. Desta forma, optámos por aproveitar o trabalho já feito pelo Arquivo.pt e utilizar o texto em bruto recolhido.
                Queremos acreditar que o tratamento feito ao texto para tentar focar no tema é o suficiente para levar o utilizador a ver a página 
                original no Arquivo.pt.
                <br /><br />

                <strong>Biografias:</strong>
                Nem todas as entidades de pesquisa utilizadas tiveram resultados na DBPedia, como tal nem todas as entidades contêm uma pequena biografia para
                contextualizar a História.
                <br /><br />

                <strong>Imagens:</strong>
                Algumas das imagens selecionadas para todas as entidades de pesquisa não representam de uma forma precisa a entidade em questão. 
                Esta limitação surge porque as imagens são automáticamente recolhidas através da API de imagens do Arquivo.pt. Como tal, dada um termo
                de pesquisa existem vários resultados retornados. Desses retornados foram aplicados alguns critérios com o objectivo de selecionar a melhor imagem.
                No entanto, nem sempre esses critérios foram eficazes por isso algumas imagens foram adicionadas manualmente para garantir um site mais correcto e agradável.
                <br /><br />
                
            </div>
        </div>
    )
}

export default Architecture;