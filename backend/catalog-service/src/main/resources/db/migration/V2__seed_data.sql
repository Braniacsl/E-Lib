INSERT INTO books (title, author, isbn, description, publication_year, publisher, total_copies, available_copies, category, language, page_count, cover_image_url, is_active, created_at, updated_at)
VALUES
(
    'Clean Code: A Handbook of Agile Software Craftsmanship',
    'Robert C. Martin',
    '978-0132350884',
    'Even bad code can function. But if code isn''t clean, it can bring a development organization to its knees.',
    2008, 'Prentice Hall', 10, 8, 'Programming', 'English', 464,
    'https://images-na.ssl-images-amazon.com/images/I/41xShlnTZTL._SX376_BO1,204,203,200_.jpg',
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'Design Patterns: Elements of Reusable Object-Oriented Software',
    'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides',
    '978-0201633610',
    'Capturing a wealth of experience about the design of object-oriented software.',
    1994, 'Addison-Wesley Professional', 8, 6, 'Programming', 'English', 395,
    'https://images-na.ssl-images-amazon.com/images/I/51szD9HC9pL._SX395_BO1,204,203,200_.jpg',
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'The Pragmatic Programmer: Your Journey To Mastery',
    'David Thomas, Andrew Hunt',
    '978-0135957059',
    'The Pragmatic Programmer is one of those rare tech books you''ll read, re-read, and read again over the years.',
    2019, 'Addison-Wesley Professional', 12, 10, 'Programming', 'English', 352,
    'https://images-na.ssl-images-amazon.com/images/I/41HXiIojloL._SX396_BO1,204,203,200_.jpg',
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'Introduction to Algorithms',
    'Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Clifford Stein',
    '978-0262033848',
    'The leading textbook on algorithms today.',
    2009, 'MIT Press', 6, 4, 'Computer Science', 'English', 1312,
    'https://images-na.ssl-images-amazon.com/images/I/51V9XzGzYpL._SX258_BO1,204,203,200_.jpg',
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'The Lord of the Rings',
    'J.R.R. Tolkien',
    '978-0544003415',
    'One Ring to rule them all, One Ring to find them, One Ring to bring them all and in the darkness bind them.',
    1954, 'Houghton Mifflin Harcourt', 15, 12, 'Fantasy', 'English', 1178,
    'https://images-na.ssl-images-amazon.com/images/I/51EstVXM1UL._SX331_BO1,204,203,200_.jpg',
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    '1984',
    'George Orwell',
    '978-0451524935',
    'A dystopian social science fiction novel and cautionary tale.',
    1949, 'Signet Classics', 20, 18, 'Science Fiction', 'English', 328,
    'https://images-na.ssl-images-amazon.com/images/I/51rWt8h5JmL._SX322_BO1,204,203,200_.jpg',
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'To Kill a Mockingbird',
    'Harper Lee',
    '978-0446310789',
    'Compassionate, dramatic, and deeply moving.',
    1960, 'Grand Central Publishing', 18, 15, 'Fiction', 'English', 384,
    'https://images-na.ssl-images-amazon.com/images/I/51IXWZzlgSL._SX322_BO1,204,203,200_.jpg',
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'The Great Gatsby',
    'F. Scott Fitzgerald',
    '978-0743273565',
    'A portrait of the Jazz Age in all of its decadence and excess.',
    1925, 'Scribner', 14, 11, 'Classic', 'English', 180,
    'https://images-na.ssl-images-amazon.com/images/I/41VnFKC9srL._SX322_BO1,204,203,200_.jpg',
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'Pride and Prejudice',
    'Jane Austen',
    '978-1503290563',
    'A romantic novel of manners that depicts the life of the English landed gentry.',
    1813, 'CreateSpace Independent Publishing', 16, 14, 'Romance', 'English', 279,
    'https://images-na.ssl-images-amazon.com/images/I/51N5Qp%2BpQ-L._SX322_BO1,204,203,200_.jpg',
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'The Hobbit',
    'J.R.R. Tolkien',
    '978-0547928227',
    'The enchanting prelude to The Lord of the Rings.',
    1937, 'Houghton Mifflin Harcourt', 12, 9, 'Fantasy', 'English', 300,
    'https://images-na.ssl-images-amazon.com/images/I/51uLvJlKpNL._SX321_BO1,204,203,200_.jpg',
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
)
ON CONFLICT (isbn) DO NOTHING;
