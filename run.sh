#!/bin/bash

if ! command -v mysql &> /dev/null
then
	echo "[Error] - mysql should be installed on the computer.";
	exit;
fi

sudo mysql --execute "CREATE USER IF NOT EXISTS 'elifegame'@'localhost' IDENTIFIED BY 'elifegame';
			CREATE DATABASE IF NOT EXISTS elifeDB;
			GRANT ALL PRIVILEGES ON *.* TO 'elifegame'@'localhost' IDENTIFIED by 'elifegame';"

mysql --user="elifegame" --password="elifegame" --database="elifeDB" --execute="CREATE TABLE IF NOT EXISTS eLifeGameStats
		(day INTEGER, population INTEGER, foodSupply INTEGER, landSupply INTEGER, houseSupply INTEGER, PRIMARY KEY (day));";
		
echo "[INFO] - mysql is set up correctly."
