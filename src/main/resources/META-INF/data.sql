INSERT INTO UserAccount (id, username, password, expiringDate) VALUES (1, 'admin', '$2a$10$32Hw5zY7uvx0EgsfE/yQNuuL.Nrt5srepNP.vlmES2VKbYAnOBqC6', 9223372036854775807);
INSERT INTO UserAccount (id, username, password, expiringDate) VALUES (2, 'user', '$2a$10$32Hw5zY7uvx0EgsfE/yQNuuL.Nrt5srepNP.vlmES2VKbYAnOBqC6', 9223372036854775807);

INSERT INTO UserAccount_authorities (UserAccount_id, authority) VALUES (1, 'ADMIN');
INSERT INTO UserAccount_authorities (UserAccount_id, authority) VALUES (1, 'USER');
INSERT INTO UserAccount_authorities (UserAccount_id, authority) VALUES (2, 'USER');