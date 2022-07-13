PRAGMA foreign_keys = ON;
INSERT INTO users(username, password)
VALUES
('qyq', '123'),
('hzc', '123'),
('xyc', '123');

INSERT INTO albums (albumname, owner, albumPath)
VALUES
('animals', 'qyq', 'album1'),
('vehicles', 'hzc', 'album1'),
('nature', 'xyc', 'concert');

INSERT INTO folders (foldername, albumId, owner, folderPath)
VALUES
('cats', '1', 'qyq', 'tree'),
('boats', '2', 'hzc', 'tree'),
('trees', '3', 'xyc', 'people');

INSERT INTO photos (photoName, photoScore, isRecommended, isStarred, folderId, albumId, owner, photoPath)
VALUES
('qyq-animals-cats-1', '80', '0', '0', '1', '1', 'qyq', 'frame1.jpg'),
('hzc-vehicles-boats-1', '90', '1', '0', '2', '2', 'hzc', 'frame1.jpg'),
('xyc-nature-trees-1', '100', '1', '1', '3', '3', 'xyc', 'frame1.jpg');
