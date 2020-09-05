' http://panoramic.1fr1.net/t6456-aide-pour-la-creation-d-un-wrapper-pour-le-langage-linotte
' Auteurs : Jack (panoramic.1fr1.net/u1) , Cpc (github.com/cpc6128/LangageLinotte)
DIM Fichier_Choisi$
LABEL Button_Click_Selectionner, Button_Click_Editer, Button_Click_Executer , Button_Click_Presse_Papier, Button_Click_Quitter

' IHM
APPLICATION_TITLE "Tablette Linotte"
CAPTION 0,"Tablette Linotte"
WIDTH 0, 800

' Creation des objets :
ALPHA 1
' Selectionner un fichier
BUTTON 3
' Editer le fichier
BUTTON 4
' Executer le fichier
BUTTON 5
' quitter
BUTTON 6
' Nom du fichier
ALPHA 7
OPEN_DIALOG 2

' creation de l'IHM
' ===================================
' positionnement des l'ALPHAs
TOP 1,20:LEFT 1,110
CAPTION 1, "LA TABLETTE LINOTTE"
FONT_BOLD 1:FONT_SIZE 1,18
' Nom du fichier
TOP 7,100:LEFT 7,500
CAPTION 7, "..."
FONT_BOLD 7:FONT_SIZE 7,18

' ===================================
' positionnement du bouton selectionner un fichier
TOP 3,100:LEFT 3,80
' dimensions du bouton
WIDTH 3,400:HEIGHT 3,80
CAPTION 3,"SELECTIONNER UN FICHIER"
FONT_BOLD 3:FONT_SIZE 3,18
ON_CLICK 3, Button_Click_Selectionner

' ===================================
' positionnement du bouton Editer un fichier
TOP 4,210:LEFT 4,80
' dimensions du bouton
WIDTH 4,400:HEIGHT 4,80
CAPTION 4,"EDITER LE FICHIER"
FONT_BOLD 4:FONT_SIZE 4,18
ON_CLICK 4, Button_Click_Editer

' ===================================
' positionnement du bouton Executer un fichier
TOP 5,340:LEFT 5,80
' dimensions du bouton
WIDTH 5,400:height 5,80
CAPTION 5,"EXECUTER LE FICHIER"
FONT_BOLD 5:FONT_SIZE 5,18
ON_CLICK 5, Button_Click_Executer

' ===================================
' positionnement du bouton Quitter
TOP 6,340:LEFT 6,500
' dimensions du bouton
WIDTH 6,200:HEIGHT 6,80
CAPTION 6,"QUITTER"
FONT_BOLD 6:FONT_SIZE 6,18
ON_CLICK 6, Button_Click_Quitter

' ===================================
' Initialisation de l'open dialog 
DIR_DIALOG 2,DIR_CURRENT$

' Thread (EDT) en cours pour l'IHM
END

' ===================================
Button_Click_Selectionner:
   Fichier_Choisi$=file_name$(2)
   ' Fichier_Choisi$=file_extract_name$(Fichier_Choisi$)
    CAPTION 7, file_extract_name$(Fichier_Choisi$)
   ' if (Fichier_Choisi$ <> "_") and (file_extract_extension$(Fichier_Choisi$) = ".liv")
     ' Fichier_Choisi$=file_extract_name$(Fichier_Choisi$)
     ' lancement de Linotte
     ' execute "java|-jar Atelier.jar -x exemples\"+Fichier_Choisi$
   ' end_if
   RETURN

' ===================================
Button_Click_Presse_Papier:
   ' lancement de Linotte
   EXECUTE "java|-jar Atelier.jar -xp"
   RETURN

' ===================================
Button_Click_Executer:
   ' lancement de Linotte
   EXECUTE "java|-jar Atelier.jar -x "+Fichier_Choisi$
   RETURN

' ===================================
Button_Click_Editer:
   ' Editer le fichier
   EXECUTE "notepad| "+Fichier_Choisi$
   RETURN

' ===================================
Button_Click_Quitter:
   ' Quitter
   TERMINATE



