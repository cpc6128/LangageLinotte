'http://panoramic.1fr1.net/t6456-aide-pour-la-creation-d-un-wrapper-pour-le-langage-linotte
dim Fichier_Choisi$
label Reboucle,Button_Click
' création de l'IHM
' ===================================
alpha 1
' positionnement de l'ALPHA
top 1,50:left 1,110
caption 1, "CHOISISSEZ UN SOURCE"
font_bold 1:FONT_SIZE 1,22
' ===================================
open_dialog 2
Reboucle:
Fichier_Choisi$=file_name$(2)
if (Fichier_Choisi$ <> "_") and (file_extract_extension$(Fichier_Choisi$) = ".liv")
 Fichier_Choisi$=file_extract_name$(Fichier_Choisi$)
 caption 1, "Source Choisi:"+chr$(13)+chr$(10)+Fichier_Choisi$
else
 goto Reboucle
end_if
' ===================================
button 3
' positionnement du bouton
top 3,150:left 3,80
' dimensions du bouton
width 3,400:height 3,120
caption 3,"EXECUTER LE SOURCE"
font_bold 3:FONT_SIZE 3,22
on_click 3, Button_Click
end
' ===================================
Button_Click:
' mémorisation du nom de fichier
if file_exists("Source_Choisi.txt") = 1 then file_delete "Source_Choisi.txt"
file_open_write 1, "Source_Choisi.txt"
file_write 1, Fichier_Choisi$
file_close 1
' lancement de Linotte
execute "java|-jar Atelier.jar -x exemples"+Fichier_Choisi$
return