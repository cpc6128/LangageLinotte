' http://panoramic.1fr1.net/t6456-aide-pour-la-creation-d-un-wrapper-pour-le-langage-linotte
' Auteurs : Jack (panoramic.1fr1.net/u1) , Cpc (github.com/cpc6128/LangageLinotte)
dim Fichier_Choisi$
label Button_Click_Selectionner, Button_Click_Editer, Button_Click_Executer , Button_Click_Presse_Papier, Button_Click_Quitter

' IHM
application_title "Tablette Linotte"  
caption 0,"Tablette Linotte"
width 0, 800
alpha 1
' Selectionner un fichier
button 3
' Editer le fichier
button 4
' Exécuter le fichier
button 5
' quitter
button 6
' Nom du fichier
alpha 7
open_dialog 2

' creation de l'IHM
' ===================================
' positionnement des l'ALPHAs
top 1,20:left 1,110
caption 1, "LA TABLETTE LINOTTE"
font_bold 1:FONT_SIZE 1,18
' Nom du fichier
top 7,100:left 7,500
caption 7, "..."
font_bold 7:FONT_SIZE 7,18

' ===================================
' positionnement du bouton sélectionner un fichier
top 3,100:left 3,80
' dimensions du bouton
width 3,400:height 3,80            
caption 3,"SELECTIONNER UN FICHIER"
font_bold 3:FONT_SIZE 3,18
on_click 3, Button_Click_Selectionner

' ===================================
' positionnement du bouton Editer un fichier
top 4,210:left 4,80
' dimensions du bouton
width 4,400:height 4,80
caption 4,"EDITER LE FICHIER"
font_bold 4:FONT_SIZE 4,18
on_click 4, Button_Click_Editer

' ===================================
' positionnement du bouton Executer un fichier
top 5,340:left 5,80
' dimensions du bouton
width 5,400:height 5,80
caption 5,"EXECUTER LE FICHIER"
font_bold 5:FONT_SIZE 5,18
on_click 5, Button_Click_Executer

' ===================================
' positionnement du bouton Quitter
top 6,340:left 6,500
' dimensions du bouton
width 6,200:height 6,80
caption 6,"QUITTER"
font_bold 6:FONT_SIZE 6,18
on_click 6, Button_Click_Quitter

' Thread (EDT) en cours pour l'IHM
end

' ===================================
Button_Click_Selectionner:
   Fichier_Choisi$=file_name$(2)
   ' Fichier_Choisi$=file_extract_name$(Fichier_Choisi$)
    caption 7, file_extract_name$(Fichier_Choisi$)
   ' if (Fichier_Choisi$ <> "_") and (file_extract_extension$(Fichier_Choisi$) = ".liv")
     ' Fichier_Choisi$=file_extract_name$(Fichier_Choisi$)
     ' lancement de Linotte
     ' execute "java|-jar Atelier.jar -x exemples\"+Fichier_Choisi$
   ' end_if
   return

' ===================================
Button_Click_Presse_Papier:
   ' lancement de Linotte
   execute "java|-jar Atelier.jar -xp"
   return

' ===================================
Button_Click_Executer:
   ' lancement de Linotte
   execute "java|-jar Atelier.jar -x "+Fichier_Choisi$
   return

' ===================================
Button_Click_Editer:
   ' Editer le fichier
   execute "notepad| "+Fichier_Choisi$
   return

' ===================================
Button_Click_Quitter:
   ' Quitter
   terminate


