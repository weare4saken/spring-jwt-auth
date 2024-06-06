BEGIN;

INSERT INTO roles (type)
VALUES ('ROLE_USER') ON CONFLICT DO NOTHING;

INSERT INTO roles (type)
VALUES ('ROLE_ADMIN') ON CONFLICT DO NOTHING;

INSERT INTO users (username, password, email)
VALUES ('user', '$2a$10$GNdiqyfyUtSfanS24/SlKOtmpil6CChLEUm6EANC/7leIPI7Fg0/i','user@example.com') ON CONFLICT DO NOTHING;

INSERT INTO users (username, password, email)
VALUES ('admin', '$2a$10$GNdiqyfyUtSfanS24/SlKOtmpil6CChLEUm6EANC/7leIPI7Fg0/i','admin@example.com') ON CONFLICT DO NOTHING;

INSERT INTO user_role (user_id, role_id)

SELECT u.id, r.id
FROM users AS u
JOIN roles AS r ON u.username = 'user' AND r.type = 'ROLE_USER'
WHERE NOT EXISTS (SELECT * FROM user_role AS ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

INSERT INTO user_role (user_id, role_id)

SELECT u.id, r.id
FROM users AS u
JOIN roles AS r ON u.username = 'admin' AND r.type = 'ROLE_USER'
WHERE NOT EXISTS (SELECT * FROM user_role AS ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

INSERT INTO user_role (user_id, role_id)

SELECT u.id, r.id
FROM users AS u
JOIN roles AS r ON u.username = 'admin' AND r.type = 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT * FROM user_role AS ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

COMMIT;