# 🧠 ThinkWise Academy — Backend Setup Guide

Questa guida descrive come configurare correttamente il backend del progetto **ThinkWise Academy**.

---

## 📌 Requisiti

- **Java 17** o superiore
- **IntelliJ IDEA** (consigliato)
- **PostgreSQL** installato e configurato
- Connessione internet per il download delle dipendenze Maven

---

## 🧩 Configurazione del Database

1. Accedi a **PostgreSQL** tramite `pgAdmin` o riga di comando
2. Crea un nuovo database (es: `thinkwise_db`)
3. Apri il progetto in **IntelliJ**
4. Modifica il file `src/main/resources/application.properties` inserendo i tuoi valori:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/thinkwise_db
   spring.datasource.username=TUO_USERNAME
   spring.datasource.password=LA_TUA_PASSWORD

   # Cloudinary (se usato per immagini)
   cloudinary.cloud_name=...
   cloudinary.api_key=...
   cloudinary.api_secret=...

   # Email (per invio notifiche)
   spring.mail.username=laTuaEmail@gmail.com
   spring.mail.password=laTuaPasswordApp
   ```

---

## ▶️ Avvio del backend

1. Apri IntelliJ e importa il progetto come **Maven Project**
2. Assicurati che il database PostgreSQL sia **attivo e accessibile**
3. Avvia l’applicazione eseguendo `ThinkWiseApplication.java`
4. Il backend sarà disponibile all’indirizzo:
   ```
   http://localhost:8080/api
   ```

---

## 📚 API disponibili

- `/api/studenti`
- `/api/calendario`
- `/api/dashboard`
- `/api/spese`
- `/api/report`
- `/api/corsi`
- `/api/insegnanti`
- `/api/aule`
- `/api/pagamenti`
- `/api/auth` *(per login e JWT)*

---

## 📝 Note aggiuntive

- Per inviare email automatiche (es. promemoria o notifiche), è necessario abilitare le "password per app" di Gmail o usare un SMTP alternativo.
- Cloudinary viene utilizzato per l’eventuale upload di immagini profilo o contenuti multimediali.

---

```
  
   ```

---
