IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'PaymentDb')
BEGIN
    CREATE DATABASE PaymentDb;
END