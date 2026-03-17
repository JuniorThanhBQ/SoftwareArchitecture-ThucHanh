-- db/init.sql
CREATE TABLE IF NOT EXISTS todos ( 
id SERIAL PRIMARY KEY, 
    title VARCHAR(255) NOT NULL, 
    completed BOOLEAN DEFAULT FALSE, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
); 

INSERT INTO todos (title, completed) VALUES 
    ('Learn Docker', true), 
    ('Learn Docker Compose', false), 
    ('Build multi-container app', false);