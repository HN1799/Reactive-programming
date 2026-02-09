CREATE TABLE users (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
 first_name VARCHAR(50) NOT NULL,
 last_name VARCHAR(50) NOT NULL,
 email VARCHAR(100) NOT NULL UNIQUE,
 password   VARCHAR(225) NOT NULL
 );

-- springboot will pick this file automatically with name schema.sql
--it will be used to precreate the database table at time of starting the app