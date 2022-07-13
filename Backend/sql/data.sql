PRAGMA foreign_keys = ON;
INSERT INTO users(username, password)
VALUES
('qyq', '123'),
('hzc', '123'),
('xyc', '123');

INSERT INTO albums (albumname, owner)
VALUES
('animals', 'qyq'),
('vehicles', 'hzc'),
('nature', 'xyc');

INSERT INTO folders (foldername, albumId, owner, folderPath)
VALUES
('cats', '1', 'qyq'),
('boats', '2', 'hzc'),
('trees', '3', 'xyc');

INSERT INTO photos (photoUrl, photoScore, isRecommended, isStarred, folderId, albumId, owner)
VALUES
('qyq-animals-cats-123', '80', '0', '0', '1', '1', 'qyq'),
('hzc-vehicles-boats-456', '90', '1', '0', '2', '2', 'hzc'),
('xyc-nature-trees-789', '100', '1', '1', '3', '3', 'xyc');
