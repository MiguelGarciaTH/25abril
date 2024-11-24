
CREATE SEQUENCE IF NOT EXISTS site_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS site (
    id integer NOT NULL DEFAULT nextval('site_seq'),
    name varchar(255) NOT NULL,
    acronym varchar(5),
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
    aliases text,
    type varchar(50) NOT NULL CHECK (type in ('CAPITAES','ESCRITORES', 'CANTORES', 'JORNALISTAS','MOVIMENTOS','EVENTOS','LOCAIS','OPRESSORES','RESISTENTES')),
    image_url text,
    biography text,
    birth_date varchar(50),
    death_date varchar(50),

    CONSTRAINT search_entity_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS changelog_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS article_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS article (
    id integer NOT NULL DEFAULT nextval('article_seq'),
    "date" timestamp without time zone NOT NULL DEFAULT LOCALTIMESTAMP,
    site_id integer NOT NULL,
    title text NOT NULL,
    original_title text NOT NULL,
    url text NOT NULL,
    text_url text NOT NULL,
    original_url text NOT NULL,
    "text" text,

    CONSTRAINT article_pk PRIMARY KEY (id),
    CONSTRAINT article_fk_site_id FOREIGN KEY (site_id) REFERENCES site(id)
);

CREATE SEQUENCE IF NOT EXISTS article_search_entity_association_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS article_search_entity_association (
    id integer NOT NULL DEFAULT nextval('article_search_entity_association_seq'),
    article_id integer NOT NULL,
    search_entity_id integer NOT NULL,
    "text" text,
    score integer,
    individual_score jsonb,

    CONSTRAINT article_entity_association_pk PRIMARY KEY (id),
    CONSTRAINT article_entity_association_uq UNIQUE (article_id, search_entity_id),
    CONSTRAINT article_entity_association_fk_article_id FOREIGN KEY (article_id) REFERENCES article(id),
    CONSTRAINT article_entity_association_fk_search_entity_id FOREIGN KEY (search_entity_id) REFERENCES search_entity(id)
);

CREATE SEQUENCE IF NOT EXISTS quote_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS quote (
    id integer NOT NULL DEFAULT nextval('quote_seq'),
    "text" text NOT NULL,
    author varchar(255),

    CONSTRAINT quote_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS rate_limiter_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS rate_limiter (
    id integer NOT NULL DEFAULT nextval('rate_limiter_seq'),
    "description" text NOT NULL,
    counter integer,
    counter_limit integer,
    sleep_time integer,
    locked boolean,

    CONSTRAINT rate_limiter_pk PRIMARY KEY (id)
);


