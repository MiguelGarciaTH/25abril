insert into quote(author, "text") values
('Jorge Sampaio', '25 de Abril sempre'),
('Manuel Alegre', '"Foram dias foram anos a esperar por um só dia.<br/>Alegrias. Desenganos.<br/>Foi o tempo que doía com seus riscos e seus danos.<br/>Foi a noite e foi o dia na esperança de um só dia."'),
('Sophia De Mello Breyner','Esta é a madrugada que eu esperava<br/>O dia inicial inteiro e limpo<br/>Onde emergimos da noite e do silêncio<br/>E livres habitamos a substância do tempo.<br/>'),
('Salgueiro Maia','Meus senhores, como todos sabem, há diversas modalidades de Estado. Os estados socialistas, os estados capitalistas e o estado a que chegámos. Ora, nesta noite solene, vamos acabar com o estado a que chegámos');

insert into rate_limiter(description, counter, counter_limit, sleep_time, locked) values
('arquivo.pt', 0, 240, 60000, false);

insert into site(name, url) values
('Publico', 'http://www.publico.pt');
--('Diário de Noticias', 'http://www.dn.pt'),
--('Jornal de Noticias', 'http://www.jn.pt'),
--('Expresso', 'http://www.expresso.pt'),
--('Observador','https://observador.pt/'),
--('Sic Noticias','https://sicnoticias.pt/'),
--('TSF', 'https://www.tsf.pt/');

insert into search_entity (name, aliases, type) values
('Processo Estudantil', null, 'EVENTO'),
('Barbieri Cardoso', null, 'POLITICO'),
('Seara Nova', null, 'JORNAL'),
('República', null, 'JORNAL'),
('O Século Ilustrado', null, 'JORNAL'),
('Diário Popular', null, 'JORNAL'),
('Flama', null, 'JORNAL'),
('A Mosca', null, 'JORNAL'),
('Raul Rego', null,  'POLITICO'),
('Henrique Galvão', null, 'POLITICO'),
('Prisão de Caxias', null, 'PRISAO'),
('Prisão do Aljube', null,  'PRISAO'),
('Prisão de Peniche', null, 'PRISAO'),
('Tarrafal', null, 'PRISAO'),
('União Nacional', 'UN, Ação Nacional Popular', 'PARTIDO'),
('Operação Outono', null, 'EVENTO'),
('Assalto ao Santa Maria',  null, 'EVENTO'),
('Movimento de Unidade Democrática','MUD', 'MOVIMENTO'),
('Junta de Salvação Nacional', null, 'MOVIMENTO'),
('Primavera Marcelista', null, 'EVENTO'),
('Operação Nó Górdio', null, 'EVENTO'),
('Mocidade Portuguesa', null, 'MOVIMENTO'),
('Golpe das Caldas', null, 'EVENTO'),
('Alexandre Carvalho e Neto', null, 'POLITICO'),
('Operação Mar Verde', null, 'EVENTO'),
('Pedro Feytor Pinto', null, 'POLITICO'),
('Rosa Casaco', null, 'POLITICO'),
('Partido Comunista dos Trabalhadores Portugueses', 'MRPP', 'PARTIDO'),
('Confederação Geral dos Trabalhadores Portugueses — Intersindical Nacional', 'CGTP', 'MOVIMENTO'),
('Marcelo Caetano', null, 'POLITICO'),
('Ação Revolucionária Armada', 'ARA', 'MOVIMENTO'),
('António Oliveira Salazar', null, 'POLITICO'),
('Mário Soares', null, 'POLITICO'),
('Francisco Sá Carneiro', null, 'POLITICO'),
('Francisco Balsemão', null, 'POLITICO'),
('Diretório Democrato-Social', null, 'MOVIMENTO'),
('Resistência Republicana Socialista', null, 'MOVIMENTO'),
('Frente Portuguesa de Libertação Nacional', 'FPLN', 'MOVIMENTO'),
('Ballet Rose', null,'EVENTO'),
('Estado Novo', null, 'MOVIMENTO'),
('Retornados', null, 'MOVIMENTO'),
('Oscar Carmona', null,'POLITICO'),
('Duarte Pacheco', null, 'POLITICO'),
('Palma Inácio ', null, 'POLITICO'),
('Ala Liberal', null, 'MOVIMENTO'),
('Cardeal Manuel Gonçalves cerejeira', 'Cardeal Cerejeira', 'POLITICO'),
('Polícia Internacional e de Defesa do Estado','PIDE, DGS, Direcção-Geral de Segurança, P.I.D.E, rua antónio maria cardoso', 'MOVIMENTO'),
('António de Spínola', 'General Spínola',  'POLITICO'),
('Massacre de WiriyAmu', null, 'EVENTO'),
('José Ribeiro Santos',  null, 'POLITICO'),
('Américo Tomás', 'Américo Thomaz','POLITICO'),
('Frente de Libertação de Moçambique', 'FRELIMO','PARTIDO'),
('Partido Comunista Português', 'PCP', 'PARTIDO'),
('Álvaro Cunhal', null, 'POLITICO'),
('Católicos Progressistas',null, 'MOVIMENTO'),
('Humberto Delgado', 'General sem medo', 'POLITICO'),
('Miller Guerra', null, 'POLITICO'),
('Cazal Ribeiro', null, 'POLITICO'),
('Ramalho Eanes', null, 'POLITICO'),
('Vasco Lourenço', null, 'POLITICO'),
('Firmino Miguel', null, 'POLITICO'),
('Costa gomes', null, 'POLITICO'),
('Forças Populares 25 de Abril', 'FP25', 'MOVIMENTO'),
('Liga de Unidade e Ação Revolucionária', 'LUAR', 'MOVIMENTO'),
('Kaúlza de Arriaga', null, 'POLITICO'),
('Carlos Fabião', null, 'POLITICO'),
('Amilcar Cabral', null, 'POLITICO'),
('Otelo Saraiva de Carvalho', null, 'POLITICO'),
('Salgado Zenha', null, 'POLITICO'),
('Salgueiro Maia', 'Capitão Maia', 'POLITICO'),
('Melo Antunes', null, 'POLITICO'),
('Garcia dos Santos', null, 'POLITICO'),
('Jaime Neves', null, 'POLITICO'),
('Fernando da Silva Pais', null, 'POLITICO'),
('Adelino da palma Carlos', null, 'POLITICO'),
('Vasco Gonçalves', null, 'POLITICO'),
('Pinheiro de Azevedo', null, 'POLITICO'),
('José Afonso', 'Zeca Afonso, Zeca', 'ARTISTA'),
('Sérgio Godinho', null, 'ARTISTA'),
('Fausto Bordalo Dias', 'Fausto', 'ARTISTA'),
('José Mario Branco', 'Zé Mario Branco', 'ARTISTA'),
('Adriano Correia de Oliveira', null, 'ARTISTA'),
('Fernando Tordo', null, 'ARTISTA'),
('Francisco Fanhais', 'Padre Fanhais', 'ARTISTA'),
('Janita Salomé', null, 'ARTISTA'),
('José Barata-Moura', null, 'ARTISTA'),
('Luís Cília', null, 'ARTISTA'),
('Manuel Freire', null, 'ARTISTA'),
('Vitorino Salomé', null, 'ARTISTA'),
('José Jorge Letria', null, 'ARTISTA'),
('Natália Correia', null,  'ARTISTA'),
('Maria Teresa Horta',  'Três Marias, Novas Cartas Portuguesas', 'ARTISTA'),
('Maria Isabel Barreno', 'Três Marias, Novas Cartas Portuguesas', 'ARTISTA'),
('Maria Velho da Costa', 'Três Marias, Novas Cartas Portuguesas', 'ARTISTA'),
('Sophia de Mello Breyner Andresen', 'Sophia de Mello Breyner', 'ARTISTA'),
('Frank Carlucci', null, 'POLITICO'),
('Isabel do Carmo', null, 'POLITICO'),
('Camilo Mortágua', null, 'POLITICO'),
('António Barreto', null, 'POLITICO'),
('Medeiros Ferreira', null, 'POLITICO'),
('Francisco Sousa Tavares', null, 'POLITICO'),
('Veiga Simão', null, 'POLITICO'),
('Baltasar Rebelo de Sousa', null, 'POLITICO'),
('José Miguel Júdice', null, 'POLITICO'),
('Eduardo Fontes', null, 'POLITICO'),
('Adelino da Silva Tinoco', 'Inspector Tinoco', 'POLITICO'),
('Manuel Tengarrinha', null, 'POLITICO'),
('Hermínio da Palma Inácio', null, 'POLITICO'),
('Exército de Libertação de Portugal','ELP', 'MOVIMENTO'),
('Partido Popular Monárquico', 'PPM', 'PARTIDO'),
('Partido do Centro Democrático Social', 'CDS', 'PARTIDO'),
('Partido Socialista','PS', 'PARTIDO'),
('Partido Social Democrata', 'PPD-PSD, PSD', 'PARTIDO'),
('Frente Socialista Popular', 'FSP', 'PARTIDO'),
('Movimento de Esquerda Socialista', 'MES', 'PARTIDO'),
('União Democrática Popular','UDP', 'PARTIDO'),
('Movimento Maria da Fonte', null, 'MOVIMENTO'),
('Comando Operacional do Continente', 'COPCON', 'MOVIMENTO'),
('Três Marias', null, 'MOVIMENTO'),
('Brigadas Revolucionárias',null, 'MOVIMENTO'),
('Movimento dos capitães',null, 'MOVIMENTO'),
('Mocidade portuguesa',null, 'MOVIMENTO'),
('Margarida Tengarrinha',null, 'POLITICO'),
('José Dias Coelho',null, 'POLITICO'),
('Fuga de Peniche',null, 'EVENTO'),
('Processo Revolucionário em Curso', 'PREC', 'EVENTO');