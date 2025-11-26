# App Foundation

[Version française ici](README.fr.md)

Soon on the Play Store (hopefully), waiting on Google to accept this app

## Overview

The main reason I made this app was to see how GitHub Actions work, how to automate compilation,
testing, and deployment directly to the Play Store. I wanted to understand the entire DevOps
pipeline, and I chose to do this with an Android application.

That said, I also needed a simple SFTP app to use at home, so I built this for myself.

### Demo

## Empty state

This is the screen when no server is configured:

![Empty State](img/syncroid-sftp-empty-state.png)

---

## Add a new server

Click **New Server** to add a server:

![Add New Server](img/syncroid-sftp-add-new-server.png)

---

## Server list

Here is the list of configured servers:

![Server List](img/syncroid-sftp-server-list.png)

---

## Upload files

Select the files to upload:

![Select Files to Upload](img/syncroid-sftp-select-files-to-upload.png)

## Security

Passwords are encrypted with AndroidKeyStore, so sensitive data stays safe on the device. The next
important step is to add fingerprint authentication to protect against man-in-the-middle attacks,
along with other security features to further strengthen the app.

## Want to contribute?

Go ahead, make yourself useful if you want. No big rules here, but please take a look at the issues
first, that’s where you’ll find what needs to be done.

### Rules

- If within a package you have two classes with the same name or similar responsibilities (for
  example `CatScream` and `CatMovement`), create a new sub-package inside that package to separate
  them (it would be a `CatBehaviour` sub-package for example).
- Classes that contain business logic or service logic must have tests before pushing, even if there
  are no clear domain layers here.
- Add JavaDoc comments. With Gemini integrated, this is easy nowadays... but if Gemini generates 300
  lines for a simple getter, just clean it up and adjust as needed.

## To do

- Add fingerprint authentication
- Refactor messy UI classes
- Create a sync service that uploads files automatically on a schedule, checking the server
  connection before starting and retrying later if offline

Since this is my first experience with Kotlin and Android, the code is quite messy and unorganized
in some places. I need to break down the code into smaller parts in several areas, and I've already
started doing that. Feel free to improve it, I’m open to any feedback!

---

Thanks for checking this out!

---
