PRAGMA foreign_keys = ON;
CREATE TABLE users(
  username VARCHAR(20) PRIMARY KEY,
  password VARCHAR(256) NOT NULL
);

CREATE TABLE albums(
  albumId INTEGER PRIMARY KEY AUTOINCREMENT,
  albumname VARCHAR(20) NOT NULL,
  owner VARCHAR(20) REFERENCES users(username) ON DELETE CASCADE,
  created DATETIME DEFAULT CURRENT_TIMESTAMP,
  albumPath VARCHAR(256) NOT NULL
);

CREATE TABLE folders(
  folderId INTEGER PRIMARY KEY AUTOINCREMENT,
  foldername VARCHAR(20) NOT NULL,
  albumId INTEGER REFERENCES albums(albumId) ON DELETE CASCADE,
  owner VARCHAR(20) REFERENCES users(username) ON DELETE CASCADE,
  folderPath VARCHAR(256) NOT NULL
);

CREATE TABLE photos(
  photoId INTEGER PRIMARY KEY AUTOINCREMENT,
  photoName VARCHAR(64) NOT NULL,
  photoScore INTEGER NOT NULL,
  isRecommended INTEGER NOT NULL,
  isStarred INTEGER NOT NULL,
  folderId INTEGER REFERENCES folders(folderId) ON DELETE CASCADE,
  albumId INTEGER REFERENCES albums(albumId) ON DELETE CASCADE,
  owner VARCHAR(20) REFERENCES users(username) ON DELETE CASCADE,
  photoPath VARCHAR(256) NOT NULL
);