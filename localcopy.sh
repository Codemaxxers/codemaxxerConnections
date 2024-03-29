#!/bin/bash

# If this script has a permission denied error, run chmod +x localcopy.sh

echo "Starting/Restarting the codemaxxer backend on the localhost. Please make sure you are running this script from the source of your project, or in this case, the codemaxxerBackend folder. (cd ___/____/____/codemaxxerBackend/)"
echo -n "Are you ready [PRESS ENTER TO CONTINUE]"
read hello
echo "----------------------------------------------------------------------------------------------------------------"
docker-compose down
git pull .
./mvnw clean
docker-compose up -d --build
echo "----------------------------------------------------------------------------------------------------------------"
echo "Update Complete."