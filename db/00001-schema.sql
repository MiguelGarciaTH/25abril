
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
    url text NOT NULL,
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
    type varchar(50) NOT NULL CHECK (type in ('CAPITAES','ESCRITORES', 'MUSICOS', 'JORNALISTAS','MOVIMENTOS','EVENTOS','LOCAIS','OPRESSORES','RESISTENTES','POLITICOS')),
    image_url text,
    biography text,
    birth_date varchar(50),
    death_date varchar(50),
    names_vector tsvector GENERATED ALWAYS AS (to_tsvector('portuguese', COALESCE(name, aliases))) STORED,

    CONSTRAINT search_entity_pk PRIMARY KEY (id)
);

CREATE INDEX names_idx ON search_entity USING GIN (names_vector);

CREATE SEQUENCE IF NOT EXISTS article_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS article (
    id integer NOT NULL DEFAULT nextval('article_seq'),
    "date" timestamp without time zone NOT NULL DEFAULT LOCALTIMESTAMP,
    site_id integer NOT NULL,
    title text NOT NULL,
    url text NOT NULL,
    trimmed_url text NOT NULL,
    image_path text,
    has_image boolean NOT NULL default false,
    "text" text,
    "summary" text,
    contextual_score numeric(10,2),
    contextual_score_details jsonb,
    summary_score numeric(10,2),
    summary_score_details jsonb,
    title_vector tsvector GENERATED ALWAYS AS (to_tsvector('portuguese', COALESCE(title))) STORED,
    summary_vector tsvector GENERATED ALWAYS AS (to_tsvector('portuguese', COALESCE("summary"))) STORED,

    CONSTRAINT article_pk PRIMARY KEY (id),
    CONSTRAINT article_fk_site_id FOREIGN KEY (site_id) REFERENCES site(id)
);
CREATE INDEX title_idx ON article USING GIN (title_vector);
CREATE INDEX summary_idx ON article USING GIN (summary_vector);

CREATE SEQUENCE IF NOT EXISTS article_search_entity_association_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS article_search_entity_association (
    id integer NOT NULL DEFAULT nextval('article_search_entity_association_seq'),
    article_id integer NOT NULL,
    search_entity_id integer NOT NULL,
    entity_score numeric(10,2),
    entity_score_details jsonb,

    CONSTRAINT article_entity_association_pk PRIMARY KEY (id),
    CONSTRAINT article_entity_association_uq UNIQUE (article_id, search_entity_id),
    CONSTRAINT article_entity_association_fk_article_id FOREIGN KEY (article_id) REFERENCES article(id),
    CONSTRAINT article_entity_association_fk_search_entity_id FOREIGN KEY (search_entity_id) REFERENCES search_entity(id)
);

CREATE SEQUENCE IF NOT EXISTS keyword_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS keyword (
    id integer NOT NULL DEFAULT nextval('keyword_seq'),
    keyword varchar(80),

    CONSTRAINT keyword_pk PRIMARY KEY (id),
    CONSTRAINT keyword_uq_keyword UNIQUE(keyword)
);

CREATE SEQUENCE IF NOT EXISTS article_keyword_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS article_keyword (
    id integer NOT NULL DEFAULT nextval('article_keyword_seq'),
    article_id integer not null,
    keyword_id integer not null,
    score numeric(10,2),

    CONSTRAINT article_keyword_pk PRIMARY KEY (id),
    CONSTRAINT article_keyword_uq_article_id_keyword_id UNIQUE(article_id, keyword_id),
    CONSTRAINT article_keyword_fk_article_id FOREIGN KEY (article_id) REFERENCES article(id),
    CONSTRAINT article_keyword_fk_keyword_id FOREIGN KEY (keyword_id) REFERENCES keyword(id)
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

CREATE SEQUENCE IF NOT EXISTS metric_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS metric (
    id integer NOT NULL DEFAULT nextval('metric_seq'),
    "key" varchar(255),
    "value" bigint,

    CONSTRAINT metric_pk PRIMARY KEY (id),
    CONSTRAINT metric_uq_key UNIQUE("key")
);


