<p align="center">
  <img src="https://camo.githubusercontent.com/66b8d1c54bd4a1048d14da50e3eecfc19da9d7c9406e38569fb3f4aebcc9fbdf/687474703a2f2f6c616e676167656c696e6f7474652e667265652e66722f6769746875622f656e74657465322e706e67">
</p>

![GitHub release (release name instead of tag name)](https://img.shields.io/github/v/release/cpc6128/LangageLinotte?style=flat-square)
![GitHub all releases](https://img.shields.io/github/downloads/cpc6128/LangageLinotte/total?style=flat-square)
![GitHub release (latest by date)](https://img.shields.io/github/downloads/cpc6128/LangageLinotte/latest/total?style=flat-square)
![GitHub release (latest by date and asset)](https://img.shields.io/github/downloads/cpc6128/LangageLinotte/v3.9/Cours.de.programmation.Linotte.version.3.9.pdf?label=Tutoriel&style=flat-square)
![Chocolatey Version](https://img.shields.io/chocolatey/v/linotte?style=flat-square)
[![linotte](https://snapcraft.io/linotte/badge.svg)](https://snapcraft.io/linotte)
![GitHub last commit](https://img.shields.io/github/last-commit/cpc6128/LangageLinotte?style=plastic?style=flat-square)
![GitHub issues](https://img.shields.io/github/issues/cpc6128/LangageLinotte?style=flat-square)
![Java CI with Maven](https://github.com/cpc6128/LangageLinotte/workflows/Java%20CI%20with%20Maven/badge.svg)

# Atelier de programmation pour le Langage Linotte

Code source officiel du langage Linotte

Linotte est le langage de programmation entièrement en français. Il offre un environnement simple, complet et pédagogique pour apprendre à programmer.

Vous voulez contribuer au projet ? N'hésitez pas à venir me rejoindre sur ce depôt.

Le site officiel du langage est <a href="http://langagelinotte.free.fr">langagelinotte.free.fr</a>

#### Téléchargement de la dernière version 3.14, avec Java 19 inclus (installation simplifiée) :

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.14/AtelierLinotte-3.14.msi">Pour Windows</a>

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.12/AtelierLinotte-3.12.dmg">Pour MacOS</a>

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.14/atelierlinotte_3.14-1_amd64.deb">Pour Ubuntu/Debian</a>

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.12/atelierlinotte_3.12-1_armhf.deb">Pour Raspberry Pi</a>

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.14/atelierlinotte-3.14-1.x86_64.rpm">Pour Redhat/Fedora</a>

- <a href="https://chocolatey.org/packages/linotte">Chocolatey :</a> `choco install linotte`

- <a href="https://snapcraft.io/linotte">Snap Store :</a> `sudo snap install linotte`

#### Téléchargement de la dernière version 3.14, sans Java :

- <a href="https://github.com/cpc6128/LangageLinotte/releases/download/v3.14/Linotte_3.14-2022-09-22-20-02.zip">Linotte_3.14.zip</a>

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

![Atelier 3.0](https://camo.githubusercontent.com/17b8abb2ff608bb6635cdbf49c5a3d529b06a83cc865f46d13352e736176b170/687474703a2f2f6c616e676167656c696e6f7474652e667265652e66722f6769746875622f6174656c6965722d64726163756c61322e706e67)

#### Pour les développeurs du langage :

- Pour mac :

  `jpackage --input . --main-jar Atelier.jar  --icon ../../linotte-resources/src/main/resources/linotte_hd.icns --app-version 3.8`

- Pour windows :

  `"C:\Program Files\Java\jdk-19\bin\jpackage" --type msi --input . --main-jar Atelier.jar --icon ..\..\..\linotte-resources\src\main\resources\linotte_new.ico --app-version 3.14 --win-menu --file-associations ..\..\..\linotte-resources\src\main\resources\mime.txt --vendor CodeVallée`

- Pour Ubuntu ou Fedora :

  `/usr/lib/jvm/jdk-17/bin/jpackage --input . --main-jar Atelier.jar  --icon ../../../linotte-resources/src/main/resources/linotte_new.png --app-version 3.10`

- Chocolatey :

  `cpack`

  `choco push linotte.3.14.00.nupkg --source https://push.chocolatey.org/`

- Snapcraft :

  `snapcraft`

  `snap install linotte_3.11.0_amd64.snap --dangerous`

  `snapcraft upload --release=stable linotte_3.11.0_amd64.snap`

  `sudo snap remove linotte`
