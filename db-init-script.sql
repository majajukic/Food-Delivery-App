IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'RestaurantDb')
BEGIN
    CREATE DATABASE RestaurantDb;
END

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'DeliveryDb')
BEGIN
    CREATE DATABASE DeliveryDb;
END

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'OrderDb')
BEGIN
    CREATE DATABASE OrderDb;
END

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'PaymentDb')
BEGIN
    CREATE DATABASE PaymentDb;
END