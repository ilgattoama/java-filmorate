MERGE INTO mpa_ratings KEY(mpa_id) VALUES
    (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');

MERGE INTO genres KEY(genre_id) VALUES
    (1, STRINGDECODE('\u041a\u043e\u043c\u0435\u0434\u0438\u044f')),
    (2, STRINGDECODE('\u0414\u0440\u0430\u043c\u0430')),
    (3, STRINGDECODE('\u041c\u0443\u043b\u044c\u0442\u0444\u0438\u043b\u044c\u043c')),
    (4, STRINGDECODE('\u0422\u0440\u0438\u043b\u043b\u0435\u0440')),
    (5, STRINGDECODE('\u0414\u043e\u043a\u0443\u043c\u0435\u043d\u0442\u0430\u043b\u044c\u043d\u044b\u0439')),
    (6, STRINGDECODE('\u0411\u043e\u0435\u0432\u0438\u043a'));