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
    role      character varying           NOT NULL,
    dt_create timestamp without time zone NOT NULL DEFAULT 'now()',
    dt_update timestamp without time zone NOT NULL DEFAULT 'now()',
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS app.roles
    OWNER to app;

CREATE TABLE app.role_privilege
(
    role_id      uuid REFERENCES app.roles (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    privilege_id uuid REFERENCES app.privileges (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT "role_privilege_PK" PRIMARY KEY (role_id, privilege_id)
);

ALTER TABLE IF EXISTS app.role_privilege
    OWNER to app;

CREATE TABLE app.users
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

CREATE TABLE app.user_roles
(
    user_id uuid REFERENCES app.users (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    role_id uuid REFERENCES app.roles (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT "user_roles_PK" PRIMARY KEY (user_id, role_id)
);

ALTER TABLE IF EXISTS app.user_roles
    OWNER to app;