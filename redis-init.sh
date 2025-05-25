#!/bin/sh
# Arranca redis-server en background
redis-server &

# Espera a que Redis esté disponible
until redis-cli -h redis ping | grep PONG; do
  echo "Esperando a que Redis esté disponible..."
  sleep 1
done

# Crea el grupo de consumidores y el stream si no existen
redis-cli -h redis XGROUP CREATE UnificadorStream UnificadorGrupo $ MKSTREAM || true

# Mantén el contenedor vivo con el proceso redis-server en foreground
wait %1
