PRAGMA foreign_keys = ON;
INSERT INTO users(username, password)
VALUES
('qyq', '123'),
('hzc', '123'),
('xyc', '123'),
('Hanyun', '123');

INSERT INTO albums (albumname, owner)
VALUES
('animals', 'qyq'),
('vehicles', 'hzc'),
('nature', 'xyc'),
('fluffy', 'qyq');

INSERT INTO folders (foldername, albumId, owner)
VALUES
('cats', '1', 'qyq'),
('boats', '2', 'hzc'),
('trees', '3', 'xyc'),
('cats', '4', 'qyq'),
('dogs', '4', 'qyq');

INSERT INTO photos (photoname, photoScore, isRecommended, isStarred, folderId, albumId, owner)
VALUES
('qyq_animals_cats_0.jpg', '80', '0', '0', '1', '1', 'qyq'),
('hzc_vehicles_boats_0.jpg', '90', '1', '0', '2', '2', 'hzc'),
('xyc_nature_trees_0.jpg', '100', '1', '1', '3', '3', 'xyc'),
('qyq_fluffy_cats_0.jpg', '60', '0', '0', '4', '4', 'qyq'),
('qyq_fluffy_cats_1.jpg', '70', '0', '0', '4', '4', 'qyq'),
('qyq_fluffy_cats_2.jpg', '80', '1', '0', '4', '4', 'qyq'),
('qyq_fluffy_dogs_0.jpg', '60', '0', '0', '5', '4', 'qyq'),
('qyq_fluffy_dogs_1.jpg', '70', '0', '0', '5', '4', 'qyq'),
('qyq_fluffy_dogs_2.jpg', '80', '1', '0', '5', '4', 'qyq');
