
CREATE DATABASE IF NOT EXISTS `barberReservation` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `barberReservation`;


CREATE TABLE `shops` (
  `shopid` int(255) NOT NULL,
  `mail` varchar(100) NOT NULL,
  `password` varchar(80) NOT NULL,
  `facebook_acess_token` varchar(100),
  `facebook_link` varchar(100),
  `name` varchar(32) NOT NULL,
  `image` varchar(1000) NOT NULL,
  `rating` float NOT NULL,
  `openingTime` varchar(7) NOT NULL,
  `closingTime` varchar(7) NOT NULL,
  `phone` varchar(15) NOT NULL DEFAULT '9090909090',
  `address` varchar(180) NOT NULL,
)

CREATE TABLE `reservations` (
  `reservationId` int(255) NOT NULL,
  `type` varchar(50) NOT NULL,
  `shopid` varchar(400) NOT NULL,
  `status` varchar(255) NOT NULL,
  `userId` varchar(400) NOT NULL,
  `bookingDate` DATETIME NOT NULL,
  `note` nvarchar(MAX),
)


CREATE TABLE `users` (
  `userId` int(255) NOT NULL,
  `hash` varchar(255) NOT NULL,
  `name` varchar(100),
  `email` varchar(240) NOT NULL,
  `phone` varchar(20) NOT NULL,
  facebook_link varchar(100)
)

CREATE TABLE `working_hours` (
  `Id` int(255) NOT NULL,
  `description` varchar(100) NOT NULL,
  `mon` varchar(50) NOT NULL,
  `tue` varchar(50) NOT NULL,
  `wed` varchar(50) NOT NULL,
  `thu` varchar(50) NOT NULL,
  `fri` varchar(50) NOT NULL,
  `sat` varchar(50) NOT NULL,
  `sun` varchar(50) NOT NULL
)

INSERT INTO `working_hours` (`Id`,`description`, `mon`, `tue`, `wed`, `thu`, `fri`, `sat`, `sun`) VALUES
(1,'ap dung tu ngay x/y/zzzz den ngay y/z/xxxx', '11:00-23:00', '11:00-23:00', '11:00-23:00', '11:00-23:00', '11:00-23:00', '11:00-23:00', '11:00-23:00'),

ALTER TABLE `shops`
  ADD PRIMARY KEY (`shopid`),
  ADD UNIQUE KEY `shopid` (`shopid`);


ALTER TABLE `reservations`
  ADD PRIMARY KEY (`reservationId`),
  ADD UNIQUE KEY `reservationId` (`reservationId`);



ALTER TABLE `users`
  ADD PRIMARY KEY (`userId`),
  ADD UNIQUE KEY `userId` (`userId`),


ALTER TABLE `working_hours`
  ADD PRIMARY KEY (`Id`),
  ADD UNIQUE KEY `Id` (`Id`);

ALTER TABLE reservations ADD CONSTRAINT fk_user_reservationId FOREIGN KEY (userId) REFERENCES users(userId);
ALTER TABLE reservations ADD CONSTRAINT fk_shop_reservationId FOREIGN KEY (shopid) REFERENCES shops(shopid);
