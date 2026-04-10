INSERT INTO users (email, password, first_name, last_name,
    username, phone_number, address)
VALUES
    ('admin@elibrary.com',
     '$2a$10$ySPERzSMY5Z9tC1cnmA9K.GU5mhFdr0VDAInP8lCyuaUXwc3nn3HK',
     'System', 'Administrator', 'admin',
     '+1234567890', '123 Library St, Bookville'),
    ('librarian@elibrary.com',
     '$2a$10$ySPERzSMY5Z9tC1cnmA9K.GU5mhFdr0VDAInP8lCyuaUXwc3nn3HK',
     'Library', 'Manager', 'librarian',
     '+1234567891', '456 Book Ave, Readville'),
    ('user@elibrary.com',
     '$2a$10$ySPERzSMY5Z9tC1cnmA9K.GU5mhFdr0VDAInP8lCyuaUXwc3nn3HK',
     'John', 'Reader', 'jreader',
     '+1234567892', '789 Novel Rd, Storytown')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT id, 'ADMIN' FROM users WHERE email = 'admin@elibrary.com'
ON CONFLICT (user_id, role) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT id, 'LIBRARIAN' FROM users WHERE email = 'librarian@elibrary.com'
ON CONFLICT (user_id, role) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT id, 'USER' FROM users WHERE email = 'user@elibrary.com'
ON CONFLICT (user_id, role) DO NOTHING;
