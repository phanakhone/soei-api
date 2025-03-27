# For open port access to wsl

# Find your WSL IP address first (run in WSL)

ip addr show eth0

# Then in PowerShell, use the actual WSL IP address

netsh interface portproxy add v4tov4 listenport=8080 listenaddress=0.0.0.0 connectport=8080 connectaddress=<WSL_IP_ADDRESS>

netsh interface portproxy add v4tov4 listenport=8888 listenaddress=0.0.0.0 connectport=8080 connectaddress=localhost

# Allow through Windows Firewall

netsh advfirewall firewall add rule name="WSL App" dir=in action=allow protocol=TCP port=8080
