# App Foundation

[Version française ici](README.fr.md)

## Overview

The main reason I made this app was to see how GitHub Actions work, how to automate compilation, testing, and deployment directly to the Play Store. I wanted to understand the entire DevOps pipeline, and I chose to do this with an Android application.

That said, I also needed a simple SFTP app to use at home, so I built this for myself.

## Security

Passwords are encrypted with AndroidKeyStore, so sensitive data stays safe on the device. The next important step is to add fingerprint authentication to protect against man-in-the-middle attacks, along with other security features to further strengthen the app.

## Want to contribute?

Go ahead, make yourself useful if you want. No big rules here.

## To Do

- Add fingerprint authentication
- Refactor messy UI classes
- Create a sync service that uploads files automatically on specific Wi-Fi and schedule
- Make the sync check server connection before starting, retry if offline

Since this is my first experience with Kotlin and Android, the code is quite messy and unorganized in some places. I need to break down the code into smaller parts in several areas, and I've already started doing that. Feel free to improve it, I’m open to any feedback!

---

Thanks for checking this out!

---
