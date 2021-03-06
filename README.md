<p align="center">
  <img src="http://langagelinotte.free.fr/github/entete2.png">
</p>

![Java CI with Maven](https://github.com/cpc6128/LangageLinotte/workflows/Java%20CI%20with%20Maven/badge.svg)

# Atelier de programmation pour le Langage Linotte

Code source officiel du langage Linotte

Linotte est un langage de programmation. 
Entièrement en français, il offre un environnement simple, complet et pédagogique pour apprendre à programmer. 

Vous voulez contribuer au projet ? N'hésitez pas à venir me rejoindre sur ce depôt.

Le site officiel du langage est <a href="http://langagelinotte.free.fr">langagelinotte.free.fr</a>

#### Téléchargement de la dernière version 3.9, avec Java inclus (installation simplifiée) :

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.9/AtelierLinotte-3.9.dmg">Pour MacOS</a>

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.9/AtelierLinotte-3.9.msi">Pour Windows</a>

#### Téléchargement de la dernière version 3.9, sans Java :

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.9/Linotte_3.9-2021-04-10-16-32.zip">Linotte_3.9.zip</a>

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.5/Linotte_3.5-2020-11-18-15-10.zip">Linotte_3.5.zip (avec la 3D)</a>

#### documentation en ligne :

- documentation en ligne : <a href="https://github.com/cpc6128/LangageLinotte/wiki">wiki</a>

- c'est en projet, nous avons besoin d'aide ! 

#### Pour contribuer à faire évoluer Linotte :
 `git clone https://github.com/cpc6128/LangageLinotte.git`
 
 `cd LangageLinotte`
 
 `mvn clean package`
 
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
