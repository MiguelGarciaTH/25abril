import React from "react";

const About = () => {
    return (
        <div>
            <h1>SOBRE</h1>
            <div class="text">
                <h2>Motivação</h2>
                O 25 de Abril de 1974 é o evento mais importante do século XX. Este dia, encerrou uma ditadura de 41 anos - o Estado Novo - e deu lugar 
                um regime democrático que prevalece até aos nossos dias.
                
                <br /><br />
                Neste trabalho, procurámos divulgar os acontecimentos, personalidades, partidos e locais que foram actores ou palco não só da Revolução 
                de Abril, mas também do Estado Novo e do periodo pós-revolucionário. Para atingir este objectivo fizemos uma recolha de todos os artigos 
                publicados na imprensa online sobre os vários intervenientes. 
                Naturalmente, as fontes noticiosas utilizadas neste projecto não são contemporâneas dos períodos referidos anteriormente. 
                No entanto, dada a relevância do Estado Novo e da Revolução de Abril na História Portuguesa, a imprensa Portuguesa tem feito ao 
                longo dos anos um trabalho apreciável de manter a História viva com artigos. 

                <br /><br />
                Este site reune todos os artigos noticiosos encontrados no Arquivo.pt para vários orgãos de comunicação social de relevância para várias 
                 entidades relacionadas aos temas do 25 de Abril e do Estado Novo. 
                 A escolha das entidades é fruto do interesse e conhecimento do autor do tema através de livros, podcasts, documentários, e de conversas 
                 com pessoas que viveram o 25 de Abril na primeira pessoa. Portanto, o autor pede desde já desculpas por não ter sido tão completo como 
                 o tema exige.
                


                <h2>Descrição</h2>


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

                <strong>Mini biografias:</strong>
                Nem todas as "entidades" utilizadas tiveram resultados na DBPedia, como tal nem todas as entidades contêm uma pequena biografia para
                contextualizar a História.

                <strong>Imagens:</strong>
                

                <h2>Referências</h2>
                <h3>Sites</h3>
                <h3>Livros</h3>
                <h3>Podcasts</h3>
                <h3>Documentários</h3>
            </div>
        </div>
    )
}

export default About;