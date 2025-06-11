SELECT * FROM college.college;
UPDATE college 
SET name = 'Boy', email = 'Boy@gmail.com', phno = '1327307890' 
WHERE id = 1;
USE college;
SELECT * FROM college
WHERE name LIKE 's%';
SELECT * FROM college;
USE college;
SELECT * FROM college.college LIMIT 0, 5000;
ALTER TABLE college ADD COLUMN phno VARCHAR(15);
UPDATE college
SET name = 'Toy', email = 'Toy@gmail.com', phno = '7327307890'
WHERE id = 13;
USE college;

UPDATE college
SET name = 'Rosy', email = 'Rosy@email1', phno = '14343143143'
WHERE id = 3;




