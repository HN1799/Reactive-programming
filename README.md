# Spring WebFlux Reactive Learning

This repository is a **learning-focused project** to understand **Spring WebFlux**, **reactive programming**, and **non-blocking communication between services**.

---

## Project Structure

```
spring_webflux_reactive_learning
│
├── WebClientDemo
├── javaScriptWebClient
├── photo-albumsService
├── usersService
├── README.md
```

---

## Module Overview

### usersService

Reactive service for **user management**.

**Key points:**
- Built using **Spring WebFlux**
- Uses **Mono** and **Flux**
- Saves users **reactively**
- Uses **Reactor Sinks** to emit newly created users
- Exposes a streaming endpoint

**Endpoint:**
- `GET /users/stream` → Streams newly created users in real time

➡️ Multiple clients can subscribe and receive updates instantly.

---

### photo-albumsService

Reactive service for **photo album management**.

**Key points:**
- Fully **non-blocking APIs**
- Called by `usersService`
- Uses reactive data flow (Mono / Flux)

---

### WebClientDemo

Demonstrates usage of **Spring WebClient**.

**Key points:**
- Non-blocking service-to-service communication
- Shows:
  - `GET`
  - `POST`
  - `PUT`
- Used by `usersService` to call `photo-albumsService`

➡️ No thread blocking while calling external services.

---

### javaScriptWebClient

Simple **JavaScript client**.

**Key points:**
- Subscribes to `/users/stream`
- Receives user data automatically when a new user is created

➡️ Demonstrates **backend → frontend real-time streaming**.

---

## User Creation Flow (Simple)

1. User is created in `usersService`
2. User is saved reactively
3. Reactor Sink emits the new user
4. `/users/stream` pushes the user to all subscribers
5. JavaScript client receives the update instantly

---

## Key Concepts Used

- Spring WebFlux
- Reactive Programming
- Mono / Flux
- WebClient
- Non-blocking APIs
- Reactor Sinks
- Hot Streams
- Real-time Streaming

---

## Purpose of This Project

- Learn **reactive programming**
- Understand **non-blocking service calls**
- Practice **data streaming using WebFlux**
- Observe **real-time updates with Flux**

---

## Note

This project is created **only for learning purposes**.

