-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost
-- Généré le : jeu. 12 jan. 2023 à 20:58
-- Version du serveur : 10.4.27-MariaDB
-- Version de PHP : 8.1.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `CHAT`
--

-- --------------------------------------------------------

--
-- Structure de la table `FriendShip`
--

CREATE TABLE `FriendShip` (
  `user_id` int(11) NOT NULL,
  `friend_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `FriendShip`
--

INSERT INTO `FriendShip` (`user_id`, `friend_id`) VALUES
(1, 2),
(1, 4),
(1, 5),
(2, 1),
(4, 1),
(5, 1),
(5, 2),
(2, 5),
(5, 6),
(6, 5),
(1, 8),
(8, 1),
(9, 5),
(5, 9);

-- --------------------------------------------------------

--
-- Structure de la table `Invitation`
--

CREATE TABLE `Invitation` (
  `id` int(11) NOT NULL,
  `sender_id` int(11) NOT NULL,
  `receiver_id` int(11) NOT NULL,
  `etat` varchar(20) DEFAULT NULL,
  `create_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `Invitation`
--

INSERT INTO `Invitation` (`id`, `sender_id`, `receiver_id`, `etat`, `create_at`) VALUES
(6, 3, 4, 'attente', '2023-01-10 22:03:36');

-- --------------------------------------------------------

--
-- Structure de la table `User`
--

CREATE TABLE `User` (
  `id` int(11) NOT NULL,
  `nom` varchar(60) NOT NULL,
  `password` varchar(50) NOT NULL,
  `adresse` varchar(128) DEFAULT NULL,
  `port` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `User`
--

INSERT INTO `User` (`id`, `nom`, `password`, `adresse`, `port`) VALUES
(1, 'Barry', 'passBarry', '/127.0.0.1', 12345),
(2, 'Bouba', 'passBouba', '/127.0.0.1', 12345),
(3, 'Rozay', 'passRozay', '/127.0.0.1', 12345),
(4, 'Breezy', 'passBreezy', '/127.0.0.1', 12345),
(5, 'Sow', 'passSow', '/127.0.0.1', 48102),
(6, 'Sadjo', 'passSadjo', '/127.0.0.1', 48194),
(7, 'Imad', 'passImad', '/127.0.0.1', 47306),
(8, 'Moha', 'passMoha', '/127.0.0.1', 36448),
(9, 'Jean', 'passJean', '/127.0.0.1', 34333),
(10, 'Brahim', 'passBrahim', '/127.0.0.1', 58743),
(11, 'Youssef', 'passYoussef', '/127.0.0.1', 55809),
(12, 'Oumar', 'passOumar', '/127.0.0.1', 50236),
(13, 'Mark', 'passMark', '/127.0.0.1', 51811),
(14, 'Pierre', 'passPierre', '/127.0.0.1', 40397),
(15, 'Sekou', 'passSekou', '/127.0.0.1', 53162),
(16, 'Alpha', 'passAlpha', '/127.0.0.1', 50461);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `FriendShip`
--
ALTER TABLE `FriendShip`
  ADD KEY `user_id` (`user_id`),
  ADD KEY `friend_id` (`friend_id`);

--
-- Index pour la table `Invitation`
--
ALTER TABLE `Invitation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `receiver_id` (`receiver_id`),
  ADD KEY `Invitation_ibfk_1` (`sender_id`);

--
-- Index pour la table `User`
--
ALTER TABLE `User`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `Invitation`
--
ALTER TABLE `Invitation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT pour la table `User`
--
ALTER TABLE `User`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `FriendShip`
--
ALTER TABLE `FriendShip`
  ADD CONSTRAINT `FriendShip_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `User` (`id`),
  ADD CONSTRAINT `FriendShip_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `User` (`id`);

--
-- Contraintes pour la table `Invitation`
--
ALTER TABLE `Invitation`
  ADD CONSTRAINT `Invitation_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `User` (`id`),
  ADD CONSTRAINT `Invitation_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `User` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
