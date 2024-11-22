--
-- Table structure for table `books`
--

CREATE TABLE `books` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                         `author` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                         `genre` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                         `status` varchar(25) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'available',
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `books`
--

INSERT INTO `books` (`id`, `title`, `author`, `genre`, `status`) VALUES
(1, 'Giải tích 1', 'Lê Phê Đô', 'giáo trình', 'available'),
(2, 'Giải tích 2', 'Lê Phê Đô', 'giáo trình', 'available');

-- --------------------------------------------------------

--
-- Table structure for table `waiting_borrow`
--

CREATE TABLE `waiting_borrow` (
                                  `membership_id` varchar(50) NOT NULL,
                                  `document_id` int NOT NULL,
                                  `document_type` varchar(50) NOT NULL,
                                  `borrow_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  `status` varchar(50) NOT NULL DEFAULT 'waiting',
                                  PRIMARY KEY (`membership_id`,`document_id`,`borrow_date`,`document_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `books_from_api`
--


CREATE TABLE `books_from_api` (
                                  `id` int NOT NULL AUTO_INCREMENT,
                                  `isbn` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
                                  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                                  `author` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                                  `publisher` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                                  `published_date` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
                                  `description` text COLLATE utf8mb4_general_ci,
                                  `status` varchar(25) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'available',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


--
-- Table structure for table `borrow_return`
--

CREATE TABLE `borrow_return` (
                                 `membership_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
                                 `document_id` int NOT NULL,
                                 `document_type` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
                                 `borrow_date` date NOT NULL,
                                 `return_date` date DEFAULT NULL,
                                 `status` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
                                 PRIMARY KEY (`membership_id`,`document_type`,`borrow_date`,`document_id`),
                                 CONSTRAINT `borrow_return_ibfk_1` FOREIGN KEY (`membership_id`) REFERENCES `users` (`membership_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `borrow_return`
--

INSERT INTO `borrow_return` (`membership_id`, `document_id`, `document_type`, `borrow_date`, `return_date`, `status`) VALUES
('kk-01', 1, 'BOOK', '2024-10-08', '2024-10-08', 'Returned');

-- --------------------------------------------------------

--
-- Table structure for table `magazines`
--

CREATE TABLE `magazines` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                             `author` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                             `issue_number` int NOT NULL,
                             `publisher` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `status` varchar(25) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'available',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `magazines`
--

INSERT INTO `magazines` (`id`, `title`, `author`, `issue_number`, `publisher`, `status`) VALUES
(1, 'Tạp chí 1', 'Ngô Văn Tuấn', 200, 'Văn hóa', 'available'),
(2, 'Tạp chí 2', 'Lenin', 200, 'Văn hóa', 'available');

-- --------------------------------------------------------

--
-- Table structure for table `manager`
--

CREATE TABLE `manager` (
                           `manager` varchar(45) NOT NULL,
                           `password` varchar(45) NOT NULL,
                           PRIMARY KEY (`manager`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `theses`
--


CREATE TABLE `theses` (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                          `author` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                          `supervisor` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                          `university` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                          `status` varchar(25) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'available',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `theses`
--

INSERT INTO `theses` (`id`, `title`, `author`, `supervisor`, `university`, `status`) VALUES
(1, 'Luận văn 1', 'Nguyễn Văn A', 'GS. Trần Bình', 'Đại học X', 'available'),
(2, 'Luận văn 2', 'Trần Văn B', 'GS. Nguyễn Văn C', 'Đại học Y', 'available');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
                         `membership_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
                         `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                         `email` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                         `phone` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
                         `password` varchar(45) COLLATE utf8mb4_general_ci NOT NULL,
                         `user_name` varchar(45) COLLATE utf8mb4_general_ci NOT NULL,
                         PRIMARY KEY (`membership_id`),
                         UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`membership_id`, `name`, `email`, `phone`) VALUES
('KK-01', 'Ngô Tuấn', 'tuan@gmail.com', '0123456789'),
('KK-02', 'Nguyễn Ân', 'an@gmail.com', '0123456789');

-