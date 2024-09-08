echo "Building Frontend..."
npm run --prefix src/main/frontend build

if [ $? -eq 0 ]; then
    echo "Frontend Built Successfully!"
else
    echo "Failed to build Frontend!"
    exit 1
fi

echo "Building Backend..."
./mvnw package -q -D skipTests

if [ $? -eq 0 ]; then
    echo "Backend Built Successfully!"
else
    echo "Failed to build Backend!"
    exit 1
fi

echo "Building Dockerfiles..."
docker-compose build

if [ $? -eq 0 ]; then
    echo "Dockerfiles Built Successfully!"
else
    echo "Failed to build Dockerfiles!"
    exit 1
fi