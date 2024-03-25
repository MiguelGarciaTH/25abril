import React from "react";

const Architecture = () => {
    return (
        <div>
            <h1>ARQUITECTURA</h1>
            <div class="text">

                <h2>Descrição</h2>
                Este project é suportado por uma arquitectura cliente e servidor. O client é uma aplicação web que faz pedidos REST a um backend aplicacional servid por uma base 
                de dados SQL.

                <h3>Stack</h3>
                <li>Aplicação web: React;</li>
                <li>Backend: Java;</li>
                <li>Base de dados (BD): Postgres;</li>
                <li>Broker de Mensagens: Kafka.</li>

                <h2>Arquitectura e Processamento</h2>

                Antes de descrever a arquitectura do sistema é importante clarificar que existiu uma fase manual inicial de criação de dados. 
                Para alimentar todo o sistema foi necessário definir um conjunto de sites de pesquisa para direcionar o Arquivo.pt a fazer recolhas penas nesses sites, e foi necessário
                definir quais os termos queriamos fazer pesquisas no Arquivo.pt. Estes termos são as várias pessoas, eventos, sitios, partidos que foram definidos com critérios pessoais
                do autor, no entanto, tendo como objectivo manter critérios objectivos de relevância histórica. Sem entrar em detalhes do esquema da BD, é importante explicar que 
                para cada um destes termos foi definida uma categoria e "aliases" para aumentar a cobetura de pesquisa. Por exemplo:
                <br /><br />
                <li>Nome: José Afonso</li>
                <li>Alias: Zeca Afonso</li>
                <li>Tipo: Artista</li>
                <br />
                Além destes campos pré-preenchidos cada entidade de pesquisa tem um campo para uma imagem e uma biografia. 


                <img class="arch" src="../src/images/arch.png"/>

                <br /><br />
                <h3>Crawler</h3> 
                O <strong>Crawler</strong> é o primeiro componente do fluxO de proessamento de dados. Periódicamente o Crawler procura na BD termos de pesquisa para enriquecer com 
                dados externos. Os dados externos são fornecidos de três fontes com três finalidades diferentes:
                
                <li>Procura de bigorafia que descreveam entidade (2) - fonte: DBPedia;</li>
                <li>Procura de imagens que representem a entidade (3) - fonte: Imagens do Arquivo.pt;</li>
                <li>Procura de artigos jornalisticos que correspondam à pesquisa por palavra chave num determinado site (4) - fonte: Arquivo.pt;</li>
                
                A biografia e fotografia são logo persistidos na BD. Em particular, para tentar selecionar a melhor fotografia, foram usados dois critérios: o tamanho da image, e se o URL e 
                a legenda da imagem contêm o nome ou alias da entidade de pesquisa. 
                
                Em relação à pesquisa no Arquivo.pt: Foram construídos URLs para cada entidade em cada site de pesquisa com o intervalo máximo temporal (i.e., desde 1996 até ao dia corrente), 
                utilizando a paginação do Arquivo.pt para iterar sobre os resultados. Para cada resultado com sucesso é guardado o intervalo da pesquisa. Desta forma é possivel parar e retomar 
                as pesquisas, além do mais este changelog permite actualizar o projecto ao longo do tempo partindo sempre da última data guardada com sucesso.

                Exemplo de um URL pedido à API do Arquivo.pt 
                <br /><br />

                <pre>
                    https://arquivo.pt/textsearch?q=Salgueiro Maia&siteSearch=www.publico.pt&from=19960101000000&to=20151022163016&maxItems=500
                </pre>
                
                A API do Arquivo.pt retorna uma lista de resultados com os vários URLs de cada referência arquivada. Para cada um destes resultados é criada uma mensagem que associa uma entidade a um site (dos 
                sites de noticias) devolvidos pelo Arquvio.pt e envia esta mensagem para o Kafa, desta forma separamos o processo de recolha de artigos e o seu processamento. 
                

                <h3>Processor</h3> 
                O <strong>Processor</strong> é o componente que está à escuta de eventos de Kafka (6) e que vai processar realmente cada resultado. A primeira coisa que este componente vai fazer é normalizar o URL 
                da notícia devolvido pelo Arquivo.pt. Desta forma conseguimos evitar guardar registo duplicados. Se o registo ainda não existir fazemos todo o processamento, se já existir utilizamos dados já 
                procesados e fazemos apenas algum processamento se o registo repetido for referente a uma outra entidade. No caso de ser um registo novo, então fazemos a recolha de todo o texto contido na página
                que é fornecido Arquivo.pt (i.e., linkToExtractedText). Através desse URL recolhemos todo o texto da página e começamos a fazer uma avaliação de relevância do texto. 
                Este processo - que é uma das partes mais relevantes do projecto - consistem em identificar as referências no texto à entidade de pesquisa mas também de enquadrar estas referências no tema do 
                projecto. Para isto definimos algumas palavras chave que definem este contexto histórico (e.g., revolução dos cravos, estado novo, 25 de abril, revolução abril ,guerra colonial, salazarismo, 
                salazarista, salazaristas, clandestinidade, abril de 1974). Esta "scoring" é feito com pattern matching utilizando regex.
                Em suma, quanto maior for o "score" resultante desta combinação maior é a relevância da noticia neste contexto.
                Se este "score" for maior do que zero guardamos a noticia, se não descartamos por não ser considerada relevante para o projecto. Nota: a mesma noticia pode ter uma relevância diferente para duas 
                entidades de pesquisa.

                No fim do processamento guardamos o score de cada relação (noticia, entidade) assim como alguns metadados da noticia, por exemplo, o texto em bruto recolhido pelo Arquivo.pt, na BD (7).

                
                <h3>REST</h3> 
                O <strong>REST</strong> é um servidor REST que vai através de uma API bem definida servir os dados já processados e armazeados na BD (8) a um cliente. 

                <h3>Web</h3> 
                O <strong>Web</strong> é uma aplicação web que apresenta de uma forma estruturada e útil os dados servidos pelo componente REST (9).

                <h2>Limitações</h2>
                A primeira limitação identificada neste proejcto é o próprio autor. A sua formação e experiência não abrange áreas como Processamento de 
                Linguagem Natural (PLN), Inteligência Artificial (IA) e programação Web. Estas três lacunas poderiam tornar pelo menos o trabalho mais
                interessante e rico. 

                <br /><br />

                Feita esta ressalva listamos algumas das questões que limitam este trabalho:
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

                <strong>Biografias:</strong>
                Nem todas as entidades de pesquisa utilizadas tiveram resultados na DBPedia, como tal nem todas as entidades contêm uma pequena biografia para
                contextualizar a História.
                <br /><br />

                <strong>Imagens:</strong>
                Algumas das imagens selecionadas para todas as entidades de pesquisa não representam de uma forma precisa a entidade em questão. 
                Esta limitação surge porque as imagens são automáticamente recolhidas através da API de imagens do Arquivo.pt. Como tal, dada um termo
                de pesquisa existem vários resultados retornados. Desses retornados foram aplicados alguns critérios com o objectivo de selecionar a melhor imagem.
                No entanto, nem sempre esses critérios foram eficazes.
                <br /><br />
                
            </div>
        </div>
    )
}

export default Architecture;