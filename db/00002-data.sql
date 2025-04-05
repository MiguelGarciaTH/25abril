insert into quote(author, "text") values
    ('Francisco Sousa Tavares', 'Povo português, vivemos um momento histórico, como talvez desde 1640 não se vive. É a libertação da Pátria. Fomos libertados'),
    ('Jorge Sampaio', '25 de Abril sempre'),
    ('Manuel Alegre', 'Foram dias foram anos a esperar por um só dia.<br/>Alegrias. Desenganos.<br/>Foi o tempo que doía com seus riscos e seus danos.<br/>Foi a noite e foi o dia na esperança de um só dia.'),
    ('Sophia De Mello Breyner','Esta é a madrugada que eu esperava<br/>O dia inicial inteiro e limpo<br/>Onde emergimos da noite e do silêncio<br/>E livres habitamos a substância do tempo.<br/>'),
    ('Salgueiro Maia','Meus senhores, como todos sabem, há diversas modalidades de Estado. Os estados socialistas, os estados capitalistas e o estado a que chegámos. Ora, nesta noite solene, vamos acabar com o estado a que chegámos');

insert into rate_limiter(description, counter, counter_limit, sleep_time, locked) values
    ('arquivo.pt', 0, 245, 60000, false),
    ('arquivo-image.pt', 0, 390, 60000, false),
    ('dbpedia', 0, 95, 60000, false),
    ('google-api', 0, 1200, 60000, false);

insert into site(name, acronym, url) values
    ('Público', null, 'http://www.publico.pt'),
    ('Diário de Notícias', 'DN', 'http://www.dn.pt'),
    ('Jornal de Notícias', 'JN', 'http://www.jn.pt'),
    ('Expresso', null, 'http://www.expresso.pt'),
    ('Observador',null,'https://observador.pt'),
    ('SIC Notícias',null,'https://sicnoticias.pt'),
    ('TSF', null, 'https://www.tsf.pt');

--insert into search_entity (name, aliases, type) values
------- POLITICOS
--('Francisco Sá Carneiro', 'Sá Carneiro', 'POLITICOS'),
--('Francisco Pinto Balsemão', null, 'POLITICOS'),
--('Miller Guerra', null, 'POLITICOS'),
--('Adelino da Palma Carlos', null, 'POLITICOS'),
--('Frank Charles Carlucci', 'Frank Carlucci', 'POLITICOS'),
--('António Barreto', null, 'POLITICOS'),
--('José Medeiros Ferreira', 'Medeiros Ferreira', 'POLITICOS'),
--('Jorge Sampaio', null, 'POLITICOS'),
--('Álvaro Cunhal', null, 'POLITICOS'),
--('Mário Soares', null, 'POLITICOS'),
--('Manuel Alegre', null, 'POLITICOS'),
--('Nuno Teotónio Pereira', null, 'POLITICOS'),
--('Humberto Delgado', 'Humberto da Silva Delgado, O General sem medo', 'POLITICOS'),
--('Salgado Zenha', null, 'POLITICOS'),
---------- RESISTENTES
--('Celeste Caeiro',null, 'RESISTENTES'),
--('Margarida Tengarrinha',null, 'RESISTENTES'),
--('Catarina Eufémia', null, 'RESISTENTES'),
--('Aurora Rodrigues', null, 'RESISTENTES'),
--('Conceição Matos', null, 'RESISTENTES'),
--('José Dias Coelho', null, 'RESISTENTES'),
--('Samora Machel', null , 'RESISTENTES'),
--('Jonas Savimbi', null, 'RESISTENTES'),
--('José Manuel Tengarrinha', null, 'RESISTENTES'),
--('Domingos Abrantes', null,  'RESISTENTES'),
--('Henrique Galvão', null, 'RESISTENTES'),
--('Carlos Antunes', null, 'RESISTENTES'),
--('Hermínio da Palma Inácio', 'Palma Inácio', 'RESISTENTES'),
--('José António Ribeiro Santos', 'José Ribeiro Santos', 'RESISTENTES'),
--('Amílcar Cabral', null, 'RESISTENTES'),
--('Alípio de Freitas', null, 'RESISTENTES'),
--('Agostinho Neto', null, 'RESISTENTES'),
--('Isabel do Carmo', null, 'RESISTENTES'),
--('Camilo Mortágua', null, 'RESISTENTES'),
--('Maria Lamas', null, 'RESISTENTES'),
--('Emídio Santana', null, 'RESISTENTES'),
---------- MUSICOS
--('Sérgio Godinho', null, 'MUSICOS'),
--('Fausto Bordalo Dias', null, 'MUSICOS'),
--('José Mário Branco', 'Zé Mário Branco', 'MUSICOS'),
--('Adriano Correia de Oliveira', null, 'MUSICOS'),
--('Ruy Mingas', 'Rui Mingas', 'MUSICOS'),
--('Francisco Fanhais', 'Padre Fanhais', 'MUSICOS'),
--('José Barata-Moura', 'José Barata Moura', 'MUSICOS'),
--('Luís Cília', null, 'MUSICOS'),
--('Manuel Freire', null, 'MUSICOS'),
--('Vitorino Salomé', null, 'MUSICOS'),
--('José Jorge Letria', 'Jorge Letria', 'MUSICOS'),
--('José Afonso', 'Zeca Afonso', 'MUSICOS'),
--('Fernando Lopes Graça', 'Fernando Lopes-Graça', 'MUSICOS'),
--('Carlos Paredes', null, 'MUSICOS'),
--('Paulo de Carvalho', null, 'MUSICOS'),
---------- ESCRITORES
--('Natália Correia', null,  'ESCRITORES'),
--('Maria Teresa Horta',  'Três Marias, Novas Cartas Portuguesas', 'ESCRITORES'),
--('Maria Isabel Barreno', 'Três Marias, Novas Cartas Portuguesas', 'ESCRITORES'),
--('Maria Velho da Costa', 'Três Marias, Novas Cartas Portuguesas', 'ESCRITORES'),
--('Sophia de Mello Breyner Andresen', 'Sophia de Mello Breyner', 'ESCRITORES'),
--('Luís de Sttau Monteiro', 'Stau Monteiro', 'ESCRITORES'),
--('José Cardoso Pires', null, 'ESCRITORES'),
--('Bernardo Santareno', null, 'ESCRITORES'),
--('José Gomes Ferreira', null ,'ESCRITORES'),
--('Soeiro Pereira Gomes', null, 'ESCRITORES'),
--('Ferreira de Castro', null, 'ESCRITORES'),
-------- JORNALISTAS
--('Joaquim Furtado', null,  'JORNALISTAS'),
--('Raul Rego', null,  'JORNALISTAS'),
--('Adelino Gomes', null, 'JORNALISTAS'),
--('Francisco Sousa Tavares', null, 'JORNALISTAS'),
--('João Paulo Diniz', null, 'JORNALISTAS'),
--('Leite de Vasconcelos', null, 'JORNALISTAS'),
--('Rádio Clube Português', 'RCP', 'JORNALISTAS'),
--('Rádio Renascença', null, 'JORNALISTAS'),
--('Carlos Albino', null, 'JORNALISTAS'),
--('Álvaro Guerra', null, 'JORNALISTAS'),
--('João Abel Manta', 'Abel Manta', 'JORNALISTAS'),
--------- OPRESSAO
--('Barbieri Cardoso', null, 'OPRESSORES'),
--('Óscar Aníbal Piçarra de Castro Cardoso', 'Óscar Cardoso', 'OPRESSORES'),
--('Secretariado Nacional de Informação', 'SNI', 'OPRESSORES'),
--('Pedro Feytor Pinto', null, 'OPRESSORES'),
--('António Rosa Casaco', null, 'OPRESSORES'),
--('Alexandre Carvalho Neto', null, 'OPRESSORES'),
--('Marcello Caetano', 'Marcelo Caetano', 'OPRESSORES'),
--('António de Oliveira Salazar', null, 'OPRESSORES'),
--('Óscar Carmona', null,'OPRESSORES'),
--('Duarte Pacheco', null, 'OPRESSORES'),
--('Cardeal Manuel Gonçalves Cerejeira', 'Cardeal Cerejeira', 'OPRESSORES'),
--('Américo Tomás', 'Américo Thomaz','OPRESSORES'),
--('Kaúlza de Arriaga', null, 'OPRESSORES'),
--('Fernando da Silva Pais', 'Silva Pais', 'OPRESSORES'),
--------('Veiga Simão', null, 'OPRESSORES'),
--------('Baltasar Rebelo de Sousa', null, 'OPRESSORES'),
--------('José Hermano Saraiva', null, 'OPRESSORES'),
--('Eduardo Fontes', 'Dadinho Fontes', 'OPRESSORES'),
--('Adelino da Silva Tinoco', 'Inspector Tinoco', 'OPRESSORES'),
--('Madalena Oliveira', 'Leninha, PIDE Leninha', 'OPRESSORES'),
--('Polícia Internacional e de Defesa do Estado','PIDE, PIDE/DGS, Direcção-Geral de Segurança, P.I.D.E, Rua António Maria cardoso', 'OPRESSORES'),
--('Prisão de Caxias', null, 'OPRESSORES'),
--('Prisão do Aljube', null,  'OPRESSORES'),
--('Prisão de Peniche', null, 'OPRESSORES'),
--('Legião Portuguesa', null, 'OPRESSORES'),
--('António Joaquim Tavares Ferro', 'António Ferro', 'OPRESSORES'),
--('Tarrafal', 'Campo de Concentração do Tarrafal, Campo da Morte Lenta', 'OPRESSORES'),
--('União Nacional', 'UN, Ação Nacional Popular', 'OPRESSORES'),
-------- MFA
--('António de Spínola', 'General Spínola, Spínola',  'CAPITAES'),
--('António Ramalho Eanes','Ramalho Eanes', 'CAPITAES'),
--('Vasco Lourenço', null, 'CAPITAES'),
--('Francisco da Costa Gomes','Costa Gomes', 'CAPITAES'),
--('Carlos Fabião', null, 'CAPITAES'),
--('Otelo Saraiva de Carvalho', 'Otelo', 'CAPITAES'),
--('Almada Contreiras', null, 'CAPITAES'),
--('Costa Correia', null, 'CAPITAES'),
--('José Alves Costa', null, 'CAPITAES'),
--('Figueiras Soares', null, 'CAPITAES'),
--('Rosa Coutinho', null , 'CAPITAES'),
--('Salgueiro Maia', 'Capitão Maia', 'CAPITAES'),
--('Ernesto Melo Antunes','Melo Antunes', 'CAPITAES'),
--('Fisher Lopes Pires', null, 'CAPITAES'),
--('Garcia dos Santos', null, 'CAPITAES'),
--('Jaime Neves', null, 'CAPITAES'),
--('Vasco Gonçalves', null, 'CAPITAES'),
--('José Pinheiro de Azevedo','Pinheiro de Azevedo', 'CAPITAES'),
--('Vítor Alves', 'Vitor Manuel Rodrigues Alves', 'CAPITAES'),
--('Capitães de Abril', 'Movimento dos Capitães', 'CAPITAES'),
--('Diniz de Almeida', null, 'CAPITAES'),
--('Carlos Matos Gomes', null, 'CAPITAES'),
--('Jorge Golias', null, 'CAPITAES'),
--('Ricardo Durão', null, 'CAPITAES'),
--('Manuel Duran Clemente', null, 'CAPITAES'),
------('Bravia Chaimite', null, 'CAPITAES')
------('Página 1', null, 'JORNAIS'),
------('Seara Nova', null, 'JORNAIS'),
------('República', null, 'JORNAIS'),
------('O Século Ilustrado', null, 'JORNAIS'),
------('Diário Popular', null, 'JORNAIS'),
------('Flama', null, 'JORNAIS'),
------('A Mosca', null, 'JORNAIS'),
------- Movimentos
----('Partido Comunista dos Trabalhadores Portugueses', 'MRPP', 'MOVIMENTOS'),
----('Frente de Libertação de Moçambique', 'FRELIMO','MOVIMENTOS'),
----('Partido Comunista Português', 'PCP', 'MOVIMENTOS'),
------('Partido Popular Monárquico', 'PPM', 'MOVIMENTOS'),
----('Partido do Centro Democrático Social', 'CDS', 'MOVIMENTOS'),
----('Partido Socialista','PS', 'MOVIMENTOS'),
----('Partido Social Democrata', 'PPD-PSD, PSD', 'MOVIMENTOS'),
----('Frente Socialista Popular', 'FSP', 'MOVIMENTOS'),
----('Movimento de Esquerda Socialista', 'MES', 'MOVIMENTOS'),
----('União Democrática Popular','UDP', 'MOVIMENTOS'),
----('Movimento de Unidade Democrática','MUD', 'MOVIMENTOS'),
----('Junta de Salvação Nacional', null, 'MOVIMENTOS'),
----('Mocidade Portuguesa', null, 'MOVIMENTOS'),
----('Confederação Geral dos Trabalhadores Portugueses — Intersindical Nacional', 'CGTP', 'MOVIMENTOS'),
----('Ação Revolucionária Armada', ' ARA ', 'MOVIMENTOS'),
----('Diretório Democrato-Social', null, 'MOVIMENTOS'),
----('Frente Portuguesa de Libertação Nacional', 'FPLN', 'MOVIMENTOS'),
----('Ala Liberal', null, 'MOVIMENTOS'),
----('Forças Populares 25 de Abril', 'FP25', 'MOVIMENTOS'),
----('Liga de Unidade e Ação Revolucionária', 'LUAR', 'MOVIMENTOS'),
----('Exército de Libertação de Portugal','ELP', 'MOVIMENTOS'),
----('Movimento Maria da Fonte', null, 'MOVIMENTOS'),
----('Comando Operacional do Continente', 'COPCON', 'MOVIMENTOS'),
----('Brigadas Revolucionárias',null, 'MOVIMENTOS'),
----('Movimento Democrático de Libertação de Portugal', 'MDLP', 'MOVIMENTOS'),
----('Comissão Democrática Eleitoral', 'CDE', 'MOVIMENTOS'),
----('Liga Comunista Internacionalista', 'LCI', 'MOVIMENTOS'),
----('Movimento Popular de Libertação de Angola', 'MPLA', 'MOVIMENTOS'),
----('União Nacional para a Independência Total de Angola', 'UNITA', 'MOVIMENTOS'),
----('Partido Africano para a Independência da Guiné e Cabo Verde', 'PAIGC', 'MOVIMENTOS'),
------- Eventos
--('Largo do Carmo', null, 'EVENTOS'),
--('Terreiro do Paço', null, 'EVENTOS'),
--('Escola Prática de Cavalaria', null, 'EVENTOS'),
--('Portugal e o Futuro', null, 'EVENTOS'),
--('Operação Outono', null, 'EVENTOS'),
--('Assalto ao Santa Maria',  null, 'EVENTOS'),
--('Primavera Marcelista', null, 'EVENTOS'),
--('Operação Nó Górdio', null, 'EVENTOS'),
--('Golpe das Caldas', 'Levantamento das Caldas, Intentona das Caldas, Revolta das Caldas', 'EVENTOS'),
--('Operação Mar Verde', null, 'EVENTOS'),
--('Massacre de WiriyAmu', null, 'EVENTOS'),
--('Crise Académica', 'Lutas Académicas', 'EVENTOS'),
--('Fuga de Peniche',null, 'EVENTOS'),
--('Operação Vagô', null, 'EVENTOS'),
--('Reunião de Alcáçovas', null, 'EVENTOS'),
--('Cascais Jazz', 'Festival de Jazz de Cascais 1971, Cascais Jazz 1971, Cascais Jazz 71', 'EVENTOS'),
--('Coliseu dos Recreios', '29 de Março, o primeiro dos cantos livres', 'EVENTOS'),
--('Processo Revolucionário em Curso', 'PREC', 'EVENTOS');