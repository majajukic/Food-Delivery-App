IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'DeliveryDb')
BEGIN
    CREATE DATABASE DeliveryDb;
END
