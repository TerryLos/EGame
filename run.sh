#!/bin/bash

if ! command -v mysql &> /dev/null
then
	echo "[Error] - mysql should be installed on the computer.";
	exit;
fi
echo "[INFO] - launching mysql."

sudo service mysql start

sudo mysql --execute "CREATE USER IF NOT EXISTS 'elifegame'@'localhost' IDENTIFIED BY 'elifegame';
			CREATE DATABASE IF NOT EXISTS elifeDB;
			GRANT ALL PRIVILEGES ON *.* TO 'elifegame'@'localhost' IDENTIFIED by 'elifegame';"

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="CREATE TABLE IF NOT EXISTS eLifeGameStats
		(day INTEGER, population INTEGER, youth INTEGER,PRIMARY KEY (day));";

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="CREATE TABLE IF NOT EXISTS eLifeGameFoodStats
		(day INTEGER, foodSupply INTEGER, meanPrice FLOAT, PRIMARY KEY (day));";

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="CREATE TABLE IF NOT EXISTS eLifeGameLandStats
		(day INTEGER, landSupply INTEGER, meanPrice FLOAT, PRIMARY KEY (day));";

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="CREATE TABLE IF NOT EXISTS eLifeGameHouseStats
		(day INTEGER, houseSupply INTEGER, meanPrice FLOAT, PRIMARY KEY (day));";

echo "[INFO] - mysql is set up correctly."
