                              APPLICATION DE CHAT EN JAVA:

DESCRIPTION DU PROJET:
                        Cet application à été créer dans le but de comprendre le fonctionnement des sockets en java.
                        il permet à plusieurs utilisateur de communiquer entrez s'ils sont amis.
                        

Technologie Utilisé:  
                        langage de programmation java , avec le protocole de Communication UDP et aussi les threads 
                        environnement de developpement Intellij community version
                        Mysql comme stockage pour la base de donnée.


Comment Lancer L'application:  

   Etape 1: Cloner le Projet dans Votre Ordinateur
            Avoir l'environnement d'exécution de java , pour ma part j'ai déjà le JDK 17,
            Cloner l'url du  projet url = ""  dans votre environnement de developpement,Avoir Mysql dans votre machine pour l'accès en base de donnée
                               
   Etape2: La librairie Mysql Connector                 
           Mysql-Connector: vous pouvez telecharger ici "https://dev.mysql.com/downloads/connector/j/" la librarie en fonction de votre OS              
           Ajouter La Librairie dans le Projet et ceci peut se faire de façon differente en fonction de votre environnement de travaille.
                                      
   Etape3: Le Script Lié à la base de donnée 
           Dans Le dossier ressource se trouve un fichier CHAT.sql , qui contient L'exportation des données de la base de donnée mysql.
           faudrai exécuter ce script qui déjà quelques lignes à fin de faciliter la manupilation de l'application
                               
   Etape4: Lancement des applications:
          Dans le package chatUDP.app , se trouve les fichiers contenant le code du programme.
          le fichier ChatServer.java  contient la methode main pour lancer le server.
          le fichier ChatClient.java contient la méthode maint pour lancer le client.     
                Nb: pour lancer plusieurs clients avec intelliJ, editer la configuration du run et autoriser allow multiple instance.
                

                                             COMMENT UTILISER L'APPLICATION UNE FOIS LANCÉE:
      
  Etape1: fonctionnalité de Login et Register.
          Vous devez faire un choix, entrez soit 1 pour faire le login soit 2 pour la registration(enregistrement). une fois fait vous entrez le username             et le password           
                
  Etape2: Le Menu D'accueil
          Ce menu vous demande de faire un choix, entre 
                       1: aller au chat           (vous entrez 1 au clavier pour cela )
                       2: Gerez les invitations   (vous entrez 2 )
                       3: Voir les amis           (vous entrez 3 )
                       4: deconnexion             (vous entrez 4 )
                       
  Chaque partie du menu d'acceil traite un cas, il suffit de suivre les instructions, car ils sont aussi préciser en console pour aider
  au client de naviguer.
  
   POUR LA PARTIE 2: Gerez Invitations:
                    Dans cette partie, on a 3 sous onglets
                        1: voir les invitations manqués    (vous entrez 1 pour celà et vous suivez les étapes )
                        2: envoyer demande                 (vous entrez 2 pour cela ,attention 
                                                            vous devez entrez le nom du user a fin d'envoyé l'invitation)
                        3: Sortir                         (va vous rédiriger vers le menu d'accueil )
         
   POUR LA PARTIE 1: ALLER AU CHAT
                          une fois la-bas vous pouvez envoyé et récevoir des messages de vous et vos amis
                          

                                                    Conclusion: 
          J'espère que vous arriverrez à installer les outils et lancer l'application pour l'utiliser.
          Toutes les suggestions sont les bienvenus.
          Merci !
                                      
 