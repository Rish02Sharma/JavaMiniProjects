(SELECT u.name AS results
 FROM MovieRating mr
 JOIN Users u ON mr.user_id = u.user_id
 GROUP BY u.name
 ORDER BY COUNT(mr.movie_id) DESC, u.name ASC
 LIMIT 1)

UNION ALL

-- Step 2: Find the movie with the highest average rating in February 2020
(SELECT m.title AS results
 FROM MovieRating mr
 JOIN Movies m ON mr.movie_id = m.movie_id
 WHERE mr.created_at >= '2020-02-01' AND mr.created_at < '2020-03-01'
 GROUP BY m.title
 ORDER BY AVG(mr.rating) DESC, m.title ASC
 LIMIT 1);



--Table: Movies
--
--+---------------+---------+
--| Column Name   | Type    |
--+---------------+---------+
--| movie_id      | int     |
--| title         | varchar |
--+---------------+---------+
--movie_id is the primary key (column with unique values) for this table.
--title is the name of the movie.
--Each movie has a unique title.
--Table: Users
--
--+---------------+---------+
--| Column Name   | Type    |
--+---------------+---------+
--| user_id       | int     |
--| name          | varchar |
--+---------------+---------+
--user_id is the primary key (column with unique values) for this table.
--The column 'name' has unique values.
--Table: MovieRating
--
--+---------------+---------+
--| Column Name   | Type    |
--+---------------+---------+
--| movie_id      | int     |
--| user_id       | int     |
--| rating        | int     |
--| created_at    | date    |
--+---------------+---------+
--(movie_id, user_id) is the primary key (column with unique values) for this table.
--This table contains the rating of a movie by a user in their review.
--created_at is the user's review date.
--
--
--Write a solution to:
--
--Find the name of the user who has rated the greatest number of movies. In case of a tie, return the lexicographically smaller user name.
--Find the movie name with the highest average rating in February 2020. In case of a tie, return the lexicographically smaller movie name.
--The result format is in the following example.

