INSERT INTO app.privileges(id, privilege)
VALUES ('211e9bf1-1ad2-4054-9fec-c086f07702f8', 'CAN_READ_TENDER');
INSERT INTO app.privileges(id, privilege)
VALUES ('6099eba7-a686-418d-92c2-f8686b97457b', 'CAN_CREATE_OFFER');
INSERT INTO app.privileges(id, privilege)
VALUES ('259c12c2-1843-4b8a-b72d-41d76fae770a', 'CAN_READ_OFFER');
INSERT INTO app.privileges(id, privilege)
VALUES ('5e60f64d-6bf4-40ac-b3e5-b34c3bb559e7', 'CAN_READ_CONTRACT_STATUS');
INSERT INTO app.privileges(id, privilege)
VALUES ('b14fa015-4feb-4c80-bf7e-de3bcffb6979', 'CAN_READ_CONTRACT_PDF');
INSERT INTO app.privileges(id, privilege)
VALUES ('a8f2c8fc-b315-4d40-845b-e1933606b6ad', 'CAN_APPROVE_DECLINE_CONTRACT');
INSERT INTO app.privileges(id, privilege)
VALUES ('8d313e56-0b9c-4d55-9f8f-538a09f74e64', 'CAN_CREATE_AND_PUBLISH_TENDER');
INSERT INTO app.privileges(id, privilege)
VALUES ('a905cc74-980a-4138-94f5-e01278c7a77e', 'CAN_ACCEPT_DECLINE_OFFER');
INSERT INTO app.privileges(id, privilege)
VALUES ('bf22153e-9657-4c00-b74c-d9d7c7b635b0', 'CAN_UPLOAD_CONTRACT');

INSERT INTO app.roles(id, role)
VALUES ('d1bdaa3d-3d8a-404f-990d-2e36a42f0329', 'CONTRACTOR');
INSERT INTO app.roles(id, role)
VALUES ('ac1139bd-5834-45f5-8632-04e6b073a5f4', 'BIDDER');
INSERT INTO app.roles(id, role)
VALUES ('41d6bce0-d501-4fba-80ac-3b32234d64bc', 'ADMIN');

INSERT INTO app.role_privilege(	role_id, privilege_id)
VALUES ('d1bdaa3d-3d8a-404f-990d-2e36a42f0329', '8d313e56-0b9c-4d55-9f8f-538a09f74e64');
INSERT INTO app.role_privilege(	role_id, privilege_id)
VALUES ('d1bdaa3d-3d8a-404f-990d-2e36a42f0329', '211e9bf1-1ad2-4054-9fec-c086f07702f8');
INSERT INTO app.role_privilege(	role_id, privilege_id)
VALUES ('d1bdaa3d-3d8a-404f-990d-2e36a42f0329', '259c12c2-1843-4b8a-b72d-41d76fae770a');
INSERT INTO app.role_privilege(	role_id, privilege_id)
VALUES ('d1bdaa3d-3d8a-404f-990d-2e36a42f0329', 'a905cc74-980a-4138-94f5-e01278c7a77e');
INSERT INTO app.role_privilege(	role_id, privilege_id)
VALUES ('d1bdaa3d-3d8a-404f-990d-2e36a42f0329', 'bf22153e-9657-4c00-b74c-d9d7c7b635b0');
INSERT INTO app.role_privilege(	role_id, privilege_id)
VALUES ('d1bdaa3d-3d8a-404f-990d-2e36a42f0329', '5e60f64d-6bf4-40ac-b3e5-b34c3bb559e7');

INSERT INTO app.role_privilege(	role_id, privilege_id)
VALUES ('ac1139bd-5834-45f5-8632-04e6b073a5f4', '211e9bf1-1ad2-4054-9fec-c086f07702f8');
INSERT INTO app.role_privilege(	role_id, privilege_id)
VALUES ('ac1139bd-5834-45f5-8632-04e6b073a5f4', '6099eba7-a686-418d-92c2-f8686b97457b');
INSERT INTO app.role_privilege(	role_id, privilege_id)
VALUES ('ac1139bd-5834-45f5-8632-04e6b073a5f4', '259c12c2-1843-4b8a-b72d-41d76fae770a');
INSERT INTO app.role_privilege(	role_id, privilege_id)
VALUES ('ac1139bd-5834-45f5-8632-04e6b073a5f4', '5e60f64d-6bf4-40ac-b3e5-b34c3bb559e7');
INSERT INTO app.role_privilege(	role_id, privilege_id)
VALUES ('ac1139bd-5834-45f5-8632-04e6b073a5f4', 'b14fa015-4feb-4c80-bf7e-de3bcffb6979');
INSERT INTO app.role_privilege(	role_id, privilege_id)
VALUES ('ac1139bd-5834-45f5-8632-04e6b073a5f4', 'a8f2c8fc-b315-4d40-845b-e1933606b6ad');