<p align="center">
  <img src="http://langagelinotte.free.fr/github/entete2.png">
</p>

![GitHub release (release name instead of tag name)](https://img.shields.io/github/v/release/cpc6128/LangageLinotte)
![GitHub all releases](https://img.shields.io/github/downloads/cpc6128/LangageLinotte/total)
![Chocolatey Version](https://img.shields.io/chocolatey/v/linotte)
![GitHub last commit](https://img.shields.io/github/last-commit/cpc6128/LangageLinotte?style=plastic)
![GitHub issues](https://img.shields.io/github/issues/cpc6128/LangageLinotte)
![Java CI with Maven](https://github.com/cpc6128/LangageLinotte/workflows/Java%20CI%20with%20Maven/badge.svg)

# Atelier de programmation pour le Langage Linotte

Code source officiel du langage Linotte

Linotte est un langage de programmation.
Entièrement en français, il offre un environnement simple, complet et pédagogique pour apprendre à programmer.

Vous voulez contribuer au projet ? N'hésitez pas à venir me rejoindre sur ce depôt.

Le site officiel du langage est <a href="http://langagelinotte.free.fr">langagelinotte.free.fr</a>

#### Téléchargement de la dernière version 3.11, avec Java 17 inclus (installation simplifiée) :

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.11/atelierlinotte_3.11-1_amd64.deb">Pour Ubuntu/Debian</a>

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.11/AtelierLinotte-3.11.msi">Pour Windows</a>

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.11/AtelierLinotte-3.11.dmg">Pour MacOS</a>

#### Téléchargement de la dernière version 3.11, sans Java :

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.11/Linotte_3.11-2021-12-17-16-32.zip">Linotte_3.11.zip</a>

#### documentation en ligne :

- documentation en ligne : <a href="https://github.com/cpc6128/LangageLinotte/wiki">wiki</a>

- c'est en projet, nous avons besoin d'aide !

#### Pour contribuer à faire évoluer Linotte :
`git clone https://github.com/cpc6128/LangageLinotte.git`

`cd LangageLinotte`

`mvn clean package`

`java -jar linotte-lanceur/target/Atelier.jar`

#### Mes autres projets :

- corunning.fr (trouver des partenaires sportifs) : <a href="https://www.corunning.fr">corunning.fr</a>

- L'Atelier Linotte sur Android (encore en version 2) : <a href="https://play.google.com/store/apps/details?id=fr.codevallee.langagelinotte.atelierlinotte">Lien PlayStore</a>

- WhereIsAlice (casse-tête sur Android) : <a href="https://play.google.com/store/apps/details?id=fr.codevallee.whereisalice">Lien PlayStore</a>

![Atelier 3.0](http://langagelinotte.free.fr/github/atelier-dracula2.png)

#### Pour les développeurs du langage :

- Pour mac :
  jpackage --input . --main-jar Atelier.jar  --icon ../../linotte-resources/src/main/resources/linotte_hd.icns --app-version 3.8

- Pour windows :
  jpackage --type msi --input . --main-jar Atelier.jar --icon ..\..\..\linotte-resources\src\main\resources\linotte_new.ico --app-version 3.8 --win-menu --file-associations ..\..\..\linotte-resources\src\main\resources\mime.txt --vendor CodeVallée

- Pour ubuntu :
  /usr/lib/jvm/jdk-17/bin/jpackage --input . --main-jar Atelier.jar  --icon ../../../linotte-resources/src/main/resources/linotte_new.png --app-version 3.10
