# Java Smart ChatBot Application (Console-Based)

## Project Overview
This project is a **Java-based ChatBot system** built using **Socket Programming**, **multi-threading**, **MySQL (JDBC)**,**java collections**.  

It allows **multiple clients** to connect to a server and interact with an **intelligent, context-aware chatbot** via the console.  

The ChatBot supports:
- **Context-aware conversations** (multi-turn memory)
- **Session-based learning** (`learn: question = answer`)
- **Keyword ranking and analysis**
- **Fuzzy matching** for user typos
- **Persistent storage** of all messages in MySQL

---

## Objectives
- Demonstrate **Java network programming** using sockets
- Implement **multi-threaded server** to handle multiple clients
- Integrate **MySQL database** for message persistence
- Build a **smart, context-aware chatbot**
- Use **Java Collections** and string processing for chatbot intelligence

---

## Technologies Used
- **Java (JDK 8+)**
- **Java Socket Programming**
- **Java Multithreading**
- **MySQL**
- **JDBC (MySQL Connector)**
- **VS Code** or any IDE supporting Java

---

## Features

### 1. ChatBot Intelligence
- **Predefined responses** (`hi`, `hello`, `bye`, etc.)
- **Context-aware follow-ups** (e.g., “What about UDP?” after TCP)
- **Fuzzy matching** for typos
- **Learning mechanism**: users can teach new questions and answers
- **Statistics & analytics**: top keywords, message counts, last messages

### 2. Multi-client Chat
- Server handles **multiple clients simultaneously** using threads
- Clients can **chat with each other** and interact with ChatBot
- Safe shutdown and resource cleanup

### 3. Database Integration
- MySQL table `messages` stores all chat messages
- Timestamped records for each message
- ChatBot can generate statistics based on database logs

---

##  How to Run

### Step 1: Setup MySQL
```sql
CREATE DATABASE chatdb;

CREATE TABLE IF NOT EXISTS messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender VARCHAR(50),
    message TEXT,
    time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
Update DBConnection.java with your username and password.

Ensure MySQL server is running.
---
```
### step 2: Compile
-> javac -d bin src/chatapp/*.java

---
### step 2: run server
->java -cp bin chatapp.ChatServerConsole

### then output example:
  Connected to MySQL
  Server started on port 6000
---
``
### step 2: run 
->java -cp bin chatapp.ChatClientConsole

### then input example:
   Enter username: tom

### client output:
    ChatBot: Welcome tom! Type 'help' to see commands.

### example console interaction:
        
/* User: hi
Bot: Hello! How can I help you today?

User: learn: what is TCP = TCP is connection-oriented
Bot: Learned successfully ✅

User: whats tcp
Bot: TCP is connection-oriented

User: what about UDP
Bot: Regarding our last topic: TCP is connection-oriented

User: time
Bot: Current time: 14:37:12.345

User: stats
Bot: 
📊 CHAT STATISTICS
------------------
Total messages: 6

Messages per user:
- tom: 6

Top keywords:
- tcp (2)
- what (2)
- is (2)
- connection-oriented (1)
- protocol (1)

Last messages per user:
- tom: [hi, learn: what is TCP = TCP is connection-oriented, whats tcp, what about UDP, time, stats]

User: help
Bot:
Commands:
help   - show help
stats  - chat analysis
learn: question = answer
Example:
learn: what is TCP = TCP is connection-oriented**\

### Type "exit" to safely disconnect the client.


       
