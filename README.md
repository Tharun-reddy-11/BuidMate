# BuildMate

BuildMate is a modern project studio and enquiry-management application for Tharun's college friends. Visitors can explore Web and AI project ideas, submit a requirement without registering, and receive an email after the admin accepts their request.

## Main flow

```text
Friend opens website → explores projects → fills one form
→ request is saved in MySQL → admin reviews it
→ admin clicks Accept & email → friend receives Gmail confirmation
```

## Technology

- Frontend: React, TypeScript, Vite, responsive CSS motion and 3D interactions
- Backend: Spring Boot, Spring Security, JWT for admin only, Spring Mail
- Database: MySQL
- Email: Gmail SMTP with a Google App Password

## Folders

```text
BuildMate/
├── backend/                 Open this folder in STS
├── frontend/                Open this folder in VS Code
├── render.yaml              Render backend deployment blueprint
└── README.md
```

## 1. Create the local MySQL database

Open MySQL Workbench and run:

```sql
CREATE DATABASE IF NOT EXISTS buildmate
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

The backend uses `root` as the default local username. For safety, the password is not saved in source code.

## 2. Configure and run the backend in STS

1. In STS select **File → Import → Maven → Existing Maven Projects**.
2. Select the `BuildMate/backend` folder.
3. Open **Run Configurations → Spring Boot App → Environment**.
4. Add:

```text
DB_PASSWORD=your_local_mysql_password
ADMIN_EMAIL=tharunreddyb30@gmail.com
ADMIN_PASSWORD=choose_a_private_admin_password
```

5. Run `BuildMateApplication.java` as **Spring Boot App**.

Backend URL:

```text
http://localhost:9292/api
```

Health check:

```text
http://localhost:9292/api/health
```

Spring Boot automatically creates/updates the tables and inserts the initial project templates.

## 3. Enable Gmail acceptance emails

Never use your normal Gmail password.

1. Enable 2-Step Verification on the Google account used to send mail.
2. Create a Google App Password.
3. Add these variables in the same STS Run Configuration:

```text
MAIL_ENABLED=true
MAIL_USERNAME=tharunreddyb30@gmail.com
MAIL_APP_PASSWORD=your_16_character_google_app_password
```

When the admin changes a request to `ACCEPTED` or clicks **Accept & email**, the backend sends the prepared welcome email. If Gmail fails, the acceptance is rolled back so the admin can retry.

## 4. Run the frontend in VS Code

Open `BuildMate/frontend` in VS Code and run:

```powershell
npm install
npm run dev
```

Open:

```text
http://localhost:5173
```

Public request form:

```text
http://localhost:5173/request
```

Admin sign-in:

```text
http://localhost:5173/admin/login
```

## application.properties

The active backend configuration is:

```text
backend/src/main/resources/application.properties
```

It includes only the required database, JPA, admin JWT, CORS, Gmail and logging settings. File uploads, profile images and Cloudinary configuration are not used.

## Recommended free deployment

Use this arrangement so friends can use the application even when your laptop is switched off:

```text
Vercel frontend → Koyeb Spring Boot API → Aiven MySQL
                                      ↘ Gmail SMTP on port 587
```

### 1. Aiven MySQL

1. Create an Aiven account and create a **MySQL Free** service.
2. Wait until the service status is **Running**.
3. Open **Databases** and create a database named `buildmate`.
4. Copy the host, port, username and password shown under **Connection information**.
5. The production JDBC value has this format:

```text
jdbc:mysql://YOUR_AIVEN_HOST:YOUR_AIVEN_PORT/buildmate?sslMode=REQUIRED&serverTimezone=Asia/Kolkata
```

Hibernate creates the tables automatically during the first backend startup. You can connect MySQL Workbench to the same Aiven host to view every online request.

### 2. Gmail acceptance email

Enable Google 2-Step Verification and create a 16-character App Password. Use the App Password only as `MAIL_APP_PASSWORD`; never upload it to GitHub.

### 3. Koyeb Spring Boot backend

Import the GitHub repository into Koyeb as a Web Service and choose:

```text
Work directory: backend
Builder: Dockerfile
Exposed port: 9292
Route: / to port 9292
Instance: Free
Health check: /api/health
```

Add these Koyeb environment variables/secrets:

```text
PORT=9292
DB_URL=jdbc:mysql://YOUR_AIVEN_HOST:YOUR_AIVEN_PORT/buildmate?sslMode=REQUIRED&serverTimezone=Asia/Kolkata
DB_USERNAME=avnadmin
DB_PASSWORD=YOUR_AIVEN_PASSWORD
JWT_SECRET=CREATE_A_LONG_RANDOM_SECRET_OF_AT_LEAST_32_CHARACTERS
ADMIN_EMAIL=tharunreddyb30@gmail.com
ADMIN_PASSWORD=CREATE_A_PRIVATE_ADMIN_PASSWORD
MAIL_USERNAME=tharunreddyb30@gmail.com
MAIL_APP_PASSWORD=YOUR_16_CHARACTER_GOOGLE_APP_PASSWORD
MAIL_ENABLED=true
FRONTEND_URLS=https://YOUR-VERCEL-SITE.vercel.app
JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=65 -XX:InitialRAMPercentage=20
```

After deployment, confirm that this returns an `UP` response:

```text
https://YOUR-KOYEB-APP.koyeb.app/api/health
```

Koyeb blocks plain SMTP port 25 but supports encrypted SMTP port 587, which this project already uses.

### 4. Vercel React frontend

Import the same GitHub repository into Vercel and choose:

```text
Root directory: frontend
Framework: Vite
Build command: npm run build
Output directory: dist
```

Add this production environment variable:

```text
VITE_API_URL=https://YOUR-KOYEB-APP.koyeb.app/api
```

Deploy and copy the final `https://...vercel.app` URL. Put that exact URL in the Koyeb `FRONTEND_URLS` value and redeploy the backend once.

### 5. Final online test

1. Open the Vercel link in an incognito window.
2. Submit one test request using the public form.
3. Sign in at `/admin/login` using the production `ADMIN_EMAIL` and `ADMIN_PASSWORD`.
4. Confirm that the test request appears.
5. Click **Accept & email** and confirm the green acceptance result, deadline reminder and Gmail message.
6. Open Aiven or connect MySQL Workbench to Aiven and confirm the row exists in `project_requests`.

Your laptop MySQL remains for local development. Online requests are stored in Aiven MySQL because a deployed backend cannot reliably connect to a database that exists only on your laptop.

## Verification

```powershell
cd backend
mvn test

cd ../frontend
npm run build
```

Do not commit real database passwords, Gmail App Passwords, JWT secrets or third-party API secrets.
