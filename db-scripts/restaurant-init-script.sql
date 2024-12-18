IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'RestaurantDb')
BEGIN
    CREATE DATABASE RestaurantDb;
END