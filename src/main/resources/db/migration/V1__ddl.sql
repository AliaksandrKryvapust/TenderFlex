CREATE SCHEMA IF NOT EXISTS app
    AUTHORIZATION app;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS app.privileges
(
    id        uuid,
    privilege character varying           NOT NULL,
    dt_create timestamp without time zone NOT NULL DEFAULT 'now()',
    dt_update timestamp without time zone NOT NULL DEFAULT 'now()',
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS app.privileges
    OWNER to app;

ALTER TABLE IF EXISTS app.privileges
    ADD CONSTRAINT "privileges_UK" UNIQUE (privilege);

CREATE TABLE app.roles
(
    id        uuid,
    role_type character varying NOT NULL,
    dt_create timestamp without time zone NOT NULL DEFAULT 'now()',
    dt_update timestamp without time zone NOT NULL DEFAULT 'now()',
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS app.roles
    OWNER to app;

CREATE TABLE IF NOT EXISTS app.role_privilege
(
    role_id      uuid REFERENCES app.roles (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    privilege_id uuid REFERENCES app.privileges (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT "role_privilege_PK" PRIMARY KEY (role_id, privilege_id)
);

ALTER TABLE IF EXISTS app.role_privilege
    OWNER to app;

CREATE TABLE IF NOT EXISTS app.users
(
    id        uuid,
    username  character varying(20)       NOT NULL,
    password  character varying(200)      NOT NULL,
    email     character varying(50)       NOT NULL,
    status    character varying(50)       NOT NULL,
    dt_create timestamp without time zone NOT NULL DEFAULT 'now()',
    dt_update timestamp without time zone NOT NULL DEFAULT 'now()',
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS app.users
    OWNER to app;

CREATE TABLE IF NOT EXISTS app.user_roles
(
    user_id uuid REFERENCES app.users (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    role_id uuid REFERENCES app.roles (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT "user_roles_PK" PRIMARY KEY (user_id, role_id)
);

ALTER TABLE IF EXISTS app.user_roles
    OWNER to app;

CREATE TABLE IF NOT EXISTS app.tenders
(
    id                  uuid                        NOT NULL,
    user_id             uuid                        NOT NULL REFERENCES app.users (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    official_name       character varying(50)       NOT NULL,
    registration_number character varying(50)       NOT NULL,
    country             character varying(50)       NOT NULL,
    town                character varying(50),
    name                character varying(50)       NOT NULL,
    surname             character varying(50)       NOT NULL,
    phone_number        bigint                      NOT NULL,
    cpv_code            character varying           NOT NULL,
    tender_type         character varying           NOT NULL,
    description         character varying(250),
    min_price           bigint                      NOT NULL,
    max_price           bigint                      NOT NULL,
    currency            character varying           NOT NULL,
    publication         date                        NOT NULL,
    submission_deadline date                        NOT NULL,
    tender_status       character varying           NOT NULL,
    dt_create           timestamp without time zone NOT NULL DEFAULT now(),
    dt_update           timestamp without time zone NOT NULL DEFAULT now(),
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS app.tenders
    OWNER to app;

CREATE TABLE IF NOT EXISTS app.offers
(
    id                  uuid                        NOT NULL,
    user_id             uuid                        NOT NULL REFERENCES app.users (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    tender_id           uuid                        NOT NULL REFERENCES app.tenders (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    offer_status        character varying           NOT NULL,
    official_name       character varying(50)       NOT NULL,
    registration_number character varying(50)       NOT NULL,
    country             character varying(50)       NOT NULL,
    town                character varying(50),
    name                character varying(50)       NOT NULL,
    surname             character varying(50)       NOT NULL,
    phone_number        bigint                      NOT NULL,
    bid_price           bigint                      NOT NULL,
    currency            character varying           NOT NULL,
    proposition_file    uuid,
    dt_create           timestamp without time zone NOT NULL DEFAULT now(),
    dt_update           timestamp without time zone NOT NULL DEFAULT now(),
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS app.offers
    OWNER to app;

CREATE TABLE IF NOT EXISTS app.contracts
(
    id                  uuid,
    contract_file       uuid,
    award_decision_file uuid,
    contract_deadline   date,
    dt_create           timestamp without time zone NOT NULL DEFAULT now(),
    dt_update           timestamp without time zone NOT NULL DEFAULT now(),
    offer_id            uuid REFERENCES app.offers (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    tender_id           uuid NOT NULL REFERENCES app.tenders (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS app.contracts
    OWNER to app;

CREATE TABLE IF NOT EXISTS app.reject_decision
(
    id                   uuid,
    offer_id             uuid REFERENCES app.offers (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    tender_id            uuid                        NOT NULL REFERENCES app.tenders (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    reject_decision_file uuid,
    dt_create            timestamp without time zone NOT NULL DEFAULT now(),
    dt_update            timestamp without time zone NOT NULL DEFAULT now(),
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS app.reject_decision
    OWNER to app;