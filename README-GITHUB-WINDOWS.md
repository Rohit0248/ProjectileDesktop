# Build Projectile Lab as a Windows .exe with GitHub Actions

1. Create a new GitHub repository.
2. Upload ALL files in this folder to the repository root. Do not upload the outer ZIP folder itself.
3. On GitHub, open **Actions**.
4. Select **Build Projectile Lab for Windows**.
5. Click **Run workflow**.
6. Wait for the green check mark.
7. Open the successful workflow run and scroll to **Artifacts**.
8. Download **ProjectileLab-Windows-Installer**.
9. Extract the downloaded ZIP. It contains `ProjectileLab.exe`.
10. Double-click `ProjectileLab.exe` on a Windows computer to install the app.

The workflow compiles the Java source on a Windows runner and uses `jpackage` to create the Windows installer. No Gradle or Android tools are required.
