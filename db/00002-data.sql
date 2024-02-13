insert into quote(author, "text") values
('Jorge Sampaio', '25 de Abril sempre'),
('Manuel Alegre', '"Foram dias foram anos a esperar por um só dia.<br/>Alegrias. Desenganos.<br/>Foi o tempo que doía com seus riscos e seus danos.<br/>Foi a noite e foi o dia na esperança de um só dia."'),
('Sophia De Mello Breyner','Esta é a madrugada que eu esperava<br/>O dia inicial inteiro e limpo<br/>Onde emergimos da noite e do silêncio<br/>E livres habitamos a substância do tempo.<br/>'),
('Salgueiro Maia','Meus senhores, como todos sabem, há diversas modalidades de Estado. Os estados socialistas, os estados capitalistas e o estado a que chegámos. Ora, nesta noite solene, vamos acabar com o estado a que chegámos');

insert into rate_limiter(description, counter, counter_limit, sleep_time, locked) values
('arquivo.pt', 0, 240, 60000, false),
('dbpedia', 0, 95, 60000, false);

insert into site(name, url) values
('Publico', 'http://www.publico.pt'),
('Diário de Notícias', 'http://www.dn.pt'),
('Jornal de Notícias', 'http://www.jn.pt'),
('Expresso', 'http://www.expresso.pt'),
('Observador','https://observador.pt/'),
('Sic Notícias','https://sicnoticias.pt/'),
('TSF', 'https://www.tsf.pt/');

insert into search_entity (name, aliases, type) values
-- Jornais
('Seara Nova', null, 'JORNAIS'),
('República', null, 'JORNAIS'),
('O Século Ilustrado', null, 'JORNAIS'),
('Diário Popular', null, 'JORNAIS'),
('Flama', null, 'JORNAIS'),
('A Mosca', null, 'JORNAIS'),
-- Artistas
('Sérgio Godinho', null, 'ARTISTAS'),
('Fausto Bordalo Dias', 'Fausto', 'ARTISTAS'),
('José Mário Branco', 'Zé Mário Branco', 'ARTISTAS'),
('Adriano Correia de Oliveira', null, 'ARTISTAS'),
('Fernando Tordo', null, 'ARTISTAS'),
('Francisco Fanhais', 'Padre Fanhais', 'ARTISTAS'),
('Janita Salomé', null, 'ARTISTAS'),
('José Barata-Moura', 'José Barata Moura', 'ARTISTAS'),
('Luís Cília', null, 'ARTISTAS'),
('Manuel Freire', null, 'ARTISTAS'),
('Vitorino Salomé', 'Vitorino', 'ARTISTAS'),
('José Jorge Letria', 'Jorge Letria', 'ARTISTAS'),
('Natália Correia', null,  'ARTISTAS'),
('Maria Teresa Horta',  'Três Marias, Novas Cartas Portuguesas', 'ARTISTAS'),
('Maria Isabel Barreno', 'Três Marias, Novas Cartas Portuguesas', 'ARTISTAS'),
('Maria Velho da Costa', 'Três Marias, Novas Cartas Portuguesas', 'ARTISTAS'),
('Sophia de Mello Breyner Andresen', 'Sophia de Mello Breyner', 'ARTISTAS'),
('José Afonso', 'Zeca Afonso, Zeca', 'ARTISTAS'),
--- Locais
('Largo do Carmo', null, 'LOCAIS'),
('Rádio Clube Português', null, 'LOCAIS'),
('Terreiro do Paço', null, 'LOCAIS'),
('Rádio Renascença', null, 'LOCAIS'),
('Escola Prática de Cavalaria', null, 'LOCAIS'),
('Rádio Clube Português', 'RCP', 'LOCAIS'),
('Prisão de Caxias', null, 'LOCAIS'),
('Prisão do Aljube', null,  'LOCAIS'),
('Prisão de Peniche', null, 'LOCAIS'),
('Tarrafal', 'Campo de Concetração do Tarrafal, Campo da Morte Lenta', 'LOCAIS'),
--- Politicos
('Margarida Tengarrinha',null, 'POLITICOS'),
('José Dias Coelho', null, 'POLITICOS'),
('José Manuel Tengarrinha', null, 'POLITICOS'),
('Raul Rego', null,  'POLITICOS'),
('Henrique Galvão', null, 'POLITICOS'),
('Barbieri Cardoso', null, 'POLITICOS'),
('Carlos Antunes', null, 'POLITICOS'),
('Pedro Feytor Pinto', null, 'POLITICOS'),
('Rosa Casaco', null, 'POLITICOS'),
('Alexandre Carvalho e Neto', null, 'POLITICOS'),
('Marcelo Caetano', null, 'POLITICOS'),
('António de Oliveira Salazar', null, 'POLITICOS'),
('Mário Soares', null, 'POLITICOS'),
('Francisco Sá Carneiro', 'Sá Carneiro', 'POLITICOS'),
('Francisco Balsemão', null, 'POLITICOS'),
('Oscar Carmona', null,'POLITICOS'),
('Duarte Pacheco', null, 'POLITICOS'),
('Palma Inácio ', null, 'POLITICOS'),
('Cardeal Manuel Gonçalves cerejeira', 'Cardeal Cerejeira', 'POLITICOS'),
('António de Spínola', 'General Spínola',  'POLITICOS'),
('José Ribeiro Santos',  null, 'POLITICOS'),
('Américo Tomás', 'Américo Thomaz','POLITICOS'),
('Álvaro Cunhal', null, 'POLITICOS'),
('Humberto Delgado', 'General sem medo', 'POLITICOS'),
('Miller Guerra', null, 'POLITICOS'),
('Cazal Ribeiro', null, 'POLITICOS'),
('Ramalho Eanes', null, 'POLITICOS'),
('Vasco Lourenço', null, 'POLITICOS'),
('Firmino Miguel', null, 'POLITICOS'),
('Costa Gomes', null, 'POLITICOS'),
('Kaúlza de Arriaga', null, 'POLITICOS'),
('Carlos Fabião', null, 'POLITICOS'),
('Amilcar Cabral', null, 'POLITICOS'),
('Otelo Saraiva de Carvalho', null, 'POLITICOS'),
('Salgado Zenha', null, 'POLITICOS'),
('Salgueiro Maia', 'Capitão Maia', 'POLITICOS'),
('Melo Antunes', null, 'POLITICOS'),
('Garcia dos Santos', null, 'POLITICOS'),
('Jaime Neves', null, 'POLITICOS'),
('Fernando da Silva Pais', null, 'POLITICOS'),
('Adelino da Palma Carlos', null, 'POLITICOS'),
('Adelino Gomes', null, 'POLITICOS'),
('Vasco Gonçalves', null, 'POLITICOS'),
('Pinheiro de Azevedo', null, 'POLITICOS'),
('Frank Carlucci', null, 'POLITICOS'),
('Isabel do Carmo', null, 'POLITICOS'),
('Camilo Mortágua', null, 'POLITICOS'),
('António Barreto', null, 'POLITICOS'),
('Medeiros Ferreira', null, 'POLITICOS'),
('Francisco Sousa Tavares', null, 'POLITICOS'),
('Veiga Simão', null, 'POLITICOS'),
('Baltasar Rebelo de Sousa', null, 'POLITICOS'),
('José Miguel Júdice', null, 'POLITICOS'),
('Eduardo Fontes', null, 'POLITICOS'),
('Adelino da Silva Tinoco', 'Inspector Tinoco', 'POLITICOS'),
('Manuel Tengarrinha', null, 'POLITICOS'),
('Hermínio da Palma Inácio', null, 'POLITICOS'),
--- Partidos
('União Nacional', 'UN, Ação Nacional Popular', 'PARTIDOS'),
('Partido Comunista dos Trabalhadores Portugueses', 'MRPP', 'PARTIDOS'),
('Frente de Libertação de Moçambique', 'FRELIMO','PARTIDOS'),
('Partido Comunista Português', 'PCP', 'PARTIDOS'),
('Partido Popular Monárquico', 'PPM', 'PARTIDOS'),
('Partido do Centro Democrático Social', 'CDS', 'PARTIDOS'),
('Partido Socialista','PS', 'PARTIDOS'),
('Partido Social Democrata', 'PPD-PSD, PSD', 'PARTIDOS'),
('Frente Socialista Popular', 'FSP', 'PARTIDOS'),
('Movimento de Esquerda Socialista', 'MES', 'PARTIDOS'),
('União Democrática Popular','UDP', 'PARTIDOS'),
--- Movimentos
('Movimento de Unidade Democrática','MUD', 'MOVIMENTOS'),
('Junta de Salvação Nacional', null, 'MOVIMENTOS'),
('Mocidade Portuguesa', null, 'MOVIMENTOS'),
('Confederação Geral dos Trabalhadores Portugueses — Intersindical Nacional', 'CGTP', 'MOVIMENTOS'),
('Ação Revolucionária Armada', 'ARA', 'MOVIMENTOS'),
('Diretório Democrato-Social', null, 'MOVIMENTOS'),
('Resistência Republicana Socialista', null, 'MOVIMENTOS'),
('Frente Portuguesa de Libertação Nacional', 'FPLN', 'MOVIMENTOS'),
--('Estado Novo', null, 'MOVIMENTOS'),
('Retornados', null, 'MOVIMENTOS'),
('Ala Liberal', null, 'MOVIMENTOS'),
('Polícia Internacional e de Defesa do Estado','PIDE, DGS, Direcção-Geral de Segurança, P.I.D.E, Rua António Maria cardoso', 'MOVIMENTOS'),
('Católicos Progressistas',null, 'MOVIMENTOS'),
('Forças Populares 25 de Abril', 'FP25', 'MOVIMENTOS'),
('Liga de Unidade e Ação Revolucionária', 'LUAR', 'MOVIMENTOS'),
('Exército de Libertação de Portugal','ELP', 'MOVIMENTOS'),
('Movimento Maria da Fonte', null, 'MOVIMENTOS'),
('Comando Operacional do Continente', 'COPCON', 'MOVIMENTOS'),
('Brigadas Revolucionárias',null, 'MOVIMENTOS'),
('Capitães de Abril', 'Movimento dos Capitães', 'MOVIMENTOS'),
--- Eventos
('Operação Outono', null, 'EVENTOS'),
('Assalto ao Santa Maria',  null, 'EVENTOS'),
('Primavera Marcelista', null, 'EVENTOS'),
('Operação Nó Górdio', null, 'EVENTOS'),
('Golpe das Caldas', null, 'EVENTOS'),
('Operação Mar Verde', null, 'EVENTOS'),
('Ballet Rose', null,'EVENTOS'),
('Massacre de WiriyAmu', null, 'EVENTOS'),
('Processo Estudantil', null, 'EVENTOS'),
('Fuga de Peniche',null, 'EVENTOS'),
('Processo Revolucionário em Curso', 'PREC', 'EVENTOS');
