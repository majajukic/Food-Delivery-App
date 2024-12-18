IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'OrderDb')
BEGIN
    CREATE DATABASE OrderDb;
END