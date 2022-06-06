#!/bin/bash

if ! command -v mysql &> /dev/null
then
	echo "[Error] - mysql should be installed on the computer.";
	exit;
fi
echo "[INFO] - launching mysql."

sudo service mysql start

sudo mysql --execute "CREATE USER IF NOT EXISTS 'elifegame'@'localhost' IDENTIFIED BY 'elifegame';"

sudo mysql --execute "CREATE DATABASE IF NOT EXISTS elifeDB;
			GRANT ALL PRIVILEGES ON *.* TO 'elifegame'@'localhost' IDENTIFIED by 'elifegame';"

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="SET GLOBAL time_zone = '+00:00';"

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="CREATE TABLE IF NOT EXISTS eLifeGameStats
		(id INTEGER,day INTEGER, population INTEGER, youth INTEGER,PRIMARY KEY (id,day));";

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="CREATE TABLE IF NOT EXISTS eLifeGameFoodStats
		(id INTEGER,day INTEGER, foodSupply INTEGER, meanPrice FLOAT, lowestPrice FLOAT, offerNbr INTEGER, demand INTEGER,PRIMARY KEY (id,day));";

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="CREATE TABLE IF NOT EXISTS eLifeGameWoodStats
		(id INTEGER,day INTEGER, woodSupply INTEGER, meanPrice FLOAT, lowestPrice FLOAT, offerNbr INTEGER, demand INTEGER,PRIMARY KEY (id,day));";

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="CREATE TABLE IF NOT EXISTS eLifeGameStoneStats
		(id INTEGER,day INTEGER, woodSupply INTEGER, meanPrice FLOAT, lowestPrice FLOAT, offerNbr INTEGER, demand INTEGER,PRIMARY KEY (id,day));";

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="CREATE TABLE IF NOT EXISTS eLifeGameStateStats
		(id INTEGER,day INTEGER, money INTEGER,PRIMARY KEY (id,day));";

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="CREATE TABLE IF NOT EXISTS eLifeGameLandStats
		(id INTEGER,day INTEGER, landSupply INTEGER, meanPrice FLOAT, lowestPrice FLOAT, offerNbr INTEGER, demand INTEGER,State INTEGER,House INTEGER,Unemployed INTEGER,Farmer INTEGER,Builder INTEGER,WoodCutter INTEGER,Miner INTEGER,PRIMARY KEY (id,day));";

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="CREATE TABLE IF NOT EXISTS eLifeGameHouseStats
		(id INTEGER,day INTEGER, houseSupply INTEGER, meanPrice FLOAT, lowestPrice FLOAT, offerNbr INTEGER, demand INTEGER,PRIMARY KEY (id,day));";

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="CREATE TABLE IF NOT EXISTS eLifeGameJobStats
		(id INTEGER,day INTEGER, farmerNbr INTEGER, farmerIncome INTEGER,builderNbr INTEGER,builderIncome INTEGER,woodcutterNbr INTEGER,woodcutterIncome INTEGER,minerNbr INTEGER , minerIncome INTEGER, unemployedNbr INTEGER, PRIMARY KEY (id,day));";

echo "[INFO] - mysql is set up correctly."
