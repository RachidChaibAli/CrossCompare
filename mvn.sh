#!/bin/bash

# Script to execute ./mvnw clean package for all Spring services


services=("PlaystationScraper" "SteamAPI" "UserManager" "API-Gateway") 

for service in "${services[@]}"; do
    echo "Building $service..."
    if [ -d "$service" ]; then
        cd "$service" || exit
        ./mvnw clean package
        if [ $? -ne 0 ]; then
            echo "Build failed for $service"
            exit 1
        fi
        cd - || exit
    else
        echo "Directory $service does not exist"
    fi
done

echo "All services built successfully!"