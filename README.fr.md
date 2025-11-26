# Socle de l'application

[English version here](README.md)

## Présentation

La principale raison pour laquelle j'ai créé cette application était de comprendre comment fonctionnent les GitHub Actions, et comment automatiser la compilation, les tests et le déploiement directement sur le Play Store. Je voulais maîtriser toute la chaîne DevOps et j'ai choisi de faire ça avec un application android.

Cela dit, j'avais aussi besoin d'une application SFTP simple à utiliser chez moi, donc je l'ai construite pour mon usage personnel.

## Sécurité

Les mots de passe sont encryptés avec AndroidKeyStore, ce qui garantit que les données sensibles restent protégées sur l'appareil. La prochaine étape importante est d'ajouter l'authentification par empreinte digitale pour se protéger contre les attaques de type man-in-the-middle, ainsi que d'autres fonctionnalités de sécurité pour renforcer l'application.

## Envie de contribuer ?

Fonce, fais-toi plaisir si tu veux. Pas de règles strictes ici.

## À faire

- Ajouter l'authentification par empreinte digitale
- Refactorer les classes UI qui sont désordonnées
- Créer un service de synchronisation qui upload automatiquement les fichiers sur un Wi-Fi spécifique à une heure programmée
- Faire en sorte que le service vérifie la connexion au serveur avant de lancer, et qu'il réessaie si hors ligne

Comme il s'agit de ma première expérience avec Kotlin et Android, le code est assez désordonné et mal organisé à certains endroits. Je dois le décomposer en plusieurs parties plus petites, et j'ai déjà commencé à le faire. N'hésitez pas à l'améliorer, je suis ouvert à tous les commentaires !

---

Merci d'avoir jeté un œil !
