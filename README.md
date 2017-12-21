# IMR3 - Interfaces Riches #


Dépôt Git du support de TP Android du module Interfaces Riches à l'intention des élèves en IM3 à l'Enssat.

L'objectif du TP est de réaliser une application Android illustrant les concepts présentés en cours.

***
## Pre-requis ##

* Android Studio 1.x
* SDK à jour
* Git d'installé

***

## Etapes ##
***

### Initialisation ###

Récupérez le code du projet sur votre machine de travail. Deux méthodes sont possibles.

* Clonez le repo sur votre machine en tapant la commande suivante dans un terminal.

`$> git clone https://bitbucket.org/marc_poppleton/imr3_android.git`

Ensuite dans Android Studio importez le projet et choissisez les valeurs par défaut.

* Ou bien dans Android Studio passez par le menu VCS > Checkout from Version Control > Git. Android Studio vous proposera alors de créer un nouveau projet à partir des sources récupérés, choisissez les valeurs par défaut.

### Etape 1 ###

A partir du code récupéré, construisez une application qui lit une vidéo téléchargée en ligne ici:

http://download.blender.org/peach/bigbuckbunny_movies/

Vous choisirez le format de vidéo qui convient en fonction des contraintes de format supporté.

##### Indices #####

[Media Formats][0]

[VideoView][1]

[MediaController][2]

[MediaPlayer.OnPreparedListener][3]

##### Solution #####

Pour consulter une solution et passer à l'étape suivante basculez sur la branch step_1

`$> git checkout step_1`

### Etape 2 ###

Ajoutez à l'application une vue qui affichera la page Wikipedia du film (Big Buck Bunny) quand la lecture du film démarre.
Vous prendrez garde à afficher la page qui correspond à la locale du terminal.

##### Indices #####

[WebView][4]

[WebChromeClient][5]

[WebViewClient][9]

##### Solution #####

Pour consulter une solution et passer à l'étape suivante basculez sur la branch step_2

`$> git checkout step_2`

### Etape 3 ###

Modifiez l'application pour qu'au lancement elle charge un fichier de meta-données contenant des URLs de pages à afficher à des moments précis du film.
Vous utiliserez le format que vous souhaitez pour définir ces timestamps.

##### Indices #####

[Handler][6]

[Runnable][7]

[InputStream][8]

##### Solution #####

Pour consulter une solution et passer à l'étape suivante basculez sur la branch step_3

`$> git checkout step_3`

### Etape 4 ###

Ajoutez à l'application un chapitrage du film. Vous ajouterez un élément d'interface pour afficher les chapitres et permettre à l'utilisateur de naviguer entre les chapitres.
Vous utiliserez le format que vous souhaitez pour définir le chapitrage.

##### Indices #####

[View.OnClickListener][10]

[InputStream][8]

##### Solution #####

Pour consulter une solution et passer à l'étape suivante basculez sur la branch step_4

`$> git checkout step_4`

### Etape 5 ###

Vous avez maintenant une application qui lit une vidéo, affiche des informations complémentaires synchronisées avec le film et propose un chapitrage que l'utilisateur peut parcourir.
Enrichissez cette base à votre guise! Vous pouvez par exemple charger le chapitrage et les metas-données de façon asynchrone, afficher une boite de progression en attendant que la vidéo soit chargée,...

Pour pouvoir évaluer votre travail, forkez ce repo, codez puis une fois votre travail terminé soumettez une pull request. La procédure est décrite [ici][13].

**May the fork be with you!**

##### Indices #####

[AsyncTask][11]

[ProgressDialog][12]

[0]: http://developer.android.com/guide/appendix/media-formats.html "Media Formats"
[1]: http://developer.android.com/reference/android/widget/VideoView.html "VideoView"
[2]: http://developer.android.com/reference/android/widget/MediaController.html "MediaController"
[3]: http://developer.android.com/reference/android/media/MediaPlayer.OnPreparedListener.html "MediaPlayer.OnPreparedListener"
[4]: http://developer.android.com/reference/android/webkit/WebView.html "WebView"
[5]: http://developer.android.com/reference/android/webkit/WebChromeClient.html "WebChromeClient"
[6]: http://developer.android.com/reference/android/os/Handler.html "Handler"
[7]: http://developer.android.com/reference/java/lang/Runnable.html "Runnable"
[8]: http://developer.android.com/reference/java/io/InputStream.html "InputStream"
[9]: http://developer.android.com/reference/android/webkit/WebViewClient.html "WebViewClient"
[10]: http://developer.android.com/reference/android/view/View.OnClickListener.html "View.OnClickListener"
[11]: http://developer.android.com/reference/android/os/AsyncTask.html "AsyncTask"
[12]: http://developer.android.com/reference/android/app/ProgressDialog.html "ProgressDialog"
[13]: https://www.atlassian.com/git/tutorials/making-a-pull-request/how-it-works "Fork"
