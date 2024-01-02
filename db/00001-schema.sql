
CREATE SEQUENCE IF NOT EXISTS site_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS site (
    id integer NOT NULL DEFAULT nextval('site_seq'),
    name varchar(255) NOT NULL,
    url varchar(255) NOT NULL,

    CONSTRAINT site_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS integration_log_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS integration_log (
    id integer NOT NULL DEFAULT nextval('integration_log_seq'),
    event_timestamp timestamp without time zone NOT NULL DEFAULT LOCALTIMESTAMP,
    url varchar(255) NOT NULL,
    origin varchar(255) NOT NULL,
    status varchar(2) NOT NULL CHECK (status in ('TS','TE','TR')),
    input_data text,
    output_data text,

    CONSTRAINT integration_log_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS search_entity_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS search_entity (
    id integer NOT NULL DEFAULT nextval('search_entity_seq'),
    name varchar(255) NOT NULL,
    abbreviation varchar(50),
    keywords varchar(200),
    aliases text,
    type varchar(50) NOT NULL CHECK (type in ('POLITICO','ARTISTA','JORNAL','PARTIDO','MOVIMENTO','EVENTO','LOCAL', 'PRISAO')),

    CONSTRAINT search_entity_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS changelog_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS changelog (
    id integer NOT NULL DEFAULT nextval('changelog_seq'),
    "from_timestamp" timestamp without time zone NOT NULL DEFAULT LOCALTIMESTAMP,
    "to_timestamp" timestamp without time zone NOT NULL DEFAULT LOCALTIMESTAMP,
    search_entity_id integer NOT NULL,
    site_id integer NOT NULL,
    total_entries integer default 0,

    CONSTRAINT changelog_pk PRIMARY KEY (id),
    CONSTRAINT changelog_fk_search_entity_id FOREIGN KEY (search_entity_id) REFERENCES search_entity(id),
    CONSTRAINT changelog_fk_site_id FOREIGN KEY (site_id) REFERENCES site(id)
);

CREATE SEQUENCE IF NOT EXISTS article_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS article (
    id integer NOT NULL DEFAULT nextval('article_seq'),
    "date" timestamp without time zone NOT NULL DEFAULT LOCALTIMESTAMP,
    site_id integer NOT NULL,
    url text NOT NULL,
    metadata_url text NOT NULL,
    no_frame_url text NOT NULL,
    text_url text NOT NULL,
    digest varchar(50) NOT NULL,

    CONSTRAINT article_pk PRIMARY KEY (id),
    CONSTRAINT article_fk_site_id FOREIGN KEY (site_id) REFERENCES site(id)
);

CREATE TABLE IF NOT EXISTS article_entity_association (
    article_id integer NOT NULL,
    search_entity_id integer NOT NULL,

    CONSTRAINT article_entity_association_pk PRIMARY KEY (article_id, search_entity_id),
    CONSTRAINT article_entity_association_fk_article_id FOREIGN KEY (article_id) REFERENCES article(id),
    CONSTRAINT article_entity_association_fk_search_entity_id FOREIGN KEY (search_entity_id) REFERENCES search_entity(id)
);