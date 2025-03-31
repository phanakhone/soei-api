# SOEI API

User management api for soei system.

## Features

1. JWT authentication

   - Validate auth jwt token
   - Refresh token

2. Users

   - Roles (SUPER_ADMIN, ADMIN, MODERATOR, USER)
   - Manage sub users
   - User Profile
   - Reset Password with token base
   - Email reset password

3. Companies

## Roles permission

1. SUPER_ADMIN

## For open port access to wsl

### Find your WSL IP address first (run in WSL)

ip addr show eth0

### Then in PowerShell, use the actual WSL IP address

netsh interface portproxy add v4tov4 listenport=8080 listenaddress=0.0.0.0 connectport=8080 connectaddress=<WSL_IP_ADDRESS>

### Allow through Windows Firewall

netsh advfirewall firewall add rule name="WSL App" dir=in action=allow protocol=TCP port=8080
