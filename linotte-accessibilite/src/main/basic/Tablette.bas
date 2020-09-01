' http://panoramic.1fr1.net/t6456-aide-pour-la-creation-d-un-wrapper-pour-le-langage-linotte
' Auteurs : Jack (panoramic.1fr1.net/u1) , Cpc (github.com/cpc6128/LangageLinotte)
dim Fichier_Choisi$
label Button_Click, Button_Click_Presse_Papier

' IHM
application_title "Tablette Linotte"  
caption 0,"Tablette Linotte"
alpha 1
button 3
button 4
open_dialog 2

' creation de l'IHM
' ===================================
' positionnement de l'ALPHA
top 1,50:left 1,110
caption 1, "LA TABLETTE LINOTTE"
font_bold 1:FONT_SIZE 1,22

' ===================================
' positionnement du bouton
top 3,100:left 3,80
' dimensions du bouton
width 3,400:height 3,120
caption 3,"LIRE UN FICHIER"
font_bold 3:FONT_SIZE 3,22
on_click 3, Button_Click

' ===================================
' positionnement du bouton
top 4,250:left 4,80
' dimensions du bouton
width 4,400:height 4,120
caption 4,"LIRE LE PRESSE-PAPIER"
font_bold 4:FONT_SIZE 4,22
on_click 4, Button_Click_Presse_Papier

' Thread en cours pour l'IHM
end

' ===================================
Button_Click:
   Fichier_Choisi$=file_name$(2)
   if (Fichier_Choisi$ <> "_") and (file_extract_extension$(Fichier_Choisi$) = ".liv")
     Fichier_Choisi$=file_extract_name$(Fichier_Choisi$)
     ' lancement de Linotte
     execute "java|-jar Atelier.jar -x exemples\"+Fichier_Choisi$
   end_if
   return

' ===================================
Button_Click_Presse_Papier:
   ' lancement de Linotte
   execute "java|-jar Atelier.jar -xp"
   return
