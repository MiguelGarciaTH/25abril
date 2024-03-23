import React from "react";

const Architecture = () => {
    return (
        <div>
            <h1>ARQUITECTURA</h1>
            <div class="text">

                <h2>Descrição</h2>
                Este projecto é uma aplicação web que é servida por um backend que integra várias tecnologias e várias fontes de dados. 

                <h3>Stack</h3>
                <li>Aplicação web: React;</li>
                <li>Backend: Java;</li>
                <li>Base de dados (BD): Postgres;</li>
                <li>Broker de Mensagens: Kafka.</li>

                <h2>Arquitectura e Processamento</h2>

                <img class="arch" src="../src/images/arch.png"/>
                Antes de qualquer tipo de processamento, foi feito um trabalho manual de levantamento e selecção de fontes de dados noticiosas e de 
                entidades de pesquisa. Estes dados foram inseridos na BD e são elas que alimentam todo o processamento. 

                <br /><br />
                <h3>Crawler:</h3> 
                Este é o primeiro componente da cadeia de processamento, periódicamente verifica se há novas entidades para processar.
                Este processamento é feito ao nível da entidade:
                <li>Procura de bigorafia que descreveam entidade (2) - fonte: DBPedia;</li>
                <li>Procura de imagens que representem a entidade (3) - fonte: Imagens do Arquivo.pt;</li>
                Sendo estes dados armazenados na tabela das entidades. 

                E depois são preparados URLs de pesquisa para o Arquivo.pt. Estes URLs são feitos para todas as entidades, para cada site de noticias, e desde a primeira data do Arquivo.pt até ao próprio dia. 
                Para cada pedido feito ao Arquivo.pt é guardado o intervalo consultado, desta forma é mantido um changelog que permite fasear a recolha e manter os dados sempre actualizados.
                Exemplo de um URL:
                <pre>
                https://arquivo.pt/textsearch?q=Salgueiro Maia&siteSearch=www.publico.pt&from=19960101000000&to=20151022163016&maxItems=500
                </pre>

                Este componente lista todas as entidades (1) previamente guardadas na BD e para cada entidade vai fazer três recolhas
                <li>(2)DBPedia: Para recolher dados biográficos sobre cada entidade;</li>
                <li>(3)Imagens Arquivo.pt: Para recolher dados biográficos sobre cada entidade;</li>
                <li>(4)Arquivo.pt: Para recolher páginas arquivadas para cada entidade;</li>
                <br/>
                Para cada entidade a imagem e biografia são persistidas na BD, e cada resultado recolhido do Arquivo.pt é enviado (5) para o Kafka para ser 
                processado depois.
                

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